package ru.prolib.aquila.utils.finexp.futures;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.utils.finexp.futures.finam.FinamWebTickDataTracker;
import ru.prolib.aquila.utils.finexp.futures.moex.MoexWebContractTracker;
import ru.prolib.aquila.web.utils.finam.FidexpFactory;
import ru.prolib.aquila.web.utils.finam.FidexpFactorySTD;
import ru.prolib.aquila.web.utils.finam.FidexpFileStorage;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;
import ru.prolib.aquila.web.utils.moex.MoexFactory;
import ru.prolib.aquila.web.utils.moex.MoexFactorySTD;

public class Service {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Service.class);
	}

	public static void main(String[] args) throws ParseException {
		final CommandLine cmd = CmdLine.parse(args);
		if ( cmd.hasOption(CmdLine.LOPT_HELP) ) {
			CmdLine.printHelpAndExit();
		} 
		if ( ! cmd.hasOption(CmdLine.LOPT_ROOT) ) {
			CmdLine.printErrorAndExit("The root directory is a required argument");
		}
		File root = new File(cmd.getOptionValue(CmdLine.LOPT_ROOT));
		if ( ! root.exists() ) {
			CmdLine.printErrorAndExit("The root directory is not exists: " + root);
		}
		if ( ! root.isDirectory() ) {
			CmdLine.printErrorAndExit("The pathname is not a directory: " + root);
		}
		
		MoexContractFileStorage moexStorage = new MoexContractFileStorage(root);
		if ( cmd.hasOption(CmdLine.LOPT_SHOW_SYMBOL_FILE) ) {
			Symbol s = new Symbol(cmd.getOptionValue(CmdLine.LOPT_SHOW_SYMBOL_FILE));
			File file = moexStorage.getDataFile(s);
			System.out.println(file);
			System.exit(0);
		}
		
		MoexFactory moexFactory = null;
		FidexpFactory finamFactory = null;
		if ( cmd.hasOption(CmdLine.LOPT_JBROWSER_CONFIG) ) {
			File jbdConfig = new File(cmd.getOptionValue(CmdLine.LOPT_JBROWSER_CONFIG));
			try {
				moexFactory = MoexFactorySTD.newFactoryJBD(jbdConfig, true);
				finamFactory = FidexpFactorySTD.newFactoryJBD(jbdConfig, true);
			} catch ( IOException e ) {
				CmdLine.printErrorAndExit("Cannot load JBrowserDriver/AHC configuration: " + jbdConfig + " " + e.getMessage());
			}
		} else {
			moexFactory = MoexFactorySTD.newFactoryJBD();
			finamFactory = FidexpFactorySTD.newFactoryJBD();
		}
		
		final CountDownLatch globalExit = new CountDownLatch(1);
		final Scheduler scheduler = new SchedulerLocal("SCHEDULER");
		Runtime.getRuntime().addShutdownHook(new Thread("SHUTDOWN") {
			@Override public void run() {
				logger.debug("Initiate shutdown.");
				globalExit.countDown();
			}
		});

		
		FinamWebTickDataTracker finamService = new FinamWebTickDataTracker(FidexpFileStorage.createStorage(root),
				globalExit, scheduler, cmd, moexFactory, finamFactory);
		MoexWebContractTracker moexService = new MoexWebContractTracker(globalExit, scheduler, moexStorage, moexFactory); 
		try {
			Instant firstRun = scheduler.getCurrentTime().plusSeconds(5);
			finamService.reschedule(firstRun);
			moexService.reschedule(firstRun);
			globalExit.await();
		} catch ( InterruptedException e ) {
			globalExit.countDown();
			Thread.currentThread().interrupt();
		} finally {
			scheduler.close();
			IOUtils.closeQuietly(finamService);
			IOUtils.closeQuietly(moexService);
		}
	}

}
