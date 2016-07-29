package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.*;


/**
 * The common task scheduler.
 * <p>
 * Scheduler implementation based on {@link java.util.Timer}.
 */
public class SchedulerLocal implements Scheduler {
	private final Timer timer;
	
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
	}

	/**
	 * Конструктор.
	 */
	public SchedulerLocal() {
		this(new Timer(true));
	}
	
	public SchedulerLocal(String threadID) {
		this(new Timer(threadID, true));
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
	public TaskHandler schedule(Runnable task, Instant time) {
		SchedulerLocal_TimerTask timerTask = makeRunOnce(task);
		timer.schedule(timerTask, toDate(time));
		return timerTask;
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.schedule(timerTask, toDate(firstTime), period);
		return timerTask;
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay) {
		SchedulerLocal_TimerTask timerTask = makeRunOnce(task);
		timer.schedule(timerTask, delay);
		return timerTask;
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay, long period) {
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.schedule(timerTask, delay, period);
		return timerTask;
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, Instant firstTime,
			long period)
	{
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.scheduleAtFixedRate(timerTask, toDate(firstTime), period);
		return timerTask;
	}

	@Override
	public synchronized TaskHandler
		scheduleAtFixedRate(Runnable task, long delay, long period)
	{
		SchedulerLocal_TimerTask timerTask = makePeriodic(task);
		timer.scheduleAtFixedRate(timerTask, delay, period);
		return timerTask;
	}
	
	@Override
	public synchronized void close() {
		timer.cancel();
	}

}
