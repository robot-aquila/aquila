package ru.prolib.aquila.core.BusinessEntities;

import java.util.TimerTask;

/**
 * Обертка задачи.
 * <p>
 * Вспомогательный класс использующийся в работе класса {@link SchedulerLocal}.
 */
class SchedulerLocal_TimerTask extends TimerTask {
	private final Runnable task;
	private final Scheduler scheduler;
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для разовых задач. После выполнения
	 * разовой задачи необходимо вычистить задачу из пула планировщика.
	 * Соответствующий планировщик передается вторым аргументом. Планировщик
	 * может быть не указан. В этом случае, удаление задачи из пула возлагается
	 * на пользовательский код.
	 * <p>
	 * @param task объект задачи
	 * @param scheduler планировщик
	 */
	SchedulerLocal_TimerTask(Runnable task, Scheduler scheduler) {
		super();
		this.task = task;
		this.scheduler = scheduler;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для периодических (повторяющихся) задач
	 * или задач, удаление которых из пула планировщика выполняется
	 * специфическим образом.
	 * <p>
	 * @param task объект задачи
	 */
	SchedulerLocal_TimerTask(Runnable task) {
		this(task,  null);
	}

	@Override
	public void run() {
		task.run();
		if ( scheduler != null ) {
			scheduler.cancel(task);
		}
	}
	
	/**
	 * Процедура сравнения нужна исключительно для упрощения тестов. 
	 */
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ||
				other.getClass() != SchedulerLocal_TimerTask.class )
		{
			return false;
		}
		SchedulerLocal_TimerTask o = (SchedulerLocal_TimerTask) other;
		return o.scheduler == scheduler && o.task == task;
	}
	
	/**
	 * Получить объект задачи.
	 * <p>
	 * @return задача
	 */
	public Runnable getTask() {
		return task;
	}
	
	/**
	 * Получить экземпляр планировщика.
	 * <p>
	 * @return планировщик или null, если автоудаление не требуется
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

}
