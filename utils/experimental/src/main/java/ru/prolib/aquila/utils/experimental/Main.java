package ru.prolib.aquila.utils.experimental;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.probe.SchedulerBuilder;
import ru.prolib.aquila.utils.experimental.sst.SecuritySimulationTest;

public class Main {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Main.class);
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		System.exit(new Main().run(args));
	}
	
	private final Map<String, Experiment> experiments;
	private final CountDownLatch globalExit;
	
	Main() {
		globalExit = new CountDownLatch(1);
		experiments = new LinkedHashMap<>();
		experiments.put("security_simulation_test", new SecuritySimulationTest());
	}
	
	private int run(String[] args) {
		Scheduler scheduler = null;
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
			if ( cmd.hasOption(CmdLine.SOPT_WITH_PROBE_SCHEDULER)) {
				Instant startTime = Instant.now();
				if ( cmd.hasOption(CmdLine.LOPT_PROBE_SCHEDULER_START_TIME) ) {
					startTime = Instant.parse(cmd.getOptionValue(CmdLine.LOPT_PROBE_SCHEDULER_START_TIME));
				}
				scheduler = new SchedulerBuilder()
					.setInitialTime(startTime)
					.setName("PROBE-SCHEDULER")
					.setExecutionSpeed(1)
					.buildScheduler();
			} else {
				scheduler = new SchedulerLocal();
			}
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					logger.debug("Initiate shutdown.");
					globalExit.countDown();
				}
			});
			experiment = experiments.get(experimentID);
			int r = experiment.run(scheduler, cmd, globalExit);
			if ( r != 0 ) {
				return r;
			}
			try {
				globalExit.await();
			} catch ( InterruptedException e ) {
				globalExit.countDown();
				Thread.currentThread().interrupt();
			}
			return experiment.getExitCode();
			
		} catch ( ParseException e ) {
			logger.error("Command line error: ", e);
			return 1;
		} finally {
			IOUtils.closeQuietly(experiment);
		}
	}

}
