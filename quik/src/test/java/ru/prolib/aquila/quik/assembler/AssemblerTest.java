package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.ISO4217;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.data.row.RowSetException;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.T2QOrder;
import ru.prolib.aquila.t2q.T2QTrade;

public class AssemblerTest {
	private IMocksControl control;
	private QUIKTerminal terminal;
	private Cache cache;
	private SymbolsCache symbolsCache;
	private AssemblerL1 l1;
	private Assembler assembler;
	private QUIKSymbol symbol;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKTerminal.class);
		cache = control.createMock(Cache.class);
		symbolsCache = control.createMock(SymbolsCache.class);
		l1 = control.createMock(AssemblerL1.class);
		assembler = new Assembler(terminal, l1);
		symbol = new QUIKSymbol("RTS-12.13", "SPBFUT", ISO4217.USD,
				SymbolType.FUTURE, "RIZ3", "RIZ3", "RTS-12.13");
		
		expect(terminal.getDataCache()).andStubReturn(cache);
		expect(cache.getSymbolsCache()).andStubReturn(symbolsCache);
	}
	
	@Test
	public void testStart() throws Exception {
		EventType onSymbolsUpdate = control.createMock(EventType.class),
				  onTradesUpdate = control.createMock(EventType.class);
		expect(cache.OnSymbolsUpdate()).andStubReturn(onSymbolsUpdate);
		expect(cache.OnTradesUpdate()).andStubReturn(onTradesUpdate);
		onSymbolsUpdate.addListener(assembler);
		onTradesUpdate.addListener(assembler);
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		EventType onSymbolsUpdate = control.createMock(EventType.class),
				  onTradesUpdate = control.createMock(EventType.class);
		expect(cache.OnSymbolsUpdate()).andStubReturn(onSymbolsUpdate);
		expect(cache.OnTradesUpdate()).andStubReturn(onTradesUpdate);
		onTradesUpdate.removeListener(assembler);
		onSymbolsUpdate.removeListener(assembler);
		control.replay();

		assembler.stop();
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Portfolio() throws Exception {
		PortfolioEntry entry = control.createMock(PortfolioEntry.class);
		expect(l1.tryAssemble(same(entry))).andReturn(true);
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Position_Assembled() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(l1.tryAssemble(same(entry))).andReturn(true);
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Position_Cached() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(l1.tryAssemble(same(entry))).andReturn(false);
		cache.put(same(entry));
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Security_Existing() throws Exception {
		SecurityEntry entry = control.createMock(SecurityEntry.class);
		expect(entry.getSymbol()).andStubReturn(symbol);
		expect(l1.tryAssemble(same(entry))).andReturn(true);
		expect(symbolsCache.put(same(symbol))).andReturn(false);
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Security_New() throws Exception {
		SecurityEntry entry = control.createMock(SecurityEntry.class);
		expect(entry.getSymbol()).andStubReturn(symbol);
		expect(entry.getShortName()).andStubReturn("RIZ3");
		expect(l1.tryAssemble(same(entry))).andReturn(true);
		expect(symbolsCache.put(same(symbol))).andReturn(true);
		l1.tryAssemblePositions(eq("RIZ3"));
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Order() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		l1.correctOrderNumerator(same(entry));
		expect(l1.fixme(same(entry))).andReturn(entry);
		cache.put(same(entry));
		l1.tryAssemble(same(entry));
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_OwnTrade_OrderNotExists() throws Exception {
		T2QTrade entry = control.createMock(T2QTrade.class);
		cache.put(same(entry));
		expect(entry.getOrderId()).andReturn(2481L);
		expect(cache.getOrder(eq(2481L))).andReturn(null);
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_OwnTrade_OrderExists() throws Exception {
		T2QTrade entry = control.createMock(T2QTrade.class);
		T2QOrder order = control.createMock(T2QOrder.class);
		cache.put(same(entry));
		expect(entry.getOrderId()).andReturn(2481L);
		expect(cache.getOrder(eq(2481L))).andReturn(order);
		l1.tryAssemble(same(order));
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Trades_Ok() throws Exception {
		TradesEntry entry = control.createMock(TradesEntry.class);
		expect(entry.next()).andReturn(true);
		cache.add(same(entry));
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Trades_NoRows() throws Exception {
		TradesEntry entry = control.createMock(TradesEntry.class);
		expect(entry.next()).andReturn(false);
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Trades_ErrorPositioning() throws Exception {
		TradesEntry entry = control.createMock(TradesEntry.class);
		expect(entry.next()).andThrow(new RowSetException("test error"));
		expect(entry.count()).andReturn(1024);
		control.replay();
		
		assembler.assemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnSymbolsUpdate() throws Exception {
		EventTypeSI onSymbolsUpdate = control.createMock(EventTypeSI.class);
		expect(cache.OnSymbolsUpdate()).andStubReturn(onSymbolsUpdate);
		l1.tryAssembleTrades();
		control.replay();
		
		assembler.onEvent(new EventImpl(onSymbolsUpdate));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnTradesUpdate_BlockRemoved() throws Exception {
		EventTypeSI onSymbolsUpdate = control.createMock(EventTypeSI.class),
				  onTradesUpdate = control.createMock(EventTypeSI.class);
		expect(cache.OnSymbolsUpdate()).andStubReturn(onSymbolsUpdate);
		expect(cache.OnTradesUpdate()).andStubReturn(onTradesUpdate);
		control.replay();
		
		assembler.onEvent(new CacheEvent(onTradesUpdate, false));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnTradesUpdate_BlockAdded() throws Exception {
		EventTypeSI onSymbolsUpdate = control.createMock(EventTypeSI.class),
				  onTradesUpdate = control.createMock(EventTypeSI.class);
		expect(cache.OnSymbolsUpdate()).andStubReturn(onSymbolsUpdate);
		expect(cache.OnTradesUpdate()).andStubReturn(onTradesUpdate);
		l1.tryAssembleTrades();
		control.replay();
		
		assembler.onEvent(new CacheEvent(onTradesUpdate, true));
		
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
		Variant<QUIKTerminal> vTerm = new Variant<QUIKTerminal>()
			.add(terminal)
			.add(control.createMock(QUIKTerminal.class));
		Variant<AssemblerL1> vL1 = new Variant<AssemblerL1>(vTerm)
			.add(l1)
			.add(control.createMock(AssemblerL1.class));
		Variant<?> iterator = vL1;
		int foundCnt = 0;
		Assembler x, found = null;
		do {
			x = new Assembler(vTerm.get(), vL1.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(l1, found.getAssemblerL1());
	}
	
	@Test
	public void testConstruct_Short() throws Exception {
		assembler = new Assembler(terminal, new AssemblerL1(terminal));
		assertEquals(assembler, new Assembler(terminal));
	}
	
}
