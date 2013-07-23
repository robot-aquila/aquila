package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.quik.QUIKEditableTerminal;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.*;

public class AssemblerL1Test {
	private IMocksControl control;
	private QUIKEditableTerminal terminal;
	private Cache cache;
	private PositionsCache posCache;
	private TradesCache tradesCache;
	private AssemblerL2 l2;
	private AssemblerL1 asm;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKEditableTerminal.class);
		cache = control.createMock(Cache.class);
		posCache = control.createMock(PositionsCache.class);
		tradesCache = control.createMock(TradesCache.class);
		l2 = control.createMock(AssemblerL2.class);
		asm = new AssemblerL1(terminal, l2);
		
		expect(terminal.getDataCache()).andStubReturn(cache);
		expect(cache.getPositionsCache()).andStubReturn(posCache);
		expect(cache.getTradesCache()).andStubReturn(tradesCache);
	}
	
	@Test
	public void testTryAssemble_Portfolio() throws Exception {
		PortfolioEntry entry = control.createMock(PortfolioEntry.class);
		expect(l2.tryAssemble(same(entry))).andReturn(true);
		control.replay();
		
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Position() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(l2.tryAssemble(same(entry))).andReturn(true);
		control.replay();
		
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Security() throws Exception {
		SecurityEntry entry = control.createMock(SecurityEntry.class);
		expect(l2.tryAssemble(same(entry))).andReturn(true);
		control.replay();
		
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemblePositions() throws Exception {
		List<PositionEntry> list = new Vector<PositionEntry>();
		list.add(control.createMock(PositionEntry.class));
		list.add(control.createMock(PositionEntry.class));
		list.add(control.createMock(PositionEntry.class));
		list.add(control.createMock(PositionEntry.class));
		expect(posCache.get(eq("LKOH"))).andReturn(list);
		expect(l2.tryAssemble(same(list.get(0)))).andReturn(false);
		expect(l2.tryAssemble(same(list.get(1)))).andReturn(true);
		posCache.purge(same(list.get(1)));
		expect(l2.tryAssemble(same(list.get(2)))).andReturn(true);
		posCache.purge(same(list.get(2)));
		expect(l2.tryAssemble(same(list.get(3)))).andReturn(false);
		control.replay();
		
		asm.tryAssemblePositions("LKOH");
		
		control.verify();
	}

	@Test
	public void testTryAssembleTrades_NoBlock() throws Exception {
		expect(tradesCache.getFirst()).andReturn(null);
		control.replay();
		
		asm.tryAssembleTrades();
		
		control.verify();
	}

	@Test
	public void testTryAssembleTrades_UnfinishedBlock() throws Exception {
		TradesEntry entry = control.createMock(TradesEntry.class);
		expect(tradesCache.getFirst()).andReturn(entry);
		expect(l2.tryAssemble(same(entry))).andReturn(false);
		control.replay();
		
		asm.tryAssembleTrades();
		
		control.verify();
	}
	
	@Test
	public void testTryAssembleTrades_FinishedBlock() throws Exception {
		TradesEntry entry = control.createMock(TradesEntry.class);
		// first block
		expect(tradesCache.getFirst()).andReturn(entry);
		expect(l2.tryAssemble(same(entry))).andReturn(true);
		tradesCache.purgeFirst();
		// second block
		expect(tradesCache.getFirst()).andReturn(entry);
		expect(l2.tryAssemble(same(entry))).andReturn(true);
		tradesCache.purgeFirst();
		// no more blocks
		expect(tradesCache.getFirst()).andReturn(null);
		control.replay();
		
		asm.tryAssembleTrades();
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKEditableTerminal> vTerm = new Variant<QUIKEditableTerminal>()
			.add(terminal)
			.add(control.createMock(QUIKEditableTerminal.class));
		Variant<AssemblerL2> vL2 = new Variant<AssemblerL2>(vTerm)
			.add(l2)
			.add(control.createMock(AssemblerL2.class));
		Variant<?> iterator = vL2;
		int foundCnt = 0;
		AssemblerL1 x, found = null;
		do {
			x = new AssemblerL1(vTerm.get(), vL2.get());
			if ( asm.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(l2, found.getAssemblerL2());
	}
	
	@Test
	public void testConstruct_Short() throws Exception {
		asm = new AssemblerL1(terminal, new AssemblerL2(terminal));
		assertEquals(asm, new AssemblerL1(terminal));
	}
	
	@Test
	public void testCorrectOrderNumerator_FixIfGreater() throws Exception {
		Counter numerator = control.createMock(Counter.class);
		T2QOrder order = control.createMock(T2QOrder.class);
		expect(order.getTransId()).andReturn(101L);
		expect(terminal.getOrderNumerator()).andReturn(numerator);
		expect(numerator.get()).andReturn(100);
		numerator.set(eq(101));
		control.replay();
		
		asm.correctOrderNumerator(order);
		
		control.verify();
	}
	
	@Test
	public void testCorrectOrderNumerator_SkipIfLessOrEq() throws Exception {
		Counter numerator = control.createMock(Counter.class);
		T2QOrder order = control.createMock(T2QOrder.class);
		expect(order.getTransId()).andReturn(80L);
		expect(terminal.getOrderNumerator()).andReturn(numerator);
		expect(numerator.get()).andReturn(100);
		control.replay();
		
		asm.correctOrderNumerator(order);
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Order_NoOrder() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		expect(l2.tryGetOrder(same(entry))).andReturn(null);
		control.replay();
		
		asm.tryAssemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Order() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		expect(entry.getOrderId()).andStubReturn(827L);
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(l2.tryGetOrder(same(entry))).andReturn(order);
		l2.tryActivate(same(order));
		List<T2QTrade> list = new Vector<T2QTrade>();
		list.add(control.createMock(T2QTrade.class));
		list.add(control.createMock(T2QTrade.class));
		list.add(control.createMock(T2QTrade.class));
		expect(cache.getOwnTradesByOrder(eq(827L))).andReturn(list);
		l2.tryAssemble(same(order), same(list.get(0)));
		l2.tryAssemble(same(order), same(list.get(1)));
		l2.tryAssemble(same(order), same(list.get(2)));
		expect(l2.tryFinalize(same(order), same(entry))).andReturn(true);
		control.replay();
		
		asm.tryAssemble(entry);
		
		control.verify();
	}

}
