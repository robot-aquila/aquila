package ru.prolib.aquila.data.replay;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.SimpleEventFactory;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.TStamped;

/**
 * The class to replay a sequence of
 * {@link ru.prolib.aquila.core.BusinessEntities.TStamped timestamped} objects.
 */
public class TStampedReplay implements Replay {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TStampedReplay.class);
	}
	
	private final Lock lock;
	private final EventQueue eventQueue;
	private final Scheduler scheduler;
	private final int minQueueSize, maxQueueSize;
	private final String serviceID;
	private final EventType onStarted, onStopped;
	private TStampedReplayService service;
	private boolean started = false;
	private CloseableIterator<? extends TStamped> reader;
	private long sequenceID = 0;
	private int queued = 0;
	
	public TStampedReplay(EventQueue eventQueue, Scheduler scheduler,
		TStampedReplayService service, String serviceID, int minQueueSize,
		int maxQueueSize)
	{
		if ( minQueueSize <= 0 ) {
			throw new IllegalArgumentException("Min queue size should be greater than zero");
		}
		if ( maxQueueSize <= minQueueSize ) {
			throw new IllegalArgumentException("Max queue size should be greater than min queue size");
		}
		this.lock = new ReentrantLock();
		this.eventQueue = eventQueue;
		this.scheduler = scheduler;
		this.service = service;
		this.minQueueSize = minQueueSize;
		this.maxQueueSize = maxQueueSize;
		this.serviceID = serviceID;
		this.onStarted = new EventTypeImpl(serviceID + ".STARTED");
		this.onStopped = new EventTypeImpl(serviceID + ".STOPPED");
	}
	
	public TStampedReplay(EventQueue eventQueue, Scheduler scheduler,
			String serviceID, int minQueueSize, int maxQueueSize)
	{
		this(eventQueue, scheduler, null, serviceID, minQueueSize, maxQueueSize);
	}
	
	/**
	 * Set service instance.
	 * <p>
	 * @param service - the service instance
	 */
	public void setService(TStampedReplayService service) {
		lock.lock();
		try {
			this.service = service;
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Test that replay is ready to start.
	 * <p>
	 * @return true if it's ready, false otherwise
	 */
	public boolean isReady() {
		lock.lock();
		try {
			return this.service != null;
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Get a service ID string.
	 * <p>
	 * The service ID is used to identify appropriate service among all scheduled tasks.
	 * <p>
	 * @return the service ID
	 */
	public String getServiceID() {
		return serviceID;
	}
	
	public EventQueue getEventQueue() {
		return eventQueue;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public TStampedReplayService getService() {
		return service;
	}
	
	public int getMinQueueSize() {
		return minQueueSize;
	}
	
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
	
	public long getSequenceID() {
		lock.lock();
		try {
			return sequenceID;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public EventType onStarted() {
		return onStarted;
	}

	@Override
	public EventType onStopped() {
		return onStopped;
	}

	@Override
	public boolean isStarted() {
		lock.lock();
		try {
			return started;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void start() throws IOException {
		lock.lock();
		try {
			if ( started ) {
				throw new IllegalStateException(serviceID + ": Already started");
			}
			reader = service.createReader();
			started = true;
			sequenceID ++;
			queued = 0;
			eventQueue.enqueue(onStarted, SimpleEventFactory.getInstance());
			logger.debug("{}: Service started.", serviceID);
		} finally {
			lock.unlock();
		}
		fillUpQueue();
	}

	@Override
	public void stop() {
		boolean wasStopped = false;
		lock.lock();
		try {
			if ( started ) {
				closeReader();
				started = false;
				wasStopped = true;
				logger.debug("{}: Service stopped.", serviceID);
			}
			if ( wasStopped ) {
				eventQueue.enqueue(onStopped, SimpleEventFactory.getInstance());
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void close() {
		stop();
		onStarted.removeAlternatesAndListeners();
		onStopped.removeAlternatesAndListeners();
	}
	
	void consume(long sequenceID, TStamped object) {
		TStampedReplayService dummyService = null;
		lock.lock();
		try {
			if ( sequenceID != this.sequenceID || ! started ) {
				return;
			}
			queued --;
			dummyService = service;
		} finally {
			lock.unlock();
		}
		
		try {
			if ( dummyService != null ) {
				dummyService.consume(object);
				fillUpQueue();
			}
		} catch ( IOException e ) {
			logger.error(serviceID + ": Unexpected exception: ", e);
			stop();
		}

	}
	
	private void closeReader() {
		lock.lock();
		try {
			if ( reader != null ) {
				reader.close();
			}
		} catch ( IOException e ) {
			logger.warn("Error closing reader: ", e);				
		} finally {
			lock.unlock();
			reader = null;
		}
	}
	
	/**
	 * Fill up the task queue.
	 */
	private void fillUpQueue() {
		boolean stop = false;
		lock.lock();
		try {
			if ( queued >= minQueueSize ) {
				return;
			}
			if ( reader != null ) {
				Instant currentTime = scheduler.getCurrentTime();
				while ( queued < maxQueueSize ) {
					try {
						if ( reader.next() ) {
							TStamped object = reader.item();
							Instant consumptionTime = service.consumptionTime(currentTime, object);
							object = service.mutate(object, consumptionTime);
							scheduler.schedule(new TStampedReplayConsume(this, sequenceID, object), consumptionTime);
							queued ++;
						} else {
							// Do not set a stop flag here because we have to
							// finish scheduled tasks. Just close the reader.
							closeReader();
							break;
						}
						
					} catch ( IOException e ) {
						logger.error(serviceID + ": Unexpected error: ", e);
						stop = true;
						break;
					}
				}
			}
			if ( reader == null && queued == 0 ) {
				stop = true;
			}
			
		} finally {
			lock.unlock();
		}
		if ( stop == true ) {
			stop();
		}
	}

}
