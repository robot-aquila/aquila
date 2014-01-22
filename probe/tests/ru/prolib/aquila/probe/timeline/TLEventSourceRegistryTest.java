package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLEventSourceRegistryTest {
	private IMocksControl control;
	private TLEventSource s1, s2, s3;
	private TLEventSourceRegistry registry;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		s1 = control.createMock(TLEventSource.class);
		s2 = control.createMock(TLEventSource.class);
		s3 = control.createMock(TLEventSource.class);
		registry = new TLEventSourceRegistry();
		registry.addSource(s1);
		registry.addSource(s2);
		registry.addSource(s3);
		registry.disableUntil(s1, new DateTime(2014,  1, 22,  0,  0, 0));
		registry.disableUntil(s3, new DateTime(2013, 12, 31, 23, 59, 0));
	}
	
	@Test
	public void testGetSources0() throws Exception {
		List<TLEventSource> expected = new Vector<TLEventSource>();
		expected.add(s1);
		expected.add(s2);
		expected.add(s3);
		assertEquals(expected, registry.getSources());
	}
	
	@Test
	public void testGetSources1_1() throws Exception {
		List<TLEventSource> expected = new Vector<TLEventSource>();
		expected.add(s1);
		expected.add(s2);
		expected.add(s3);
		assertEquals(expected,
				registry.getSources(new DateTime(2014,  2,  1,  0,  0,  0)));
	}
	
	@Test
	public void testGetSources1_2() throws Exception {
		List<TLEventSource> expected = new Vector<TLEventSource>();
		expected.add(s2);
		expected.add(s3);
		assertEquals(expected,
				registry.getSources(new DateTime(2014,  1,  1,  0,  0,  0)));
	}
	
	@Test
	public void testGetSources1_3() throws Exception {
		List<TLEventSource> expected = new Vector<TLEventSource>();
		expected.add(s2);
		assertEquals(expected,
				registry.getSources(new DateTime(2012, 12, 31, 23, 59, 59)));
	}

	@Test
	public void testRemoveSource() throws Exception {
		registry.removeSource(s2);
		registry.removeSource(s3);
		List<TLEventSource> expected = new Vector<TLEventSource>();
		expected.add(s1);
		assertEquals(expected, registry.getSources());
	}
	
	@Test
	public void testClose() throws Exception {
		s1.close();
		s2.close();
		s3.close();
		control.replay();
		
		registry.close();
		
		control.verify();
		assertEquals(new Vector<TLEventSource>(), registry.getSources());
	}

}
