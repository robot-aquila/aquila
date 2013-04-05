package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.util.ObservableComplex;

/**
 * 2012-02-03
 * $Id: CounterSetImplTest.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public class CounterSetImplTest {
	IMocksControl control;
	CounterSetImpl set;
	ObservableComplex observable;
	TestCounter<Integer> c1;
	Counter<Integer> c2,c3,c4;
	ServiceLocator locator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		c1 = new TestCounter<Integer>();
		control = createStrictControl();
		observable = control.createMock(ObservableComplex.class);
		c2 = control.createMock(Counter.class);
		c3 = control.createMock(Counter.class);
		c4 = control.createMock(Counter.class);
		locator = control.createMock(ServiceLocator.class);
		set = new CounterSetImpl(observable);
	}
	
	@Test
	public void testAdd_Ok() throws Exception {
		set.add("foobar", c1);
		assertSame(c1, set.get("foobar"));
	}
	
	@Test (expected=CounterAlreadyExistsException.class)
	public void testAdd_ThrowsIfExists() throws Exception {
		set.add("foobar", c1);
		set.add("foobar", c1);
	}
	
	@Test
	public void testRemove_Ok() throws Exception {
		set.add("foo", c1);
		set.remove("foo");
		assertFalse(set.iterator().hasNext());
	}
	
	@Test
	public void testRemove_NotExists() throws Exception {
		set.remove("foo");
		assertFalse(set.iterator().hasNext());
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		set.add("zulu", c1);
		assertSame(c1, set.get("zulu"));
	}
	
	@Test (expected=CounterNotExistsException.class)
	public void tetsGet_ThrowsNotExists() throws Exception {
		set.get("zulu");
	}
	
	@Test
	public void testStartService_Ok() throws Exception {
		observable.addObserver(set);
		observable.addObservable(c2);
		c2.startService(locator);
		observable.addObservable(c3);
		c3.startService(locator);
		observable.addObservable(c4);
		c4.startService(locator);
		control.replay();
		
		set.add("a", c2);
		set.add("b", c3);
		set.add("c", c4);
		set.startService(locator);
		
		control.verify();
	}
	
	@Test
	public void testStopService_Ok() throws Exception {
		c2.stopService();
		observable.deleteObservable(c2);
		c3.stopService();
		observable.deleteObservable(c3);
		c4.stopService();
		observable.deleteObservable(c4);
		observable.deleteObserver(set);
		control.replay();
		
		set.add("a", c2);
		set.add("b", c3);
		set.add("c", c4);
		set.stopService();
		
		control.verify();
	}
	
	@Test
	public void testIterator() throws Exception {
		set.add("a", c2);
		set.add("b", c3);
		set.add("c", c4);
		control.replay();
		
		Iterator<Map.Entry<String, Counter<?>>> it = set.iterator();
		assertTrue(it.hasNext());
		Map.Entry<String, Counter<?>> e = it.next();
		assertEquals("a", e.getKey());
		assertSame(c2, e.getValue());
		
		assertTrue(it.hasNext());
		e = it.next();
		assertEquals("b", e.getKey());
		assertSame(c3, e.getValue());
		
		assertTrue(it.hasNext());
		e = it.next();
		assertEquals("c", e.getKey());
		assertSame(c4, e.getValue());
		
		assertFalse(it.hasNext());
		
		control.verify();
	}

}
