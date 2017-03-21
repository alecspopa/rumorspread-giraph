package org.school.rumorspread;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableFactories;

public class RumorSpreadVertexValue implements Writable {

	private List<DoubleWritable> values;

	public RumorSpreadVertexValue() {
	}
	  
	public RumorSpreadVertexValue(List<DoubleWritable> values) {
		set(values);
	}
	
	public void readFields(DataInput in) throws IOException {
		this.values = new ArrayList<DoubleWritable>();
		
		int length = in.readInt();
		for (int i = 0; i < length; i++) {
			Writable value = WritableFactories.newInstance(DoubleWritable.class);
		    value.readFields(in);
		    
			this.values.add((DoubleWritable) value);
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(values.size());
		for (int i = 0; i < values.size(); i++) {
			values.get(i).write(out);
		}
	}
	
	public void set(List<DoubleWritable> values) { 
		this.values = values; 
	}
	
	public List<DoubleWritable> get() { 
		return values; 
	}
	
	public void add(DoubleWritable value) {
		values.add(value);
	}
	
	public DoubleWritable getValueAtIndex(int t) {
		return this.values.get(t);
	}
	
	public DoubleWritable getLastValue() {
		return getValueAtIndex(this.values.size() - 1);
	}
	
	public double getValuesSum(int t) {
		double sum = 0.0;
		
		for (int i = 0; i < t; i++) {
			sum += values.get(i).get();
	    }
		
		return sum;
	}
	
	public double getValuesSum() {	
		return getValuesSum(values.size());
	}
	
	public String[] toStrings() {
		String[] strings = new String[values.size()];
	    
		for (int i = 0; i < values.size(); i++) {
			strings[i] = values.get(i).toString();
	    }
		
	    return strings;
	}
	
	public String toString() {
		return String.join(" ", toStrings());
	}

}
