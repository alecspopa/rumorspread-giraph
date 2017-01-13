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
	
	public static final int MAX_SUPERSTEPS = 3;
	
	@Override
    public void compute(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex, Iterable<DoubleWritable> messages) throws IOException {
		LOG.info("==============================");
		
		
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
			
			vertex.getValue().add(new DoubleWritable(vertexValue));
		}
		
		// send current value to all edges		
		if (getSuperstep() < MAX_SUPERSTEPS) {
			DoubleWritable message = vertex.getValue().getLastValue();
			sendMessageToAllEdges(vertex, message);
		} else {
			vertex.voteToHalt();
		}
    }

}
