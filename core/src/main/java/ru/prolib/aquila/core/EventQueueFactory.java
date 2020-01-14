package ru.prolib.aquila.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ru.prolib.aquila.core.concurrency.SelectiveBarrier;
import ru.prolib.aquila.core.eqs.EventQueueServiceBuilder;
import ru.prolib.aquila.core.eqs.v4.V4Dispatcher;
import ru.prolib.aquila.core.eqs.v4.V4Queue;
import ru.prolib.aquila.core.eqs.v4.V4QueueService;
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
		BlockingQueue<EventDispatchingRequest> queue = new LinkedBlockingQueue<>();
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
	
	public EventQueue createV4(String name, long put_timeout_millis, boolean separate_service_thread) {
		SelectiveBarrier barrier = new SelectiveBarrier();
		BlockingQueue<EventDispatchingRequest> read_queue = new LinkedBlockingQueue<>();
		V4Queue write_queue = new V4Queue(read_queue, barrier, put_timeout_millis);
		EventQueueService service = new V4QueueService(write_queue);
		if ( separate_service_thread ) {
			service = new EventQueueServiceBuilder().delegateToSeparateThread(service, name + ".SERVICE");
		}
		Thread disp_thread = new Thread(new V4Dispatcher(read_queue, barrier, service));
		disp_thread.setName(name + ".DISPATCHER");
		disp_thread.setDaemon(true);
		disp_thread.start();
		return new EventQueueImpl(write_queue, service, name);
	}
	
	public EventQueue createV4(String name, long put_timeout_millis) {
		return createV4(name, put_timeout_millis, false);
	}
	
	public EventQueue createV4(String name) {
		return createV4(name, V4Queue.DEFAULT_TIMEOUT_MILLIS);
	}
	
	public EventQueue createV4() {
		return createV4(getNextQueueName());
	}

	public EventQueue createDefault(String name) {
		return createV4(name);
	}

	public EventQueue createDefault() {
		return createV4();
	}
	
}
