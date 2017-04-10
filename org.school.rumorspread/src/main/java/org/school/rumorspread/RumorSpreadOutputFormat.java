package org.school.rumorspread;

import java.io.IOException;
import java.util.Iterator;

import org.apache.giraph.graph.Vertex;
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

	protected class RumorSpreadVertexWriter extends TextVertexWriterToEachLine {

	    @Override
	    protected Text convertVertexToLine(Vertex<I, V, E> vertex) throws IOException {
	    	JSONArray json = new JSONArray();
	    	
	    	String vertexIdStr = vertex.getId().toString();
			json.put(Integer.parseInt(vertexIdStr));

			JSONArray valuesJson = new JSONArray();
			
			Iterator<DoubleWritable> itr = vertex.getValue().iterator();
			
			while (itr.hasNext()) {
				try {
					valuesJson.put(itr.next().get());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			json.put(valuesJson);
	      
    		return new Text(json.toString());
	    }
	    
	}
}
