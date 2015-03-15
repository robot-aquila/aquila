package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.utils.Variant;

public class PositionsCacheTest {
	private static Account account1, account2;
	private static PositionEntry entry1, entry2, entry3;
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventTypeSI type;
	private PositionsCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account1 = new Account("TEST");
		account2 = new Account("BEST");
		entry1 = new PositionEntry(account1, "LKOH", 1L, 2L, 12.34d);
		entry2 = new PositionEntry(account2, "SBER", 2L, 4L, 15.34d);
		entry3 = new PositionEntry(account1, "SBER", 5L, 8L, 10.13d);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		type = control.createMock(EventTypeSI.class);
		cache = new PositionsCache(dispatcher, type);
	}
	
	@Test
	public void testPut() throws Exception {
		dispatcher.dispatch(eq(new EventImpl(type)));
		control.replay();
		
		cache.put(entry1);
	
		control.verify();
		assertEquals(entry1, cache.get(account1, "LKOH"));
	}
	
	@Test
	public void testGet_ByAccAndSecShortName() throws Exception {
		cache.set(entry3);
		
		assertSame(entry3, cache.get(new Account("TEST"), "SBER"));
		assertNull(cache.get(account1, "zulu24"));
		assertNull(cache.get(new Account("GAP"), "SBER"));
	}
	
	@Test
	public void testGet_BySecShortName() throws Exception {
		cache.set(entry1);
		cache.set(entry2);
		cache.set(entry3);
		
		List<PositionEntry> expected = new Vector<PositionEntry>();
		expected.add(entry2);
		expected.add(entry3);
		
		assertEquals(expected, cache.get("SBER"));
	}
	
	@Test
	public void testGet_All() throws Exception {
		cache.set(entry1);
		cache.set(entry2);
		cache.set(entry3);
		
		List<PositionEntry> expected = new Vector<PositionEntry>();
		expected.add(entry1);
		expected.add(entry2);
		expected.add(entry3);
		
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testPurge() throws Exception {
		cache.set(entry1);
		cache.set(entry2);
		cache.set(entry3);

		cache.purge(entry2);
		
		assertNull(cache.get(account2, "SBER"));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<PositionEntry> rows1 = new Vector<PositionEntry>();
		rows1.add(entry1);
		rows1.add(entry3);
		List<PositionEntry> rows2 = new Vector<PositionEntry>();
		rows2.add(entry3);
		rows2.add(entry2);
		for ( PositionEntry entry : rows1 ) {
			cache.set(entry);
		}
		Variant<List<PositionEntry>> vRows = new Variant<List<PositionEntry>>()
			.add(rows1)
			.add(rows2);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vRows)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventTypeSI> vType = new Variant<EventTypeSI>(vDisp)
			.add(type)
			.add(control.createMock(EventTypeSI.class));
		Variant<?> iterator = vType;
		int foundCnt = 0;
		PositionsCache x, found = null;
		do {
			x = new PositionsCache(vDisp.get(), vType.get());
			for ( PositionEntry entry : vRows.get() ) {
				x.set(entry);
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
