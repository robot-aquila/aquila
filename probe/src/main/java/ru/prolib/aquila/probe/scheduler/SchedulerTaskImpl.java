package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.concurrency.LID;

public class SchedulerTaskImpl implements SchedulerTask {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SchedulerTaskImpl.class);
	}
	
	private final LID lid;
	private final Lock lock;
	private final long period;
	private final Runnable runnable;
	private SchedulerTaskState state;
	private Instant nextExecutionTime;
	
	public SchedulerTaskImpl(Runnable runnable, long period) {
		this.lid = LID.createInstance();
		this.lock = new ReentrantLock();
		this.runnable = runnable;
		this.period = period;
		this.state = SchedulerTaskState.PENDING;
	}
	
	public SchedulerTaskImpl(Runnable runnable) {
		this(runnable, 0);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.scheduler.SchedulerTask#isPeriodic()
	 */
	@Override
	public boolean isPeriodic() {
		return period != 0;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.scheduler.SchedulerTask#isScheduled()
	 */
	@Override
	public boolean isScheduled() {
		return stateEqualsTo(SchedulerTaskState.SCHEDULED);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.scheduler.SchedulerTask#getState()
	 */
	@Override
	public SchedulerTaskState getState() {
		lock();
		try {
			return state;
		} finally {
			unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.scheduler.SchedulerTask#getPeriod()
	 */
	@Override
	public long getPeriod() {
		return period;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.scheduler.SchedulerTask#getRunnable()
	 */
	@Override
	public Runnable getRunnable() {
		lock();
		try {
			return runnable;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Test that the task is in specified state.
	 * <p>
	 * @param state - expected task state
	 * @return true if the task in the expected state, false otherwise
	 */
	public boolean stateEqualsTo(SchedulerTaskState state) {
		return getState() == state;
	}
	
	/**
	 * Schedule the task for the first execution.
	 * <p>
	 * @param baseTime - the base time of the task execution
	 * @param delay - delay
	 * @return the time of the first execution
	 * @throws IllegalStateException - if the task in the wrong state
	 */
	public Instant scheduleForFirstExecution(Instant baseTime, long delay) {
		lock();
		try {
			if ( state != SchedulerTaskState.PENDING ) {
				throw new IllegalStateException("Unexpected task state: " + state);
			}
			state = SchedulerTaskState.SCHEDULED;
			return nextExecutionTime = baseTime.plusMillis(delay);
		} finally {
			unlock();
		}
	}
	
	public Instant scheduleForFirstExecution(Instant baseTime) {
		return scheduleForFirstExecution(baseTime, 0);
	}
	
	/**
	 * Schedule the task for the next execution.
	 * <p>
	 * This method is used to calculate the next time of execution of periodic tasks.
	 * <p>
	 * @param baseTime - the base time of the task execution
	 * @return the time of the next execution
	 * @throws IllegalStateException - the task in the wrong state or it is not
	 * a periodic task
	 */
	public Instant scheduleForNextExecution(Instant baseTime) {
		lock();
		try {
			if ( state != SchedulerTaskState.SCHEDULED ) {
				throw new IllegalStateException("Unexpected task state: " + state);
			}
			if ( ! isPeriodic() ) {
				throw new IllegalStateException("Not a periodic task");
			}
			return nextExecutionTime = baseTime.plusMillis(period);
		} finally {
			unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.scheduler.SchedulerTask#getNextExecutionTime()
	 */
	@Override
	public Instant getNextExecutionTime() {
		lock();
		try {
			return nextExecutionTime;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Execute the task.
	 * <p>
	 * @throws IllegalStateException - the task is in the wrong state
	 */
	public void execute() {
		lock();
		try {
			if ( state != SchedulerTaskState.SCHEDULED ) {
				throw new IllegalStateException("Unexpected task state: " + state);
			}
			try {
				runnable.run();
				if ( period == 0 ) {
					state = SchedulerTaskState.EXECUTED;
				}
			} catch ( Exception e ) {
				logger.error(runnable + " threw an exception: {}", e);
				state = SchedulerTaskState.ERROR;
			}
			
		} finally {
			unlock();
		}
	}
	
	@Override
	public String toString() {
		lock();
		try {
			return getClass().getSimpleName() + "[" +
				(state == SchedulerTaskState.SCHEDULED ? nextExecutionTime : state) +
				(isPeriodic() ? " P:" + period : "") +
				" " + runnable + "]";
		} finally {
			unlock();
		}
	}
	
	@Override
	public boolean cancel() {
		lock();
		try {
			if ( state == SchedulerTaskState.PENDING
			  || state == SchedulerTaskState.SCHEDULED )
			{
				state = SchedulerTaskState.CANCELLED;
				return true;
			} else {
				return false;
			}
		} finally {
			unlock();
		}
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SchedulerTaskImpl.class ) {
			return false;
		}
		SchedulerTaskImpl o = (SchedulerTaskImpl) other;
		return new EqualsBuilder()
			.append(nextExecutionTime, o.nextExecutionTime)
			.append(period, o.period)
			.append(runnable, o.runnable)
			.append(state, o.state)
			.isEquals();
	}
	
}
