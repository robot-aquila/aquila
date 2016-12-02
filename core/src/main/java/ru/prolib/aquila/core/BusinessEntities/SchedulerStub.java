package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SchedulerStub implements Scheduler {
	private final List<SchedulerStubTask> tasks;
	private TimeStrategy currentTimeStrategy;
	
	public SchedulerStub(TimeStrategy currentTimeStrategy) {
		this.tasks = new ArrayList<>();
		this.currentTimeStrategy = currentTimeStrategy;
	}
	
	public SchedulerStub() {
		this(new FixedTimeStrategy(Instant.EPOCH));
	}
	
	/**
	 * Get list of scheduled tasks.
	 * <p>
	 * @return list of tasks
	 */
	public List<SchedulerStubTask> getScheduledTasks() {
		ArrayList<SchedulerStubTask> result = new ArrayList<>(tasks);
		Collections.sort(result);
		return result;
	}
	
	public int getNumScheduledTasks() {
		return tasks.size();
	}
	
	public void clearScheduledTasks() {
		tasks.clear();
	}
	
	/**
	 * Set a time strategy for scheduler's current time.
	 * <p>
	 * @param strategy - the strategy instance
	 */
	public void setTimeStrategy(TimeStrategy strategy) {
		IOUtils.closeQuietly(currentTimeStrategy);
		currentTimeStrategy = strategy;
	}
	
	/**
	 * Set a fixed-time strategy for scheduler's current time.
	 * <p>
	 * @param timestamp - the time to use as current time
	 */
	public void setFixedTime(Instant timestamp) {
		setTimeStrategy(new FixedTimeStrategy(timestamp));
	}
	
	public void setFixedTime(String timeString) {
		setFixedTime(Instant.parse(timeString));
	}
	
	/**
	 * Set an iterable time strategy for scheduler's current time.
	 * <p>
	 * @param iterator - the set of time
	 */
	public void setIterableTimeStrategy(CloseableIterator<Instant> iterator) {
		setTimeStrategy(new IterableTimeStrategy(iterator));
	}
	
	@Override
	public Instant getCurrentTime() {
		return currentTimeStrategy.getTime();
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant time) {
		return add(SchedulerStubTask.atTime(time, task));
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
		return add(SchedulerStubTask.atTimePeriodic(firstTime, period, task));
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay) {
		return add(SchedulerStubTask.withDelay(getCurrentTime()
				.plusMillis(delay), delay, task));
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay, long period) {
		return add(SchedulerStubTask.withDelayPeriodic(getCurrentTime()
				.plusMillis(delay), delay, period, task));
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, Instant firstTime,
			long period)
	{
		return add(SchedulerStubTask.atTimeFixedRate(firstTime, period, task));
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, long delay, long period) {
		return add(SchedulerStubTask.withDelayFixedRate(getCurrentTime()
				.plusMillis(delay), delay, period, task));
	}

	@Override
	public void close() {
		tasks.clear();
		IOUtils.closeQuietly(currentTimeStrategy);
	}
	
	private SchedulerStubTask add(SchedulerStubTask handler) {
		tasks.add(handler);
		return handler;
	}

	@Override
	public TaskHandler schedule(SPRunnable task) {
		return SPRunnableTaskHandler.schedule(this, task);
	}

}
