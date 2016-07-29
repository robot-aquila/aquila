package ru.prolib.aquila.utils.finexp.futures;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.data.storage.file.FileStorage;
import ru.prolib.aquila.data.storage.file.FileStorageImpl;
import ru.prolib.aquila.data.storage.file.FilesetInfo;

public class TickDataStorageService {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TickDataStorageService.class);
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
		final FilesetInfo filesetInfo = FilesetInfo.createInstance(".csv.gz", ".part.csv.gz");
		final FileStorage storage = new FileStorageImpl(root, filesetInfo);
		final CountDownLatch globalExit = new CountDownLatch(1);
		final Scheduler scheduler = new SchedulerLocal();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override public void run() {
				logger.debug("Initiate shutdown.");
				globalExit.countDown();
			}
		});
		scheduler.schedule(new UpdateLocalDatabaseTask(storage, globalExit, new SchedulerLocal(), cmd), 1000L);
		try {
			globalExit.await();
		} catch ( InterruptedException e ) {
			globalExit.countDown();
			Thread.currentThread().interrupt();
		}
	}
	


}
