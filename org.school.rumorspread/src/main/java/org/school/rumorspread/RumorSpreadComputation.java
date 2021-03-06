package org.school.rumorspread;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RumorSpreadComputation extends BasicComputation<LongWritable, RumorSpreadVertexValue, FloatWritable, FloatWritable> {
	
	// think of this as T_max
	public static final int MAX_SUPERSTEPS = 28;
	
	// R_constant in the MPI implementation
	public static final double alpha = 0.02;
	
	public static final double beta = 6;
	
	/**
	 * V would try to infect each of its outgoing neighbor, 
	 * succeeding in doing so with probability p_v,u 
	 */
	public static final double probOfInfection = 0.01;
	
	@Override
    public void compute(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex, Iterable<FloatWritable> messages) throws IOException {
		
		if (getSuperstep() >= 1) {
			if (vertex.getValue().size() == 0) {
				// Avoid computing for Vertex initialized with empty constructor
				return;
			}
			
			float vertexValueForT_MinusOne = vertex.getValue().getLastValue().get();
			float vertexValueForT = vertexValueForT_MinusOne;
			
			// if current vertex is infected it cannot be infected again
			if (vertexValueForT_MinusOne != 1.0) {
				float prodOneMinusNeighborWeightsWithValues = 1.0f;
				float prodNeighborWeightsWithOneMinusValues = 1.0f;
				float rut = (float) Math.exp(alpha * vertex.getValue().getValuesSum() - beta);
				
				/**
				 *  At each time step t, each node is either infected or susceptible, 
				 *  and every node v that was infected at time t − 1 has
				 *  a single chance to infect each of its neighbors u.
				 *  
				 *  Messages are the values of the neighbors at t - 1
				 */
				for (FloatWritable message : messages) {
					double valueOfNeighborAtTMinusOne = message.get();

					prodOneMinusNeighborWeightsWithValues *= (1 - probOfInfection * valueOfNeighborAtTMinusOne);
					prodNeighborWeightsWithOneMinusValues *= probOfInfection * (1 - valueOfNeighborAtTMinusOne);
				}
				
				vertexValueForT = (float) (1 - 
						((1 - vertexValueForT_MinusOne) * 
						prodOneMinusNeighborWeightsWithValues + 
						prodNeighborWeightsWithOneMinusValues * 
						prodOneMinusRukMinusOnePlusPrevValue(vertex)) *
						(1 - rut));
				
				// truncate value for vertex to 3 decimals				
				vertexValueForT = BigDecimal.valueOf(vertexValueForT)
					    .setScale(3, RoundingMode.HALF_UP)
					    .floatValue();
			}
			
			vertex.getValue().add(new FloatWritable(vertexValueForT));
		}
		
		// send current value to all edges		
		if (getSuperstep() < MAX_SUPERSTEPS && vertex.getValue().size() > 0) {
			FloatWritable message = vertex.getValue().getLastValue();
			sendMessageToAllEdges(vertex, message);
		} else {
			vertex.voteToHalt();
		}
    }
	
	private double prodOneMinusRukMinusOnePlusPrevValue(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex) {
		double prod = 1.0;
		
		// size() contains t-1 values because the t value is added after this computation
		for (int i = 1; i <= vertex.getValue().size(); i++) {
			prod *= (1.0 - Math.exp(alpha * vertex.getValue().getValuesSum(i) - beta) - 1.0 + vertex.getValue().get(i - 1).get());
		}
		
		return prod;
	}

}
