package ru.prolib.aquila.datatools.tickdatabase.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

/**
 * Advanced flushing strategy for a tick writer.
 */
public class SmartFlushTickWriter implements TickWriter, Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SmartFlushTickWriter.class);
	}
	
	private final Scheduler scheduler;
	private final TickWriter writer;
	private final String streamId;
	private final SmartFlushSetup setup;
	private LocalDateTime lastTime;
	private boolean hasUpdate;
	
	/**
	 * Constructor.
	 * <p>
	 * @param writer - tick writer
	 * @param scheduler - scheduler
	 * @param streamId - identifier for log messages
	 * @param setup - smart flush setup
	 */
	public SmartFlushTickWriter(TickWriter writer, Scheduler scheduler,
			String streamId, SmartFlushSetup setup)
	{
		super();
		this.writer = writer;
		this.scheduler = scheduler;
		this.streamId = streamId;
		this.setup = setup;
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
		this(writer, scheduler, streamId, new SmartFlushSetup());
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
	 * Get smart flush setup.
	 * <p>
	 * @return setup
	 */
	public SmartFlushSetup getSetup() {
		return setup;
	}
	
	/**
	 * Get time of last flush.
	 * <p> 
	 * @return the time of last flush
	 */
	public synchronized LocalDateTime getLastFlushTime() {
		return lastTime;
	}
	
	/**
	 * Set time of last flash.
	 * <p>
	 * @param time - the time to set
	 */
	protected synchronized void setLastFlushTime(LocalDateTime time) {
		this.lastTime = time;
	}
	
	/**
	 * Check update since the last flush.
	 * <p>
	 * @return true - if there's at least the one update, false - no updates
	 */
	public synchronized boolean hasUpdate() {
		return hasUpdate;
	}
	
	/**
	 * Set update sign.
	 * <p>
	 * @param update - true - mark that the update available, false - reset the
	 * update sign.
	 */
	protected synchronized void setHasUpdate(boolean update) {
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
	public synchronized void write(Tick tick) throws IOException {
		if ( lastTime == null ) {
			long period = setup.getExecutionPeriod();
			scheduler.schedule(this, period, period);
			logger.info(streamId + ": Started");
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
		LocalDateTime time = scheduler.getCurrentTime();
		long diff = Math.abs(ChronoUnit.MILLIS.between(time, lastTime));
		if ( diff > setup.getFlushPeriod() ) {
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
