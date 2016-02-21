package ru.prolib.aquila.datatools.tickdatabase;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class SimpleL1Replay {
	private static final int MIN_QUEUE_SIZE = 100;
	private static final int MAX_QUEUE_SIZE = 200;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimpleL1Replay.class);
	}
	
	public interface ReaderFactory {
	
		public L1UpdateReader createReader(File file) throws IOException;
		
	}
	
	public static class SimpleCsvL1ReaderFactory implements ReaderFactory {

		@Override
		public L1UpdateReader createReader(File file) throws IOException {
			return new SimpleCsvL1UpdateReader(file);
		}
		
	}
	
	public static class ConsumeTickTask implements Runnable {
		private final SimpleL1Replay consumer;
		private final L1Update update;
		private final long sequenceID;
		
		ConsumeTickTask(SimpleL1Replay consumer, L1Update update, long id) {
			super();
			this.consumer = consumer;
			this.update = update;
			this.sequenceID = id;
		}

		@Override
		public void run() {
			consumer.consumeUpdate(update, sequenceID);
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ConsumeTickTask.class ) {
				return false;
			}
			ConsumeTickTask o = (ConsumeTickTask) other;
			return new EqualsBuilder()
				.append(consumer, o.consumer)
				.append(update, o.update)
				.append(sequenceID, o.sequenceID)
				.isEquals();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()
				+ "[consumer=" + consumer + ", symbol=" + update.getSymbol()
				+ ", tick=" + update.getTick() + ", sequenceID=" + sequenceID
				+ "]";
		}
		
	}
	
	private final Lock lock;
	private final Scheduler scheduler;
	private final L1UpdateConsumer consumer;
	private final ReaderFactory readerFactory;
	private boolean started = false;
	private L1UpdateReader reader;
	private long sequenceID = 0;
	private int queued = 0;
	private final int minQueueSize, maxQueueSize;
	/**
	 * Time difference between the time of data feed and time of scheduler.
	 */
	private Duration timeDiff = null;
	
	public SimpleL1Replay(Scheduler scheduler, L1UpdateConsumer consumer,
			ReaderFactory readerFactory, int minQueueSize, int maxQueueSize)
	{
		super();
		if ( minQueueSize <= 0 ) {
			throw new IllegalArgumentException("Min queue size should be greater than zero");
		}
		if ( maxQueueSize <= minQueueSize ) {
			throw new IllegalArgumentException("Max queue size should be greater than min queue size");
		}
		this.lock = new ReentrantLock();
		this.scheduler = scheduler;
		this.consumer = consumer;
		this.readerFactory = readerFactory;
		this.minQueueSize = minQueueSize;
		this.maxQueueSize = maxQueueSize;
	}
	
	public SimpleL1Replay(Scheduler scheduler, L1UpdateConsumer consumer,
			ReaderFactory readerFactory)
	{
		this(scheduler, consumer, readerFactory, MIN_QUEUE_SIZE, MAX_QUEUE_SIZE);
	}
	
	public SimpleL1Replay(Scheduler scheduler, L1UpdateConsumer consumer) {
		this(scheduler, consumer, new SimpleCsvL1ReaderFactory());
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public L1UpdateConsumer getConsumer() {
		return consumer;
	}
	
	public ReaderFactory getReaderFactory() {
		return readerFactory;
	}
	
	public int getMinQueueSize() {
		return minQueueSize;
	}
	
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
	
	public void close() {
		stopReadingUpdates();
	}
	
	public boolean isStarted() {
		lock.lock();
		try {
			return started;
		} finally {
			lock.unlock();
		}
	}
	
	public void startReadingUpdates(File file) throws Exception {
		lock.lock();
		try {
			if ( started ) {
				throw new IllegalStateException("Already started");
			}
			reader = readerFactory.createReader(file);
			started = true;
			sequenceID ++;
			queued = 0;
			timeDiff = null;
			fillUpQueue();
			
		} finally {
			lock.unlock();
		}
	}
	
	private void closeReader() {
		if ( reader != null ) {
			try {
				reader.close();
			} catch ( IOException e ) {
				logger.warn("Error closing reader: ", e);				
			}
			reader = null;
		}
	}
	
	public void stopReadingUpdates() {
		lock.lock();
		try {
			if ( started ) {
				closeReader();
				started = false;
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	private void fillUpQueue() {
		if ( queued >= minQueueSize ) {
			return;
		}
		if ( reader != null ) {
			while ( queued < maxQueueSize ) {
				try {
					if ( reader.nextUpdate() ) {
						L1Update update = reader.getUpdate();
						Tick oldTick = update.getTick();
						Instant newTime = getScheduleTime(update.getSymbol(),
								oldTick.getTime());
						L1Update newUpdate = toNewTimeUpdate(update, newTime);
						scheduler.schedule(new ConsumeTickTask(this,
							newUpdate, sequenceID), newTime);
						queued ++;
						
					} else {
						closeReader();
						break;
					}
				} catch ( IOException e ) {
					logger.error("Unexpected error: ", e);
					closeReader();
				}
			}
		}
		if ( reader == null && queued == 0 ) {
			stopReadingUpdates();
		}
	}

	public void consumeUpdate(L1Update update, long sequenceID) {
		lock.lock();
		try {
			if ( sequenceID != this.sequenceID || ! started ) {
				return; // skip this update
			}
			queued --;
			consumer.consume(update);
			fillUpQueue();
			
		} finally {
			lock.unlock();
		}
	}
	
	private L1Update toNewTimeUpdate(L1Update oldUpdate, Instant newTime) {
		Tick oldTick = oldUpdate.getTick();
		L1Update newUpdate = new L1UpdateImpl(oldUpdate.getSymbol(),
				Tick.of(oldTick.getType(), newTime,
				oldTick.getPrice(), oldTick.getSize(), oldTick.getValue()));
		return newUpdate;
	}
	
	/**
	 * Convert update time to schedule time.
	 * <p>
	 * @param symbol - symbol of update
	 * @param updateTime - update time
	 * @return schedule time of update
	 */
	private Instant getScheduleTime(Symbol symbol, Instant updateTime) {
		Instant currentTime = scheduler.getCurrentTime();		
		if ( timeDiff == null ) {
			timeDiff = Duration.between(updateTime, currentTime);
			return currentTime;
		}
		return updateTime.compareTo(currentTime) < 0
				? currentTime : updateTime.plus(timeDiff);
	}

}
