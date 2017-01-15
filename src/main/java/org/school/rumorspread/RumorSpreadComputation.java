package org.school.rumorspread;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;

import java.io.IOException;

public class RumorSpreadComputation extends BasicComputation<LongWritable, RumorSpreadVertexValue, FloatWritable, DoubleWritable> {
	
	private static final Logger LOG = Logger.getLogger(RumorSpreadComputation.class);

	public static final int TIME_MAX = 3;
	public static final int MAX_SUPERSTEPS = 3;
	
	@Override
    public void compute(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex, Iterable<DoubleWritable> messages) throws IOException {
		LOG.info("==============================");
		
		// array to store time of infection of node
		boolean[] infected = new boolean[TIME_MAX];
		
		// if vertex is infected at time 0 mark that
		if (vertex.getValue().getInfected().get() == 1.0) {
			infected[0] = true;
		}
		
		for (int t = 0; t < TIME_MAX; t++) {
			rumorCascade(vertex, infected, t);
		}
		
		// value of all other nodes
		if (getSuperstep() >= 1) {
			// set value of current vertex based on the computation
			double sum = 0;
			for (DoubleWritable message : messages) {
				sum += message.get();
			}

			double vertexValue = 0.0;
			if ((sum / vertex.getNumEdges()) >= 0.5) {
				vertexValue = 1.0;
			}
			
			vertex.getValue().setInfected(new DoubleWritable(vertexValue));
		}
		
		// send current value to all edges		
		if (getSuperstep() < MAX_SUPERSTEPS) {
			sendMessageToAllEdges(vertex, vertex.getValue().getInfected());
		} else {
			vertex.voteToHalt();
		}
    }
	
	protected void rumorCascade(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex, boolean[] infected, int t) {
		
	}

}
