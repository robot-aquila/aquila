package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class TLSimpleEventSourceTest {
	private IMocksControl control;
	private TLEvent e1,e2,e3,e4,e5;
	private TLSimpleEventSource source;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		e1 = control.createMock(TLEvent.class);
		e2 = control.createMock(TLEvent.class);
		e3 = control.createMock(TLEvent.class);
		e4 = control.createMock(TLEvent.class);
		e5 = control.createMock(TLEvent.class);
		source = new TLSimpleEventSource();
	}

	@Test
	public void testAdd() throws Exception {
		List<TLEvent> expected = new ArrayList<TLEvent>(),
				actual = new ArrayList<TLEvent>();
		expected.add(e1);
		expected.add(e2);
		expected.add(e3);
		expected.add(e4);
		expected.add(e5);
		
		source.add(e1).add(e2).add(e3).add(e4).add(e5);
		TLEvent x;
		while ( (x = source.pullEvent()) != null ) {
			actual.add(x);
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		List<TLEvent> expected = new ArrayList<TLEvent>(),
				actual = new ArrayList<TLEvent>();
		expected.add(e1);
		expected.add(e2);
		expected.add(e3);
		expected.add(e4);
		expected.add(e5);
		
		source = new TLSimpleEventSource(expected);
		TLEvent x;
		while ( (x = source.pullEvent()) != null ) {
			actual.add(x);
		}

		assertEquals(5, actual.size());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClose() throws Exception {
		source.add(e1).add(e2).add(e3).add(e4).add(e5);
		
		assertFalse(source.closed());
		source.close();
		assertTrue(source.closed());
		assertNull(source.pullEvent());
	}

}
