package org.school.rumorspread;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Random;

public class RumorSpreadComputation extends BasicComputation<LongWritable, RumorSpreadVertexValue, FloatWritable, RumorSpreadMessage> {
	
	private static final Logger LOG = Logger.getLogger(RumorSpreadComputation.class);
	
	// think of this as T_max
	public static final int MAX_SUPERSTEPS = 3;
	
	@Override
    public void compute(Vertex<LongWritable, RumorSpreadVertexValue, FloatWritable> vertex, Iterable<RumorSpreadMessage> messages) throws IOException {
		LOG.info("==============================");
		
		if (getSuperstep() >= 1) {
			double vertexValueForT = vertex.getValue().getLastValue().get();
			
			// if current vertex is infected it cannot be infected again
			if (vertexValueForT != 1.0) {
				/**
				 *  At each time step t, each node is either infected or susceptible, 
				 *  and every node v that was infected at time t − 1 has
				 *  a single chance to infect each of its neighbors u.
				 *  
				 *  Messages are the values of the neighbors at t - 1
				 */
				for (RumorSpreadMessage message : messages) {
					double valueOfNeighborAtTMinusOne = message.getValue();
					
					if (valueOfNeighborAtTMinusOne == 0.0) {
						// neighbor was not infected at previous step it cannot infect
						// continue to next neighbor
						continue;
					}
					
					/**
					 * Then v would try to infect each of its outgoing neighbor, 
					 * succeeding in doing so with probability p_v,u 
					 * given by the element G_v,u of the adjacency matrix. 
					 * The success of the event is simulated by generating a random number between 0 and 1, 
					 * and checking if its value is less than the probability of success (G_v,u)
					 * 
					 * 
					 */
					LongWritable messageVertexId = new LongWritable(message.getVertexId());
					float edgeValue = vertex.getEdgeValue(messageVertexId).get();
					
					double random = new Random().nextInt(11) / 10;
					if (random < edgeValue) {
						vertexValueForT = 1.0;
					}
				}
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

}
