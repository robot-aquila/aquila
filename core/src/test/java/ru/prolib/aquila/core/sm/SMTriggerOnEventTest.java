package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;

public class SMTriggerOnEventTest {
	private IMocksControl control;
	private SMTriggerRegistry registry;
	private EventType type;
	private Event event;
	private SMState state;
	private SMInput in1;
	private SMTriggerOnEvent trigger;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		registry = control.createMock(SMTriggerRegistry.class);
		type = new EventTypeImpl();
		event = new EventImpl(type);
		state = new SMState();
		in1 = state.registerInput(null);
		trigger = new SMTriggerOnEvent(type, in1);
	}
	
	@Test
	public void testOnEvent_DefaultInput() throws Exception {
		trigger = new SMTriggerOnEvent(type);
		trigger.activate(registry);
		registry.input(same(event));
		control.replay();
		
		trigger.onEvent(event);

		control.verify();
	}
	
	@Test
	public void testOnEvent_SpecifiedInput() throws Exception {
		trigger.activate(registry);
		registry.input(same(in1), same(event));
		control.replay();
		
		trigger.onEvent(event);

		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfInactive() throws Exception {
		control.replay();
		
		trigger.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testActivate() throws Exception {
		trigger.activate(registry);
		assertTrue(type.isListener(trigger));
	}
	
	@Test
	public void testActivate_SkipIfActive() throws Exception {
		type = control.createMock(EventType.class);
		trigger = new SMTriggerOnEvent(type);
		type.addListener(same(trigger));
		control.replay();
		
		trigger.activate(registry);
		trigger.activate(registry);
		
		control.verify();
	}
	
	@Test
	public void testDeactivate() throws Exception {
		type = control.createMock(EventType.class);
		trigger = new SMTriggerOnEvent(type);
		type.addListener(same(trigger));
		type.removeListener(same(trigger));
		control.replay();
		
		trigger.activate(registry);
		trigger.deactivate();
		
		control.verify();
	}
	
	@Test
	public void testDeactivate_SkipIfInactive() throws Exception {
		control.replay();
		
		trigger.deactivate();
		
		control.verify();
	}

}
