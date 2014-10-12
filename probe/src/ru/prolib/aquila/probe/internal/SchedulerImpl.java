package ru.prolib.aquila.probe.internal;

import java.util.Hashtable;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.KW;
import ru.prolib.aquila.probe.timeline.TLOutOfIntervalException;
import ru.prolib.aquila.probe.timeline.TLSTimeline;

/**
 * Планировщик на базе хронологии {@link TLSTimeline}.
 * <p>
 * Методы {@link #scheduleAtFixedRate(Runnable, DateTime, long)} и
 * {@link #scheduleAtFixedRate(Runnable, long, long)} работают точно так же,
 * как и fixed-delay.
 */
public class SchedulerImpl implements Scheduler {
	private final Map<KW<Runnable>, SchedulerTask> tasks;
	private final TLSTimeline timeline;
	
	public SchedulerImpl(TLSTimeline timeline) {
		super();
		this.timeline = timeline;
		tasks = new Hashtable<KW<Runnable>, SchedulerTask>();
	}

	@Override
	public void cancel(Runnable task) {
		tasks.remove(new KW<Runnable>(task));
	}

	@Override
	public DateTime getCurrentTime() {
		return timeline.getPOA();
	}

	@Override
	public TaskHandler getTaskHandler(Runnable task) {
		return new TaskHandlerImpl(task, this);
	}

	@Override
	public TaskHandler schedule(Runnable task, DateTime time) {
		if ( time == null ) {
			throw new NullPointerException();
		}
		if ( scheduled(task) ) {
			throw new IllegalStateException();
		}
		Interval wp = timeline.getInterval();
		if ( wp.isBefore(time) ) {
			return getTaskHandler(task);
		}
		if ( wp.isAfter(time) ) {
			time = wp.getStart();
		}
		SchedulerTask ctrl = new OneTimeTask(this, task);
		try {
			timeline.schedule(time, ctrl);
		} catch ( TLOutOfIntervalException e ) {
			throw new RuntimeException("Unexpected exception", e);
		}
		TaskHandler h = getTaskHandler(task);
		tasks.put(new KW<Runnable>(task), ctrl);
		return h;
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay) {
		if ( delay < 0 ) {
			throw new IllegalArgumentException();
		}
		return schedule(task, timeline.getPOA().plus(delay));
	}

	@Override
	public TaskHandler schedule(Runnable task, DateTime firstTime, long period) {
		if ( firstTime == null ) {
			throw new NullPointerException();
		}
		if ( period <= 0 ) {
			throw new IllegalArgumentException();
		}
		if ( scheduled(task) ) {
			throw new IllegalStateException();
		}
		Interval wp = timeline.getInterval();
		if ( wp.isBefore(firstTime) ) {
			return getTaskHandler(task);
		}
		if ( wp.isAfter(firstTime) ) {
			firstTime = wp.getStart();
		}
		SchedulerTask ctrl =
				new RepeatedFixDelayTask(this, task, timeline, period - 1);
		try {
			timeline.schedule(firstTime, ctrl);
		} catch ( TLOutOfIntervalException e ) {
			throw new RuntimeException("Unexpected exception", e);
		}
		TaskHandler h = getTaskHandler(task);
		tasks.put(new KW<Runnable>(task), ctrl);
		return h;
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay, long period) {
		if ( delay < 0 ) {
			throw new IllegalArgumentException();
		}
		return schedule(task, timeline.getPOA().plus(delay), period);
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, DateTime firstTime,
			long period)
	{
		return schedule(task, firstTime, period);
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, long delay,
			long period)
	{
		if ( delay < 0) {
			throw new IllegalArgumentException();
		}
		return scheduleAtFixedRate(task, timeline.getPOA().plus(delay), period);
	}

	@Override
	public boolean scheduled(Runnable task) {
		if ( task == null ) {
			throw new NullPointerException();
		}
		return tasks.containsKey(new KW<Runnable>(task));
	}

}
