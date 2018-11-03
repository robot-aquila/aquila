package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class SMTriggerOnEventTest {
	private IMocksControl control;
	private SMTriggerRegistry registry, registryMock2;
	private EventType type, type2;
	private Event event;
	private SMStateHandler state;
	private SMInput in1, inMock2;
	private SMTriggerOnEvent trigger;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		registry = control.createMock(SMTriggerRegistry.class);
		registryMock2 = control.createMock(SMTriggerRegistry.class);
		type = new EventTypeImpl("TYPE1");
		type2 = new EventTypeImpl("TYPE2");
		event = new EventImpl(type);
		state = new SMStateHandler();
		in1 = state.registerInput(null);
		inMock2 = control.createMock(SMInput.class);
		trigger = new SMTriggerOnEvent(type, in1);
	}
	
	@Test
	public void testCtor() {
		assertSame(type, trigger.getEventType());
		assertSame(in1, trigger.getInput());
		assertNull(trigger.getProxy());
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
	
	@Test
	public void testHashCode() {
		int expected1 = new HashCodeBuilder(113257, 891)
				.append(type)
				.append(in1)
				.append((SMTriggerRegistry)null)
				.build();
		int expected2 = new HashCodeBuilder(113257, 891)
				.append(type)
				.append(in1)
				.append(registry)
				.build();
		
		assertEquals(expected1, trigger.hashCode());
		trigger.activate(registry);
		assertEquals(expected2, trigger.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(trigger.equals(trigger));
		assertFalse(trigger.equals(null));
		assertFalse(trigger.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<EventType> vET = new Variant<>(type, type2);
		Variant<SMInput> vIN = new Variant<>(vET, in1, inMock2);
		Variant<SMTriggerRegistry> vTR = new Variant<>(vIN, registry, registryMock2);
		Variant<?> iterator = vTR;
		int foundCnt = 0;
		trigger.activate(registry);
		SMTriggerOnEvent x, found = null;
		do {
			x = new SMTriggerOnEvent(vET.get(), vIN.get());
			x.activate(vTR.get());
			if ( trigger.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getEventType());
		assertSame(in1, found.getInput());
		assertSame(registry, found.getProxy());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("SMTriggerOnEvent[eventType=TYPE1,input=")
				.append(in1)
				.append(",proxy=")
				.append(registry)
				.append("]")
				.toString();
		
		trigger.activate(registry);
		assertEquals(expected, trigger.toString());
	}

}
