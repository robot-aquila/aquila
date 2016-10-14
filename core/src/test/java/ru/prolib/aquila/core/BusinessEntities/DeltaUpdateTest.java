package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;

public class DeltaUpdateTest {
	private static Instant time1 = Instant.parse("2016-05-19T00:20:48Z");
	private static Instant time2 = Instant.parse("2010-08-20T10:40:50Z");
	private Map<Integer, Object> contents1, contents2;
	private DeltaUpdate update1, update2;

	@Before
	public void setUp() throws Exception {
		contents1 = new HashMap<>();
		contents1.put(1, "foo");
		contents1.put(2, "bar");
		contents2 = new HashMap<>();
		contents2.put(1, "zulu");
		update1 = new DeltaUpdate(time1, true, contents1);
		update2 = new DeltaUpdate(time2, false, contents2);
	}

	@Test
	public void testCtor3() {
		assertEquals(time1, update1.getTime());
		assertTrue(update1.isSnapshot());
		assertEquals(contents1, update1.getContents());
		
		assertEquals(time2, update2.getTime());
		assertFalse(update2.isSnapshot());
		assertEquals(contents2, update2.getContents());
	}
	
	@Test
	public void testEquals() {
		assertTrue(update1.equals(update1));
		assertTrue(update1.equals(new DeltaUpdate(time1, true, contents1)));
		assertFalse(update1.equals(new DeltaUpdate(time2, true, contents1)));
		assertFalse(update1.equals(new DeltaUpdate(time1, false, contents1)));
		assertFalse(update1.equals(new DeltaUpdate(time1, true, contents2)));
		assertFalse(update1.equals(null));
		assertFalse(update1.equals(this));
		assertFalse(update1.equals(update2));
	}
	
	@Test
	public void testToString() {
		assertEquals("DeltaUpdate[2016-05-19T00:20:48Z snapshot {1=foo, 2=bar}]", update1.toString());
		assertEquals("DeltaUpdate[2010-08-20T10:40:50Z {1=zulu}]", update2.toString());
	}

}
