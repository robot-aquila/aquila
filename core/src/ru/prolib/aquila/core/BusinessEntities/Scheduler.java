package ru.prolib.aquila.core.BusinessEntities;

import java.util.TimerTask;
import org.joda.time.DateTime;

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
	public DateTime getCurrentTime();

	/**
	 * Schedules the specified task for execution at the specified time.
	 * <p> 
	 * @param task - task to be scheduled
	 * @param time - time at which task is to be executed
	 */
	public TaskHandler schedule(Runnable task, DateTime time);
	
	/**
	 * Schedules the specified task for repeated fixed-delay execution,
	 * beginning at the specified time.
	 * <p>
	 * @param task - task to be scheduled
	 * @param firstTime - First time at which task is to be executed
	 * @param period - time in milliseconds between successive task executions
	 */
	public TaskHandler schedule(Runnable task, DateTime firstTime, long period);

	/**
	 * Schedules the specified task for execution after the specified delay.
	 * <p>
	 * @param task - task to be scheduled
	 * @param delay - delay in milliseconds before task is to be executed
	 */
	public TaskHandler schedule(Runnable task, long delay);

	/**
	 * Schedules the specified task for repeated fixed-delay execution,
	 * beginning after the specified delay.
	 * <p>
	 * @param task - task to be scheduled
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param period - time in milliseconds between successive task executions
	 */
    public TaskHandler schedule(Runnable task, long delay, long period);

    /**
     * Schedules the specified task for repeated fixed-rate execution, beginning
     * at the specified time.
     * <p>
     * @param task - task to be scheduled
     * @param firstTime - First time at which task is to be executed
     * @param period - time in milliseconds between successive task executions
     */
    public TaskHandler
    	scheduleAtFixedRate(Runnable task, DateTime firstTime, long period);
    
    /**
     * Schedules the specified task for repeated fixed-rate execution, beginning
     * after the specified delay.
     * <p>
     * @param task - task to be scheduled
     * @param delay - delay in milliseconds before task is to be executed
     * @param period - time in milliseconds between successive task executions
     */
    public TaskHandler
    	scheduleAtFixedRate(Runnable task, long delay, long period);
    
    /**
     * Отменить задачу.
     * <p>
     * Данный метод просто вызывает {@link TimerTask#cancel()} задачи. Метод
     * предназначен в отсновном для тестирования пользовательских классов.
     * <p>
     * @param task задача
     */
    public void cancel(Runnable task);
    
	/**
	 * Проверить состояния задачи.
	 * <p>
	 * @param task задача
	 * @return true - запланировано исполнение задачи, false - отменена,
	 * исполнена или нет такой задачи
	 */
    public boolean scheduled(Runnable task);
    
    /**
     * Получить дескриптор задачи.
     * <p>
     * @param task задача
     * @return дескриптор задачи или null, если указанная задача отменена,
     * исполнена или нет такой задачи 
     */
    public TaskHandler getTaskHandler(Runnable task);

}
