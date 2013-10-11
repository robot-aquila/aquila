package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.joda.time.DateTime;

/**
 * Стандартный планировщик задач.
 */
public class SchedulerLocal implements Scheduler {
	private final Timer timer;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param timer таймер
	 */
	SchedulerLocal(Timer timer) {
		super();
		this.timer = timer;
	}
	
	Timer getTimer() {
		return timer;
	}
	
	public SchedulerLocal() {
		this(new Timer(true));
	}

	@Override
	public DateTime getCurrentTime() {
		return new DateTime();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == SchedulerLocal.class;
	}

	@Override
	public void schedule(TimerTask task, DateTime time) {
		timer.schedule(task, time.toDate());
	}

	@Override
	public void schedule(TimerTask task, DateTime firstTime, long period) {
		timer.schedule(task, firstTime.toDate(), period);
	}

	@Override
	public void schedule(TimerTask task, long delay) {
		timer.schedule(task, delay);
	}

	@Override
	public void schedule(TimerTask task, long delay, long period) {
		timer.schedule(task, delay, period);
	}

	@Override
	public void scheduleAtFixedRate(TimerTask task, DateTime firstTime,
			long period)
	{
		timer.scheduleAtFixedRate(task, firstTime.toDate(), period);
	}

	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		timer.scheduleAtFixedRate(task, delay, period);
	}

	@Override
	public boolean cancel(TimerTask task) {
		return task.cancel();
	}

}
