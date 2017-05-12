package ru.prolib.aquila.probe.scheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SchedulerWorkingPass {
	private static final long MIN_DELAY = 1;
	private static final long MIN_QUANT = 100;
	private final BlockingQueue<Cmd> queue;
	private final SchedulerState state;
	private final Clock clock;
	
	public SchedulerWorkingPass(BlockingQueue<Cmd> queue, SchedulerState state, Clock clock) {
		this.queue = queue;
		this.state = state;
		this.clock = clock;
	}
	
	public SchedulerWorkingPass(BlockingQueue<Cmd> queue, SchedulerState state) {
		this(queue, state, Clock.systemUTC());
	}
	
	public BlockingQueue<Cmd> getCommandQueue() {
		return queue;
	}
	
	public SchedulerState getSchedulerState() {
		return state;
	}
	
	public Clock getClock() {
		return clock;
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
			cmd = queue.take();
			state.processCommand(cmd);
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
			for ( SchedulerTaskImpl task : slot.getTasks() ) {
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
		if ( realtimeDelay >= MIN_DELAY ) {
			long t1 = clock.millis();
			cmd = queue.poll(realtimeDelay, TimeUnit.MILLISECONDS);
			long t2 = clock.millis();
			if ( cmd == null ) {
				state.setCurrentTime(currentTime.plusMillis(targetDelay));				
			} else {
				state.setCurrentTime(currentTime.plusMillis((t2 - t1) * speed));
				state.processCommand(cmd);
			}
		} else {
			state.setCurrentTime(currentTime.plusMillis(targetDelay));
		}
	}

}
