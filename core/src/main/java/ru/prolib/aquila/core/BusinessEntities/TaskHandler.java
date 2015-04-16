package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс дескриптора задачи.
 */
public interface TaskHandler {
	
	/**
	 * Отменить задачу.
	 */
	public void cancel();
	
	/**
	 * Проверить состояния задачи.
	 * <p>
	 * @return true - запланировано исполнение задачи, false - снята или
	 * завершена
	 */
	public boolean scheduled();
	
	/**
	 * Получить объект задачи.
	 * <p>
	 * @return задача
	 */
	public Runnable getTask();
	
	/**
	 * Получить экземпляр планировщика.
	 * <p>
	 * @return планировщик
	 */
	public Scheduler getScheduler();

}
