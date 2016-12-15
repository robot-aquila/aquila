package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class SPRunnableTaskHandler implements Runnable, TaskHandler {
	private final Scheduler scheduler;
	private final SPRunnable runnable;
	private boolean cancelled = false;
	
	public static SPRunnableTaskHandler schedule(Scheduler scheduler, SPRunnable runnable) {
		SPRunnableTaskHandler handler = new SPRunnableTaskHandler(scheduler, runnable);
		handler.reschedule();
		return handler;
	}
	
	public SPRunnableTaskHandler(Scheduler scheduler, SPRunnable runnable) {
		this.scheduler = scheduler;
		this.runnable = runnable;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public SPRunnable getRunnable() {
		return runnable;
	}

	@Override
	public void run() {
		if ( ! cancelled ) {
			if ( runnable.isLongTermTask() ) {
				new Thread("LONG-TERM[" + toString() + "]") {
					@Override
					public void run() {
						runnable.run();
						reschedule();
					}
				}.start();
			} else {
				runnable.run();
				reschedule();				
			}
		}
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public boolean cancel() {
		if ( cancelled ) {
			return false;
		} else {
			cancelled = true;
			return true;
		}
	}
	
	public void reschedule() {
		if ( cancelled ) {
			return;
		}
		Instant next = runnable.getNextExecutionTime(scheduler.getCurrentTime());
		if ( next == null ) {
			cancelled = true;
		} else {
			scheduler.schedule(this, next);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SPRunnableTaskHandler.class ) {
			return false;
		}
		SPRunnableTaskHandler o = (SPRunnableTaskHandler) other;
		return new EqualsBuilder()
			.append(o.scheduler, scheduler)
			.append(o.runnable, runnable)
			.append(o.cancelled, cancelled)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + runnable + "]";
	}

}
