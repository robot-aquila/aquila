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
public class PROBEScheduler implements Scheduler {
	public static final int MODE_CLOSED = -1;
	public static final int MODE_WAIT = 0;
	public static final int MODE_PULL_AND_RUN = 1;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PROBEScheduler.class);
	}
	
	static class Task implements TaskHandler {
		static final int ERROR = -1;
		static final int VIRGIN = 0;
		static final int SCHEDULED = 1;
		static final int EXECUTED = 2;
		static final int CANCELLED = 3;
		
		private final Lock lock;
		private final long period;
		private Runnable runnable;
		private int state = VIRGIN;
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
				if ( state == SCHEDULED ) {
					try {
						runnable.run();
						if ( period == 0 ) {
							state = EXECUTED;
							runnable = null;
						}
					} catch ( Exception e ) {
						// TODO: make message more informative
						Object args[] = { runnable.toString(), e };
						logger.error("Unhandled exception (Task ID: {}): ", args);
						state = ERROR;
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
		
		public int getState() {
			lock.lock();
			try {
				return state;
			} finally {
				lock.unlock();
			}
		}
		
		public long scheduleForFirstExecution(long currentTime, long delay) {
			lock.lock();
			try {
				if ( state == VIRGIN ) {
					nextExecutionTime = currentTime + delay;
					state = SCHEDULED;
				}
				return nextExecutionTime;
			} finally {
				lock.unlock();
			}
		}
		
		public long scheduleForNextExecution(long currentTime) {
			lock.lock();
			try {
				if ( state == SCHEDULED && period != 0 ) {
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
				boolean result = (state == SCHEDULED);
				state = CANCELLED;
				runnable = null;
				return result;
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
		
		public void clear() {
			lock.lock();
			try {
				for ( TaskStack stack : stacks.values() ) {
					stack.clearTasks();
				}
				stacks.clear();
			} finally {
				lock.unlock();
			}
		}
		
		public TaskStack popNextStack() {
			lock.lock();
			try {
				if ( stacks.size() == 0 ) {
					return null;
				}
				Long t = null;
				for ( Long x : stacks.keySet() ) {
					if ( t == null || x < t ) {
						t = x;
					}
				}
				return stacks.remove(t);
			} finally {
				lock.unlock();
			}
		}
		
	}
	
	private final Object lock = new Object();
	private final TaskStackQueue queue = new TaskStackQueue();
	private long currentTime = 0;
	private int mode = MODE_WAIT;
	
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
			return Instant.ofEpochMilli(currentTime);
		}
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant time) {
		return schedule(task, time, 0);
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
		synchronized ( lock ) {
			long delay = firstTime.toEpochMilli() - currentTime;
			if ( delay < 0 ) {
				delay = 0;
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
			Task x = new Task(task, period);
			x.scheduleForFirstExecution(currentTime, delay);
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
		setMode(MODE_CLOSED);
	}
	
	/**
	 * Switch to PULL_AND_RUN mode.
	 */
	public void switchToPullAndRun() {
		setMode(MODE_PULL_AND_RUN);
	}
	
	private void setMode(int newMode) {
		synchronized ( lock ) {
			if ( mode != newMode ) {
				mode = newMode;
				lock.notifyAll();
			}
		}		
	}
	
	private void mainLoop() {
		logger.info("Worker thread started");
		TaskStack stackForExec = null;
		long timeShift = 0;
		for ( ; ; ) {
			synchronized ( lock ) {
				if ( mode == MODE_WAIT ) {
					try {
						lock.wait();
					} catch ( InterruptedException e ) {
						Thread.interrupted();
						break;
					}
					continue;
				}
				if ( mode == MODE_CLOSED ) {
					queue.clear();
					break;
				}
				// MODE_PULL_AND_RUN
				stackForExec = queue.popNextStack();
				if ( stackForExec == null ) {
					// No more tasks in queue.
					// Wait for state change or new tasks.
					try {
						lock.wait();
						continue;
					} catch ( InterruptedException e ) {
						Thread.interrupted();
						break;
					}
				} else {
					long stackTime = stackForExec.getTime();
					timeShift = 0;
					if ( stackTime > currentTime ) {
						currentTime = stackTime + 1;
						timeShift = -1;
					}
				}
			} // end synchronization block
			
			for ( Task taskForExec : stackForExec.getTasks() ) {
				taskForExec.lock();
				try {
					taskForExec.execute();
					if ( taskForExec.isRepeating()
					  && taskForExec.getState() == Task.SCHEDULED )
					{
						taskForExec.scheduleForNextExecution(currentTime + timeShift);
						queue.addTask(taskForExec);
					}
					
				} finally {
					taskForExec.unlock();
				}
			}
			stackForExec.clearTasks();
			
		}
		logger.info("Worker thread finished");
	}

}
