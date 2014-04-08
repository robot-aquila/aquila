package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import org.junit.*;

public class SMTriggerRegistryTest {
	private Object data = new Object();
	private IMocksControl control;
	private SMTriggerRegistry registry;
	private SMStateMachine machine;
	private SMState s1, s2;
	private SMInput in1;
	private SMTrigger t1, t2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		machine = control.createMock(SMStateMachine.class);
		s1 = new SMState();
		s2 = new SMState();
		in1 = s1.registerInput(null);
		registry = new SMTriggerRegistry(machine, s1);
		t1 = control.createMock(SMTrigger.class);
		t2 = control.createMock(SMTrigger.class);
	}
	
	@Test
	public void testRegisterAndActivate() throws Exception {
		t1.activate(same(registry));
		control.replay();
		
		registry.registerAndActivate(t1);
		
		control.verify();
	}
	
	@Test
	public void testRegisterAndActivate_SkipsIfExists() throws Exception {
		t1.activate(same(registry));
		control.replay();
		
		registry.registerAndActivate(t1);
		registry.registerAndActivate(t1);
		
		control.verify();
	}
	
	@Test
	public void testDeactivateAndRemove() throws Exception {
		t1.activate(same(registry));
		t1.deactivate();
		control.replay();
		
		registry.registerAndActivate(t1);
		registry.deactivateAndRremove(t1);
		
		control.verify();
	}
	
	@Test
	public void testDeactivateAndRemove_SkipsIfExists() throws Exception {
		control.replay();
		
		registry.deactivateAndRremove(t1);
		
		control.verify();
	}
	
	@Test
	public void testDeactivateAndRemoveAll() throws Exception {
		t1.activate(same(registry));
		t2.activate(same(registry));
		t1.deactivate();
		t2.deactivate();
		control.replay();
		
		registry.registerAndActivate(t1);
		registry.registerAndActivate(t2);
		registry.deactivateAndRemoveAll();
		
		control.verify();
	}
	
	@Test
	public void testInput1() throws Exception {
		expect(machine.getCurrentState()).andReturn(s1);
		machine.input(same(data));
		control.replay();
		
		registry.input(data);
		
		control.verify();
	}
	
	@Test (expected=SMRuntimeException.class)
	public void testInput1_ThrowsIfMachineThrows() throws Exception {
		expect(machine.getCurrentState()).andReturn(s1);
		machine.input(same(data));
		expectLastCall().andThrow(new SMAmbiguousInputException());
		control.replay();
		
		registry.input(data);
		
		control.verify();
	}

	@Test (expected=SMRuntimeException.class)
	public void testInput1_ThrowsIfDifferentState() throws Exception {
		expect(machine.getCurrentState()).andReturn(s2);
		control.replay();
		
		registry.input(data);
		
		control.verify();
	}

	@Test
	public void testInput2() throws Exception {
		expect(machine.getCurrentState()).andReturn(s1);
		machine.input(same(in1), same(data));
		control.replay();
		
		registry.input(in1, data);
		
		control.verify();
	}
	
	@Test (expected=SMRuntimeException.class)
	public void testInput2_ThrowsIfMachineThrows() throws Exception {
		expect(machine.getCurrentState()).andReturn(s1);
		machine.input(same(in1), same(data));
		expectLastCall().andThrow(new SMTransitionNotExistsException());
		control.replay();
		
		registry.input(in1, data);
		
		control.verify();
	}

	@Test (expected=SMRuntimeException.class)
	public void testInput2_ThrowsIfDifferentState() throws Exception {
		expect(machine.getCurrentState()).andReturn(s2);
		control.replay();
		
		registry.input(in1, data);
		
		control.verify();
	}

}
