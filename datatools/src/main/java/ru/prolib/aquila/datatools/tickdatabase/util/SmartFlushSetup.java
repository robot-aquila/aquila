package ru.prolib.aquila.datatools.tickdatabase.util;

public class SmartFlushSetup {
	public static final long DEFAULT_EXECUTION_PERIOD = 60000; // 1 minute
	public static final long DEFAULT_FLUSH_PERIOD = 300000; // 5 minutes
	private long executionPeriod, flushPeriod;
	
	public SmartFlushSetup() {
		super();
		this.executionPeriod = DEFAULT_EXECUTION_PERIOD;
		this.flushPeriod = DEFAULT_FLUSH_PERIOD;
	}
	
	/**
	 * Get the check execution period.
	 * <p>
	 * @return milliseconds
	 */
	public long getExecutionPeriod() {
		return executionPeriod;
	}
	
	/**
	 * Set the check execution period.
	 * <p>
	 * @param period - milliseconds between checks
	 */
	public void setExecutionPeriod(long period) {
		this.executionPeriod = period;
	}
	
	/**
	 * Get flush period.
	 * <p>
	 * @return milliseconds since last flush 
	 */
	public long getFlushPeriod() {
		return flushPeriod;
	}
	
	/**
	 * Set flush period.
	 * <p>
	 * @param period - milliseconds between flushes
	 */
	public void setFlushPeriod(long period) {
		this.flushPeriod = period;
	}

}
