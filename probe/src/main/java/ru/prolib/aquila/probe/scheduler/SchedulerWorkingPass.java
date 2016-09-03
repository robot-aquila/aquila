package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SchedulerWorkingPass {
	private static final long MIN_DELAY = 1;
	private static final long MIN_QUANT = 100;
	private final BlockingQueue<Cmd> queue;
	private final SchedulerState state;
	
	public SchedulerWorkingPass(BlockingQueue<Cmd> queue, SchedulerState state) {
		this.queue = queue;
		this.state = state;
	}
	
	public BlockingQueue<Cmd> getCommandQueue() {
		return queue;
	}
	
	public SchedulerState getSchedulerState() {
		return state;
	}
	
	public void execute() throws InterruptedException {
		Cmd cmd = queue.poll();
		if ( cmd != null ) {
			state.processCommand(cmd);
			return;
		}
		
		switch ( state.getMode() ) {
		case CLOSE:
			return;
		case WAIT:
			state.processCommand(queue.take());
			return;
		case RUN_STEP:
		case RUN_CUTOFF:
		case RUN:
		default:
			break;
		}

		Instant currentTime = state.getCurrentTime();
		if ( state.hasSlotForExecution() ) {
			SchedulerSlot slot = state.removeNextSlot();
			for ( SchedulerTask task : slot.getTasks() ) {
				if ( task.isScheduled() ) {
					task.execute();
					if ( task.isScheduled() ) {
						// It may be only a periodic and not cancelled task.
						task.scheduleForNextExecution(currentTime);
						state.addTask(task);
					}
				}
			}
			slot.clearTasks();
			if ( state.getMode() == SchedulerMode.RUN_STEP ) {
				state.switchToWait();
			}
			return;
		}
		
		Instant targetTime = state.getNextTargetTime();
		if ( state.isModeWait() ) {
			return;
		}
		int speed = state.getExecutionSpeed();
		if ( speed == 0 ) {
			// Zero execution speed: shift the time
			// if the target time is defined.
			if ( targetTime != null ) {
				state.setCurrentTime(targetTime);
			}
			return;
		}
		
		// We will move forward in any case.
		// Fix the target time then.
		if ( targetTime == null ) {
			targetTime = Instant.MAX;
		}
		long targetDelay = MIN_QUANT;
		if ( ChronoUnit.DAYS.between(currentTime, targetTime) <= 1 ) {
			targetDelay = ChronoUnit.MILLIS.between(currentTime, targetTime);
			if ( targetDelay > MIN_QUANT) {
				targetDelay = MIN_QUANT;
			}
		}

		long realtimeDelay = targetDelay / speed;
		cmd = null;
		if ( realtimeDelay >= MIN_DELAY ) {
			cmd = queue.poll(realtimeDelay, TimeUnit.MILLISECONDS);
		}
		state.setCurrentTime(currentTime.plusMillis(targetDelay));
		if ( cmd != null ) {
			state.processCommand(cmd);
		}
	}

}
