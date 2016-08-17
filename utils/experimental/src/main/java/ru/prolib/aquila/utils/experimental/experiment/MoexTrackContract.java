package ru.prolib.aquila.utils.experimental.experiment;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;
import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DeltaUpdate;
import ru.prolib.aquila.data.storage.DeltaUpdateWriter;
import ru.prolib.aquila.utils.experimental.CmdLine;
import ru.prolib.aquila.utils.experimental.Experiment;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.moex.Moex;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

/**
 * Track the contract info updates at the MOEX site.
 */
public class MoexTrackContract implements Experiment, Runnable {
	private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MoexTrackContract.class);
	}
	
	private final CountDownLatch globalExit;
	private Scheduler scheduler;
	private File root;
	private MoexContractFileStorage storage;
	private Symbol symbol;
	private UpdatableStateContainer container;
	private int exitCode = 0;
	
	public MoexTrackContract(CountDownLatch globalExit) {
		this.globalExit = globalExit;
	}
	
	@Override
	public int getExitCode() {
		return exitCode;
	}

	@Override
	public int run(Scheduler scheduler, CommandLine cmd) {
		if ( ! CmdLine.testRootDirectory(cmd) ) {
			return 1;
		}
		if ( ! cmd.hasOption(CmdLine.LOPT_SYMBOL) ) {
			CmdLine.printError("Symbol must be specified.");
			return 1;
		}
		this.scheduler = scheduler;
		root = new File(cmd.getOptionValue(CmdLine.LOPT_ROOT));
		storage = new MoexContractFileStorage(root);
		symbol = new Symbol(cmd.getOptionValue(CmdLine.LOPT_SYMBOL));
		container = new UpdatableStateContainerImpl("CONTRACT-" + symbol);
		run();
		return 0;
	}
	
	@Override
	public void run() {
		boolean isSnapshot = true;
		try ( CloseableIterator<DeltaUpdate> reader = storage.createReader(symbol) ) {
			while ( reader.next() ) {
				container.update(reader.item().getContents());
				isSnapshot = false;
			}
		} catch ( IOException e ) {
			logErrorAndGlobalExit("IO error: ", e);
			return;
		}
		
		try ( Moex moex = new Moex() ) {
			container.update(moex.getContractDetails(symbol));
			if ( container.hasChanged() ) {
				DeltaUpdate update = new DeltaUpdate(scheduler.getCurrentTime(),
						isSnapshot, container.getUpdatedContent());
				try ( DeltaUpdateWriter writer = storage.createWriter(symbol) ) {
					writer.writeUpdate(update);
					logger.debug("Update written: {}", update);
				} catch ( DataStorageException e ) {
					logErrorAndGlobalExit("Data storage error: ", e);
					return;
				} catch ( DataFormatException e ) {
					logErrorAndGlobalExit("Data format error: ", e);
					return;
				}			
			}
			
		} catch ( WUWebPageException e ) {
			logger.warn("Something is wrong with the web-interface. We'll try later.  ", e);
			return;
		} catch ( IOException e ) {
			logErrorAndGlobalExit("IO error: ", e);
			return;
		}
		
		Instant nextUpdateTime = getNextUpdateTime();
		logger.debug("The next update scheduled: {}",
				LocalDateTime.ofInstant(nextUpdateTime, ZoneId.systemDefault()));
		scheduler.schedule(this, nextUpdateTime);
	}
	
	private void logErrorAndGlobalExit(String msg, Throwable t) {
		logger.error(msg, t);
		exitCode = 1;
		globalExit.countDown();
	}
	
	private Instant getNextUpdateTime() {
		ZonedDateTime current = scheduler.getCurrentTime().atZone(ZONE), next = null;
		LocalTime time = current.toLocalTime();
		if ( time.compareTo(LocalTime.of(19, 30)) >= 0 ) {
			next = current.plusDays(1).withHour(10).withMinute(0);
			logger.debug("> 19:30 - wait for session open at {}", next);
			return next.toInstant();
		} else if ( time.compareTo(LocalTime.of(19, 0)) >= 0 ) {
			next = current.plusMinutes(1);
			logger.debug("19:00-19:30 - check for updates every minute, next at {}", next);
		} else if ( time.compareTo(LocalTime.of(14, 30)) >= 0 ) {
			next = current.withHour(19).withMinute(0);
			logger.debug("14:30-19:00 - wait for evening session end at {}", next);
		} else if ( time.compareTo(LocalTime.of(14, 0)) >= 0 ) {
			next = current.plusMinutes(1);
			logger.debug("14:00-14:30 - check for updates every minute, next at {}", next);
		} else if ( time.compareTo(LocalTime.of(10, 30)) >= 0 ) {
			next = current.withHour(14).withMinute(0);
			logger.debug("10:30-14:00 - wait for intraday clearing at {}", next);
		} else if ( time.compareTo(LocalTime.of(10, 0)) >= 0 ) {
			next = current.plusMinutes(1);
			logger.debug("10:00-10:30 - check for updates every minute, next at {}", next);
		} else if ( time.compareTo(LocalTime.of(10, 0)) < 0 ) {
			next = current.withHour(10).withMinute(0);
			logger.debug("< 10:00 - wait for session open at {}", next);
		} else {
			next = current.plusHours(1);
			logger.error("Unknown case at: {}", current);
		}
		return next.toInstant();
	}

}
