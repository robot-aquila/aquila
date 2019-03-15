package ru.prolib.aquila.core.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlushController implements FlushIndicator {
	protected static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FlushController.class);
	}
	
	/**
	 * Controller just created. It's waiting for start tracking.
	 * All requests to count in or count down are ignored.
	 * Request for waiting of complete flush will cause exception. 
	 */
	public static final int NEW   = 1;
	
	/**
	 * Controller in tracking mode. It accumulates requests which
	 * change counter. But reaching zero will not cause state
	 * switch until controller go to waiting state. So this mode
	 * is between monitoring started and waiting of complete flush.
	 * If there are events raised in this period it will be counted
	 * and moment of their processing can be detected.  
	 */
	public static final int TRACK = 2;
	
	/**
	 * Controller in waiting of flush mode. Controller go to wait
	 * mode at first call of waiting function. If there are several
	 * threads want to wait the first one will cause switch to wait
	 * mode. Controller become to the done mode when counter reach
	 * zero.
	 */
	public static final int WAIT  = 3;
	
	/**
	 * This mode indicates of complete flush. All consecutive
	 * requests for waiting will be ended immediately.
	 */
	public static final int DONE  = 4;
	
	private final Lock lock;
	private final Condition condition;
	private int status = NEW;
	private long counter;
	
	public FlushController() {
		lock = new ReentrantLock();
		condition = lock.newCondition();
	}
	
	public int getStatus() {
		lock.lock();
		try {
			return status;
		} finally {
			lock.unlock();
		}
	}
	
	public long getCounter() {
		lock.lock();
		try {
			return counter;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void start() {
		lock.lock();
		try {
			switch ( status ) {
			case NEW:
				status = TRACK;
				break;
			case TRACK:
			case WAIT:
			case DONE:
			default:
				break;
			}
		} finally {
			lock.unlock();
		}
	}
	
	public long countUp() {
		lock.lock();
		try {
			switch ( status ) {
			case TRACK:
			case WAIT:
				counter ++;
				break;
			case NEW:
			case DONE:
			default:
				break;
			}
			return counter;
		} finally {
			lock.unlock();
		}
	}
	
	public boolean countDown() {
		lock.lock();
		try {
			switch ( status ) {
			case TRACK:
				counter --;
				break;
			case WAIT:
				counter --;
				if ( counter <= 0L ) {
					status = DONE;
					condition.signalAll();
				}
				break;
			case NEW:
			case DONE:
			default:
				break;
			}
			return status == DONE;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void waitForFlushing(long duration, TimeUnit unit)
			throws InterruptedException, TimeoutException
	{
		lock.lock();
		int start_status = status;
		try {
			switch ( status ) {
			case NEW:
				throw new IllegalStateException();
			case TRACK:
				status = WAIT;
			case WAIT:
				if ( counter == 0L ) {
					// Special case: if we started at zero then it highly possible
					// that none have been counted up between start and wait. In other
					// words - nothing happened to track. If we'll wait it will
					// be always timeout. So just skip this case.
					status = DONE;
				} else {
					while ( status != DONE ) {
						if ( ! condition.await(duration, unit) ) {
							throw new TimeoutException(
									"counter=" + counter +
									" status=" + status +
									" start_status=" + start_status
								);
						}
					}
				}
				break;
			case DONE:
			default:
				break;
			}
		} finally {
			lock.unlock();
		}
	}

}
