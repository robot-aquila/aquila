package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;

public class SchedulerTask implements TaskHandler, Lockable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SchedulerTask.class);
	}
	
	private final LID lid;
	private final Lock lock;
	private final long period;
	private final Runnable runnable;
	private SchedulerTaskState state;
	private Instant nextExecutionTime;
	
	public SchedulerTask(Runnable runnable, long period) {
		this.lid = LID.createInstance();
		this.lock = new ReentrantLock();
		this.runnable = runnable;
		this.period = period;
		this.state = SchedulerTaskState.PENDING;
	}
	
	public SchedulerTask(Runnable runnable) {
		this(runnable, 0);
	}
	
	/**
	 * Test that the task is periodic.
	 * <p>
	 * @return true if the task is periodic, false otherwise
	 */
	public boolean isPeriodic() {
		return period != 0;
	}
	
	/**
	 * Test that the task is scheduled for execution.
	 * <p>
	 * @return
	 */
	public boolean isScheduled() {
		return stateEqualsTo(SchedulerTaskState.SCHEDULED);
	}
	
	/**
	 * Get current state of the task.
	 * <p>
	 * @return the task state
	 */
	public SchedulerTaskState getState() {
		lock();
		try {
			return state;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Get period of the task.
	 * <p>
	 * @return period of the task or zero if the task is not periodic
	 */
	public long getPeriod() {
		return period;
	}
	
	/**
	 * Get runnable instance of the task.
	 * <p>
	 * @return the runnable
	 */
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
	
	/**
	 * Get next execution time.
	 * <p>
	 * @return - the next execution time calculated by calling one of
	 * {@link #scheduleForFirstExecution(Instant, long)} or
	 * {@link #scheduleForNextExecution(Instant)} methods. 
	 */
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
	
}
