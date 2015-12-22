package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class SymbolsCacheTest {
	private static QUIKSymbol symbol1, symbol2, symbol3, symbol4;
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType type;
	private SymbolsCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol1 = new QUIKSymbol("LKOH", "RTSST", ISO4217.RUB, SymbolType.STOCK, "LKOH", "LKOH", "Лукоил");
		symbol2 = new QUIKSymbol("LKOH", "EQBR",  ISO4217.RUB, SymbolType.STOCK, "LKOH", "Лукоил", "АО ЛУКОИЛ");
		symbol3 = new QUIKSymbol("RTS-12.13", "SPBFUT", ISO4217.USD, SymbolType.FUTURE, "RIZ3", "RIZ3", "RTS-12.13");
		symbol4 = new QUIKSymbol("RTS-12.3", "SPBFUT", ISO4217.USD, SymbolType.FUTURE, "RIZ3", "RIZ3", "RTS-12.3");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		type = control.createMock(EventType.class);
		cache = new SymbolsCache(dispatcher, type);
	}
	
	@Test
	public void testGet_ByShortName() throws Exception {
		cache.set(symbol1);
		cache.set(symbol2);
		cache.set(symbol3);
		
		assertEquals(symbol1, cache.get("LKOH"));
		assertEquals(symbol2, cache.get("Лукоил"));
		assertEquals(symbol3, cache.get("RIZ3"));
		assertNull(cache.get("zulu24"));
	}
	
	@Test
	public void testGet_ByShortName_LastForSameNames() throws Exception {
		cache.set(symbol4);
		
		assertSame(symbol4, cache.get("RIZ3"));
		
		cache.set(symbol3);
		
		assertSame(symbol3, cache.get("RIZ3"));
	}
	
	@Test
	public void testGet_BySystemCodeAndClass() throws Exception {
		cache.set(symbol1);
		cache.set(symbol2);
		cache.set(symbol3);
		
		assertEquals(symbol1, cache.get("LKOH", "RTSST"));
		assertEquals(symbol2, cache.get("LKOH", "EQBR"));
		assertEquals(symbol3, cache.get("RIZ3", "SPBFUT"));
		assertNull(cache.get("zulu24", "buzz"));
		assertNull(cache.get("LKOH", "buzz"));
		assertNull(cache.get("zulu24", "EQBR"));
	}
	
	@Test
	public void testGet_BySystemCodeAndClass_LastForSameCom() throws Exception {
		cache.set(symbol4);
		
		assertSame(symbol4, cache.get("RIZ3", "SPBFUT"));
		
		cache.set(symbol3);
		
		assertSame(symbol3, cache.get("RIZ3", "SPBFUT"));
	}
	
	@Test
	public void testGet_All() throws Exception {
		cache.set(symbol1);
		cache.set(symbol2);
		cache.set(symbol3);
		cache.set(symbol4);
		
		List<QUIKSymbol> expected = new Vector<QUIKSymbol>();
		expected.add(symbol1);
		expected.add(symbol2);
		expected.add(symbol3);
		expected.add(symbol4);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testPut_New() throws Exception {
		dispatcher.dispatch(eq(new EventImpl(type)));
		control.replay();
		
		assertTrue(cache.put(symbol2));
		
		control.verify();
		assertEquals(symbol2, cache.get("Лукоил"));
	}
	
	@Test
	public void testPut_Existing() throws Exception {
		cache.set(symbol3);
		control.replay();
		
		assertFalse(cache.put(symbol3));
		
		control.verify();
		assertEquals(symbol3, cache.get("RIZ3"));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<QUIKSymbol> rows1, rows2;
		rows1 = new Vector<QUIKSymbol>();
		rows1.add(symbol1);
		rows1.add(symbol3);
		rows2 = new Vector<QUIKSymbol>();
		rows2.add(symbol2);
		for ( QUIKSymbol symbol : rows1 ) cache.set(symbol);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vType = new Variant<EventType>(vDisp)
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<List<QUIKSymbol>> vRows =
				new Variant<List<QUIKSymbol>>(vType)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		SymbolsCache x, found = null;
		do {
			x = new SymbolsCache(vDisp.get(), vType.get());
			for ( QUIKSymbol symbol : vRows.get() ) x.set(symbol);
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(type, found.OnUpdate());
		for ( QUIKSymbol symbol : rows1 ) {
			assertSame(symbol, cache.get(symbol.getShortName()));
			assertSame(symbol,
					cache.get(symbol.getSystemCode(), symbol.getExchangeID()));
		}
		List<Symbol> expected = new Vector<Symbol>();
		expected.add(symbol1);
		expected.add(symbol3);
		assertEquals(expected, cache.get());
	}

}
