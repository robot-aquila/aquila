package ru.prolib.aquila.core.BusinessEntities;

import java.util.TimerTask;

/**
 * Wrapper of a scheduler task.
 */
class SchedulerLocal_TimerTask extends TimerTask implements TaskHandler {
	private final boolean runOnce;
	private Runnable runnable;
	
	SchedulerLocal_TimerTask(Runnable runnable, boolean runOnce) {
		super();
		this.runOnce = runOnce;
		this.runnable = runnable;
	}

	@Override
	public synchronized void run() {
		if ( runnable != null ) {
			runnable.run();
			if ( runOnce ) {
				runnable = null;
			}
		}
	}
	
	@Override
	public synchronized boolean cancel() {
		runnable = null;
		return super.cancel();
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == this ) {
			return true;
		}
		if ( o == null || o.getClass() != SchedulerLocal_TimerTask.class ) {
			return false;
		}
		SchedulerLocal_TimerTask other = (SchedulerLocal_TimerTask) o;
		return other.runOnce == runOnce
			&& other.runnable == runnable;
	}

}
