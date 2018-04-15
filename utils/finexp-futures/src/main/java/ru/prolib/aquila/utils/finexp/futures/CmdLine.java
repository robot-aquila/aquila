package ru.prolib.aquila.utils.finexp.futures;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CmdLine {
	public static final String SOPT_ROOT = "r";
	public static final String LOPT_ROOT = "root-directory";
	public static final String SOPT_HELP = "h";
	public static final String LOPT_HELP = "help";
	public static final String LOPT_SKIP_INTEGRITY_TEST = "skip-integrity-test";
	public static final String LOPT_SHOW_SYMBOL_FILE = "show-symbol-file";
	public static final String LOPT_JBROWSER_CONFIG = "jbrowser-config";

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
		options.addOption(Option.builder()
				.longOpt(LOPT_SKIP_INTEGRITY_TEST)
				.desc("Skip an integrity test of the web interface.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_SHOW_SYMBOL_FILE)
				.desc("Show path to file of symbol primary data obtained from "
					+ "MOEX site then exit. The symbol must be provided as "
					+ "option argument.")
				.hasArg()
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_JBROWSER_CONFIG)
				.desc("Use JBrowserDriver configuration file. This should be an "
					+ "ini-file with jbrowser-driver section which contains redefinitions "
					+ "of one or more configurable JBrowserDriver options. See package "
					+ "ru.prolib.aquila.web.utils class WebDriverFactoryJBD for more details.")
				.hasArg()
				.build());
		return options;
	}
	
	public static CommandLine parse(String[] args) throws ParseException {
		final Options options = CmdLine.buildOptions();
		return new DefaultParser().parse(options, args);
	}
	
	public static void printHelpAndExit() {
		HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("finexp-futures", buildOptions());
        System.exit(0);
	}
	
	public static void printErrorAndExit(String msg) {
		System.err.println("Error: " + msg);
		System.err.println("Use --help to show all available options.");
		System.exit(1);
	}
	
}
