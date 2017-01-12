package org.school.rumorspread;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;

import java.io.IOException;

public class RumorSpreadComputation extends BasicComputation<LongWritable, DoubleWritable, FloatWritable, DoubleWritable> {
	
	private static final Logger LOG = Logger.getLogger(RumorSpreadComputation.class);
	
	public static final int MAX_SUPERSTEPS = 1;
	
	@Override
    public void compute(Vertex<LongWritable, DoubleWritable, FloatWritable> vertex, Iterable<DoubleWritable> messages) throws IOException {
		System.out.print("hello");
		LOG.info("hello-log4j-hello");
		
		// value of all other nodes
		if (getSuperstep() >= 1) {
			// set value of current vertex based on the computation
			DoubleWritable thisVertexValue = computeVertexValueFromVertexMessages_Sample(vertex, messages);
			vertex.setValue(thisVertexValue);
		}
		
		// send current value to all edges		
		if (getSuperstep() < MAX_SUPERSTEPS) {
			DoubleWritable message = new DoubleWritable(vertex.getValue().get());
			sendMessageToAllEdges(vertex, message);
		} else {
			vertex.voteToHalt();
		}
    }
	
	private DoubleWritable computeVertexValueFromVertexMessages_Sample(Vertex<LongWritable, DoubleWritable, FloatWritable> vertex, Iterable<DoubleWritable> messages) {
		double sum = 0;
		for (DoubleWritable message : messages) {
			sum += message.get();
		}

		double thisVertexValue = 0.0;
		if ((sum / vertex.getNumEdges()) >= 0.5) {
			thisVertexValue = 1.0;
		}
		
		return new DoubleWritable(thisVertexValue);
	}
	
	private DoubleWritable computeVertexValueFromVertexMessages_UnifiedModel(Vertex<LongWritable, DoubleWritable, FloatWritable> vertex, Iterable<DoubleWritable> messages) {
		double sum = 0;
		for (DoubleWritable message : messages) {
			sum += message.get();
		}

		double thisVertexValue = 0.0;
		if ((sum / vertex.getNumEdges()) >= 0.5) {
			thisVertexValue = 1.0;
		}
		
		return new DoubleWritable(thisVertexValue);
	}
}
