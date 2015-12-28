package ru.prolib.aquila.core.BusinessEntities;

import java.time.LocalDateTime;

/**
 * Интерфейс планировщика задач.
 * <p>
 * Дизайн планировщика {@link java.util.Timer} не плох, за исключением того,
 * что ограничивает единственной реализацией, расширение которой довольно
 * проблематично. Данный интерфейс повторяет дизайн стандартного планировщика
 * Java, позволяя реализовать произвольное количество имплементаций под
 * различные задачи.
 */
public interface Scheduler {
	
	/**
	 * Получить текущее время.
	 * <p>
	 * @return текущее время
	 */
	public LocalDateTime getCurrentTime();

	/**
	 * Schedules the specified task for execution at the specified time. If the
	 * time is in the past, the task is scheduled for immediate execution.
	 * <p> 
	 * @param task - task to be scheduled
	 * @param time - time at which task is to be executed
	 * @return a new task handler
	 * @throws NullPointerException if task or time is null
	 * @throws IllegalStateException if task already scheduled
	 */
	public TaskHandler schedule(Runnable task, LocalDateTime time);
	
	/**
	 * Schedules the specified task for repeated fixed-delay execution,
	 * beginning at the specified time. If the time is in the past, the task is
	 * scheduled for immediate execution.
	 * <p>
	 * @param task - task to be scheduled
	 * @param firstTime - First time at which task is to be executed
	 * @param period - time in milliseconds between successive task executions
	 * @return a new task handler
	 * @throws NullPointerException if task or time is null
	 * @throws IllegalStateException if task already scheduled
	 * @throws IllegalArgumentException if period &lt;= 0 
	 */
	public TaskHandler schedule(Runnable task, LocalDateTime firstTime, long period);

	/**
	 * Schedules the specified task for execution after the specified delay.
	 * <p>
	 * @param task - task to be scheduled
	 * @param delay - delay in milliseconds before task is to be executed
	 * @return a new task handler
	 * @throws NullPointerException if task is null
	 * @throws IllegalStateException if task already scheduled
	 * @throws IllegalArgumentException if delay &lt; 0
	 */
	public TaskHandler schedule(Runnable task, long delay);

	/**
	 * Schedules the specified task for repeated fixed-delay execution,
	 * beginning after the specified delay.
	 * <p>
	 * @param task - task to be scheduled
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param period - time in milliseconds between successive task executions
	 * @return a new task handler
	 * @throws NullPointerException if task is null
	 * @throws IllegalStateException if task already scheduled
	 * @throws IllegalArgumentException if delay &lt; 0 or period &lt;= 0
	 */
    public TaskHandler schedule(Runnable task, long delay, long period);

    /**
     * Schedules the specified task for repeated fixed-rate execution, beginning
     * at the specified time.  If the time is in the past, the task is scheduled
     * for immediate execution.
     * <p>
     * @param task - task to be scheduled
     * @param firstTime - First time at which task is to be executed
     * @param period - time in milliseconds between successive task executions
     * @return a new task handler
     * @throws NullPointerException if task or time is null
	 * @throws IllegalStateException if task already scheduled
	 * @throws IllegalArgumentException if period &lt;= 0
     */
    public TaskHandler
    	scheduleAtFixedRate(Runnable task, LocalDateTime firstTime, long period);
    
    /**
     * Schedules the specified task for repeated fixed-rate execution, beginning
     * after the specified delay.
     * <p>
     * @param task - task to be scheduled
     * @param delay - delay in milliseconds before task is to be executed
     * @param period - time in milliseconds between successive task executions
     * @return a new task handler
     * @throws NullPointerException if task is null
	 * @throws IllegalStateException if task already scheduled
	 * @throws IllegalArgumentException if delay &lt; 0 or period &lt;= 0
     */
    public TaskHandler
    	scheduleAtFixedRate(Runnable task, long delay, long period);
    
    /**
     * Отменить задачу.
     * <p>
     * @param task задача
     * @throws NullPointerException if task is null
     */
    public void cancel(Runnable task);
    
	/**
	 * Проверить состояние задачи.
	 * <p>
	 * @param task задача
	 * @return true - запланировано исполнение задачи, false - отменена,
	 * исполнена или нет такой задачи.
	 * @throws NullPointerException if task is null
	 */
    public boolean scheduled(Runnable task);
    
    /**
     * Получить дескриптор задачи.
     * <p>
     * @param task задача
     * @return дескриптор задачи. Дескриптор возвращается всегда, независимо от
     * наличия указанной задачи в реестре задач.
     * @throws NullPointerException if task is null
     */
    public TaskHandler getTaskHandler(Runnable task);

}
