package ru.prolib.aquila.probe;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;

/**
 * Scheduler with control over execution.
 * <p>
 * Modes:
 * WAIT - do nothing (wait for mode change). This is default mode.
 * PULL_AND_RUN - pull a task and run it immediately
 */
@Deprecated
public class PROBEScheduler extends Observable implements Scheduler {	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PROBEScheduler.class);
	}
	
	public enum Mode {
		/**
		 * Waiting for mode change. Adding new tasks has no effect.
		 */
		WAIT,
		
		/**
		 * Close scheduler.
		 */
		CLOSE,
		
		/**
		 * Waiting for new tasks or mode change.
		 * When a new task came then execute it immediately.
		 */
		PULL_AND_RUN
	}
	
	/**
	 * Scheduler state constants.
	 */
	public enum State {
		/**
		 * Waiting for mode change.
		 */
		WAIT_FOR_MODE,

		/**
		 * Waiting for new tasks or mode change.
		 */
		WAIT_FOR_TASK,
		
		/**
		 * Executing stack of tasks.
		 */
		EXECUTING,
		
		/**
		 * Simulating real-time delay between two time points.
		 */
		DELAY,
		
		/**
		 * Scheduler closed.
		 */
		CLOSED,
	}
	
	public enum TaskState {
		VIRGIN,
		ERROR,
		SCHEDULED,
		EXECUTED,
		CANCELLED
	}
	
	static class Task implements TaskHandler {
		private final Lock lock;
		private final long period;
		private Runnable runnable;
		private TaskState state = TaskState.VIRGIN;
		private long nextExecutionTime;
		
		public Task(Runnable runnable, long period) {
			super();
			this.lock = new ReentrantLock();
			this.period = period;
			this.runnable = runnable;
		}
		
		public Task(Runnable runnable) {
			this(runnable, 0);
		}
		
		public void lock() {
			lock.lock();
		}
		
		public void unlock() {
			lock.unlock();
		}
		
		public void execute() {
			lock.lock();
			try {
				if ( state == TaskState.SCHEDULED ) {
					try {
						runnable.run();
						if ( period == 0 ) {
							state = TaskState.EXECUTED;
							runnable = null;
						}
					} catch ( Exception e ) {
						// TODO: make the message more informative
						logger.error("Unhandled exception (Task ID: {}): {}", runnable, e);
						state = TaskState.ERROR;
						runnable = null;
					}
				}
			} finally {
				lock.unlock();
			}
		}
		
		public boolean isRepeating() {
			return period != 0;
		}
		
		public TaskState getState() {
			lock.lock();
			try {
				return state;
			} finally {
				lock.unlock();
			}
		}
		
		public boolean isState(TaskState state) {
			return getState() == state;
		}
		
		public long scheduleForFirstExecution(long currentTime, long delay) {
			lock.lock();
			try {
				if ( state == TaskState.VIRGIN ) {
					nextExecutionTime = currentTime + delay;
					state = TaskState.SCHEDULED;
				}
				return nextExecutionTime;
			} finally {
				lock.unlock();
			}
		}
		
		public long scheduleForNextExecution(long currentTime) {
			lock.lock();
			try {
				if ( state == TaskState.SCHEDULED && period != 0 ) {
					nextExecutionTime = currentTime + period;
				}
				return nextExecutionTime;
			} finally {
				lock.unlock();
			}
		}
		
		public long getNextExecutionTime() {
			lock.lock();
			try {
				return nextExecutionTime;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean cancel() {
			lock.lock();
			try {
				if ( state == TaskState.SCHEDULED ) {
					state = TaskState.CANCELLED;
					runnable = null;
					return true;
				} else {
					return false;
				}
			} finally {
				lock.unlock();
			}
		}
		
	}
	
	static class TaskStack {
		private final long time;
		private final ArrayList<Task> tasks = new ArrayList<Task>();
		
		TaskStack(long time, Task task) {
			super();
			this.time = time;
			this.tasks.add(task);
		}
		
		public long getTime() {
			return time;
		}
		
		public void addTask(Task task) {
			tasks.add(task);
		}
		
		public List<Task> getTasks() {
			return tasks;
		}
		
		public void clearTasks() {
			tasks.clear();
		}
		
		public void cancelTasks() {
			for ( Task task : tasks ) {
				task.cancel();
			}
		}
		
	}
	
	static class TaskStackQueue {
		private final Lock lock = new ReentrantLock();
		private final Map<Long, TaskStack> stacks = new Hashtable<Long, TaskStack>();
		
		public void addTask(Task task) {
			lock.lock();
			task.lock();
			try {
				long time = task.getNextExecutionTime();
				TaskStack stack = stacks.get(time);
				if ( stack == null ) {
					stack = new TaskStack(time, task);
					stacks.put(time, stack);
				} else {
					stack.addTask(task);
				}
			} finally {
				task.unlock();
				lock.unlock();
			}
		}
		
		public void lock() {
			lock.lock();
		}
		
		public void unlock() {
			lock.unlock();
		}
		
		public int getSize() {
			lock.lock();
			try {
				return stacks.size();
			} finally {
				lock.unlock();
			}
		}
		
		public void close() {
			lock.lock();
			try {
				for ( TaskStack stack : stacks.values() ) {
					stack.cancelTasks();
					stack.clearTasks();
				}
				stacks.clear();
			} finally {
				lock.unlock();
			}
		}
		
		/**
		 * Get timestamp of lower stack.
		 * <p>
		 * @return timestamp of lower stack on the timeline or null if no stack
		 * available
		 */
		public Long getTimeOfNextStack() {
			lock.lock();
			try {
				Long t = null;
				for ( Long x : stacks.keySet() ) {
					if ( t == null || x < t ) {
						t = x;
					}
				}
				return t;
			} finally {
				lock.unlock();
			}
		}
		
		public TaskStack popNextStack() {
			lock.lock();
			try {
				Long t = getTimeOfNextStack();
				if ( t == null ) {
					return null;
				}
				return stacks.remove(t);
			} finally {
				lock.unlock();
			}
		}
		
	}
	
	private final Object lock = new Object();
	private final TaskStackQueue queue = new TaskStackQueue();
	private long currentTimestamp = 0;
	private Mode mode = Mode.WAIT;
	private State state = State.WAIT_FOR_MODE;
	private boolean withRealtimeDelays = false;
	
	public PROBEScheduler() {
		super();
		Thread thread = new Thread() {
			@Override public void run() {
				mainLoop();
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public Instant getCurrentTime() {
		synchronized ( lock ) {
			return Instant.ofEpochMilli(currentTimestamp);
		}
	}
	
	public long getCurrentTimestamp() {
		synchronized ( lock ) {
			return currentTimestamp;
		}
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant time) {
		return schedule(task, time, 0);
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
		synchronized ( lock ) {
			long delay = firstTime.toEpochMilli() - currentTimestamp;
			if ( delay < 0 ) {
				throw new IllegalArgumentException("Task " + task
					+ " in the past " + firstTime
					+ ". Current time is " + getCurrentTime());
			}
			return schedule(task, delay, period);
		}
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay) {
		return schedule(task, delay, 0);
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay, long period) {
		synchronized ( lock ) {
			if ( mode == Mode.CLOSE ) {
				throw new IllegalStateException("Object is closed");
			}
			if ( delay < 0 ) {
				throw new IllegalArgumentException("Task " + task
					+ " delay must be positive but: " + delay);
			}
			if ( period < 0 ) {
				throw new IllegalArgumentException("Task " + task
					+ " period must be positive but: " + period);
			}
			Task x = new Task(task, period);
			x.scheduleForFirstExecution(currentTimestamp, delay);
			queue.addTask(x);
			lock.notifyAll();
			return x;
		}
	}

	@Override
	public TaskHandler
		scheduleAtFixedRate(Runnable task, Instant firstTime, long period)
	{
		return schedule(task, firstTime, period);
	}

	@Override
	public TaskHandler
		scheduleAtFixedRate(Runnable task, long delay, long period)
	{
		return schedule(task, delay, period);
	}

	@Override
	public void close() {
		synchronized ( lock ) {
			if ( mode != Mode.CLOSE ) {
				setMode(Mode.CLOSE);
			}
		}
	}
	
	public State getState() {
		synchronized ( lock ) {
			return state;
		}
	}
	
	/**
	 * Get current execution mode.
	 * <p>
	 * @return mode of execution
	 */
	public Mode getMode() {
		synchronized ( lock ) {
			return mode;
		}
	}
	
	public boolean getRealtimeDelays() {
		synchronized ( lock ) {
			return withRealtimeDelays;
		}
	}
	
	public void setRealtimeDelays(boolean delays) {
		synchronized ( lock ) {
			if ( withRealtimeDelays != delays ) {
				withRealtimeDelays = delays;
				lock.notifyAll();
			}
		}
	}
	
	/**
	 * Switch to pull and run mode.
	 */
	public void switchToPullAndRun() {
		setMode(Mode.PULL_AND_RUN);
	}
	
	/**
	 * Switch to wait for mode change mode.
	 */
	public void switchToWait() {
		setMode(Mode.WAIT);
	}
	
	private void setMode(Mode newMode) {
		synchronized ( lock ) {
			if ( mode == Mode.CLOSE ) {
				throw new IllegalStateException("Object is closed");
			}
			if ( mode != newMode ) {
				mode = newMode;
				lock.notifyAll();
			}
		}		
	}
	
	private void switchState(State newState) {
		synchronized ( lock ) {
			if ( state != newState ) {
				state = newState;
				setChanged();
				notifyObservers();
			}
		}
	}
	
	private void mainLoop() {
		logger.info("Worker thread started");
		TaskStack stackForExec = null;
		long timeShift = 0;
		for ( ; ; ) {
			synchronized ( lock ) {
				if ( mode == Mode.WAIT ) {
					try {
						switchState(State.WAIT_FOR_MODE);
						lock.wait();
					} catch ( InterruptedException e ) {
						Thread.interrupted();
						break;
					}
					continue;
				}
				if ( mode == Mode.CLOSE ) {
					break;
				}
				// Mode PULL_AND_RUN
				if ( withRealtimeDelays ) {
					// If delay emulation enabled, then we have to wait
					// some time until next stack's time will be reached.
					Long next = queue.getTimeOfNextStack();
					if ( next != null ) {
						long delay = next - currentTimestamp - 1;
						if ( delay > 0 ) {
							try {
								switchState(State.DELAY);
								long start = System.currentTimeMillis();
								lock.wait(delay);
								long actual = System.currentTimeMillis() - start;
								currentTimestamp += Math.min(delay, actual);
								continue;
							} catch ( InterruptedException e ) {
								Thread.interrupted();
								break;
							}
						}
					}
				}
				
				stackForExec = queue.popNextStack();
				if ( stackForExec == null ) {
					// No more tasks in queue.
					// Wait for state change or new tasks.
					try {
						switchState(State.WAIT_FOR_TASK);
						lock.wait();
						continue;
					} catch ( InterruptedException e ) {
						Thread.interrupted();
						break;
					}
				} else {
					long stackTime = stackForExec.getTime();
					timeShift = 0;
					if ( stackTime >= currentTimestamp ) {
						currentTimestamp = stackTime + 1;
						timeShift = -1;
					}
				}
				switchState(State.EXECUTING);
			} // end synchronization block
			
			for ( Task taskForExec : stackForExec.getTasks() ) {
				taskForExec.lock();
				try {
					taskForExec.execute();
					if ( taskForExec.isRepeating()
					  && taskForExec.isState(TaskState.SCHEDULED) )
					{
						taskForExec.scheduleForNextExecution(currentTimestamp + timeShift);
						queue.addTask(taskForExec);
					}
					
				} finally {
					taskForExec.unlock();
				}
			}
			stackForExec.clearTasks();
			
		}
		synchronized ( lock ) {
			queue.close();
			switchState(State.CLOSED);
			deleteObservers();
		}
		logger.info("Worker thread finished");
	}

}
