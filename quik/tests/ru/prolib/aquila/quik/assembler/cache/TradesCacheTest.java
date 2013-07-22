package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class TradesCacheTest {
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType type;
	private TradesEntry entry1, entry2, entry3, entry4;
	private TradesCache cache;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		type = control.createMock(EventType.class);
		entry1 = control.createMock(TradesEntry.class);
		entry2 = control.createMock(TradesEntry.class);
		entry3 = control.createMock(TradesEntry.class);
		entry4 = control.createMock(TradesEntry.class);
		cache = new TradesCache(dispatcher, type);
	}
	
	@Test
	public void testGetFirst() throws Exception {
		cache.addEntry(entry1);
		cache.addEntry(entry2);
		cache.addEntry(entry3);
		cache.addEntry(entry4);
		
		assertSame(entry1, cache.getFirst());
		assertSame(entry1, cache.getFirst());
		assertSame(entry1, cache.getFirst());
	}
	
	@Test
	public void testGetFirst_IfNoData() throws Exception {
		assertNull(cache.getFirst());
	}
	
	@Test
	public void testPurgeFirst() throws Exception {
		dispatcher.dispatch(eq(new CacheEvent(type, false)));
		control.replay();
		cache.addEntry(entry1);
		cache.addEntry(entry2);
		cache.addEntry(entry3);
		cache.addEntry(entry4);

		cache.purgeFirst();
		
		control.verify();
		assertSame(entry2, cache.getFirst());
		List<TradesEntry> expected = new Vector<TradesEntry>();
		expected.add(entry2);
		expected.add(entry3);
		expected.add(entry4);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testPurgeFirst_NoEventsIfNoData() throws Exception {
		control.replay();
		
		cache.purgeFirst();
		
		control.verify();
	}
	
	@Test
	public void testGet_All() throws Exception {
		cache.addEntry(entry1);
		cache.addEntry(entry2);
		cache.addEntry(entry3);
		cache.addEntry(entry4);

		List<TradesEntry> expected = new Vector<TradesEntry>();
		expected.add(entry1);
		expected.add(entry2);
		expected.add(entry3);
		expected.add(entry4);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testOnUpdate() throws Exception {
		assertSame(type, cache.OnUpdate());
	}
	
	@Test
	public void testAdd() throws Exception {
		dispatcher.dispatch(eq(new CacheEvent(type, true)));
		control.replay();
		
		cache.add(entry1);
		
		control.verify();
		assertSame(entry1, cache.getFirst());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<TradesEntry> rows1 = new Vector<TradesEntry>();
		rows1.add(entry1);
		rows1.add(entry2);
		List<TradesEntry> rows2 = new Vector<TradesEntry>();
		rows2.add(entry3);
		rows2.add(entry4);
		for ( TradesEntry entry : rows1 ) {
			cache.addEntry(entry);
		}
		Variant<List<TradesEntry>> vRows = new Variant<List<TradesEntry>>()
			.add(rows1)
			.add(rows2);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vRows)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vType = new Variant<EventType>(vDisp)
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<?> iterator = vType;
		int foundCnt = 0;
		TradesCache x, found = null;
		do {
			x = new TradesCache(vDisp.get(), vType.get());
			for ( TradesEntry entry : vRows.get() ) {
				x.addEntry(entry);
			}
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(type, found.OnUpdate());
		assertEquals(rows1, found.get());
	}

}
