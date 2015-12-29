package ru.prolib.aquila.probe.internal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.KW;
import ru.prolib.aquila.probe.timeline.*;

/**
 * Планировщик на базе хронологии {@link TLSTimeline}.
 * <p>
 * Методы {@link #scheduleAtFixedRate(Runnable, DateTime, long)} и
 * {@link #scheduleAtFixedRate(Runnable, long, long)} работают точно так же,
 * как и fixed-delay.
 */
public class SchedulerImpl implements Scheduler {
	private final Map<KW<Runnable>, SchedulerTask> tasks;
	private final Timeline timeline;
	
	public SchedulerImpl(Timeline timeline) {
		super();
		this.timeline = timeline;
		tasks = new Hashtable<KW<Runnable>, SchedulerTask>();
	}

	@Override
	public void cancel(Runnable task) {
		tasks.remove(new KW<Runnable>(task));
	}

	@Override
	public LocalDateTime getCurrentTime() {
		return timeline.getPOA();
	}

	@Override
	public TaskHandler getTaskHandler(Runnable task) {
		return new TaskHandlerImpl(task, this);
	}

	@Override
	public TaskHandler schedule(Runnable task, LocalDateTime time) {
		if ( time == null ) {
			throw new NullPointerException();
		}
		if ( scheduled(task) ) {
			throw new IllegalStateException();
		}
		Instant instant = time.toInstant(ZoneOffset.UTC);
		Interval wp = timeline.getRunInterval();
		if ( wp.getEnd().isBefore(instant) ) {
			return getTaskHandler(task);
		}
		if ( wp.getStart().isAfter(instant) ) {
			time = LocalDateTime.ofInstant(wp.getStart(), ZoneOffset.UTC);
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
		return schedule(task, timeline.getPOA().plus(delay, ChronoUnit.MILLIS));
	}

	@Override
	public TaskHandler schedule(Runnable task, LocalDateTime firstTime, long period) {
		if ( firstTime == null ) {
			throw new NullPointerException();
		}
		if ( period <= 0 ) {
			throw new IllegalArgumentException();
		}
		if ( scheduled(task) ) {
			throw new IllegalStateException();
		}
		Instant instant = firstTime.toInstant(ZoneOffset.UTC);
		Interval wp = timeline.getRunInterval();
		if ( wp.getEnd().isBefore(instant) ) {
			return getTaskHandler(task);
		}
		if ( wp.getStart().isAfter(instant) ) {
			firstTime = LocalDateTime.ofInstant(wp.getStart(), ZoneOffset.UTC);
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
		return schedule(task, timeline.getPOA().plus(delay, ChronoUnit.MILLIS), period);
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, LocalDateTime firstTime,
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
		return scheduleAtFixedRate(task, timeline.getPOA().plus(delay, ChronoUnit.MILLIS), period);
	}

	@Override
	public boolean scheduled(Runnable task) {
		if ( task == null ) {
			throw new NullPointerException();
		}
		return tasks.containsKey(new KW<Runnable>(task));
	}

}
