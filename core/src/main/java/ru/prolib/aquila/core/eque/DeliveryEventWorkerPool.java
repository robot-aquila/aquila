package ru.prolib.aquila.core.eque;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class DeliveryEventWorkerPool {
	private final String queueName;
	private final int numWorkers;
	private final List<DeliveryEventWorker> workers;
	private final BlockingQueue<DeliveryEventTask> queue;
	
	private void startWorker() {
		DeliveryEventWorker w = new DeliveryEventWorker(queue);
		w.setName(queueName + ".DEW#" + (workers.size() + 1));
		w.setDaemon(true);
		w.start();
		workers.add(w);
	}
	
	public DeliveryEventWorkerPool(String queueName, int numWorkers) {
		this.queueName = queueName;
		this.numWorkers = numWorkers;
		this.workers = new ArrayList<>();
		this.queue = new LinkedBlockingQueue<>();
		for ( int i = 0; i < numWorkers; i ++ ) {
			startWorker();
		}
	}
	
	public void shutdown() throws InterruptedException {
		for ( int i = 0; i < numWorkers; i ++ ) {
			queue.put(DeliveryEventTask.EXIT);
		}
	}
	
	public void setFinishSignal(CountDownLatch finished) {
		for ( DeliveryEventWorker w : workers ) {
			w.setFinishSignal(finished);
		}
	}
	
	public void enqueue(DeliveryEventTask task) throws InterruptedException {
		queue.put(task);
	}

}
