package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-04-09
 * $Id: EventTypeImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventTypeImplTest {
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventTypeImpl type;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcherImpl.class);
		type = new EventTypeImpl(dispatcher, "MyType");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct1_ThrowsIfDispatcherIsNull() throws Exception {
		new EventTypeImpl(null);
	}
	
	@Test
	public void testConstruct1_Ok() throws Exception {
		int autoId = EventTypeImpl.getAutoId();
		type = new EventTypeImpl(dispatcher);
		assertSame(dispatcher, type.getEventDispatcher());
		assertEquals("EvtType" + autoId, type.getId());
		assertEquals(autoId + 1, EventTypeImpl.getAutoId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(dispatcher, type.getEventDispatcher());
		assertEquals("MyType", type.getId());
	}
	
	@Test
	public void testToString() throws Exception {
		expect(dispatcher.asString()).andReturn("MyDisp");
		control.replay();
		assertEquals("MyDisp.MyType", type.toString());
		control.verify();
	}
	
	@Test
	public void testAsString() throws Exception {
		expect(dispatcher.asString()).andReturn("MyDisp");
		control.replay();
		assertEquals("MyDisp.MyType", type.asString());
		control.verify();
	}
	
	@Test
	public void testAddListener() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		dispatcher.addListener(same(type), same(listener));
		control.replay();
		type.addListener(listener);
		control.verify();
	}
	
	@Test
	public void testRemoveListener() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		dispatcher.removeListener(same(type), same(listener));
		control.replay();
		type.removeListener(listener);
		control.verify();
	}
	
	@Test
	public void testIsListener() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		expect(dispatcher.isTypeListener(same(type), same(listener)))
			.andReturn(true);
		control.replay();
		assertTrue(type.isListener(listener));
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(type.equals(type));
		assertFalse(type.equals(null));
		assertFalse(type.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<EventListener> list1 = new Vector<EventListener>();
		list1.add(control.createMock(EventListener.class));
		list1.add(control.createMock(EventListener.class));
		expect(dispatcher.getListeners(same(type))).andStubReturn(list1);
		control.replay();
		List<EventListener> list2 = new Vector<EventListener>();
		list2.add(list1.get(0));
		
		Variant<String> vId = new Variant<String>()
			.add("MyType")
			.add("AnotherType");
		Variant<List<EventListener>> vLs = new Variant<List<EventListener>>(vId)
			.add(list1)
			.add(list2);
		Variant<?> iterator = vLs;
		int foundCnt = 0;
		EventTypeImpl x = null, found = null;
		List<EventListener> foundList = null;
		do {
			IMocksControl control2 = createStrictControl();
			EventDispatcher disp = control2.createMock(EventDispatcher.class);
			x = new EventTypeImpl(disp, vId.get());
			expect(disp.getListeners(same(x))).andStubReturn(vLs.get());
			control2.replay();
			if ( type.equals(x) ) {
				foundCnt ++;
				found = x;
				foundList = vLs.get();
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("MyType", found.getId());
		assertEquals(list1, foundList);
	}

}
