package org.school.rumorspread;

import java.io.IOException;
import java.util.List;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.common.collect.Lists;

/**
 * File input sample (JSON format)
 * 
 * <code>long</code> vertexId
 * <code>double</code> vertexValue
 * <code>integer</code> TIME_MAX
 * <code>double</code> infectedAtTimex
 * <code>long</code> destEdgeId
 * <code>double</code> destEdgeValue
 *
 * [vertexId, vertexValue, [TIME_MAX, infectedAtTime0, infectedAtTime1, ...], [[destEdgeId, destEdgeValue], [destEdgeId, destEdgeValue], ...]]
 */

public class RumorSpreadInputFormat extends TextVertexInputFormat<LongWritable, RumorSpreadVertexValue, FloatWritable> {

	
	@Override
	public TextVertexInputFormat<LongWritable, RumorSpreadVertexValue, FloatWritable>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException {
		return new RumorSpreadVertexReader();
	}
	
	class RumorSpreadVertexReader extends TextVertexReaderFromEachLineProcessedHandlingExceptions<JSONArray, JSONException> {

		@Override
		protected JSONArray preprocessLine(Text line) throws JSONException, IOException {
			return new JSONArray(line.toString());
		}
	
		@Override
		protected LongWritable getId(JSONArray jsonVertex) throws JSONException, IOException {
			return new LongWritable(jsonVertex.getLong(0));
		}
	
		@Override
		protected RumorSpreadVertexValue getValue(JSONArray jsonVertex) throws JSONException, IOException {
			DoubleWritable value = new DoubleWritable(jsonVertex.getDouble(1));
			
			JSONArray jsonVertexValueArray = jsonVertex.getJSONArray(2);
			DoubleWritable[] numberOfInfectedAtTimeT = new DoubleWritable[jsonVertexValueArray.length()];
			
			for (int i = 0; i < jsonVertexValueArray.length(); i++) {
				numberOfInfectedAtTimeT[i] = new DoubleWritable(jsonVertexValueArray.getDouble(i));
			}
			
			return new RumorSpreadVertexValue(value, numberOfInfectedAtTimeT);
		}
	
		@Override
		protected Iterable<Edge<LongWritable, FloatWritable>> getEdges(JSONArray jsonVertex) throws JSONException, IOException {
			JSONArray jsonEdgeArray = jsonVertex.getJSONArray(3);
			List<Edge<LongWritable, FloatWritable>> edges = Lists.newArrayListWithCapacity(jsonEdgeArray.length());
		      
			for (int i = 0; i < jsonEdgeArray.length(); ++i) {
				JSONArray jsonEdge = jsonEdgeArray.getJSONArray(i);
				
				edges.add(EdgeFactory.create(new LongWritable(jsonEdge.getLong(0)), new FloatWritable((float) jsonEdge.getDouble(1))));
			}
			
			return edges;
		}
	}
	
}
