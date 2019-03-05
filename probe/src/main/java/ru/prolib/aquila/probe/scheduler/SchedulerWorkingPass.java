package ru.prolib.aquila.probe.scheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SchedulerWorkingPass {
	private static final long MIN_DELAY = 1;
	private static final long QUANT = 100;
	
	static class Helper {
		private final BlockingQueue<Cmd> queue;
		private final SchedulerState state;
		private final Clock clock;
		
		public Helper(BlockingQueue<Cmd> queue,
				SchedulerState state,
				Clock clock)
		{
			this.queue = queue;
			this.state = state;
			this.clock = clock;
		}
		
		/**
		 * Simulate the time flow to specified time point.
		 * <p>
		 * Note: due to potential command processing this call
		 * may cause change of state.
		 * <p>
		 * @param targetTime - target time
		 * @return true if target time is reached, false otherwise
		 * @throws InterruptedException thread was interrupted
		 */
		boolean travelTo(Instant targetTime) throws InterruptedException {
			int speed = state.getExecutionSpeed();
			if ( speed == 0 ) {
				// Zero execution speed: shift the time
				// if the target time is defined.
				if ( targetTime != null ) {
					state.setCurrentTime(targetTime);
					return true;
				} else {
					return false;
				}
			}
			
			// We will move forward in any case.
			// Fix the target time then.
			if ( targetTime == null ) {
				targetTime = Instant.MAX;
			}
			Instant current_time = state.getCurrentTime();
			long simu_delay = QUANT;
			if ( ChronoUnit.DAYS.between(current_time, targetTime) <= 1 ) {
				simu_delay = ChronoUnit.MILLIS.between(current_time, targetTime);
				if ( simu_delay > QUANT) {
					simu_delay = QUANT;
				}
			}
			
			long real_delay = simu_delay / speed;
			if ( real_delay >= MIN_DELAY ) {
				long t1 = clock.millis();
				Cmd cmd = queue.poll(real_delay, TimeUnit.MILLISECONDS);
				long t2 = clock.millis();
				if ( cmd == null ) {
					state.setCurrentTime(current_time = current_time.plusMillis(simu_delay));				
				} else {
					state.setCurrentTime(current_time = current_time.plusMillis((t2 - t1) * speed));
					state.processCommand(cmd);
				}
			} else {
				state.setCurrentTime(current_time = current_time.plusMillis(simu_delay));
			}
			
			return current_time.compareTo(targetTime) >= 0;
		}
		
		/**
		 * Execute all tasks scheduled for execution at next time point.
		 * <p>
		 * @return true if at least one task was executed, false otherwise
		 * @throws InterruptedException thread was interrupted
		 */
		boolean executeTasks() throws InterruptedException {
			Instant curr_time = state.getCurrentTime();
			if ( ! state.hasSlotForExecution() ) {
				return false;
			}
			SchedulerSlot slot = state.removeNextSlot();
			state.beforeExecution(curr_time);
			for ( SchedulerTaskImpl task : slot.getTasks() ) {
				// DO NOT LOCK THE TASK! WILL CAUSE DEADLOCKS!
				if ( task.isScheduled() ) {
					task.execute();
					if ( task.isScheduled() ) {
						// It may be only a periodic and not cancelled task.
						task.scheduleForNextExecution(curr_time);
						state.addTask(task);
					}
				}
			}
			slot.clearTasks();
			state.afterExecution(curr_time);
			state.waitForThread(curr_time);
			if ( state.getMode() == SchedulerMode.RUN_STEP ) {
				state.switchToWait();
			}
			return true;
		}
		
	}

	private final BlockingQueue<Cmd> queue;
	private final SchedulerState state;
	private final Helper helper;
	
	public SchedulerWorkingPass(BlockingQueue<Cmd> queue,
			SchedulerState state,
			Helper helper)
	{
		this.queue = queue;
		this.state = state;
		this.helper = helper;
	}
	
	public SchedulerWorkingPass(BlockingQueue<Cmd> queue, SchedulerState state, Clock clock) {
		this(queue, state, new Helper(queue, state, clock));
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

		Instant targetTime = state.getNextTargetTime();
		switch ( state.getMode() ) {
		case RUN:
		case RUN_STEP:
			if ( helper.travelTo(targetTime) ) {
				helper.executeTasks();
			}
			break;
		case RUN_CUTOFF:
			boolean target_is_cutoff = state.getCutoffTime().equals(targetTime);
			if ( helper.travelTo(targetTime) ) {
				if ( target_is_cutoff ) {
					state.switchToWait();
				} else {
					helper.executeTasks();
				}
			}
			break;
		case WAIT:
		case CLOSE:
		default:
			break;
		}
	}

}
