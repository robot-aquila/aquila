package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;
import org.joda.time.DateTime;

/**
 * Стандартный планировщик задач.
 * <p>
 * Реализация планировщика, основанная на {@link java.util.Timer}.
 */
public class SchedulerLocal implements Scheduler {
	private final Timer timer;
	/**
	 * Задача может быть удалена из пула двумя способами: в случае явного вызова
	 * метода {@link #cancel(Runnable)} или автоматически после завершения
	 * разовой задачи.
	 */ 
	private final SchedulerLocal_Pool pool;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param timer таймер
	 */
	SchedulerLocal(Timer timer, SchedulerLocal_Pool pool) {
		super();
		this.timer = timer;
		this.pool = pool;
	}

	/**
	 * Конструктор.
	 */
	public SchedulerLocal() {
		this(new Timer(true), new SchedulerLocal_Pool());
	}
	
	/**
	 * Получить планировщик.
	 * <p>
	 * @return планировщик
	 */
	Timer getTimer() {
		return timer;
	}
	
	/**
	 * Получить пул задач.
	 * <p>
	 * @return пул задач
	 */
	SchedulerLocal_Pool getPool() {
		return pool;
	}
	
	@Override
	public DateTime getCurrentTime() {
		return new DateTime();
	}
	
	/**
	 * Создать разовую задачу.
	 * <p>
	 * @param task задача
	 * @return задача в обертке
	 * @throws IllegalArgumentException если экземпляр задачи уже в пуле
	 */
	private SchedulerLocal_TimerTask makeRunOnce(Runnable task) {
		return pool.put(new SchedulerLocal_TimerTask(task, this));
	}
	
	/**
	 * Make a periodic task.
	 * <p>
	 * @param task - the task instance
	 * @return the task handler
	 */
	private SchedulerLocal_TimerTask makePeriodic(Runnable task) {
		return pool.put(new SchedulerLocal_TimerTask(task)); 	
	}
	
	/**
	 * Создать дескриптор задачи.
	 * <p>
	 * @param task задача
	 * @return дескриптор задачи
	 */
	private TaskHandler createHandler(Runnable task) {
		return new TaskHandlerImpl(task, this);
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, DateTime time) {
		timer.schedule(makeRunOnce(task), time.toDate());
		return createHandler(task);
	}

	@Override
	public synchronized
		TaskHandler schedule(Runnable task, DateTime firstTime, long period)
	{
		timer.schedule(makePeriodic(task), firstTime.toDate(), period);
		return createHandler(task);
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, long delay) {
		timer.schedule(makeRunOnce(task), delay);
		return createHandler(task);
	}

	@Override
	public synchronized TaskHandler
		schedule(Runnable task, long delay, long period)
	{
		timer.schedule(makePeriodic(task), delay, period);
		return createHandler(task);
	}

	@Override
	public synchronized TaskHandler
		scheduleAtFixedRate(Runnable task, DateTime firstTime, long period)
	{
		timer.scheduleAtFixedRate(makePeriodic(task), firstTime.toDate(), period);
		return createHandler(task);
	}

	@Override
	public synchronized TaskHandler
		scheduleAtFixedRate(Runnable task, long delay, long period)
	{
		timer.scheduleAtFixedRate(makePeriodic(task), delay, period);
		return createHandler(task);
	}

	@Override
	public synchronized void cancel(Runnable task) {
		if ( pool.exists(task) ) {
			TimerTask tt = pool.get(task);
			tt.cancel();
			pool.remove(task);
		}
	}

	@Override
	public synchronized boolean scheduled(Runnable task) {
		return pool.exists(task);
	}

	@Override
	public synchronized TaskHandler getTaskHandler(Runnable task) {
		if ( pool.exists(task) ) {
			return new TaskHandlerImpl(task, this);
		} else {
			return null;
		}
	}

}
