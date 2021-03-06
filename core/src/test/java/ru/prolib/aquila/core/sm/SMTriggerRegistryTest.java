package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.KW;

public class SMTriggerRegistryTest {
	private Object data = new Object();
	private IMocksControl control;
	private SMTriggerRegistry registry;
	private SMStateMachine machine;
	private SMStateHandler s1, s2;
	private SMInput in1;
	private SMTrigger t1, t2;
	private Set<KW<SMTrigger>> triggers;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		machine = control.createMock(SMStateMachine.class);
		s1 = new SMStateHandler();
		s2 = new SMStateHandler();
		in1 = s1.registerInput(null);
		triggers = new LinkedHashSet<>();
		registry = new SMTriggerRegistry(machine, s1, triggers);
		t1 = control.createMock(SMTrigger.class);
		t2 = control.createMock(SMTrigger.class);
	}
	
	@Test
	public void testAdd() throws Exception {
		t1.activate(same(registry));
		control.replay();
		
		registry.add(t1);
		
		control.verify();
		Set<KW<SMTrigger>> expected = new LinkedHashSet<>();
		expected.add(new KW<>(t1));
		assertEquals(expected, triggers);
	}
	
	@Test
	public void testAdd_SkipsIfClosed() throws Exception {
		control.replay();
		registry.close();
		
		registry.add(t1);
		
		control.verify();
		Set<KW<SMTrigger>> expected = new LinkedHashSet<>();
		assertEquals(expected, triggers);
	}
	
	@Test
	public void testAdd_SkipsIfExists() throws Exception {
		t1.activate(same(registry));
		control.replay();
		
		registry.add(t1);
		registry.add(t1);
		
		control.verify();
	}
	
	@Test
	public void testRemove() throws Exception {
		t1.activate(same(registry));
		t1.deactivate();
		control.replay();
		
		registry.add(t1);
		registry.remove(t1);
		
		control.verify();
	}
	
	@Test
	public void testRemove_SkipsIfExists() throws Exception {
		control.replay();
		
		registry.remove(t1);
		
		control.verify();
	}
	
	@Test
	public void testRemoveAll() throws Exception {
		t1.activate(same(registry));
		t2.activate(same(registry));
		t1.deactivate();
		t2.deactivate();
		control.replay();
		
		registry.add(t1);
		registry.add(t2);
		registry.removeAll();
		
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

	@Test
	public void testInput1_SkipsIfDifferentState() throws Exception {
		expect(machine.getCurrentState()).andReturn(s2);
		control.replay();
		
		registry.input(data);
		
		control.verify();
	}
	
	@Test
	public void testInput1_SkipsIfClosed() throws Exception {
		registry.close();
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

	@Test
	public void testInput2_SkipsIfDifferentState() throws Exception {
		expect(machine.getCurrentState()).andReturn(s2);
		control.replay();
		
		registry.input(in1, data);
		
		control.verify();
	}
	
	@Test
	public void testInput2_SkipsIfClosed() throws Exception {
		registry.close();
		control.replay();
		
		registry.input(in1, data);
		
		control.verify();
	}
	
	@Test
	public void testClose() {
		triggers.add(new KW<>(t1));
		triggers.add(new KW<>(t2));
		t1.deactivate();
		t2.deactivate();
		control.replay();
		
		registry.close();
		
		control.verify();
		Set<KW<SMTrigger>> expected = new LinkedHashSet<>();
		assertEquals(expected, triggers);
		assertTrue(registry.isClosed());
	}
	
	@Test
	public void testClose_SkipsIfClosed() {
		registry.close();
		triggers.add(new KW<>(t1));
		triggers.add(new KW<>(t2));
		control.replay();
		
		registry.close();
		
		control.verify();
		Set<KW<SMTrigger>> expected = new LinkedHashSet<>();
		expected.add(new KW<>(t1));
		expected.add(new KW<>(t2));
		assertEquals(expected, triggers);
	}

}
