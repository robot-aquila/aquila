package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SchedulerStateTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static class WaitForModeChange implements Observer {
		private final SchedulerMode expectedMode;
		private final CountDownLatch signal;
		
		WaitForModeChange(SchedulerMode expectedMode) {
			this.expectedMode = expectedMode;
			this.signal = new CountDownLatch(1);
		}

		@Override
		public void update(Observable observable, Object unused) {
			observable.deleteObserver(this);
			final SchedulerState state = (SchedulerState) observable;
			if ( state.getMode() == expectedMode ) {
				signal.countDown();
			}
		}
		
	}
	
	private IMocksControl control;
	private Observer observerMock;
	private SchedulerSlots slotsMock;
	private SchedulerState state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observerMock = control.createMock(Observer.class);
		slotsMock = control.createMock(SchedulerSlots.class);
		state = new SchedulerState(slotsMock);
	}
	
	@Test
	public void testCtor1() {
		assertSame(slotsMock, state.getSchedulerSlots());
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(Instant.EPOCH, state.getCurrentTime());
		assertNull(state.getCutoffTime());
		assertEquals(0, state.getExecutionSpeed());
	}
	
	@Test
	public void testCtor0() {
		state = new SchedulerState();
		assertNotNull(state.getSchedulerSlots());
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(Instant.EPOCH, state.getCurrentTime());
		assertNull(state.getCutoffTime());
		assertEquals(0, state.getExecutionSpeed());
	}
	
	@Test
	public void testProcessCommand_Close() throws Exception {
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.CLOSE);
		state.addObserver(wait);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		control.replay();

		
		state.processCommand(new CmdClose());
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.CLOSE, state.getMode());
		assertNull(state.getCutoffTime());
	}
	
	@Test
	public void testProcessCommand_ThrowsIfClosed() {
		state.processCommand(new CmdClose());
		
		Cmd list[] = {
				new CmdClose(),
				new CmdModeSwitchRun(),
				new CmdModeSwitchRunCutoff(T("2016-08-30T00:00:00Z")),
				new CmdModeSwitchRunStep(),
				new CmdModeSwitchWait(),
				new CmdScheduleTask(control.createMock(SchedulerTask.class)),
				new CmdShiftBackward(Instant.EPOCH),
				new CmdShiftForward(T("2048-01-01T00:00:00Z"))
		};
		for ( int i = 0; i < list.length; i ++) {
			try {
				state.processCommand(list[i]);
				fail("At #" + i + "Expected exception: "
						+ IllegalStateException.class.getSimpleName());
			} catch ( IllegalStateException e ) { }
		}
	}
	
	@Test
	public void testProcessCommand_ModeSwitch_Run() throws Exception {
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.RUN);
		state.addObserver(wait);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		control.replay();
		
		state.processCommand(new CmdModeSwitchRun());
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.RUN, state.getMode());
		assertNull(state.getCutoffTime());
	}
	
	@Test
	public void testProcessCommand_ModeSwitch_RunCutoff() throws Exception {
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.RUN_CUTOFF);
		state.addObserver(wait);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		control.replay();
		
		state.processCommand(new CmdModeSwitchRunCutoff(T("2050-08-01T00:00:00Z")));
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.RUN_CUTOFF, state.getMode());
		assertEquals(T("2050-08-01T00:00:00Z"), state.getCutoffTime());
	}
	
	@Test
	public void testProcessCommand_ModeSwitch_RunStep() throws Exception {
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.RUN_STEP);
		state.addObserver(wait);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		control.replay();
		
		state.processCommand(new CmdModeSwitchRunStep());
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.RUN_STEP, state.getMode());
		assertNull(state.getCutoffTime());
	}
	
	@Test
	public void testProcessCommand_ModeSwitch_Wait() throws Exception {
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		control.replay();
		
		state.processCommand(new CmdModeSwitchWait());
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertNull(state.getCutoffTime());
	}
	
	@Test
	public void testProcessCommand_ScheduleTask() {
		state.addObserver(observerMock);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		SchedulerTask taskMock = control.createMock(SchedulerTask.class);
		slotsMock.addTask(taskMock);
		control.replay();
		
		state.processCommand(new CmdScheduleTask(taskMock));
		
		control.verify();
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2016-08-31T00:00:00Z"), state.getCutoffTime());
	}
	
	@Test
	public void testProcessCommand_ShiftBackward() {
		state.addObserver(observerMock);
		state.setCurrentTime(T("2016-01-01T00:00:00Z"));
		control.replay();
		
		state.processCommand(new CmdShiftBackward(T("2015-01-01T00:00:00Z")));
		
		control.verify();
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2015-01-01T00:00:00Z"), state.getCurrentTime());
	}
	
	@Test
	public void testProcessCommand_ShiftForward() {
		state.addObserver(observerMock);
		state.setCurrentTime(T("2016-01-01T00:00:00Z"));
		slotsMock.clear();
		control.replay();
		
		state.processCommand(new CmdShiftForward(T("2025-01-01T00:00:00Z")));
		
		control.verify();
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2025-01-01T00:00:00Z"), state.getCurrentTime());
	}
	
	@Test
	public void testProcessCommand_SetExecutionSpeed() {
		state.addObserver(observerMock);
		control.replay();
		
		state.processCommand(new CmdSetExecutionSpeed(2));
		
		control.verify();
		assertEquals(2, state.getExecutionSpeed());
	}
	
	@Test
	public void testIsClosed() {
		state.setMode(SchedulerMode.WAIT);
		control.replay();
		
		assertFalse(state.isClosed());
		
		state.setMode(SchedulerMode.CLOSE);
		
		assertTrue(state.isClosed());
		
		control.verify();
	}
	
	@Test
	public void testHasSlotForExecution_NoSlot() {
		expect(slotsMock.getNextSlot()).andReturn(null);
		control.replay();
		
		assertFalse(state.hasSlotForExecution());
		
		control.verify();
	}

	@Test
	public void testHasSlotForExecution_HasSlotGtCurrentTime() {
		state.setCurrentTime(T("2016-08-31T03:10:30Z"));
		expect(slotsMock.getNextSlot()).andReturn(new SchedulerSlot(T("2016-08-31T23:59:59Z")));
		control.replay();
		
		assertFalse(state.hasSlotForExecution());
		
		control.verify();
	}
	
	@Test
	public void testHasSlotForExecution_HasSlotEqCurrentTime() {
		state.setCurrentTime(T("2016-08-31T03:10:30Z"));
		expect(slotsMock.getNextSlot()).andReturn(new SchedulerSlot(T("2016-08-31T03:10:30Z")));
		control.replay();
		
		assertTrue(state.hasSlotForExecution());
		
		control.verify();
	}
	
	@Test
	public void testHasSlotForExecution_HasSlotLtCurrentTime() {
		state.setCurrentTime(T("2016-08-31T03:10:30Z"));
		expect(slotsMock.getNextSlot()).andReturn(new SchedulerSlot(T("2016-08-31T00:00:00Z")));
		control.replay();
		
		assertTrue(state.hasSlotForExecution());
		
		control.verify();
	}
	
	@Test
	public void testRemoveNextSlot() {
		SchedulerSlot slot = new SchedulerSlot(T("1978-12-01T13:48:12Z"));
		expect(slotsMock.removeNextSlot()).andReturn(slot);
		control.replay();
		
		assertEquals(slot, state.removeNextSlot());
		
		control.verify();
	}
	
	@Test
	public void testAddTask() {
		SchedulerTask taskMock = control.createMock(SchedulerTask.class);
		slotsMock.addTask(taskMock);
		control.replay();
		
		state.addTask(taskMock);
		
		control.verify();
	}
	
	@Test
	public void testSwitchToWait() throws Exception {
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		state.setCutoffTime(T("2016-08-31T02:08:46Z"));
		state.setMode(SchedulerMode.RUN_STEP);
		control.replay();
		
		state.switchToWait();
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertNull(state.getCutoffTime());
		assertEquals(SchedulerMode.WAIT, state.getMode());
	}
	
	@Test
	public void testGetNextSlotTime() {
		SchedulerSlot slot = new SchedulerSlot(T("2017-06-02T00:00:00Z"));
		expect(slotsMock.getNextSlot()).andReturn(slot);
		control.replay();
		
		assertEquals(T("2017-06-02T00:00:00Z"), state.getNextSlotTime());
		
		control.verify();
	}
	
	@Test
	public void testGetNextSlotTime_NoSlot() {
		expect(slotsMock.getNextSlot()).andReturn(null);
		control.replay();
		
		assertNull(state.getNextSlotTime());
		
		control.verify();
	}
	
	@Test
	public void testGetNextTargetTime_RunStep_HasSlot() {
		state.setMode(SchedulerMode.RUN_STEP);
		SchedulerSlot slot = new SchedulerSlot(T("2016-08-31T02:45:00Z"));
		expect(slotsMock.getNextSlot()).andReturn(slot);
		state.addObserver(observerMock);
		control.replay();
		
		assertEquals(T("2016-08-31T02:45:00Z"), state.getNextTargetTime());
		
		control.verify();
	}
	
	@Test
	public void testGetNextTargetTime_RunStep_NoSlot() throws Exception {
		state.setMode(SchedulerMode.RUN_STEP);
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		expect(slotsMock.getNextSlot()).andReturn(null);
		control.replay();
		
		assertNull(state.getNextTargetTime());
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.WAIT, state.getMode());
	}
	
	@Test
	public void testGetNextTargetTime_RunCutoff_CutoffLtCurrent() throws Exception {
		state.setMode(SchedulerMode.RUN_CUTOFF);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		state.setCurrentTime(T("2016-08-31T00:00:01Z"));
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		control.replay();
		
		assertNull(state.getNextTargetTime());
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertNull(state.getCutoffTime());
	}
	
	@Test
	public void testGetNextTargetTime_RunCutoff_CutoffEqCurrent() throws Exception {
		state.setMode(SchedulerMode.RUN_CUTOFF);
		state.setCutoffTime(T("2016-08-31T00:00:00Z"));
		state.setCurrentTime(T("2016-08-31T00:00:00Z"));
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		control.replay();
		
		assertNull(state.getNextTargetTime());
		
		control.verify();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertNull(state.getCutoffTime());
	}
	
	@Test
	public void testGetNextTargetTime_RunCutoff_CutoffGtCurrent_NoSlot() {
		state.setMode(SchedulerMode.RUN_CUTOFF);
		state.setCutoffTime(T("2016-08-31T00:00:01Z"));
		state.setCurrentTime(T("2016-08-31T00:00:00Z"));
		expect(slotsMock.getNextSlot()).andReturn(null);
		state.addObserver(observerMock);
		control.replay();
		
		assertEquals(T("2016-08-31T00:00:01Z"), state.getNextTargetTime());
		
		control.verify();
		assertEquals(SchedulerMode.RUN_CUTOFF, state.getMode());
		assertEquals(T("2016-08-31T00:00:01Z"), state.getCutoffTime());
	}
	
	@Test
	public void testGetNextTargetTime_RunCutoff_CutoffGtCurrent_HasSlotLtCutoff() {
		SchedulerSlot slot = new SchedulerSlot(T("2016-08-31T00:00:00.500Z"));
		state.setMode(SchedulerMode.RUN_CUTOFF);
		state.setCutoffTime(T("2016-08-31T00:00:01Z"));
		state.setCurrentTime(T("2016-08-31T00:00:00Z"));
		expect(slotsMock.getNextSlot()).andReturn(slot);
		state.addObserver(observerMock);
		control.replay();
		
		assertEquals(T("2016-08-31T00:00:00.500Z"), state.getNextTargetTime());
		
		control.verify();
		assertEquals(SchedulerMode.RUN_CUTOFF, state.getMode());
		assertEquals(T("2016-08-31T00:00:01Z"), state.getCutoffTime());
	}
	
	@Test
	public void testGetNextTargetTime_RunCutoff_CutoffGtCurrent_HasSlotGtCutoff() {
		SchedulerSlot slot = new SchedulerSlot(T("2016-08-31T00:00:01.500Z"));
		state.setMode(SchedulerMode.RUN_CUTOFF);
		state.setCutoffTime(T("2016-08-31T00:00:01Z"));
		state.setCurrentTime(T("2016-08-31T00:00:00Z"));
		expect(slotsMock.getNextSlot()).andReturn(slot);
		state.addObserver(observerMock);
		control.replay();
		
		assertEquals(T("2016-08-31T00:00:01Z"), state.getNextTargetTime());
		
		control.verify();
		assertEquals(SchedulerMode.RUN_CUTOFF, state.getMode());
		assertEquals(T("2016-08-31T00:00:01Z"), state.getCutoffTime());
	}

	@Test
	public void testGetNextTargetTime_Run_HasSlot() {
		SchedulerSlot slot = new SchedulerSlot(T("1970-08-30T00:00:00Z"));
		state.setMode(SchedulerMode.RUN);
		expect(slotsMock.getNextSlot()).andReturn(slot);
		state.addObserver(observerMock);
		control.replay();
		
		assertEquals(T("1970-08-30T00:00:00Z"), state.getNextTargetTime());
		
		control.verify();
		assertEquals(SchedulerMode.RUN, state.getMode());
	}

	@Test
	public void testGetNextTargetTime_Run_NoSlot() {
		state.setMode(SchedulerMode.RUN);
		expect(slotsMock.getNextSlot()).andReturn(null);
		state.addObserver(observerMock);
		control.replay();
		
		assertNull(state.getNextTargetTime());
		
		control.verify();
		assertEquals(SchedulerMode.RUN, state.getMode());
	}
	
	@Test
	public void testGetNextTargetTime_ThrowsUnexpectedMode() {
		SchedulerMode list[] = {
				SchedulerMode.CLOSE,
				SchedulerMode.WAIT
		};
		for ( int i = 0; i < list.length; i ++ ) {
			state.setMode(list[i]);
			try {
				state.getNextTargetTime();
				fail("At #" + i + "Expected exception: "
						+ IllegalStateException.class.getSimpleName());
			} catch ( IllegalStateException e ) { }
		}
	}

	@Test
	public void testIsModeWait() {
		state.setMode(SchedulerMode.RUN);
		
		assertFalse(state.isModeWait());
		
		state.setMode(SchedulerMode.WAIT);
		
		assertTrue(state.isModeWait());
	}
	
	@Test
	public void testGetTimeOfSlots() {
		Set<Instant> expected = new HashSet<>();
		expected.add(T("2015-02-13T00:00:00Z"));
		expected.add(T("2015-02-14T00:00:00Z"));
		expected.add(T("2015-02-15T00:00:00Z"));
		expect(slotsMock.getTimeOfSlots()).andReturn(expected);
		control.replay();
		
		assertSame(expected, state.getTimeOfSlots());
		
		control.verify();
	}
	
	@Test
	public void testGetSlot() {
		SchedulerSlot slot = new SchedulerSlot(T("2016-01-02T00:00:00Z"));
		expect(slotsMock.getSlot(T("2016-01-02T00:00:00Z"))).andReturn(slot);
		expect(slotsMock.getSlot(T("2016-01-02T00:00:01Z"))).andReturn(null);
		control.replay();
		
		assertSame(slot, state.getSlot(T("2016-01-02T00:00:00Z")));
		assertNull(state.getSlot(T("2016-01-02T00:00:01Z")));
		
		control.verify();
	}

}
