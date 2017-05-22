package org.school.rumorspread;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.conf.GiraphConstants;
import org.apache.giraph.io.formats.GiraphFileInputFormat;
import org.apache.giraph.job.GiraphJob;
import org.apache.giraph.utils.ConfigurationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class GiraphAppRunner implements Tool {

	private static Options OPTIONS;

	static {
		OPTIONS = new Options();
		OPTIONS.addOption("h", "help", false, "Help");
		OPTIONS.addOption("in", "inputFile", true, "Input file path");
		OPTIONS.addOption("out", "outputDir", true, "Output dir path");
		OPTIONS.addOption("w", "workers", true, "Number of workers");
		OPTIONS.addOption("l", "local", false, "Local run or not");
	}
	
	private static final Logger LOG = Logger.getLogger(RumorSpreadComputation.class);
	
	private Configuration conf;
	
	private String inputPath;
	private String outputPath;
	private Integer noWorkers;
	private Boolean isLocalRun;
	
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
	
	public Boolean getIsLocalRun() {
		return isLocalRun;
	}

	public void setIsLocalRun(Boolean isLocalRun) {
		this.isLocalRun = isLocalRun;
	}
	
	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public int run(String[] args) throws Exception {
		CommandLine cmd = parseArgs(args);
		
		if (null == cmd) {
			return 0; // user requested help/info printout, don't run a job.
	    }
		
		setInputPath(cmd.getOptionValue("in"));
		setOutputPath(cmd.getOptionValue("out"));
		setNoWorkers(Integer.parseInt(cmd.getOptionValue("w")));
		setIsLocalRun(cmd.hasOption("l"));
		
		GiraphConfiguration giraphConf = new GiraphConfiguration(getConf());
		
		giraphConf.setComputationClass(RumorSpreadComputation.class);
		giraphConf.setVertexInputFormatClass(RumorSpreadInputFormat.class);
		
		GiraphFileInputFormat.addVertexInputPath(giraphConf, new Path(getInputPath()));
		
		giraphConf.setVertexOutputFormatClass(RumorSpreadOutputFormat.class);
		 
		giraphConf.setWorkerConfiguration(0, getNoWorkers(), 100);
		giraphConf.setLocalTestMode(getIsLocalRun());
		giraphConf.setMaxNumberOfSupersteps(RumorSpreadComputation.MAX_SUPERSTEPS);
		
		GiraphConstants.SPLIT_MASTER_WORKER.set(giraphConf, !getIsLocalRun());
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
	
	public static CommandLine parseArgs(final String[] args) throws ParseException {
		if (args.length == 0) {
			throw new IllegalArgumentException("No arguments were provided (try -h)");
		}
		    
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(OPTIONS, args);

		// simply printing help or info, return normally but kill job run
	    if (cmd.hasOption("h")) {
	      printHelp();
	      return null;
	    }
	    
	    return cmd;
	}
	
	private static void printHelp() {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp(ConfigurationUtils.class.getName(), OPTIONS, true);
	}

}
