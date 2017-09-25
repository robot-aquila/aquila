package ru.prolib.aquila.core.BusinessEntities;

import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper of a scheduler task.
 */
class SchedulerLocal_TimerTask extends TimerTask implements TaskHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SchedulerLocal_TimerTask.class);
	}
	
	private final boolean runOnce;
	private Runnable runnable;
	
	SchedulerLocal_TimerTask(Runnable runnable, boolean runOnce) {
		super();
		this.runOnce = runOnce;
		this.runnable = runnable;
	}

	@Override
	public void run() {
		Runnable dummy = null;
		synchronized ( this ) {
			dummy = runnable;
		}
		if ( dummy != null ) {
			try {
				dummy.run();
			} catch ( Exception e ) {
				logger.error("Unhandled exception: ", e);
			}
			synchronized ( this ) {
				if ( runOnce ) {
					runnable = null;
				}
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
		return new EqualsBuilder()
			.append(other.runOnce, runOnce)
			.append(other.runnable, runnable)
			.isEquals();
	}

}
