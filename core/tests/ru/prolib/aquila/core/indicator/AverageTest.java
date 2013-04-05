package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.CompositeEvent;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.EditableSeries;
import ru.prolib.aquila.core.data.ValueEvent;
import ru.prolib.aquila.core.indicator.Average;

/**
 * 2012-05-14<br>
 * $Id: AverageTest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class AverageTest {
	private IMocksControl control;
	private EventType type0,type1,type2,type3;
	private LinkedHashMap<EventType, Event> state;
	private CompositeEvent event;
	private EditableSeries<Double> target;
	private Average indicator;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type0 = control.createMock(EventType.class);
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		type3 = control.createMock(EventType.class);
		target = control.createMock(EditableSeries.class);
		indicator = new Average(target);
		state = new LinkedHashMap<EventType, Event>();
		event = new CompositeEvent(type0, state);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(target, indicator.getTarget());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTargetIsNull() throws Exception {
		new Average(null);
	}
	
	@Test
	public void testOnEvent_Ok() throws Exception {
		state.put(type1, new ValueEvent<Double>(type1, 10.0d, 0));
		state.put(type2, new ValueEvent<Double>(type2, 20.0d, 0));
		state.put(type3, new ValueEvent<Double>(type3, 30.0d, 0));
		target.add(20.0d);
		control.replay();
		
		indicator.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_NullIfNoEvents() throws Exception {
		target.add(null);
		control.replay();
		
		indicator.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_NullIfOneOfEventHasNull() throws Exception {
		state.put(type1, new ValueEvent<Double>(type1, 10.0d, 0));
		state.put(type2, new ValueEvent<Double>(type2, null, 0));
		state.put(type3, new ValueEvent<Double>(type3, 30.0d, 0));
		target.add(null);
		control.replay();
		
		indicator.onEvent(event);
		
		control.verify();
	}

}
