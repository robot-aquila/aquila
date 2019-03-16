package ru.prolib.aquila.core.eque;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.utils.FlushControl;

public class EventQueueStats {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EventQueueStats.class);
	}
	
	private final AtomicLong buildTaskListTime, dispatchTime, deliveryTime,
		totalEventsSent, totalEventsDispatched;
	private final FlushControl flushControl;
	private final Map<String, Long> eventsPerThread;
	
	public EventQueueStats(FlushControl flushControl) {
		this.flushControl = flushControl;
		this.buildTaskListTime = new AtomicLong(0);
		this.dispatchTime = new AtomicLong(0);
		this.deliveryTime = new AtomicLong(0);
		this.totalEventsSent = new AtomicLong(0);
		this.totalEventsDispatched = new AtomicLong(0);
		this.eventsPerThread = new HashMap<>();
	}
	
	public void addEventSent() {
		totalEventsSent.incrementAndGet();
		synchronized ( this ) {
			String thread_name = Thread.currentThread().getName();
			Long x = eventsPerThread.get(thread_name);
			if ( x == null ) {
				eventsPerThread.put(thread_name, 1L);
			} else {
				eventsPerThread.put(thread_name, x + 1L);
			}
		}
	}
	
	public void addEventDispatched() {
		flushControl.countDown();
		totalEventsDispatched.incrementAndGet();
	}
	
	public void addBuildingTaskListTime(long time) {
		buildTaskListTime.addAndGet(time);
	}

	public void addDispatchingTime(long time) {
		dispatchTime.addAndGet(time);
	}
	
	public void addDeliveryTime(long time) {
		deliveryTime.addAndGet(time);
	}
	
	public long getBuildingTaskListTime() {
		return buildTaskListTime.get();
	}
	
	public long getDispatchingTime() {
		return dispatchTime.get();
	}
	
	public long getDeliveryTime() {
		return deliveryTime.get();
	}
	
	public long getTotalEventsSent() {
		return totalEventsSent.get();
	}
	
	public long getTotalEventsDispatched() {
		return totalEventsDispatched.get();
	}
	
	public synchronized void dumpSecondaryStats() {
		Iterator<Entry<String, Long>> it = eventsPerThread.entrySet().iterator();
		String l_sep = System.lineSeparator();
		StringBuilder sb = new StringBuilder()
				.append(l_sep)
				.append("Event queue secondary stats ----------------------------").append(l_sep)
				.append("Events per thread:").append(l_sep);
		while ( it.hasNext() ) {
			Entry<String, Long> item = it.next();
			sb.append(item.getKey()).append(" => ").append(item.getValue()).append(l_sep);
		}
		logger.debug(sb.toString());
	}
	
}
