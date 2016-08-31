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
	
	SchedulerWorkingPass getWorkingPass() {
		return pass;
	}
	
	SchedulerState getSchedulerState() {
		return state;
	}
	
	@Override
	public void run() {
		try {
			while ( ! state.isClosed() ) {
				pass.execute();
			}
		} catch ( InterruptedException e ) {
			logger.error("Thread interrupted: ", e);
			Thread.currentThread().interrupt();
		}
	}

}
