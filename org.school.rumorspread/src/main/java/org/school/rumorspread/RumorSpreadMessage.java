package org.school.rumorspread;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class RumorSpreadMessage implements Writable {

	private long vertexId;
	private double value;
	
	public RumorSpreadMessage() {
	}
	  
	public RumorSpreadMessage(long vertexId, double value) {
		setVertexId(vertexId);
		setValue(value);
	}
	
	public long getVertexId() {
		return vertexId;
	}

	public void setVertexId(long vertexId) {
		this.vertexId = vertexId;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeLong(vertexId);
		out.writeDouble(value);
	}

	public void readFields(DataInput in) throws IOException {
		vertexId = in.readLong();
		value = in.readDouble();
	}

}
