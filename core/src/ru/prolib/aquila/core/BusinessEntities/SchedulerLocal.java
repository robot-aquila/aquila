package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

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
	public Date getCurrentTime() {
		return new Date();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == SchedulerLocal.class;
	}

	@Override
	public void schedule(TimerTask task, Date time) {
		timer.schedule(task, time);
	}

	@Override
	public void schedule(TimerTask task, Date firstTime, long period) {
		timer.schedule(task, firstTime, period);
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
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period)
	{
		timer.scheduleAtFixedRate(task, firstTime, period);
	}

	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		timer.scheduleAtFixedRate(task, delay, period);
	}

}
