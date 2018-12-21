package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.probe.ThreadSynchronizer;

public class SchedulerState extends Observable implements ThreadSynchronizer {
	private final Lock lock;
	private final SchedulerSlots slots;
	private final Set<ThreadSynchronizer> synchronizers;
	private SchedulerMode mode;
	private Instant currentTime, cutoffTime;
	private int executionSpeed;
	
	SchedulerState(SchedulerSlots slots, Set<ThreadSynchronizer> synchronizers) {
		this.lock = new ReentrantLock();
		this.slots = slots;
		this.synchronizers = synchronizers;
		mode = SchedulerMode.WAIT;
		currentTime = Instant.EPOCH;
		executionSpeed = 0;
	}
	
	public SchedulerState() {
		this(new SchedulerSlots(), new HashSet<>());
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
	
	void processCommand(Cmd cmd) {
		lock.lock();
		try {
			if ( isClosed() ) {
				throw new IllegalStateException("Scheduler state: " + mode);
			}
			
			switch ( cmd.getType() ) {
			case CLOSE:
				mode = SchedulerMode.CLOSE;
				cutoffTime = null;
				setChanged();
				notifyObservers();
				break;
			case MODE_SWITCH:
				mode = ((CmdModeSwitch) cmd).getMode();
				if ( mode == SchedulerMode.RUN_CUTOFF ) {
					cutoffTime = ((CmdModeSwitchRunCutoff) cmd).getCutoff();
				} else {
					cutoffTime = null;
				}
				setChanged();
				notifyObservers();
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
			case SET_EXECUTION_SPEED:
				executionSpeed = ((CmdSetExecutionSpeed) cmd).getExecutionSpeed();
			default:
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	public boolean isClosed() {
		lock.lock();
		try {
			return mode == SchedulerMode.CLOSE;
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
	
	/**
	 * Test that there are tasks for execution before or exactly at current
	 * time point. In other words is there task which must be executed prior
	 * to traveling to the next time point. 
	 * <p>
	 * @return true if there is task to execute, false otherwise
	 */
	boolean hasSlotForExecution() {
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
	
	private SchedulerSlot getNextSlot() {
		lock.lock();
		try {
			return slots.getNextSlot();
		} finally {
			lock.unlock();
		}
	}
	
	SchedulerSlot removeNextSlot() {
		lock.lock();
		try {
			return slots.removeNextSlot();
		} finally {
			lock.unlock();
		}
	}
	
	void addTask(SchedulerTaskImpl task) {
		lock.lock();
		try {
			slots.addTask(task);
		} finally {
			lock.unlock();
		}
	}
	
	void switchToWait() {
		lock.lock();
		try {
			cutoffTime = null;
			mode = SchedulerMode.WAIT;
			setChanged();
			notifyObservers();
		} finally {
			lock.unlock();
		}
	}
	
	Instant getNextSlotTime() {
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
	Instant getNextTargetTime() {
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
	
	public Set<Instant> getTimeOfSlots() {
		lock.lock();
		try {
			return slots.getTimeOfSlots();
		} finally {
			lock.unlock();
		}
	}
	
	public SchedulerSlot getSlot(Instant time) {
		lock.lock();
		try {
			return slots.getSlot(time);
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
	
	void setMode(SchedulerMode newMode) {
		lock.lock();
		try {
			this.mode = newMode;
		} finally {
			lock.unlock();
		}
	}
	
	public void addSynchronizer(ThreadSynchronizer synchronizer) {
		lock.lock();
		try {
			synchronizers.add(synchronizer);
		} finally {
			lock.unlock();
		}
	}
	
	public void removeSynchronizer(ThreadSynchronizer synchronizer) {
		lock.lock();
		try {
			synchronizers.remove(synchronizer);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void beforeExecution(Instant currentTime) {
		List<ThreadSynchronizer> x;
		lock.lock();
		try {
			x = new ArrayList<>(synchronizers);
		} finally {
			lock.unlock();
		}
		for ( ThreadSynchronizer s : x ) {
			s.beforeExecution(currentTime);
		}
	}

	@Override
	public void afterExecution(Instant currentTime) {
		List<ThreadSynchronizer> x;
		lock.lock();
		try {
			x = new ArrayList<>(synchronizers);
		} finally {
			lock.unlock();
		}
		for ( ThreadSynchronizer s : x ) {
			s.afterExecution(currentTime);
		}
	}

	@Override
	public void waitForThread(Instant currentTime) throws InterruptedException {
		List<ThreadSynchronizer> x;
		lock.lock();
		try {
			x = new ArrayList<>(synchronizers);
		} finally {
			lock.unlock();
		}
		for ( ThreadSynchronizer s : x ) {
			s.waitForThread(currentTime);
		}
	}

}
