package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class DescriptorsCacheTest {
	private static SecurityDescriptor descr1, descr2, descr3;
	private static SecurityEntry entry1, entry2, entry3;
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType type;
	private DescriptorsCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("LKOH","RTSST", "RUB",SecurityType.STK);
		descr2 = new SecurityDescriptor("LKOH","EQBR",  "RUB",SecurityType.STK);
		descr3 = new SecurityDescriptor("RIM2","SPBFUT","USD",SecurityType.FUT);
		entry1 = new SecurityEntry(0, null, null, 0d, 0d, 0, 0d, 0d, 0d,
				"", "LKOH", 0d, 0d, 0d, 0d, descr1);
		entry2 = new SecurityEntry(0, null, null, 0d, 0d, 0, 0d, 0d, 0d,
				"", "ЛУКОЙЛ", 0d, 0d, 0d, 0d, descr2);
		entry3 = new SecurityEntry(0, null, null, 0d, 0d, 0, 0d, 0d, 0d,
				"", "RIM2", 0d, 0d, 0d, 0d, descr3);

	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		type = control.createMock(EventType.class);
		cache = new DescriptorsCache(dispatcher, type);
	}
	
	@Test
	public void testGet_ByShortName() throws Exception {
		cache.set(entry1);
		cache.set(entry2);
		cache.set(entry3);
		
		assertEquals(descr1, cache.get("LKOH"));
		assertEquals(descr2, cache.get("ЛУКОЙЛ"));
		assertEquals(descr3, cache.get("RIM2"));
		assertNull(cache.get("zulu24"));
	}
	
	@Test
	public void testGet_ByCodeAndClass() throws Exception {
		cache.set(entry1);
		cache.set(entry2);
		cache.set(entry3);
		
		assertEquals(descr1, cache.get("LKOH", "RTSST"));
		assertEquals(descr2, cache.get("LKOH", "EQBR"));
		assertEquals(descr3, cache.get("RIM2", "SPBFUT"));
		assertNull(cache.get("zulu24", "buzz"));
		assertNull(cache.get("LKOH", "buzz"));
		assertNull(cache.get("zulu24", "EQBR"));
	}
	
	@Test
	public void testGet_All() throws Exception {
		cache.set(entry1);
		cache.set(entry2);
		cache.set(entry3);
		
		List<SecurityDescriptor> expected = new Vector<SecurityDescriptor>();
		expected.add(descr1);
		expected.add(descr2);
		expected.add(descr3);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testPut_New() throws Exception {
		dispatcher.dispatch(eq(new EventImpl(type)));
		control.replay();
		
		cache.put(entry2);
		
		control.verify();
		assertEquals(descr2, cache.get("ЛУКОЙЛ"));
	}
	
	@Test
	public void testPut_Existing() throws Exception {
		cache.set(entry3);
		control.replay();
		
		cache.put(entry3);
		
		control.verify();
		assertEquals(descr3, cache.get("RIM2"));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<SecurityEntry> rows1 = new Vector<SecurityEntry>();
		rows1.add(entry1);
		rows1.add(entry3);
		List<SecurityEntry> rows2 = new Vector<SecurityEntry>();
		rows2.add(entry2);
		for ( SecurityEntry entry : rows1 ) {
			cache.set(entry);
		}
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vType = new Variant<EventType>(vDisp)
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<List<SecurityEntry>> vRows =
				new Variant<List<SecurityEntry>>(vType)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		DescriptorsCache x, found = null;
		do {
			x = new DescriptorsCache(vDisp.get(), vType.get());
			for ( SecurityEntry entry : vRows.get() ) {
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
		for ( SecurityEntry entry : rows1 ) {
			SecurityDescriptor desc = entry.getDescriptor();
			assertEquals(desc, cache.get(entry.getShortName()));
			assertEquals(desc, cache.get(desc.getCode(), desc.getClassCode()));
		}
		List<SecurityDescriptor> expected = new Vector<SecurityDescriptor>();
		expected.add(descr1);
		expected.add(descr3);
		assertEquals(expected, cache.get());
	}

}
