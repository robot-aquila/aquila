package ru.prolib.aquila.core.BusinessEntities;

import java.util.Hashtable;
import java.util.Map;
import ru.prolib.aquila.core.utils.KW;

/**
 * Пул задач.
 * <p>
 * Вспомогательный класс использующийся в работе класса {@link SchedulerLocal}.
 * Пул задач служит для сопоставления экземпляров задач с оберткой-реализацией
 * задачи под {@link java.util.Timer}.
 */
class SchedulerLocal_Pool {
	private final Map<KW<Runnable>, SchedulerLocal_TimerTask> pool;
	
	/**
	 * Конструктор.
	 */
	SchedulerLocal_Pool() {
		super();
		pool = new Hashtable<KW<Runnable>, SchedulerLocal_TimerTask>();
	}
	
	/**
	 * Проверить наличие задачи в пулею
	 * <p>
	 * @param task задача
	 * @return true - задача в пуле, false - задача не из пула
	 */
	public boolean exists(Runnable task) {
		return pool.containsKey(new KW<Runnable>(task));
	}

	/**
	 * Добавить задачу в пул.
	 * <p>
	 * @param task обертка задачи
	 * @return полученный аргумент 
	 * @throws IllegalArgumentException соответствующая задача уже в пуле
	 */
	public SchedulerLocal_TimerTask put(SchedulerLocal_TimerTask task) {
		if ( exists(task.getTask()) ) {
			throw new IllegalArgumentException("The task already exists");
		}
		pool.put(new KW<Runnable>(task.getTask()), task);
		return task;
	}
	
	/**
	 * Удалить задачу из пула.
	 * <p>
	 * Задачи не принадлежащие пулу игнорируются.
	 * <p>
	 * @param task задача
	 */
	public void remove(Runnable task) {
		pool.remove(new KW<Runnable>(task));
	}
	
	/**
	 * Получить обертку задачи таймера.
	 * <p>
	 * @param task задача
	 * @return обертка задачи или null, если задача не принадлежит пулу
	 */
	public SchedulerLocal_TimerTask get(Runnable task) {
		return pool.get(new KW<Runnable>(task));
	}

}
