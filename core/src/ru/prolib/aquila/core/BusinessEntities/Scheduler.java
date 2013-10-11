package ru.prolib.aquila.core.BusinessEntities;

import java.util.TimerTask;
import org.joda.time.DateTime;

/**
 * Интерфейс планировщика задач.
 */
public interface Scheduler {
	
	/**
	 * Получить текущее время.
	 * <p>
	 * @return текущее время
	 */
	public DateTime getCurrentTime();

	/**
	 * Schedules the specified task for execution at the specified time.
	 * <p> 
	 * @param task - task to be scheduled
	 * @param time - time at which task is to be executed
	 */
	public void schedule(TimerTask task, DateTime time);
	
	/**
	 * Schedules the specified task for repeated fixed-delay execution,
	 * beginning at the specified time.
	 * <p>
	 * @param task - task to be scheduled
	 * @param firstTime - First time at which task is to be executed
	 * @param period - time in milliseconds between successive task executions
	 */
	public void schedule(TimerTask task, DateTime firstTime, long period);

	/**
	 * Schedules the specified task for execution after the specified delay.
	 * <p>
	 * @param task - task to be scheduled
	 * @param delay - delay in milliseconds before task is to be executed
	 */
	public void schedule(TimerTask task, long delay);

	/**
	 * Schedules the specified task for repeated fixed-delay execution,
	 * beginning after the specified delay.
	 * <p>
	 * @param task - task to be scheduled
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param period - time in milliseconds between successive task executions
	 */
    public void schedule(TimerTask task, long delay, long period);

    /**
     * Schedules the specified task for repeated fixed-rate execution, beginning
     * at the specified time.
     * <p>
     * @param task - task to be scheduled
     * @param firstTime - First time at which task is to be executed
     * @param period - time in milliseconds between successive task executions
     */
    public void scheduleAtFixedRate(TimerTask task, DateTime firstTime,
    		long period);
    
    /**
     * Schedules the specified task for repeated fixed-rate execution, beginning
     * after the specified delay.
     * <p>
     * @param task - task to be scheduled
     * @param delay - delay in milliseconds before task is to be executed
     * @param period - time in milliseconds between successive task executions
     */
    public void scheduleAtFixedRate(TimerTask task, long delay, long period);
    
    /**
     * Отменить задачу.
     * <p>
     * Данный метод просто вызывает {@link TimerTask#cancel()} задачи. Метод
     * предназначен в отсновном для тестирования пользовательских классов.
     * <p>
     * @param task задача
     * @return true if this task is scheduled for one-time execution and has not
     * yet run, or this task is scheduled for repeated execution. Returns false
     * if the task was scheduled for one-time execution and has already run, or
     * if the task was never scheduled, or if the task was already cancelled.
     * (Loosely speaking, this method returns true if it prevents one or more
     * scheduled executions from taking place.)
     */
    public boolean cancel(TimerTask task);

}
