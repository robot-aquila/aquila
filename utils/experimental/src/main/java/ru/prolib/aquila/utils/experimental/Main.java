package ru.prolib.aquila.utils.experimental;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.utils.experimental.experiment.MoexTrackContract;

public class Main {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Main.class);
	}

	public static void main(String[] args) {
		System.exit(new Main().run(args));
	}
	
	private final Map<String, Experiment> experiments;
	private final CountDownLatch globalExit;
	
	Main() {
		globalExit = new CountDownLatch(1);
		experiments = new LinkedHashMap<>();
		experiments.put("moex_track_contract", new MoexTrackContract(globalExit));
	}
	
	private int run(String[] args) {
		final Scheduler scheduler = new SchedulerLocal();
		Experiment experiment = null;
		try {
			final CommandLine cmd = CmdLine.parse(args);
			if ( cmd.hasOption(CmdLine.LOPT_HELP) ) {
				CmdLine.printHelp();
				return 0;
			}
			if ( cmd.hasOption(CmdLine.LOPT_LIST_EXPERIMENTS) ) {
				for ( String experimentID : experiments.keySet() ) {
					System.out.println(experimentID);
				}
				return 0;
			}
			if ( ! cmd.hasOption(CmdLine.SOPT_EXPERIMENT) ) {
				CmdLine.printError("Experiment name must be specified.");
				return 1;
			}
			String experimentID = cmd.getOptionValue(CmdLine.SOPT_EXPERIMENT);
			if ( ! experiments.containsKey(experimentID) ) {
				CmdLine.printError("Unknown experiment: " + experimentID);
				return 1;
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					logger.debug("Initiate shutdown.");
					globalExit.countDown();
				}
			});
			experiment = experiments.get(experimentID);
			int r = experiment.run(scheduler, cmd);
			if ( r == 0 ) {
				try {
					globalExit.await();
				} catch ( InterruptedException e ) {
					globalExit.countDown();
					Thread.currentThread().interrupt();
				}
			}
			return r;
			
		} catch ( ParseException e ) {
			logger.error("Command line error: ", e);
			return 1;
		} finally {
			IOUtils.closeQuietly(experiment);
		}
	}

}
