package ru.prolib.aquila.datatools.tickdatabase.util;

import java.io.IOException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

/**
 * Advanced flushing strategy for a tick writer.
 */
public class SmartFlushTickWriter implements TickWriter, Runnable {
	private static final Logger logger;
	public static final long DEFAULT_EXECUTION_PERIOD = 60000; // 1 minute
	public static final long DEFAULT_FLUSH_PERIOD = 300000; // 5 minutes
	
	static {
		logger = LoggerFactory.getLogger(SmartFlushTickWriter.class);
	}
	
	private final Scheduler scheduler;
	private final TickWriter writer;
	private final long executionPeriod, flushPeriod;
	private final String streamId;
	private DateTime lastTime;
	private boolean hasUpdate;
	
	/**
	 * Constructor.
	 * <p>
	 * @param writer - tick writer
	 * @param scheduler - scheduler
	 * @param streamId - identifier for log messages
	 * @param executionPeriod - period of check execution
	 * @param flushPeriod - period of flush since last update
	 */
	public SmartFlushTickWriter(TickWriter writer, Scheduler scheduler,
			String streamId, long executionPeriod, long flushPeriod)
	{
		super();
		this.writer = writer;
		this.scheduler = scheduler;
		this.streamId = streamId;
		this.executionPeriod = executionPeriod;
		this.flushPeriod = flushPeriod;
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param writer - tick writer
	 * @param scheduler - scheduler
	 * @param streamId - identifier for log messages
	 */
	public SmartFlushTickWriter(TickWriter writer, Scheduler scheduler,
			String streamId)
	{
		this(writer, scheduler, streamId,
				DEFAULT_EXECUTION_PERIOD, DEFAULT_FLUSH_PERIOD);
	}
	
	/**
	 * Get controlled tick writer.
	 * <p>
	 * @return the tick writer
	 */
	public TickWriter getTickWriter() {
		return writer;
	}
	
	/**
	 * Get scheduler.
	 * <p>
	 * @return scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	/**
	 * Get the stream ID.
	 * <p>
	 * @return stream ID
	 */
	public String getStreamId() {
		return streamId;
	}
	
	/**
	 * Get execution period.
	 * <p>
	 * @return period of check execution
	 */
	public long getExecutionPeriod() {
		return executionPeriod;
	}
	
	/**
	 * Get flush period.
	 * <p>
	 * @return period of flush since last update
	 */
	public long getFlushPeriod() {
		return flushPeriod;
	}
	
	public synchronized DateTime getLastFlushTime() {
		return lastTime;
	}
	
	protected synchronized void setLastFlushTime(DateTime time) {
		this.lastTime = time;
	}
	
	public synchronized boolean hasUpdate() {
		return hasUpdate;
	}
	
	public synchronized void setHasUpdate(boolean update) {
		this.hasUpdate = update;
	}

	@Override
	public synchronized void close() throws IOException {
		scheduler.cancel(this);
		writer.close();
	}

	@Override
	public synchronized void flush() throws IOException {
		writer.flush();
	}

	@Override
	public synchronized void write(Tick tick) throws GeneralException {
		if ( lastTime == null ) {
			scheduler.schedule(this, executionPeriod, executionPeriod);
		}
		writer.write(tick);
		lastTime = scheduler.getCurrentTime();
		hasUpdate = true;
	}

	@Override
	public synchronized void run() {
		if ( ! hasUpdate ) {
			return;
		}
		DateTime time = scheduler.getCurrentTime();
		if ( time.getMillis() - lastTime.getMillis() > flushPeriod ) {
			try {
				writer.flush();
				hasUpdate = false;
				lastTime = time;
				logger.info(streamId + ": Flushed");
			} catch ( IOException e ) {
				logger.error(streamId + ": Flush error: ", e);	
			}
		}
	}

}
