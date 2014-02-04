package ru.prolib.aquila.core.BusinessEntities;

/**
 * Реализация типового дескриптора задачи.
 * <p>
 * Фактически служит для инкапсуляции связки задачи и планировщика с
 * предоставлением доступа к этим экземплярам. Остальные методы просто
 * делегирует планировщику. Данной реализации должно быть достаточно как для
 * пользователей, так и для большинства реализаций планировщика. 
 */
public class TaskHandlerImpl implements TaskHandler {
	private final Runnable task;
	private final Scheduler scheduler;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param task задача
	 * @param scheduler планировщик
	 */
	public TaskHandlerImpl(Runnable task, Scheduler scheduler) {
		super();
		this.task = task;
		this.scheduler = scheduler;
	}

	@Override
	public void cancel() {
		scheduler.cancel(task);
	}

	@Override
	public boolean scheduled() {
		return scheduler.scheduled(task);
	}

	@Override
	public Runnable getTask() {
		return task;
	}

	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TaskHandlerImpl.class ) {
			return false;
		}
		TaskHandlerImpl o = (TaskHandlerImpl) other;
		return o.scheduler == scheduler && o.task == task;
	}

}
