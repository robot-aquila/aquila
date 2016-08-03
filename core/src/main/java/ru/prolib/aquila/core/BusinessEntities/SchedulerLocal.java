package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The common task scheduler.
 * <p>
 * Scheduler implementation based on {@link java.util.Timer}.
 */
public class SchedulerLocal implements Scheduler {
	private static final Logger logger;
	private static final SchedulerLocal_TimerTask NULL_TASK;
	
	static {
		logger = LoggerFactory.getLogger(SchedulerLocal.class);
		NULL_TASK = new SchedulerLocal_TimerTask(null, true);
	}
	
	private final Timer timer;
	private final String timerID;
	private boolean closed;
	private boolean errorLogged;
	
	/**
	 * Constructor.
	 * <p>
	 * For testing purposes only.
	 * <p>
	 * @param timer - the timer
	 */
	SchedulerLocal(Timer timer) {
		super();
		this.timer = timer;
		this.timerID = "DEFAULT";
	}

	/**
	 * Конструктор.
	 */
	public SchedulerLocal() {
		this(new Timer(true));
	}
	
	public SchedulerLocal(String threadID) {
		this.timer = new Timer(threadID, true);
		this.timerID = threadID;
	}
	
	/**
	 * Get timer.
	 * <p>
	 * For testing purposes only.
	 * <p>
	 * @return timer
	 */
	Timer getTimer() {
		return timer;
	}
	
	@Override
	public Instant getCurrentTime() {
		return Instant.now();
	}
	
	/**
	 * Создать разовую задачу.
	 * <p>
	 * @param task задача
	 * @return задача в обертке
	 */
	private SchedulerLocal_TimerTask makeRunOnce(Runnable task) {
		return new SchedulerLocal_TimerTask(task, true);
	}
	
	/**
	 * Make a periodic task.
	 * <p>
	 * @param task - the task instance
	 * @return the task handler
	 */
	private SchedulerLocal_TimerTask makePeriodic(Runnable task) {
		return new SchedulerLocal_TimerTask(task, false); 	
	}
	
	private Date toDate(Instant time) {
		return Date.from(time);
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, Instant time) {
		if ( closed ) {
			return nullTask();
		}
		SchedulerLocal_TimerTask timerTask = makeRunOnce(task);
		timer.schedule(timerTask, toDate(time));
		return timerTask;
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, Instant firstTime, long period) {
		if ( closed ) {
			return nullTask();
		}
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.schedule(timerTask, toDate(firstTime), period);
		return timerTask;
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, long delay) {
		if ( closed ) {
			return nullTask();
		}
		SchedulerLocal_TimerTask timerTask = makeRunOnce(task);
		timer.schedule(timerTask, delay);
		return timerTask;
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, long delay, long period) {
		if ( closed ) {
			return nullTask();
		}
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.schedule(timerTask, delay, period);
		return timerTask;
	}

	@Override
	public synchronized TaskHandler scheduleAtFixedRate(Runnable task,
			Instant firstTime, long period)
	{
		if ( closed ) {
			return nullTask();
		}
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.scheduleAtFixedRate(timerTask, toDate(firstTime), period);
		return timerTask;
	}

	@Override
	public synchronized TaskHandler
		scheduleAtFixedRate(Runnable task, long delay, long period)
	{
		if ( closed ) {
			return nullTask();
		}
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.scheduleAtFixedRate(timerTask, delay, period);
		return timerTask;
	}
	
	@Override
	public synchronized void close() {
		if ( ! closed ) {
			timer.cancel();
			closed = true;
			logger.debug("{}: scheduler closed.", timerID);
		}
	}
	
	private TaskHandler nullTask() {
		if ( errorLogged ) {
			return NULL_TASK;
		}
		errorLogged = true;
		IllegalStateException e = new IllegalStateException(timerID +
			": Scheduler is closed. Next requests will be silently discarded.");
		logger.error("Illegal state: ", e);
		throw e;
	}

}
