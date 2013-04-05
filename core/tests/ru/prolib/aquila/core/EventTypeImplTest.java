package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

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

}
