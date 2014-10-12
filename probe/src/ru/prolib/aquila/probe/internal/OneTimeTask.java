package ru.prolib.aquila.probe.internal;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;

/**
 * Разовая задача.
 * <p>
 * После исполнения, удаляет задачу из реестра планировщика.
 */
public class OneTimeTask implements SchedulerTask {
	private final Scheduler scheduler;
	private final Runnable task;
	
	public OneTimeTask(Scheduler scheduler, Runnable task) {
		super();
		this.scheduler = scheduler;
		this.task = task;
	}

	@Override
	public void run() {
		if ( scheduler.scheduled(task) ) {
			task.run();
			scheduler.cancel(task);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != OneTimeTask.class ) {
			return false;
		}
		OneTimeTask o = (OneTimeTask) other;
		return o.scheduler == scheduler
				&& o.task == task;
	}

}
