package ru.prolib.aquila.utils.experimental.experiment;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.utils.experimental.CmdLine;
import ru.prolib.aquila.utils.experimental.Experiment;
import ru.prolib.aquila.utils.experimental.experiment.moex.MoexAllFuturesUpdateHandler;
import ru.prolib.aquila.utils.experimental.experiment.moex.MoexContractTrackingSchedule;
import ru.prolib.aquila.utils.experimental.experiment.moex.UpdateHandler;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.moex.Moex;
import ru.prolib.aquila.web.utils.moex.MoexContractField;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

/**
 * Track the contract info updates at the MOEX site.
 */
public class MoexTrackContract implements Experiment, Runnable {
	private static final Set<Integer> EXPECTED_FIELDS_AFTER_MARKET_OPENS;
	private static final Set<Integer> EXPECTED_FIELDS_AFTER_CLEARING;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MoexTrackContract.class);
		EXPECTED_FIELDS_AFTER_MARKET_OPENS = new HashSet<>();
		EXPECTED_FIELDS_AFTER_CLEARING = new HashSet<>();
		// Do not add a tick value because it is constant for many contracts
		EXPECTED_FIELDS_AFTER_CLEARING.add(MoexContractField.LOWER_PRICE_LIMIT);
		EXPECTED_FIELDS_AFTER_CLEARING.add(MoexContractField.UPPER_PRICE_LIMIT);
		EXPECTED_FIELDS_AFTER_CLEARING.add(MoexContractField.SETTLEMENT_PRICE);
		EXPECTED_FIELDS_AFTER_CLEARING.add(MoexContractField.INITIAL_MARGIN);
	}
	
	private final CountDownLatch globalExit;
	private final Moex moex;
	private final MoexContractTrackingSchedule updateSchedule;
	private Scheduler scheduler;
	private MoexContractFileStorage storage;
	private int exitCode = 0;
	/**
	 * If the handler is defined then we're in the update tracking mode.
	 */
	private UpdateHandler updateHandler;
	
	public MoexTrackContract(CountDownLatch globalExit) {
		this.globalExit = globalExit;
		this.moex = new Moex();
		this.updateSchedule = new MoexContractTrackingSchedule();
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
		this.scheduler = scheduler;
		storage = new MoexContractFileStorage(new File(cmd.getOptionValue(CmdLine.LOPT_ROOT)));
		run();
		return 0;
	}
	
	@Override
	public void run() {
		// Лучше рассматривать задачу, отталкиваясь от того, в каком периоде
		// времени мы находимся. Возможны два варианта:
		// 1) Мы в периоде отслеживания обновлений. В этом случае необходимо
		// открыть хендлер, либо работать с ранее открытым хендлером до тех пор,
		// пока очередное обновление не будет записано. Если обновление
		// записано, то закрывает хендлер и решедулим на начало следующего
		// периода отслеживания. Если обновление не записано, то выполняем
		// решедулинг на некоторое время вперед.
		// 2) Мы за пределами периода отслеживания обновлений. Если есть
		// открытый хендлер, то его безусловно нужно закрыть. Дальше нужно
		// выполнить решедулинг на начало следующего периода отслеживания
		// обновлений.
		
		Instant currentTime = scheduler.getCurrentTime();
		if ( updateSchedule.isTrackingPeriod(currentTime) ) {
			logger.debug("Inside a tracking period.");
			if ( updateHandler == null ) {
				Instant updatePlannedTime = null;
				Set<Integer> expectedChangedTokens = null;
				if ( updateSchedule.isMarketOpeningTrackingPeriod(currentTime) ) {
					updatePlannedTime = updateSchedule.withMarketOpeningTime(currentTime);
					expectedChangedTokens = EXPECTED_FIELDS_AFTER_MARKET_OPENS;
				} else if ( updateSchedule.isIntradayClearingTrackingPeriod(currentTime) ) {
					updatePlannedTime = updateSchedule.withIntradayClearingTime(currentTime);
					expectedChangedTokens = EXPECTED_FIELDS_AFTER_CLEARING;
				} else {
					updatePlannedTime = updateSchedule.withEveningClearingTime(currentTime);
					expectedChangedTokens = EXPECTED_FIELDS_AFTER_CLEARING;
				}
				updateHandler = createHandler(updatePlannedTime, expectedChangedTokens);
				logger.debug("Handler created. Update time: {} Expected tokens: {}",
						updateSchedule.toZDT(updatePlannedTime), expectedChangedTokens);
			}
			try {
				boolean done = updateHandler.execute(); // it may take some time
				currentTime = scheduler.getCurrentTime();
				if ( done ) {
					closeHandler();
					reschedule(updateSchedule.getNextTrackingPeriodStart(currentTime));
					logger.debug("Rescheduled. Next tracking period at: {}",
							updateSchedule.toZDT(updateSchedule.getNextTrackingPeriodStart(currentTime)));
				} else {
					reschedule(updateSchedule.getNextUpdateTime(currentTime));
					logger.debug("Rescheduled. Update not available. Next update time: {}",
							updateSchedule.toZDT(updateSchedule.getNextUpdateTime(currentTime)));
				}
			} catch ( WUWebPageException e ) {
				logger.warn("The MOEX site error. We'll try later.", e);				
			} catch ( DataStorageException e ) {
				logErrorAndGlobalExit("The task stopped because of local storage error: ", e);
			}
			
		} else {
			logger.debug("Outside of a tracking period.");
			closeHandler();
			reschedule(updateSchedule.getNextTrackingPeriodStart(currentTime));
			logger.debug("Rescheduled. Next tracking period at: {}",
					updateSchedule.toZDT(updateSchedule.getNextTrackingPeriodStart(currentTime)));
		}
	}

	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(updateHandler);
		IOUtils.closeQuietly(moex);
	}
	
	private void closeHandler() {
		if ( updateHandler != null ) {
			IOUtils.closeQuietly(updateHandler);
			updateHandler = null;
			logger.debug("Handler closed.");
		}
	}
	
	private void logErrorAndGlobalExit(String msg, Throwable t) {
		logger.error(msg, t);
		exitCode = 1;
		globalExit.countDown();
	}
	
	private void reschedule(Instant at) {
		if ( globalExit.getCount() > 0 ) {
			scheduler.schedule(this, at);
		}
	}
	
	private UpdateHandler createHandler(Instant updatePlannedTime, Set<Integer> expectedChangedTokens) {
		 return new MoexAllFuturesUpdateHandler(globalExit, moex, storage,
				 updatePlannedTime, expectedChangedTokens);
	}

}
