package ru.prolib.aquila.core.eque;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;

public class DispatcherThread extends Thread {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DispatcherThread.class);
	}
	
	private final BlockingQueue<EventDispatchingRequest> queue;
	private final DispatchingMethod dispatchingMethod;
	private final EventQueueService service;
	
	public DispatcherThread(BlockingQueue<EventDispatchingRequest> queue,
			DispatchingMethod dispatchingMethod,
			EventQueueService service)
	{
		this.queue = queue;
		this.dispatchingMethod = dispatchingMethod;
		this.service = service;
	}
	
	private static ExecutorService
		createExecutor(String queueName, int numOfWorkerThreads)
	{
		return Executors.newFixedThreadPool(numOfWorkerThreads,
				new WorkerThreadFactory(queueName));
	}
	
	public static DispatcherThread
		createOriginal(BlockingQueue<EventDispatchingRequest> queue,
				String queueName,
				int numOfWorkerThreads,
				EventQueueService service)
	{
		DispatcherThread t = new DispatcherThread(queue,
			new DispatchingMethodOriginal(createExecutor(queueName, numOfWorkerThreads)),
			service);
		t.setName(queueName + ".DISPATCHER");
		t.setDaemon(true);
		return t;
	}
	
	public static DispatcherThread
		createComplFutures(BlockingQueue<EventDispatchingRequest> queue,
				String queueName,
				int numOfWorkerThreads,
				EventQueueService service)
	{
		DispatcherThread t = new DispatcherThread(queue,
			new DispatchingMethodComplFutures(createExecutor(queueName, numOfWorkerThreads)),
			service);
		t.setName(queueName + ".DISPATCHER");
		t.setDaemon(true);
		return t;
	}
	
	public static DispatcherThread
		createRightHere(BlockingQueue<EventDispatchingRequest> queue,
				String queueName,
				EventQueueService service)
	{
		DispatcherThread t = new DispatcherThread(queue,
			new DispatchingMethodRightHere(),
			service);
		t.setName(queueName + ".DISPATCHER");
		return t;
	}
	
	public static DispatcherThread
		createQueueWorkers(BlockingQueue<EventDispatchingRequest> queue,
				String queueName,
				int numOfWorkerThreads,
				EventQueueService service)
	{
		DispatcherThread t = new DispatcherThread(queue,
			new DispatchingMethodQueueWorkers(queueName, numOfWorkerThreads),
			service);
		t.setName(queueName + ".DISPATCHER");
		t.setDaemon(true);
		return t;
	}
	
	@Override
	public void run() {
		try {
			//logger.debug("Dispatcher started");
			EventDispatchingRequest request;
			while ( (request = queue.take()) != null ) {
				if ( request == EventDispatchingRequest.EXIT ) {
					//logger.debug("Exit signal acquired...");
					dispatchingMethod.shutdown();
					break;
				}
				long b1 = System.nanoTime();
				List<DeliveryEventTask> tasks = buildTaskList(request);
				long b2 = System.nanoTime();
				dispatchingMethod.dispatch(tasks);
				service.addDispatchingTime(System.nanoTime() - b2);
				service.addPreparingTime(b2 - b1);
				service.eventDispatched();
			}
		} catch ( InterruptedException e ) {
			logger.error("Interrupted: ", e);
			Thread.currentThread().interrupt();
		} catch ( Throwable e ) {
			logger.error("Unexpected exception: ", e);
		} finally {
			service.shutdown();
		}
		//logger.debug("Dispatcher terminated");
	}
	
	private List<DeliveryEventTask> buildTaskList(EventDispatchingRequest request) {
		List<DeliveryEventTask> list = new LinkedList<>();
		Set<EventType> allTypes = new LinkedHashSet<>();
		for ( EventType type : ESUtils.getAllUniqueTypes(allTypes, request.type) ) {
			Event event = request.factory.produceEvent(type);
			for ( EventListener listener : type.getListeners() ) {
				list.add(new DeliveryEventTask(event, listener, service));
			}
		}
		return list;
	}
	
}