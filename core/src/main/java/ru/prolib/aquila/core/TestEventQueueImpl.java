package ru.prolib.aquila.core;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This event queue implementation for testing purposes only and shouldn't be used for production code.
 */
public class TestEventQueueImpl implements EventQueue {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TestEventQueueImpl.class);
	}
	
	private final LinkedList<Event> cache;
	private final String name;
	private boolean queueProcessing = false;
	
	TestEventQueueImpl(String name, LinkedList<Event> cache) {
		this.name = name;
		this.cache = cache;
	}
	
	public TestEventQueueImpl(String name) {
		this(name, new LinkedList<>());
	}
	
	public TestEventQueueImpl() {
		this("TEST-QUEUE");
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public void enqueue(EventType type, EventFactory factory) {
		if ( type.hasAlternates() ) {
			Set<EventType> alternates = new HashSet<EventType>();
			alternates.add(type);
			fillUniqueAlternates(alternates, type);
			for ( EventType alternate : alternates ) {
				enqueue(factory.produceEvent(alternate));
			}
		} else {
			enqueue(factory.produceEvent(type));	
		}
	}
	
	private void fillUniqueAlternates(Set<EventType> alternates, EventType type) {
		for ( EventType alternate : type.getAlternateTypes() ) {
			if ( ! alternates.contains(alternate) ) {
				alternates.add(alternate);
				fillUniqueAlternates(alternates, alternate);
			}
		}
	}

	private void enqueue(Event event) {
		if ( event == null ) {
			throw new NullPointerException("The event cannot be null");
		}
		synchronized ( this ) {
			cache.add(event);
			if ( queueProcessing ) {
				return;
			}
			queueProcessing = true;
		}
		for ( ;; ) {
			synchronized ( this ) {
				event = cache.pollFirst();
				if ( event == null ) {
					queueProcessing = false;
					break;
				}
			}
			for ( EventListener listener : event.getType().getListeners() ) {
				try {
					listener.onEvent(event);
				} catch ( Throwable t ) {
					logger.error("Unhandled exception: ", t);
				}
			}
		}
	}
	
}
