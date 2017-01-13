package org.school.rumorspread;

import java.io.IOException;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.IdWithValueTextOutputFormat;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

@SuppressWarnings("rawtypes")
public class RumorSpreadOutputFormat<I extends WritableComparable, V extends RumorSpreadVertexValue, E extends Writable> 
	extends TextVertexOutputFormat<I, V, E> {

	/** Specify the output delimiter */
	public static final String LINE_TOKENIZE_VALUE = "output.delimiter";
	
	/** Default output delimiter */
	public static final String LINE_TOKENIZE_VALUE_DEFAULT = "\t";

	/** Reverse id and value order? */
	public static final String REVERSE_ID_AND_VALUE = "reverse.id.and.value";
	  
	/** Default is to not reverse id and value order. */
	public static final boolean REVERSE_ID_AND_VALUE_DEFAULT = false;

	@Override
	public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
		return new RumorSpreadVertexWriter();
	}

	/**
	 * Vertex writer used with {@link IdWithValueTextOutputFormat}.
	 */
	protected class RumorSpreadVertexWriter extends TextVertexWriterToEachLine {
		/** Saved delimiter */
		private String delimiter;

	    @Override
	    public void initialize(TaskAttemptContext context) throws IOException, InterruptedException {
	      super.initialize(context);
	      
	      delimiter = getConf().get(LINE_TOKENIZE_VALUE, LINE_TOKENIZE_VALUE_DEFAULT);
	    }

	    @Override
	    protected Text convertVertexToLine(Vertex<I, V, E> vertex) throws IOException {
	    	StringBuilder str = new StringBuilder();
	    	str.append(vertex.getId().toString());
	    	str.append(delimiter);
    		str.append(vertex.getValue().toString());
	      
    		return new Text(str.toString());
	    }
	}
}
