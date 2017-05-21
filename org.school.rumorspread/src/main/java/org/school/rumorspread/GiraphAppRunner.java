package org.school.rumorspread;

import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.conf.GiraphConstants;
import org.apache.giraph.io.formats.GiraphFileInputFormat;
import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class GiraphAppRunner implements Tool {

	private static final Logger LOG = Logger.getLogger(RumorSpreadComputation.class);
	
	private Configuration conf;
	
	private String inputPath;
	private String outputPath;
	private Integer noWorkers;
	
	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	public Integer getNoWorkers() {
		return noWorkers;
	}

	public void setNoWorkers(Integer noWorkers) {
		this.noWorkers = noWorkers;
	}
	
	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public int run(String[] args) throws Exception {
		// input and output file path
		setInputPath(args[0]);
		setOutputPath(args[1]);
		setNoWorkers(Integer.parseInt(args[2]));
		
		GiraphConfiguration giraphConf = new GiraphConfiguration(getConf());
		
		giraphConf.setComputationClass(RumorSpreadComputation.class);
		giraphConf.setVertexInputFormatClass(RumorSpreadInputFormat.class);
		
		GiraphFileInputFormat.addVertexInputPath(giraphConf, new Path(getInputPath()));
		
		giraphConf.setVertexOutputFormatClass(RumorSpreadOutputFormat.class);
		 
		giraphConf.setWorkerConfiguration(0, getNoWorkers(), 100);
		giraphConf.setLocalTestMode(true);
		giraphConf.setMaxNumberOfSupersteps(RumorSpreadComputation.MAX_SUPERSTEPS);
		
		GiraphConstants.SPLIT_MASTER_WORKER.set(giraphConf, false);
		GiraphConstants.USE_OUT_OF_CORE_GRAPH.set(giraphConf, true);
		
		GiraphJob job = new GiraphJob(giraphConf, getClass().getName());
		
		FileOutputFormat.setOutputPath(job.getInternalJob(), new Path(getOutputPath()));
		
		LOG.info("==============================");
		LOG.info("Starting computation");
		LOG.info("==============================");
		
		return job.run(true) ? 0 : -1;
	}
	
	public static void main(String[] args) throws Exception {
		ToolRunner.run(new GiraphAppRunner(), args);
	}

}
