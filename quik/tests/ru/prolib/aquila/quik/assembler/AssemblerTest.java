package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class AssemblerTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private Cache cache;
	private AssemblerHighLvl high;
	private Assembler assembler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		high = control.createMock(AssemblerHighLvl.class);
		terminal = new TerminalBuilder().createTerminal("foo");
		cache = new CacheBuilder().createCache(terminal);
		assembler = new Assembler(terminal, cache, high);
	}
	
	@Test
	public void testStart() throws Exception {
		assembler.start();
		
		assertTrue(terminal.OnSecurityAvailable().isListener(assembler));
		assertTrue(terminal.OnPortfolioAvailable().isListener(assembler));
		assertTrue(terminal.OnOrderAvailable().isListener(assembler));
		assertTrue(terminal.OnStopOrderAvailable().isListener(assembler));
		assertTrue(cache.OnOrdersCacheUpdate().isListener(assembler));
		assertTrue(cache.OnPortfoliosFCacheUpdate().isListener(assembler));
		assertTrue(cache.OnPositionsFCacheUpdate().isListener(assembler));
		assertTrue(cache.OnSecuritiesCacheUpdate().isListener(assembler));
		assertTrue(cache.OnTradesCacheUpdate().isListener(assembler));
		assertTrue(cache.OnStopOrdersCacheUpdate().isListener(assembler));
	}
	
	@Test
	public void testStop() throws Exception {
		assembler.start();
		assembler.stop();
		
		assertFalse(terminal.OnSecurityAvailable().isListener(assembler));
		assertFalse(terminal.OnPortfolioAvailable().isListener(assembler));
		assertFalse(terminal.OnOrderAvailable().isListener(assembler));
		assertFalse(terminal.OnStopOrderAvailable().isListener(assembler));
		assertFalse(cache.OnOrdersCacheUpdate().isListener(assembler));
		assertFalse(cache.OnPortfoliosFCacheUpdate().isListener(assembler));
		assertFalse(cache.OnPositionsFCacheUpdate().isListener(assembler));
		assertFalse(cache.OnSecuritiesCacheUpdate().isListener(assembler));
		assertFalse(cache.OnTradesCacheUpdate().isListener(assembler));
		assertFalse(cache.OnStopOrdersCacheUpdate().isListener(assembler));
	}
	
	@Test
	public void testOnEvent_OnSecurityAvailable() throws Exception {
		high.adjustOrders();
		high.adjustStopOrders();
		high.adjustPositions();
		control.replay();
		
		assembler.onEvent(new EventImpl(terminal.OnSecurityAvailable()));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnPortfolioAvailable() throws Exception {
		high.adjustOrders();
		high.adjustStopOrders();
		high.adjustPositions();
		control.replay();
		
		assembler.onEvent(new EventImpl(terminal.OnPortfolioAvailable()));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnOrderAvailable() throws Exception {
		high.adjustOrders();
		high.adjustStopOrders();
		control.replay();
		
		assembler.onEvent(new EventImpl(terminal.OnOrderAvailable()));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnStopOrderAvailable() throws Exception {
		high.adjustStopOrders();
		control.replay();
		
		assembler.onEvent(new EventImpl(terminal.OnStopOrderAvailable()));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnOrdersCacheUpdate() throws Exception {
		high.adjustOrders();
		control.replay();
		
		assembler.onEvent(new EventImpl(cache.OnOrdersCacheUpdate()));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnPortfoliosFCacheUpdate() throws Exception {
		high.adjustPortfolios();
		control.replay();
		
		assembler.onEvent(new EventImpl(cache.OnPortfoliosFCacheUpdate()));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnPositionsFCacheUpdate() throws Exception {
		high.adjustPositions();
		control.replay();
		
		assembler.onEvent(new EventImpl(cache.OnPositionsFCacheUpdate()));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnSecuritiesCacheUpdate() throws Exception {
		high.adjustSecurities();
		control.replay();
		
		assembler.onEvent(new EventImpl(cache.OnSecuritiesCacheUpdate()));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnTradesCacheUpdate() throws Exception {
		high.adjustOrders();
		high.adjustPositions();
		control.replay();
		
		assembler.onEvent(new EventImpl(cache.OnTradesCacheUpdate()));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnStopOrdersCacheUpdate() throws Exception {
		high.adjustStopOrders();
		control.replay();
		
		assembler.onEvent(new EventImpl(cache.OnStopOrdersCacheUpdate()));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(assembler.equals(assembler));
		assertFalse(assembler.equals(null));
		assertFalse(assembler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		TerminalBuilder tb = new TerminalBuilder();
		CacheBuilder cb = new CacheBuilder();
		EditableTerminal t1 = tb.createTerminal("foo"),
			t2 = tb.createTerminal("foo");
		Cache c1 = cb.createCache(t1), c2 = cb.createCache(t2);
		assembler = new Assembler(t1, c1, high);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<Cache> vCache = new Variant<Cache>(vTerm)
			.add(c1)
			.add(c2);
		Variant<AssemblerHighLvl> vHi = new Variant<AssemblerHighLvl>(vCache)
			.add(high)
			.add(control.createMock(AssemblerHighLvl.class));
		Variant<?> iterator = vHi;
		int foundCnt = 0;
		Assembler x = null, found = null;
		do {
			x = new Assembler(vTerm.get(), vCache.get(), vHi.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(c1, found.getCache());
		assertSame(high, found.getAssemblerHighLevel());
	}

}
