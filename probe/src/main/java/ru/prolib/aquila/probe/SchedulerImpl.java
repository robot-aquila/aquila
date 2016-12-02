package ru.prolib.aquila.probe;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.SPRunnableTaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.probe.scheduler.Cmd;
import ru.prolib.aquila.probe.scheduler.CmdClose;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchRun;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchRunCutoff;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchRunStep;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchWait;
import ru.prolib.aquila.probe.scheduler.CmdScheduleTask;
import ru.prolib.aquila.probe.scheduler.CmdSetExecutionSpeed;
import ru.prolib.aquila.probe.scheduler.CmdShiftForward;
import ru.prolib.aquila.probe.scheduler.SchedulerState;
import ru.prolib.aquila.probe.scheduler.SchedulerTaskImpl;

public class SchedulerImpl implements Scheduler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SchedulerImpl.class);
	}
	
	private final Lock lock;
	private final BlockingQueue<Cmd> commandQueue;
	private final SchedulerState state;
	private boolean closed = false;
	
	/**
	 * Constructor.
	 * <p>
	 * It's for internal usage only. Use the {@link SchedulerBuilder} to build
	 * a scheduler instance.
	 * <p>
	 * @param commandQueue - the command queue
	 * @param state - scheduler state
	 */
	SchedulerImpl(BlockingQueue<Cmd> commandQueue, SchedulerState state) {
		this.lock = new ReentrantLock();
		this.commandQueue = commandQueue;
		this.state = state;
	}
	
	/**
	 * Get the scheduler state.
	 * <p>
	 * @return state
	 */
	public SchedulerState getState() {
		return state;
	}
	
	BlockingQueue<Cmd> getCommandQueue() {
		return commandQueue;
	}
	
	@Override
	public Instant getCurrentTime() {
		return state.getCurrentTime();
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant time) {
		SchedulerTaskImpl handler = new SchedulerTaskImpl(task);
		handler.scheduleForFirstExecution(time);
		send(new CmdScheduleTask(handler));
		return handler;
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
		SchedulerTaskImpl handler = new SchedulerTaskImpl(task, period);
		handler.scheduleForFirstExecution(firstTime);
		send(new CmdScheduleTask(handler));
		return handler;
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay) {
		checkClosed();
		SchedulerTaskImpl handler = new SchedulerTaskImpl(task);
		handler.scheduleForFirstExecution(getCurrentTime(), delay);
		send(new CmdScheduleTask(handler));
		return handler;
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay, long period) {
		checkClosed();
		SchedulerTaskImpl handler = new SchedulerTaskImpl(task, period);
		handler.scheduleForFirstExecution(getCurrentTime(), delay);
		send(new CmdScheduleTask(handler));
		return handler;
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, Instant firstTime, long period) {
		return schedule(task, firstTime, period);
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, long delay, long period) {
		return schedule(task, delay, period);
	}

	@Override
	public void close() {
		lock.lock();
		try {
			if ( ! closed ) {
				send(new CmdClose());
				closed = true;
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Test that the scheduler is closed.
	 * <p>
	 * @return true if the scheduler is closed, false otherwise
	 */
	public boolean isClosed() {
		lock.lock();
		try {
			return closed;
		} finally {
			lock.unlock();
		}
	}
	
	public void setModeRun() {
		send(new CmdModeSwitchRun());
	}
	
	public void setModeRun(Instant cutoffTime) {
		send(new CmdModeSwitchRunCutoff(cutoffTime));
	}
	
	public void setModeStep() {
		send(new CmdModeSwitchRunStep());
	}
	
	public void setModeWait() {
		send(new CmdModeSwitchWait());
	}
	
	public void setCurrentTime(Instant newTime) {
		send(new CmdShiftForward(newTime));
	}
	
	public void setExecutionSpeed(int speed) {
		send(new CmdSetExecutionSpeed(speed));
	}
	
	private void checkClosed() {
		if ( isClosed() ) {
			throw new IllegalStateException("Scheduler closed");
		}		
	}
	
	private void send(Cmd cmd) {
		checkClosed();
		try {
			commandQueue.put(cmd);
		} catch ( InterruptedException e ) {
			logger.error("Unexpected interruption: ", e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public TaskHandler schedule(SPRunnable task) {
		return SPRunnableTaskHandler.schedule(this, task);
	}

}
