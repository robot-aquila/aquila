package ru.prolib.aquila.probe.scheduler;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerWorker implements Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SchedulerWorker.class);
	}

	private final SchedulerWorkingPass pass;
	private final SchedulerState state;
	
	SchedulerWorker(SchedulerWorkingPass pass, SchedulerState state) {
		this.pass = pass;
		this.state = state;
	}
	
	public SchedulerWorker(BlockingQueue<Cmd> queue, SchedulerState state) {
		this(new SchedulerWorkingPass(queue, state), state);
	}
	
	public SchedulerWorkingPass getWorkingPass() {
		return pass;
	}
	
	public SchedulerState getSchedulerState() {
		return state;
	}
	
	@Override
	public void run() {
		logger.debug("Worker thread started");
		try {
			while ( ! state.isClosed() ) {
				pass.execute();
			}
		} catch ( InterruptedException e ) {
			logger.error("Thread interrupted: ", e);
			Thread.currentThread().interrupt();
		}
		logger.debug("Worker thread finished");
	}

}
