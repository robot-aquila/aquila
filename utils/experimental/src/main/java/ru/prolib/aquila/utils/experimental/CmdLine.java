package ru.prolib.aquila.utils.experimental;

import java.io.File;
import java.time.ZoneId;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.timeframe.ZTFMinutes;

public class CmdLine {
	public static final String SOPT_ROOT = "r";
	public static final String SOPT_HELP = "h";
	public static final String SOPT_EXPERIMENT = "e";
	public static final String SOPT_SYMBOL = "s";
	public static final String SOPT_LIST_EXPERIMENTS = "l";
	public static final String SOPT_WITH_PROBE_SCHEDULER = "p";
	public static final String SOPT_PROBE_SCHEDULER_START_TIME = "t";
	
	public static final String LOPT_ROOT = "root-directory";
	public static final String LOPT_HELP = "help";
	public static final String LOPT_EXPERIMENT = "experiment";
	public static final String LOPT_SYMBOL = "symbol";
	public static final String LOPT_LIST_EXPERIMENTS = "list-experiments";
	public static final String LOPT_WITH_PROBE_SCHEDULER = "with-probe-scheduler";
	public static final String LOPT_PROBE_SCHEDULER_START_TIME = "probe-scheduler-start-time";
	public static final String LOPT_TIMEFRAME = "timeframe";
	
	public static Options buildOptions() {
		Options options = new Options();
		options.addOption(Option.builder(SOPT_ROOT)
                .longOpt(LOPT_ROOT)
                .hasArg()
                .desc("The root directory of the data storage.")
                .build());
		options.addOption(Option.builder(SOPT_HELP)
				.longOpt(LOPT_HELP)
				.desc("Show help page and exit.")
				.build());
		options.addOption(Option.builder(SOPT_EXPERIMENT)
				.longOpt(LOPT_EXPERIMENT)
				.hasArg()
				.desc("Experiment name.")
				.build());
		options.addOption(Option.builder(SOPT_SYMBOL)
				.longOpt(LOPT_SYMBOL)
				.hasArg()
				.desc("Symbol.")
				.build());
		options.addOption(Option.builder(SOPT_LIST_EXPERIMENTS)
				.longOpt(LOPT_LIST_EXPERIMENTS)
				.desc("List available experiments.")
				.build());
		options.addOption(Option.builder(SOPT_WITH_PROBE_SCHEDULER)
				.longOpt(LOPT_WITH_PROBE_SCHEDULER)
				.desc("Use PROBE scheduler.")
				.build());
		options.addOption(Option.builder(SOPT_PROBE_SCHEDULER_START_TIME)
				.longOpt(LOPT_PROBE_SCHEDULER_START_TIME)
				.hasArg()
				.desc("Set start time of PROBE scheduler. If omitted then"
					+ " current time will be used. This option has only effect"
					+ " in combination with --" + LOPT_WITH_PROBE_SCHEDULER + " option.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_TIMEFRAME)
				.hasArg()
				.desc("Specify a timeframe by template TN where T is a chrono unit"
					+ " (M - minutes, H - hours, D - days), N - is length of interval"
					+ " in chrono units. For example: M1 - 1 minute interval, H4 - 4"
					+ " hours interval.")
				.build());
		return options;
	}
	
	public static CommandLine parse(String[] args) throws ParseException {
		final Options options = CmdLine.buildOptions();
		return new DefaultParser().parse(options, args);
	}
	
	public static void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("experimental", buildOptions());
	}
	
	public static void printError(String msg) {
		System.err.println("Error: " + msg);
		System.err.println("Use --help to show available options.");
	}

	public static boolean testRootDirectory(CommandLine cmd) {
		if ( ! cmd.hasOption(CmdLine.LOPT_ROOT) ) {
			printError("The root directory is a required argument");
			return false;
		}
		File root = new File(cmd.getOptionValue(CmdLine.LOPT_ROOT));
		if ( ! root.exists() ) {
			printError("The root directory is not exists: " + root);
			return false;
		}
		if ( ! root.isDirectory() ) {
			printError("The pathname is not a directory: " + root);
			return false;
		}
		return true;
	}
	
	public static ZTFrame getTimeFrame(CommandLine cmd, ZoneId zoneID) {
		String x = cmd.getOptionValue(CmdLine.LOPT_TIMEFRAME, "M1");
		String u = x.substring(0, 1).toLowerCase();
		String n = x.substring(1);
		if ( n.length() > 0 ) {
			try {
				int p = Integer.parseInt(n);
				switch ( u ) {
				case "m":
					return new ZTFMinutes(p, zoneID);
				case "h": // TODO: 
				case "d": // TODO:
				default:
					break;	
				}	
			} catch ( NumberFormatException e ) { }	
		}
		throw new IllegalArgumentException("Unsupported timeframe: " + x);
	}

}
