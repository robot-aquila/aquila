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

	private void testAdd(TLSimpleEventSource source) throws Exception {
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
	public void testAdd_Construct0() throws Exception {
		testAdd(new TLSimpleEventSource());
	}
	
	@Test
	public void testAdd_Construct1_Id() throws Exception {
		testAdd(new TLSimpleEventSource("foo"));
	}
	
	@Test
	public void testAdd_Construct1_List() throws Exception {
		testAdd(new TLSimpleEventSource(new ArrayList<TLEvent>()));
	}
	
	@Test
	public void testAdd_Construct2() throws Exception {
		testAdd(new TLSimpleEventSource("foo", new ArrayList<TLEvent>()));
	}
	
	private void testContents(TLSimpleEventSource source,
							  List<TLEvent> expected,
							  int expectedSize)
					throws Exception
	{
		List<TLEvent> actual = new ArrayList<TLEvent>();
		TLEvent x;
		while ( (x = source.pullEvent()) != null ) {
			actual.add(x);
		}

		assertEquals(expectedSize, expected.size());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConstruct() throws Exception {
		List<TLEvent> expected = new ArrayList<TLEvent>();
		expected.add(e1);
		expected.add(e2);
		expected.add(e3);
		expected.add(e4);
		expected.add(e5);
		testContents(new TLSimpleEventSource(expected), expected, 5);
		testContents(new TLSimpleEventSource("foo", expected), expected, 5);
	}
	
	@Test
	public void testClose() throws Exception {
		source.add(e1).add(e2).add(e3).add(e4).add(e5);
		
		assertFalse(source.closed());
		source.close();
		assertTrue(source.closed());
		assertNull(source.pullEvent());
	}
	
	@Test
	public void testToString() throws Exception {
		List<TLEvent> list = new ArrayList<TLEvent>();
		list.add(e1);
		list.add(e2);
		list.add(e3);
		TLSimpleEventSource src1 = new TLSimpleEventSource("foo"),
				src2 = new TLSimpleEventSource("bar", list),
				src3 = new TLSimpleEventSource(),
				src4 = new TLSimpleEventSource(list);
		
		assertEquals("foo", src1.toString());
		assertEquals("bar", src2.toString());
		assertEquals(src3.getClass().getName() + "@"
				+ String.format("%x", src3.hashCode()), src3.toString());
		assertEquals(src4.getClass().getName()+ "@"
				+ String.format("%x", src4.hashCode()), src4.toString());
	}

}
