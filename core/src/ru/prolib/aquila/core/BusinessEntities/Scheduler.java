package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;
import java.util.TimerTask;

/**
 * Интерфейс планировщика задач.
 */
public interface Scheduler {
	
	/**
	 * Получить текущее время.
	 * <p>
	 * @return текущее время
	 */
	public Date getCurrentTime();

	/**
	 * Schedules the specified task for execution at the specified time.
	 * <p> 
	 * @param task - task to be scheduled
	 * @param time - time at which task is to be executed
	 */
	public void schedule(TimerTask task, Date time);
	
	/**
	 * Schedules the specified task for repeated fixed-delay execution,
	 * beginning at the specified time.
	 * <p>
	 * @param task - task to be scheduled
	 * @param firstTime - First time at which task is to be executed
	 * @param period - time in milliseconds between successive task executions
	 */
	public void schedule(TimerTask task, Date firstTime, long period);

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
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period);
    
    /**
     * Schedules the specified task for repeated fixed-rate execution, beginning
     * after the specified delay.
     * <p>
     * @param task - task to be scheduled
     * @param delay - delay in milliseconds before task is to be executed
     * @param period - time in milliseconds between successive task executions
     */
    public void scheduleAtFixedRate(TimerTask task, long delay, long period);

}
