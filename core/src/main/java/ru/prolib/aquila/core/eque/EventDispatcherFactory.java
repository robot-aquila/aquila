package ru.prolib.aquila.core.eque;

import java.util.concurrent.BlockingQueue;

public class EventDispatcherFactory {
	
	public Thread createLegacyDispatcher(DispatchingType type,
			BlockingQueue<EventDispatchingRequest> queue,
			String queue_name,
			int num_workers,
			EventQueueService queue_service)
	{
		switch ( type ) {
		case OLD_ORIGINAL:
			return DispatcherThread.createOriginal(queue, queue_name, num_workers, queue_service);

		case OLD_COMPL_FUTURES:
			return DispatcherThread.createComplFutures(queue, queue_name, num_workers, queue_service);

		case OLD_RIGHT_HERE:
			return DispatcherThread.createRightHere(queue, queue_name, queue_service);
			
		case NEW_QUEUE_4WORKERS:
			return DispatcherThread.createQueueWorkers(queue, queue_name, 4, queue_service);
			
		case NEW_QUEUE_6WORKERS:
			return DispatcherThread.createQueueWorkers(queue, queue_name, 6, queue_service);
			
		case NEW_RIGHT_HERE_NO_TIME_STATS:
			return DispatcherThreadV2.createNoTimeStats(queue, queue_name, queue_service);
			
		case NEW_RIGHT_HERE_V3:
			return DispatcherThreadV3.createV3(queue, queue_name, queue_service);
			
		default:
			throw new IllegalArgumentException("Unsupported dispatching type: " + type);
		}
	}

}
