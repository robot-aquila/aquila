package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class SchedulerSlot implements Comparable<SchedulerSlot> {
	private final Instant time;
	private final List<SchedulerTask> tasks = new ArrayList<>();
	
	public SchedulerSlot(Instant time) {
		this.time = time;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public List<SchedulerTask> getTasks() {
		return tasks;
	}
	
	public SchedulerSlot clearTasks() {
		tasks.clear();
		return this;
	}
	
	public SchedulerSlot addTask(SchedulerTask task) {
		tasks.add(task);
		return this;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SchedulerSlot.class ) {
			return false;
		}
		SchedulerSlot o = (SchedulerSlot) other;
		return new EqualsBuilder()
			.append(time, o.time)
			.append(tasks, o.tasks)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + time + " " + tasks + "]";
	}

	@Override
	public int compareTo(SchedulerSlot o) {
		return time.compareTo(o.time);
	}

}
