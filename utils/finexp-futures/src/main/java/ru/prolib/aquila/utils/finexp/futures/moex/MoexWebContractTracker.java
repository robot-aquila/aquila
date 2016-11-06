package ru.prolib.aquila.utils.finexp.futures.moex;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.utils.LongTermTask;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.utils.finexp.futures.moex.components.MoexAllFuturesUpdateHandler;
import ru.prolib.aquila.utils.finexp.futures.moex.components.MoexContractTrackingSchedule;
import ru.prolib.aquila.utils.finexp.futures.moex.components.UpdateHandler;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.moex.Moex;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

public class MoexWebContractTracker implements Runnable, Closeable {
	private static final Logger logger;
	private static final String TASK_NAME = "MOEX-WEB-CONTRACT-TRACKER";
	
	static {
		logger = LoggerFactory.getLogger(MoexWebContractTracker.class);
	}
	
	private final CountDownLatch globalExit;
	private final MoexContractTrackingSchedule updateSchedule;
	private final Scheduler scheduler;
	private final MoexContractFileStorage storage;
	/**
	 * If the handler is defined then we're in the update tracking mode.
	 */
	private UpdateHandler updateHandler;

	public MoexWebContractTracker(CountDownLatch globalExit,
			Scheduler scheduler, MoexContractFileStorage storage,
			MoexContractTrackingSchedule updateSchedule)
	{
		this.globalExit = globalExit;
		this.updateSchedule = updateSchedule;
		this.scheduler = scheduler;
		this.storage = storage;
	}
	
	public MoexWebContractTracker(CountDownLatch globalExit,
			Scheduler scheduler, MoexContractFileStorage storage)
	{
		this(globalExit, scheduler, storage, new MoexContractTrackingSchedule());
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
				if ( updateSchedule.isMarketOpeningTrackingPeriod(currentTime) ) {
					updatePlannedTime = updateSchedule.withMarketOpeningTime(currentTime);
				} else if ( updateSchedule.isIntradayClearingTrackingPeriod(currentTime) ) {
					updatePlannedTime = updateSchedule.withIntradayClearingTime(currentTime);
				} else {
					updatePlannedTime = updateSchedule.withEveningClearingTime(currentTime);
				}
				updateHandler = createHandler(updatePlannedTime);
				logger.debug("Handler created. Update time: {}", updateSchedule.toZDT(updatePlannedTime));
			}
			boolean done = false;
			try {
				done = updateHandler.execute(); // it may take some time
			} catch ( WUWebPageException e ) {
				logger.warn("The MOEX site error. We'll try later.", e);
			} catch ( DataStorageException e ) {
				logErrorAndGlobalExit("The task stopped because of local storage error: ", e);
				return;
			}
			
			currentTime = scheduler.getCurrentTime();
			if ( done ) {
				closeHandler();
				reschedule(updateSchedule.getNextTrackingPeriodStart(currentTime));
				logger.debug("Rescheduled. Next tracking period at: {}",
						updateSchedule.toZDT(updateSchedule.getNextTrackingPeriodStart(currentTime)));
			} else {
				reschedule(updateSchedule.getNextUpdateTime(currentTime));
				logger.debug("Rescheduled. Next update time: {}",
						updateSchedule.toZDT(updateSchedule.getNextUpdateTime(currentTime)));
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
		globalExit.countDown();
	}
	
	public void reschedule(Instant at) {
		if ( globalExit.getCount() > 0 ) {
			scheduler.schedule(new LongTermTask(this, TASK_NAME), at);
		}
	}
	
	private UpdateHandler createHandler(Instant updatePlannedTime) {
		 return new MoexAllFuturesUpdateHandler(globalExit, new Moex(), storage, updatePlannedTime);
	}

}
