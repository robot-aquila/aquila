package ru.prolib.aquila.core;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.same;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

/**
 * 2012-04-22<br>
 * $Id: CompositeEventTypeImplTest.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class CompositeEventTypeImplTest {
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType type1,type2,type3;
	private CompositeEventRule rule;
	private CompositeEventGenerator gen;
	private CompositeEventTypeImpl type;
	private List<EventType> types;
	private EventType[] typesArr;
	private LinkedHashMap<EventType, Event> expState;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		type3 = control.createMock(EventType.class);
		rule = control.createMock(CompositeEventRule.class);
		gen = control.createMock(CompositeEventGenerator.class);
		expState = new LinkedHashMap<EventType, Event>();
		expState.put(type1, null);
		expState.put(type3, null);
		expState.put(type2, null);	
		
		types = new LinkedList<EventType>();
		types.add(type1);
		types.add(type2);
		types.add(type3);
		typesArr = types.toArray(new EventType[types.size()]);
		
		type = new CompositeEventTypeImpl(dispatcher, types, rule, gen);
	}
	
	@Test
	public void testConstruct4L_Ok() throws Exception {
		assertSame(dispatcher, type.getEventDispatcher());
		assertSame(rule, type.getRule());
		assertEquals(expState, type.getCurrentState());
		assertSame(gen, type.getEventGenerator());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4L_ThrowsIfDispatcherIsNull() throws Exception {
		new CompositeEventTypeImpl(null, types, rule, gen);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4L_ThrowsIfTypesIsNull() throws Exception {
		new CompositeEventTypeImpl(dispatcher, (List<EventType>)null,rule, gen);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct4L_ThrowsIfTypesZeroSize() throws Exception {
		new CompositeEventTypeImpl(dispatcher, new LinkedList<EventType>(),
				rule, gen);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4L_ThrowsIfRuleIsNull() throws Exception {
		new CompositeEventTypeImpl(dispatcher, types, null, gen);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4L_ThrowsIfGeneratorIsNull() throws Exception {
		new CompositeEventTypeImpl(dispatcher, types, rule, null);
	}
	
	@Test
	public void testConstruct4A_Ok() throws Exception {
		type = new CompositeEventTypeImpl(dispatcher, typesArr, rule, gen);
		assertSame(dispatcher, type.getEventDispatcher());
		assertSame(rule, type.getRule());
		assertEquals(expState, type.getCurrentState());
		assertSame(gen, type.getEventGenerator());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4A_ThrowsIfDispatcherIsNull() throws Exception {
		new CompositeEventTypeImpl(null, typesArr, rule, gen);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4A_ThrowsIfTypesIsNull() throws Exception {
		new CompositeEventTypeImpl(dispatcher, (EventType[])null, rule, gen);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct4A_ThrowsIfTypesZeroLength() throws Exception {
		new CompositeEventTypeImpl(dispatcher, new EventType[0], rule, gen);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4A_ThrowsIfRuleIsNull() throws Exception {
		new CompositeEventTypeImpl(dispatcher, typesArr, null, gen);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct4A_ThrowsIfGeneratorIsNull() throws Exception {
		new CompositeEventTypeImpl(dispatcher, typesArr, rule, null);
	}

	@Test
	public void testAddListener() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		type1.addListener(same(type));
		type2.addListener(same(type));
		type3.addListener(same(type));
		control.replay();
		
		type.addListener(listener);
		
		control.verify();
	}
	
	@Test
	public void testRemoveListener() throws Exception {
		EventListener listener2 = control.createMock(EventListener.class);
		type1.removeListener(same(type));
		type2.removeListener(same(type));
		type3.removeListener(same(type));
		
		control.replay();
		
		type.removeListener(listener2);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfUnknownType() throws Exception {
		EventType unknown = control.createMock(EventType.class);
		Event e = new EventImpl(unknown);
		control.replay();
		
		type.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfTestNewEventReturnsFalse() throws Exception {
		Event e = new EventImpl(type1);
		expect(rule.testNewEvent(same(e), eq(expState))).andReturn(false);
		control.replay();
		
		type.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfTestNewStateReturnsFalse() throws Exception {
		Event e = new EventImpl(type2);
		LinkedHashMap<EventType, Event> state1,state2;
		state1 = new LinkedHashMap<EventType, Event>(expState);
		expState.put(type2, e);
		state2 = new LinkedHashMap<EventType, Event>(expState);
		
		expect(rule.testNewEvent(same(e), eq(state1))).andReturn(true);
		expect(rule.testNewState(eq(state2))).andReturn(false);
		control.replay();
		
		type.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_Dispatch() throws Exception {
		Event e = new EventImpl(type2);
		LinkedHashMap<EventType, Event> state1,state2;
		state1 = new LinkedHashMap<EventType, Event>(expState);
		expState.put(type2, e);
		state2 = new LinkedHashMap<EventType, Event>(expState);
		
		Event generatedEvent = new CompositeEvent(type, state2);
		expect(rule.testNewEvent(same(e), eq(state1))).andReturn(true);
		expect(rule.testNewState(eq(state2))).andReturn(true);
		expect(gen.generateEvent(same(type), eq(state2), same(e)))
			.andReturn(generatedEvent);
		dispatcher.dispatch(same(generatedEvent));
		
		// Для проверки сброса состояния после диспетчеризации композитного
		// события проверим этот факт через вызов метода проверки события
		Event e2 = new EventImpl(type3);
		expect(rule.testNewEvent(same(e2), eq(state1))).andReturn(false);
		control.replay();
		
		type.onEvent(e);
		type.onEvent(e2);
		
		control.verify();
	}

}
