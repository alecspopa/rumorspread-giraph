package org.school.rumorspread;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.giraph.utils.ArrayListWritable;
import org.apache.hadoop.io.DoubleWritable;

public class RumorSpreadVertexValue extends ArrayListWritable<DoubleWritable> {

	private static final long serialVersionUID = 227809916468865164L;

	public RumorSpreadVertexValue() {
	}
	
	public RumorSpreadVertexValue(ArrayList<DoubleWritable> values) {
		this.clear();
		this.addAll(values);
	}
	
	@Override
	public void setClass() {
		setClass(DoubleWritable.class);
	}

	public DoubleWritable getLastValue() {
		return this.get(this.size() - 1);
	}
	
	public double getValuesSum() {	
		return getValuesSum(this.size());
	}
	
	public double getValuesSum(int t) {
		double sum = 0.0;
		
		Iterator<DoubleWritable> itr = this.iterator();
		for (int i = 0; i < t && i < this.size() && itr.hasNext(); i++) {
			sum +=  itr.next().get();
		}
		
		return sum;
	}
	
	public String[] toStrings() {
		String[] strings = new String[this.size()];
		
		Iterator<DoubleWritable> itr = this.iterator();
		for (int i = 0; i < this.size() && itr.hasNext(); i++) {
			strings[i] = itr.next().toString();
		}
		
	    return strings;
	}
	
	public String toString() {
		return StringUtils.join(this.toStrings(), " ");
	}
}
