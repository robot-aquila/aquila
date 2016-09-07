package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SchedulerSlotsTest {
	
	public static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private Runnable runnable1Mock, runnable2Mock, runnable3Mock; 
	private LinkedList<SchedulerSlot> actualSlotList;
	private Map<Instant, SchedulerSlot> actualSlotMap;
	private SchedulerSlots slots;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		runnable1Mock = control.createMock(Runnable.class);
		runnable2Mock = control.createMock(Runnable.class);
		runnable3Mock = control.createMock(Runnable.class);
		actualSlotList = new LinkedList<>();
		actualSlotMap = new HashMap<>();
		slots = new SchedulerSlots(actualSlotList, actualSlotMap);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddTask_ThrowsIfTaskNotScheduled() {
		slots.addTask(new SchedulerTaskImpl(runnable1Mock));
	}
	
	@Test
	public void testAddTask() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:15:00Z"));
		
		slots.addTask(task1);
		
		LinkedList<SchedulerSlot> expectedSlotList = new LinkedList<>();
		SchedulerSlot expectedSlot = new SchedulerSlot(T("2016-08-29T18:15:00Z")).addTask(task1); 
		expectedSlotList.add(expectedSlot);
		assertEquals(expectedSlotList, actualSlotList);
		assertEquals(expectedSlot, slots.getNextSlot());
		Map<Instant, SchedulerSlot> expectedSlotMap = new HashMap<>();
		expectedSlotMap.put(T("2016-08-29T18:15:00Z"), expectedSlotList.get(0));
		assertEquals(expectedSlotMap, actualSlotMap);
	}
	
	@Test
	public void testAddTask_DeterminesHeadSlot() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		SchedulerTaskImpl task2 = new SchedulerTaskImpl(runnable2Mock);
		SchedulerTaskImpl task3 = new SchedulerTaskImpl(runnable3Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:25:00Z"));
		task2.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		task3.scheduleForFirstExecution(T("2016-08-29T18:30:00Z"));
		
		slots.addTask(task1);
		slots.addTask(task2);
		slots.addTask(task3);
		
		LinkedList<SchedulerSlot> expectedSlotList = new LinkedList<>();
		SchedulerSlot expectedSlot = new SchedulerSlot(T("2016-08-29T18:20:00Z")).addTask(task2);
		expectedSlotList.add(expectedSlot);
		expectedSlotList.add(new SchedulerSlot(T("2016-08-29T18:25:00Z")).addTask(task1));
		expectedSlotList.add(new SchedulerSlot(T("2016-08-29T18:30:00Z")).addTask(task3));
		assertEquals(expectedSlotList, actualSlotList);
		assertEquals(expectedSlot, slots.getNextSlot());
		Map<Instant, SchedulerSlot> expectedSlotMap = new HashMap<>();
		expectedSlotMap.put(T("2016-08-29T18:20:00Z"), expectedSlotList.get(0));
		expectedSlotMap.put(T("2016-08-29T18:25:00Z"), expectedSlotList.get(1));
		expectedSlotMap.put(T("2016-08-29T18:30:00Z"), expectedSlotList.get(2));
		assertEquals(expectedSlotMap, actualSlotMap);
	}
	
	@Test
	public void testAddTask_AppendToExistingSlot() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		SchedulerTaskImpl task2 = new SchedulerTaskImpl(runnable2Mock);
		SchedulerTaskImpl task3 = new SchedulerTaskImpl(runnable3Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		task2.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		task3.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		
		slots.addTask(task1);
		slots.addTask(task2);
		slots.addTask(task3);
		
		LinkedList<SchedulerSlot> expectedSlotList = new LinkedList<>();
		SchedulerSlot expectedSlot = new SchedulerSlot(T("2016-08-29T18:20:00Z"))
			.addTask(task1)
			.addTask(task2)
			.addTask(task3);
		expectedSlotList.add(expectedSlot);
		assertEquals(expectedSlotList, actualSlotList);
		assertEquals(expectedSlot, slots.getNextSlot());
		Map<Instant, SchedulerSlot> expectedSlotMap = new HashMap<>();
		expectedSlotMap.put(T("2016-08-29T18:20:00Z"), expectedSlot);
		assertEquals(expectedSlotMap, actualSlotMap);
	}

	@Test
	public void testRemoveNextSlot() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		SchedulerTaskImpl task2 = new SchedulerTaskImpl(runnable2Mock);
		SchedulerTaskImpl task3 = new SchedulerTaskImpl(runnable3Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:25:00Z"));
		task2.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		task3.scheduleForFirstExecution(T("2016-08-29T18:30:00Z"));
		slots.addTask(task1);
		slots.addTask(task2);
		slots.addTask(task3);
		List<SchedulerSlot> expectedSlotList = new ArrayList<>();
		expectedSlotList.add(new SchedulerSlot(T("2016-08-29T18:20:00Z")).addTask(task2));
		expectedSlotList.add(new SchedulerSlot(T("2016-08-29T18:25:00Z")).addTask(task1));
		expectedSlotList.add(new SchedulerSlot(T("2016-08-29T18:30:00Z")).addTask(task3));
		
		List<SchedulerSlot> removedSlotList = new ArrayList<>();
		SchedulerSlot slot = null;
		while ( (slot = slots.removeNextSlot()) != null ) {
			removedSlotList.add(slot);
		}
		
		assertEquals(expectedSlotList, removedSlotList);
		assertEquals(new LinkedList<SchedulerSlot>(), actualSlotList);
		assertEquals(new HashMap<Instant, SchedulerSlot>(), actualSlotMap);
		assertNull(slots.getNextSlot());
		assertNull(slots.removeNextSlot());
	}
	
	@Test
	public void testRemoveNextSlot_SortsRemainingSlots() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		SchedulerTaskImpl task2 = new SchedulerTaskImpl(runnable2Mock);
		SchedulerTaskImpl task3 = new SchedulerTaskImpl(runnable3Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		task2.scheduleForFirstExecution(T("2016-08-29T18:30:00Z"));
		task3.scheduleForFirstExecution(T("2016-08-29T18:25:00Z"));
		slots.addTask(task1);
		slots.addTask(task2);
		slots.addTask(task3);
		
		assertNotNull(slots.removeNextSlot());
		
		List<SchedulerSlot> expectedSlotList = new ArrayList<>();
		expectedSlotList.add(new SchedulerSlot(T("2016-08-29T18:25:00Z")).addTask(task3));
		expectedSlotList.add(new SchedulerSlot(T("2016-08-29T18:30:00Z")).addTask(task2));
		assertEquals(expectedSlotList, actualSlotList);
		assertEquals(expectedSlotList.get(0), slots.getNextSlot());
		Map<Instant, SchedulerSlot> expectedSlotMap = new HashMap<>();
		expectedSlotMap.put(T("2016-08-29T18:25:00Z"), expectedSlotList.get(0));
		expectedSlotMap.put(T("2016-08-29T18:30:00Z"), expectedSlotList.get(1));
		assertEquals(expectedSlotMap, actualSlotMap);
	}
	
	@Test
	public void testClear() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		SchedulerTaskImpl task2 = new SchedulerTaskImpl(runnable2Mock);
		SchedulerTaskImpl task3 = new SchedulerTaskImpl(runnable3Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		task2.scheduleForFirstExecution(T("2016-08-29T18:30:00Z"));
		task3.scheduleForFirstExecution(T("2016-08-29T18:25:00Z"));
		slots.addTask(task1);
		slots.addTask(task2);
		slots.addTask(task3);
		
		slots.clear();
		
		assertEquals(new ArrayList<SchedulerSlot>(), actualSlotList);
		assertEquals(new HashMap<Instant, SchedulerSlot>(), actualSlotMap);
	}
	
	@Test
	public void testGetTimeOfSlots() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		SchedulerTaskImpl task2 = new SchedulerTaskImpl(runnable2Mock);
		SchedulerTaskImpl task3 = new SchedulerTaskImpl(runnable3Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:25:00Z"));
		task2.scheduleForFirstExecution(T("2016-08-29T18:20:00Z"));
		task3.scheduleForFirstExecution(T("2016-08-29T18:30:00Z"));
		slots.addTask(task1);
		slots.addTask(task2);
		slots.addTask(task3);
		
		Set<Instant> actual = slots.getTimeOfSlots();
		
		Set<Instant> expected = new HashSet<>();
		expected.add(T("2016-08-29T18:25:00Z"));
		expected.add(T("2016-08-29T18:20:00Z"));
		expected.add(T("2016-08-29T18:30:00Z"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSlot() {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock);
		SchedulerTaskImpl task2 = new SchedulerTaskImpl(runnable2Mock);
		SchedulerTaskImpl task3 = new SchedulerTaskImpl(runnable3Mock);
		task1.scheduleForFirstExecution(T("2016-08-29T18:25:00Z"));
		task2.scheduleForFirstExecution(T("2016-08-29T18:25:00Z"));
		task3.scheduleForFirstExecution(T("2016-08-29T18:30:00Z"));
		slots.addTask(task1);
		slots.addTask(task2);
		slots.addTask(task3);
		
		assertNull(slots.getSlot(T("2016-08-29T18:15:00Z")));
		SchedulerSlot actual = slots.getSlot(T("2016-08-29T18:25:00Z"));
		
		SchedulerSlot expected = new SchedulerSlot(T("2016-08-29T18:25:00Z"))
			.addTask(task1)
			.addTask(task2);
		assertEquals(expected, actual);
	}

}
