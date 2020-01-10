package ru.prolib.aquila.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ru.prolib.aquila.core.eqs.EventQueueServiceBuilder;
import ru.prolib.aquila.core.eque.DispatchingType;
import ru.prolib.aquila.core.eque.EventDispatcherFactory;
import ru.prolib.aquila.core.eque.EventDispatchingRequest;
import ru.prolib.aquila.core.eque.EventQueueService;

public class EventQueueFactory {
	public static final DispatchingType DEFAULT_DISPATCHING_TYPE = DispatchingType.NEW_RIGHT_HERE_V3;
	public static final int DEFAULT_NUM_OF_WORKERS = 4;

	private static int queueLastIndex;

	public synchronized static String getNextQueueName() {
		queueLastIndex ++;
		return "EQUE-" + queueLastIndex;
	}

	public EventQueue createLegacy(String name, int num_workers, DispatchingType disp_type) {
		BlockingQueue<EventDispatchingRequest> queue = new LinkedBlockingQueue<EventDispatchingRequest>();
		EventQueueService service = new EventQueueServiceBuilder().createLegacyService(); 
		new EventDispatcherFactory().createLegacyDispatcher(disp_type, queue, name, num_workers, service).start();
		return new EventQueueImpl(queue, service, name);
	}
	
	public EventQueue createLegacy(String name) {
		return createLegacy(name, DEFAULT_NUM_OF_WORKERS, DEFAULT_DISPATCHING_TYPE);
	}
	
	public EventQueue createLegacy(DispatchingType disp_type) {
		return createLegacy(getNextQueueName(), DEFAULT_NUM_OF_WORKERS, disp_type);
	}
	
	public EventQueue createLegacy() {
		return createLegacy(getNextQueueName());
	}

	public EventQueue createDefault(String name) {
		return createLegacy(name);
	}

	public EventQueue createDefault() {
		return createLegacy();
	}
	
}
