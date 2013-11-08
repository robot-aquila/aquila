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
	private static QUIKSecurityDescriptor descr1, descr2, descr3, descr4;
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType type;
	private DescriptorsCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new QUIKSecurityDescriptor("LKOH", "RTSST", ISO4217.RUB,
				SecurityType.STK, "LKOH", "LKOH", "Лукоил");
		descr2 = new QUIKSecurityDescriptor("LKOH", "EQBR",  ISO4217.RUB,
				SecurityType.STK, "LKOH", "Лукоил", "АО ЛУКОИЛ");
		descr3 = new QUIKSecurityDescriptor("RTS-12.13", "SPBFUT", ISO4217.USD,
				SecurityType.FUT, "RIZ3", "RIZ3", "RTS-12.13");
		descr4 = new QUIKSecurityDescriptor("RTS-12.3", "SPBFUT", ISO4217.USD,
				SecurityType.FUT, "RIZ3", "RIZ3", "RTS-12.3");
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
		cache.set(descr1);
		cache.set(descr2);
		cache.set(descr3);
		
		assertEquals(descr1, cache.get("LKOH"));
		assertEquals(descr2, cache.get("Лукоил"));
		assertEquals(descr3, cache.get("RIZ3"));
		assertNull(cache.get("zulu24"));
	}
	
	@Test
	public void testGet_ByShortName_LastForSameNames() throws Exception {
		cache.set(descr4);
		
		assertSame(descr4, cache.get("RIZ3"));
		
		cache.set(descr3);
		
		assertSame(descr3, cache.get("RIZ3"));
	}
	
	@Test
	public void testGet_BySystemCodeAndClass() throws Exception {
		cache.set(descr1);
		cache.set(descr2);
		cache.set(descr3);
		
		assertEquals(descr1, cache.get("LKOH", "RTSST"));
		assertEquals(descr2, cache.get("LKOH", "EQBR"));
		assertEquals(descr3, cache.get("RIZ3", "SPBFUT"));
		assertNull(cache.get("zulu24", "buzz"));
		assertNull(cache.get("LKOH", "buzz"));
		assertNull(cache.get("zulu24", "EQBR"));
	}
	
	@Test
	public void testGet_BySystemCodeAndClass_LastForSameCom() throws Exception {
		cache.set(descr4);
		
		assertSame(descr4, cache.get("RIZ3", "SPBFUT"));
		
		cache.set(descr3);
		
		assertSame(descr3, cache.get("RIZ3", "SPBFUT"));
	}
	
	@Test
	public void testGet_All() throws Exception {
		cache.set(descr1);
		cache.set(descr2);
		cache.set(descr3);
		cache.set(descr4);
		
		List<QUIKSecurityDescriptor> expected =
			new Vector<QUIKSecurityDescriptor>();
		expected.add(descr1);
		expected.add(descr2);
		expected.add(descr3);
		expected.add(descr4);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testPut_New() throws Exception {
		dispatcher.dispatch(eq(new EventImpl(type)));
		control.replay();
		
		assertTrue(cache.put(descr2));
		
		control.verify();
		assertEquals(descr2, cache.get("Лукоил"));
	}
	
	@Test
	public void testPut_Existing() throws Exception {
		cache.set(descr3);
		control.replay();
		
		assertFalse(cache.put(descr3));
		
		control.verify();
		assertEquals(descr3, cache.get("RIZ3"));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<QUIKSecurityDescriptor> rows1, rows2;
		rows1 = new Vector<QUIKSecurityDescriptor>();
		rows1.add(descr1);
		rows1.add(descr3);
		rows2 = new Vector<QUIKSecurityDescriptor>();
		rows2.add(descr2);
		for ( QUIKSecurityDescriptor descr : rows1 ) cache.set(descr);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vType = new Variant<EventType>(vDisp)
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<List<QUIKSecurityDescriptor>> vRows =
				new Variant<List<QUIKSecurityDescriptor>>(vType)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		DescriptorsCache x, found = null;
		do {
			x = new DescriptorsCache(vDisp.get(), vType.get());
			for ( QUIKSecurityDescriptor descr : vRows.get() ) x.set(descr);
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(type, found.OnUpdate());
		for ( QUIKSecurityDescriptor descr : rows1 ) {
			assertSame(descr, cache.get(descr.getShortName()));
			assertSame(descr,
					cache.get(descr.getSystemCode(), descr.getClassCode()));
		}
		List<SecurityDescriptor> expected = new Vector<SecurityDescriptor>();
		expected.add(descr1);
		expected.add(descr3);
		assertEquals(expected, cache.get());
	}

}
