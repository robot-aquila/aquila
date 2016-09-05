package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SchedulerSlots {
	private LinkedList<SchedulerSlot> slotList;
	private Map<Instant, SchedulerSlot> slotMap;
	
	SchedulerSlots(LinkedList<SchedulerSlot> slotList, Map<Instant, SchedulerSlot> slotMap) {
		this.slotList = slotList;
		this.slotMap = slotMap;
	}
	
	public SchedulerSlots() {
		this(new LinkedList<>(), new HashMap<>());
	}
	
	/**
	 * Add scheduled task to an appropriate slot.
	 * <p>
	 * The task must be assigned to execution.
	 * <p> 
	 * @param task - the task to add
	 * @throws IllegalArgumentException - the task is not scheduled for execution
	 */
	public void addTask(SchedulerTask task) {
		Instant slotTime = task.getNextExecutionTime();
		if ( slotTime == null ) {
			throw new IllegalArgumentException("Task must be scheduled for execution");
		}
		getOrCreateSlot(slotTime).addTask(task);
	}
	
	/**
	 * Retrieves the next slot scheduled for the execution.
	 * <p>
	 * @return the slot or null if no slot available
	 */
	public SchedulerSlot getNextSlot() {
		return slotList.size() > 0 ? slotList.getFirst() : null;
	}

	/**
	 * Retrieves and removes the next slot scheduled for the execution.
	 * <p>
	 * @return the next slot
	 */
	public SchedulerSlot removeNextSlot() {
		if ( slotList.size() > 0 ) {
			SchedulerSlot slot = slotList.removeFirst();
			slotMap.remove(slot.getTime());
			Collections.sort(slotList);
			return slot;
		} else {
			return null;
		}
	}
	
	/**
	 * Clear all data.
	 */
	public void clear() {
		slotList.clear();
		slotMap.clear();
	}
	
	/**
	 * Get list of time of all existing slots.
	 * <p>
	 * @return the set of time instants which have at least one scheduled task.
	 */
	public Set<Instant> getTimeOfSlots() {
		return new HashSet<>(slotMap.keySet());
	}
	
	/**
	 * Get slot at specified time point.
	 * <p>
	 * @param time - the time point to get a slot at
	 * @return the slot instance or null if no slot at the time point
	 */
	public SchedulerSlot getSlot(Instant time) {
		return slotMap.get(time);
	}
	
	private SchedulerSlot getOrCreateSlot(Instant slotTime) {
		SchedulerSlot slot = slotMap.get(slotTime);
		if ( slot == null ) {
			slot = new SchedulerSlot(slotTime);
			slotMap.put(slotTime, slot);
			if ( slotList.size() == 0 ) {
				slotList.add(slot);
			} else {
				SchedulerSlot headSlot = slotList.get(0);
				if ( slotTime.compareTo(headSlot.getTime()) < 0 ) {
					slotList.addFirst(slot);
				} else {
					slotList.add(slot);
				}
			}
		}
		return slot;
	}

}
