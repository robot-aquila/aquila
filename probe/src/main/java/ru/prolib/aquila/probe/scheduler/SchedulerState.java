package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerState {
	private final Lock lock;
	private final SchedulerSlots slots;
	private SchedulerMode mode;
	private SchedulerStatus status;
	private Instant currentTime, cutoffTime;
	private int executionSpeed;
	
	public SchedulerState(SchedulerSlots slots) {
		this.lock = new ReentrantLock();
		this.slots = slots;
		mode = SchedulerMode.WAIT;
		status = SchedulerStatus.PAUSED;
		currentTime = Instant.EPOCH;
		executionSpeed = 0;
	}
	
	public SchedulerState() {
		this(new SchedulerSlots());
	}
	
	SchedulerSlots getSchedulerSlots() {
		return slots;
	}
	
	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		lock.unlock();
	}
	
	public void processCommand(Cmd cmd) {
		lock.lock();
		try {
			if ( isClosed() ) {
				throw new IllegalStateException("Scheduler state: " + mode);
			}
			
			switch ( cmd.getType() ) {
			case CLOSE:
				mode = SchedulerMode.CLOSE;
				status = SchedulerStatus.CLOSED;
				cutoffTime = null;
				break;
			case MODE_SWITCH:
				mode = ((CmdModeSwitch) cmd).getMode();
				if ( mode == SchedulerMode.RUN_CUTOFF ) {
					cutoffTime = ((CmdModeSwitchRunCutoff) cmd).getCutoff();
				} else {
					cutoffTime = null;
				}
				break;
			case SHIFT_FORWARD:
				slots.clear();
				currentTime = ((CmdShiftForward) cmd).getTime();
				break;
			case SHIFT_BACKWARD:
				currentTime = ((CmdShiftBackward) cmd).getTime();
				break;
			case SCHEDULE_TASK:
				slots.addTask(((CmdScheduleTask) cmd).getTask());
				break;
			default:
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	public boolean isClosed() {
		lock.lock();
		try {
			return status == SchedulerStatus.CLOSED;
		} finally {
			lock.unlock();
		}
	}
	
	public SchedulerMode getMode() {
		lock.lock();
		try {
			return mode;
		} finally {
			lock.unlock();
		}
	}
	
	public SchedulerStatus getStatus() {
		lock.lock();
		try {
			return status;
		} finally {
			lock.unlock();
		}
	}
	
	public Instant getCurrentTime() {
		lock.lock();
		try {
			return currentTime;
		} finally {
			lock.unlock();
		}
	}
	
	public Instant getCutoffTime() {
		lock.lock();
		try {
			return cutoffTime;
		} finally {
			lock.unlock();
		}
	}
	
	public int getExecutionSpeed() {
		lock.lock();
		try {
			return executionSpeed;
		} finally {
			lock.unlock();
		}
	}
	
	public boolean hasSlotForExecution() {
		lock.lock();
		try {
			SchedulerSlot slot = getNextSlot();
			if ( slot == null ) {
				return false;
			}
			return ! slot.getTime().isAfter(currentTime);
		} finally {
			lock.unlock();
		}
	}
	
	public SchedulerSlot getNextSlot() {
		return slots.getNextSlot();
	}
	
	public void addTask(SchedulerTask task) {
		slots.addTask(task);
	}
	
	public void switchToWait() {
		lock.lock();
		try {
			cutoffTime = null;
			mode = SchedulerMode.WAIT;
			status = SchedulerStatus.PAUSED;
		} finally {
			lock.unlock();
		}
	}
	
	public Instant getNextSlotTime() {
		SchedulerSlot slot = getNextSlot();
		return slot == null ? null : slot.getTime();
	}
	
	/**
	 * Get next target time.
	 * <p>
	 * This method may change current mode. The mode MUST be checked after the
	 * call. In the {@link SchedulerMode#RUN_STEP} mode the mode will be
	 * switched to {@link SchedulerMode#WAIT} if next slot is not available.
	 * In the {@link SchedulerMode#RUN_CUTOFF} mode the mode will be switched
	 * to {@link SchedulerMode#WAIT} if the cut-off time was reached.
	 * <p>
	 * @return the next target time or null if target time cannot be determined
	 * or/and mode was changed.
	 * @throws IllegalStateException - called in wrong mode
	 */
	public Instant getNextTargetTime() {
		lock.lock();
		try {
			switch ( mode ) {
			case RUN_STEP:
				Instant targetTime = getNextSlotTime();
				if ( targetTime == null ) {
					switchToWait();
					return null;
				}
				return targetTime;
			case RUN_CUTOFF:
				if ( cutoffTime.compareTo(currentTime) <= 0 ) {
					switchToWait();
					return null;
				}
				Instant nextSlotTime = getNextSlotTime();
				if ( nextSlotTime != null && nextSlotTime.compareTo(cutoffTime) <= 0 ) {
					return nextSlotTime;
				}
				return cutoffTime;
			case RUN:
				return getNextSlotTime();
			default:
				throw new IllegalStateException("Unexpected mode: " + mode);
			}
		} finally {
			lock.unlock();
		}
	}
	
	public boolean isModeWait() {
		lock.lock();
		try {
			return mode == SchedulerMode.WAIT;
		} finally {
			lock.unlock();
		}
	}
	
	void setCurrentTime(Instant newTime) {
		lock.lock();
		try {
			this.currentTime = newTime;
		} finally {
			lock.unlock();
		}
	}
	
	void setCutoffTime(Instant newTime) {
		lock.lock();
		try {
			this.cutoffTime = newTime;
		} finally {
			lock.unlock();
		}
	}
	
	void setStatus(SchedulerStatus newStatus) {
		lock.lock();
		try {
			this.status = newStatus;
		} finally {
			lock.unlock();
		}
	}
	
	void setMode(SchedulerMode newMode) {
		lock.lock();
		try {
			this.mode = newMode;
		} finally {
			lock.unlock();
		}
	}

}
