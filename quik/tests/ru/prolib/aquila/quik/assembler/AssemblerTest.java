package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class AssemblerTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private Cache cache, cacheMock;
	private AssemblerHighLvl high;
	private Assembler assembler, assembler2;
	
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
		cacheMock = control.createMock(Cache.class);
		assembler = new Assembler(terminal, cache, high);
		assembler2 = new Assembler(terminal, cacheMock, high);
	}
	
	@Test
	public void testStart() throws Exception {
		high.start();
		control.replay();
		
		assembler.start();
		
		control.verify();
		assertTrue(cache.OnOrdersCacheUpdate().isListener(assembler));
		assertTrue(cache.OnPortfoliosFCacheUpdate().isListener(assembler));
		assertTrue(cache.OnPositionsFCacheUpdate().isListener(assembler));
		assertTrue(cache.OnSecuritiesCacheUpdate().isListener(assembler));
		assertTrue(cache.OnTradesCacheUpdate().isListener(assembler));
		assertTrue(cache.OnStopOrdersCacheUpdate().isListener(assembler));
	}
	
	@Test
	public void testStop() throws Exception {
		high.start();
		high.stop();
		control.replay();

		assembler.start();
		assembler.stop();
		
		control.verify();
		assertFalse(cache.OnOrdersCacheUpdate().isListener(assembler));
		assertFalse(cache.OnPortfoliosFCacheUpdate().isListener(assembler));
		assertFalse(cache.OnPositionsFCacheUpdate().isListener(assembler));
		assertFalse(cache.OnSecuritiesCacheUpdate().isListener(assembler));
		assertFalse(cache.OnTradesCacheUpdate().isListener(assembler));
		assertFalse(cache.OnStopOrdersCacheUpdate().isListener(assembler));
	}
	
	@Test
	public void testOnEvent() throws Exception {
		high.adjustSecurities();
		high.adjustPortfolios();
		high.adjustStopOrders();
		expect(cacheMock.hasFilledWithoutLinkedId()).andReturn(false);
		high.adjustOrders();
		high.adjustPositions();
		control.replay();
		
		assembler2.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_UnadjustedStops() throws Exception {
		high.adjustSecurities();
		high.adjustPortfolios();
		high.adjustStopOrders();
		expect(cacheMock.hasFilledWithoutLinkedId()).andReturn(true);
		high.adjustPositions();
		control.replay();
		
		assembler2.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_IfAdjustOrderThrows() throws Exception {
		terminal = control.createMock(EditableTerminal.class);
		assembler = new Assembler(terminal, cache, high);
		high.adjustSecurities();
		high.adjustPortfolios();
		high.adjustStopOrders();
		high.adjustOrders();
		expectLastCall().andThrow(new OrderAlreadyExistsException(null));
		terminal.firePanicEvent(eq(2), eq("Multithreading related issue."));
		control.replay();
		
		assembler.onEvent(null);
		
		control.verify();
	}

	@Test
	public void testOnEvent_IfAdjustStopOrderThrows() throws Exception {
		terminal = control.createMock(EditableTerminal.class);
		assembler = new Assembler(terminal, cache, high);
		high.adjustSecurities();
		high.adjustPortfolios();
		high.adjustStopOrders();
		expectLastCall().andThrow(new OrderAlreadyExistsException(null));
		terminal.firePanicEvent(eq(2), eq("Multithreading related issue."));
		control.replay();
		
		assembler.onEvent(null);
		
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
