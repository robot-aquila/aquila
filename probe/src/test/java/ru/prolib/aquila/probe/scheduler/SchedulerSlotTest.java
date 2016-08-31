package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SchedulerSlotTest {
	private IMocksControl control;
	private SchedulerTask task1, task2, task3;
	private SchedulerSlot slot;

	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		task1 = control.createMock(SchedulerTask.class);
		task2 = control.createMock(SchedulerTask.class);
		task3 = control.createMock(SchedulerTask.class);
		slot = new SchedulerSlot(T("2016-08-26T15:10:00Z"));
	}

	@Test
	public void testCtor() {
		List<SchedulerTask> expected = new ArrayList<>();
		assertEquals(T("2016-08-26T15:10:00Z"), slot.getTime());
		assertEquals(expected, slot.getTasks());
	}
	
	@Test
	public void testAddTask() {
		assertSame(slot, slot.addTask(task1));
		assertSame(slot, slot.addTask(task2));

		List<SchedulerTask> expected = new ArrayList<>();
		expected.add(task1);
		expected.add(task2);
		assertEquals(expected, slot.getTasks());
		
		assertSame(slot, slot.addTask(task3));
		
		expected.clear();
		expected.add(task1);
		expected.add(task2);
		expected.add(task3);
		assertEquals(expected, slot.getTasks());
	}
	
	@Test
	public void testClearTasks() {
		slot.addTask(task2);
		slot.addTask(task3);
		
		assertSame(slot, slot.clearTasks());
		
		List<SchedulerTask> expected = new ArrayList<>();
		assertEquals(expected, slot.getTasks());
	}
	
}
