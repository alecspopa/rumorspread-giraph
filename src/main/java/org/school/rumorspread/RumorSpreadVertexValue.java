package org.school.rumorspread;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;

public class RumorSpreadVertexValue implements Writable {

	private DoubleWritable infected;
	private DoubleWritable[] numberOfInfectedAtTimeT;

	public RumorSpreadVertexValue() {
	}
	  
	public RumorSpreadVertexValue(DoubleWritable value, DoubleWritable[] numberOfInfectedAtTimeT) {
		setInfected(value);
		setNumberOfInfectedAtTimeT(numberOfInfectedAtTimeT);
	}
	
	public void readFields(DataInput in) throws IOException {
		this.infected = new DoubleWritable(in.readDouble());
		int numberOfInfectedAtTimeTLength = in.readInt();
		
		this.numberOfInfectedAtTimeT = new DoubleWritable[numberOfInfectedAtTimeTLength];
		
		for (int i = 0; i < numberOfInfectedAtTimeTLength; i++) {
			this.numberOfInfectedAtTimeT[i] = new DoubleWritable(in.readDouble());
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeDouble(this.infected.get());
		out.writeInt(numberOfInfectedAtTimeT.length);
		
		for (int i = 0; i < numberOfInfectedAtTimeT.length; i++) {
			numberOfInfectedAtTimeT[i].write(out);
		}
	}
	
	public DoubleWritable getInfected() {
		return infected;
	}

	public void setInfected(DoubleWritable value) {
		this.infected = value;
	}
	
	public DoubleWritable[] getNumberOfInfectedAtTimeT() {
		return numberOfInfectedAtTimeT;
	}

	public void setNumberOfInfectedAtTimeT(DoubleWritable[] numberOfInfectedAtTimeT) {
		this.numberOfInfectedAtTimeT = numberOfInfectedAtTimeT;
	}
	
}
