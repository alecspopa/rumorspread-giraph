package org.school.rumorspread;

import java.io.IOException;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.IdWithValueTextOutputFormat;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.json.JSONArray;
import org.json.JSONException;

@SuppressWarnings("rawtypes")
public class RumorSpreadOutputFormat<I extends WritableComparable, V extends RumorSpreadVertexValue, E extends Writable> 
	extends TextVertexOutputFormat<I, V, E> {

	@Override
	public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
		return new RumorSpreadVertexWriter();
	}

	/**
	 * Vertex writer used with {@link IdWithValueTextOutputFormat}.
	 */
	protected class RumorSpreadVertexWriter extends TextVertexWriterToEachLine {

	    @Override
	    protected Text convertVertexToLine(Vertex<I, V, E> vertex) throws IOException {
	    	JSONArray json = new JSONArray();
			json.put(vertex.getId());
			
			try {
				json.put(vertex.getValue().getInfected().get());
				
				JSONArray numberOfInfectedAtTimeTJson = new JSONArray();

				DoubleWritable[] numberOfInfectedAtTimeT = vertex.getValue().getNumberOfInfectedAtTimeT();
				int numberOfInfectedAtTimeTLength = vertex.getValue().getNumberOfInfectedAtTimeT().length;
				
				for (int i = 0; i < numberOfInfectedAtTimeTLength; i++) {
					numberOfInfectedAtTimeTJson.put(numberOfInfectedAtTimeT[i].get());
				}
				
				json.put(numberOfInfectedAtTimeTJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	
    		return new Text(json.toString());
	    }
	    
	}
}
