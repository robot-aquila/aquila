package ru.prolib.aquila.ib.assembler.cache;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.junit.*;
import ru.prolib.aquila.core.utils.SimpleCounter;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.assembler.cache.ExecIdCache;

public class ExecIdCacheTest {
	private ExecIdCache cache;

	@Before
	public void setUp() throws Exception {
		cache = new ExecIdCache();
	}
	
	@Test
	public void testConstruct0() throws Exception {
		assertEquals(new ExecIdCache(new SimpleCounter()), new ExecIdCache());
	}
	
	@Test
	public void testGetId() throws Exception {
		assertEquals(1L, cache.getId("ABC"));
		assertEquals(2L, cache.getId("xxx"));
		assertEquals(3L, cache.getId("zyx"));
		assertEquals(1L, cache.getId("ABC"));
		assertEquals(2L, cache.getId("xxx"));
		assertEquals(4L, cache.getId("foo"));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<String> rows1 = new Vector<String>();
		rows1.add("a");
		rows1.add("b");
		rows1.add("c");
		List<String> rows2 = new Vector<String>();
		rows2.add("xan");
		rows2.add("yaw");
		List<String> rows3 = new Vector<String>();
		rows3.add("foo");
		rows3.add("arc");
		rows3.add("zoo");
		for ( String row : rows1 ) cache.getId(row);
		Variant<List<String>> vRows = new Variant<List<String>>()
			.add(rows1)
			.add(rows2)
			.add(rows3);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		ExecIdCache x = null, found = null;
		do {
			x = new ExecIdCache();
			for ( String row : vRows.get() ) x.getId(row);
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(1L, found.getId("a"));
		assertEquals(2L, found.getId("b"));
		assertEquals(3L, found.getId("c"));
	}

}
