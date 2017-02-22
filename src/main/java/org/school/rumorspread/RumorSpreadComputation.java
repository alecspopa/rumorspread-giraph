package org.school.rumorspread;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;

public class RumorSpreadComputation extends BasicComputation<LongWritable, RumorSpreadVertexValue, FloatWritable, RumorSpreadMessage> {
	
	// think of this as T_max
	public static final int MAX_SUPERSTEPS = 3;
	
	// Rconstant in the MPI implementation
	public static final double alpha = 0.02;
	
	public static final double beta = 6; 
	
	@Override
    public void compute(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex, Iterable<RumorSpreadMessage> messages) throws IOException {
		
		if (getSuperstep() >= 1) {
			double vertexValueForT_MinusOne = vertex.getValue().getLastValue().get();
			double vertexValueForT = vertexValueForT_MinusOne;
			
			// if current vertex is infected it cannot be infected again
			if (vertexValueForT_MinusOne != 1.0) {
				double prodOneMinusNeighborWeightsWithValues = 1.0;
				double prodNeighborWeightsWithOneMinuxValues = 1.0;
				double rut = Math.exp(alpha * vertex.getValue().getValuesSum() - beta);
				
				/**
				 *  At each time step t, each node is either infected or susceptible, 
				 *  and every node v that was infected at time t − 1 has
				 *  a single chance to infect each of its neighbors u.
				 *  
				 *  Messages are the values of the neighbors at t - 1
				 */
				for (RumorSpreadMessage message : messages) {
					double valueOfNeighborAtTMinusOne = message.getValue();

					/**
					 * Then v would try to infect each of its outgoing neighbor, 
					 * succeeding in doing so with probability p_v,u 
					 */
					LongWritable messageVertexId = new LongWritable(message.getVertexId());
					float edgeValue = vertex.getEdgeValue(messageVertexId).get();
					
					prodOneMinusNeighborWeightsWithValues *= (1 - edgeValue * valueOfNeighborAtTMinusOne);
					prodNeighborWeightsWithOneMinuxValues *= edgeValue * (1 - valueOfNeighborAtTMinusOne);
				}
				
				vertexValueForT = 1 - (1 - vertexValueForT_MinusOne) * prodOneMinusNeighborWeightsWithValues + prodNeighborWeightsWithOneMinuxValues * prodOneMinusRukMinusOnePlusPrevValue(vertex) * (1 - rut);
			}
			
			vertex.getValue().add(new DoubleWritable(vertexValueForT));
		}
		
		// send current value to all edges		
		if (getSuperstep() < MAX_SUPERSTEPS) {
			RumorSpreadMessage message = new RumorSpreadMessage(vertex.getId().get(), vertex.getValue().getLastValue().get());
			sendMessageToAllEdges(vertex, message);
		} else {
			vertex.voteToHalt();
		}
    }
	
	private double prodOneMinusRukMinusOnePlusPrevValue(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex) {
		double prod = 1.0;
		
		// size() contains t-1 values because the t value is added after this computation
		for (int i = 1; i <= vertex.getValue().get().size(); i++) {
			prod *= (1.0 - Math.exp(alpha * vertex.getValue().getValuesSum(i) - beta) - 1.0 + vertex.getValue().getValueAtIndex(i - 1).get());
		}
		
		return prod;
	}

}
