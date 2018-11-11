package ru.prolib.aquila.core.eque;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeliveryEventWorker extends Thread {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DeliveryEventWorker.class);
	}
	
	private final BlockingQueue<DeliveryEventTask> queue;
	private CountDownLatch finished;
	
	public DeliveryEventWorker(BlockingQueue<DeliveryEventTask> queue) {
		this.queue = queue;
	}
	
	public synchronized void setFinishSignal(CountDownLatch finished) {
		this.finished = finished;
	}

	@Override
	public void run() {
		try {
			DeliveryEventTask task = null;
			while ( (task = queue.take()) != null ) {
				if ( task == DeliveryEventTask.EXIT ) {
					break;
				}
				task.call();
				CountDownLatch x = null;
				synchronized ( this ) {
					x = finished;
				}
				x.countDown();
			}
		} catch ( InterruptedException e ) {
			logger.error("Interrupted: ", e);
			Thread.currentThread().interrupt();
		}
	}

}
