package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * The long-term runnable instance wrapper.
 * <p>
 * This class may used for long-term tasks which are scheduled for execution
 * using scheduler. This wrapper uses additional thread to run the underlying
 * task. This mean that the scheduler's main thread will be released ASAP and
 * that will make it available to execute another tasks. 
 */
public class LongTermTask implements Runnable {
	private final Runnable runnable;
	private final String name;
	
	public LongTermTask(Runnable runnable, String name) {
		this.runnable = runnable;
		this.name = name;
	}
	
	public LongTermTask(Runnable runnable) {
		this(runnable, LongTermTask.class.getSimpleName());
	}
	
	public Runnable getRunnable() {
		return runnable;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public void run() {
		new Thread(runnable, name).start();
	}
	
	@Override
	public String toString() {
		return name + "[" + runnable + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != LongTermTask.class ) {
			return false;
		}
		LongTermTask o = (LongTermTask) other;
		return new EqualsBuilder()
			.append(runnable, o.runnable)
			.append(name, o.name)
			.isEquals();
	}

}
