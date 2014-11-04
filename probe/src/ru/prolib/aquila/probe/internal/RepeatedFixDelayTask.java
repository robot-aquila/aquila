package ru.prolib.aquila.probe.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.probe.timeline.TLOutOfIntervalException;
import ru.prolib.aquila.probe.timeline.TLSTimeline;

/**
 * Периодическая задача с фиксированным интервалом между исполнениями.
 * <p>
 * Для задачи такого типа подразумевается, что каждое последующее исполнение
 * планируется на время, соответствующее времени окончания текущего исполнения
 * плюс указанный интервал исполнения. 
 */
public class RepeatedFixDelayTask implements SchedulerTask {
	private final TLSTimeline timeline;
	private final Scheduler scheduler;
	private final Runnable task;
	private final long delay;
	
	public RepeatedFixDelayTask(Scheduler scheduler, Runnable task,
			TLSTimeline timeline, long delay)
	{
		super();
		this.timeline = timeline;
		this.scheduler = scheduler;
		this.task = task;
		this.delay = delay;
	}

	@Override
	public void run() {
		if ( scheduler.scheduled(task) ) {
			task.run();
			try {
				timeline.schedule(timeline.getPOA().plus(delay), this);
			} catch ( TLOutOfIntervalException e ) {
				scheduler.cancel(task);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != RepeatedFixDelayTask.class ) {
			return false;
		}
		RepeatedFixDelayTask o = (RepeatedFixDelayTask) other;
		return new EqualsBuilder()
			.append(o.timeline, timeline)
			.append(o.delay, delay)
			.append(o.scheduler, scheduler)
			.append(o.task, task)
			.isEquals();
	}

}
