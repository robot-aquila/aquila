package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.quik.assembler.cache.CacheBuilder;
import ru.prolib.aquila.quik.assembler.cache.PortfolioEntry;
import ru.prolib.aquila.quik.assembler.cache.PositionEntry;
import ru.prolib.aquila.quik.assembler.cache.SecurityEntry;
import ru.prolib.aquila.quik.dde.*;

public class AssemblerHighLvlTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private Cache cache;
	private AssemblerMidLvl middle;
	private AssemblerHighLvl assembler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		cache = control.createMock(Cache.class);
		middle = control.createMock(AssemblerMidLvl.class);
		assembler = new AssemblerHighLvl(terminal, cache, middle);
	}
	
	@Test
	public void testAdjustOrders() throws Exception {
		List<Order> orders = new Vector<Order>();
		orders.add(control.createMock(EditableOrder.class));
		orders.add(control.createMock(EditableOrder.class));
		orders.add(control.createMock(EditableOrder.class));

		List<OrderCache> entries = new Vector<OrderCache>();
		entries.add(control.createMock(OrderCache.class));
		expect(entries.get(0).getId()).andStubReturn(125L);
		entries.add(control.createMock(OrderCache.class));
		expect(entries.get(1).getId()).andStubReturn(824L);
		entries.add(control.createMock(OrderCache.class));
		expect(entries.get(2).getId()).andStubReturn(576L);
		
		expect(terminal.getOrders()).andReturn(orders);
		expect(middle.checkIfOrderRemoved((EditableOrder) same(orders.get(0))))
			.andReturn(false);
		expect(middle.checkIfOrderRemoved((EditableOrder) same(orders.get(1))))
			.andReturn(true);
		expect(middle.checkIfOrderRemoved((EditableOrder) same(orders.get(2))))
			.andReturn(false);
		
		expect(cache.getAllOrders()).andReturn(entries);
		// Для существующих заявок всегда выполняется обновление
		expect(terminal.isOrderExists(eq(125L))).andReturn(true);
		middle.updateExistingOrder(same(entries.get(0)));
		// Для новых заявок создание возможно только есть нет ожидающих
		expect(terminal.isOrderExists(eq(824L))).andReturn(false);
		expect(terminal.hasPendingOrders()).andReturn(true);
		// Если нет ожидающих, то новая заявка создается.
		// На практике, в одном проходе количество ожидаемых измениться не может
		// Здесь такое допущение исключительно в целях тестирования.
		expect(terminal.isOrderExists(eq(576L))).andReturn(false);
		expect(terminal.hasPendingOrders()).andReturn(false);
		middle.createNewOrder(same(entries.get(2)));
		control.replay();
		
		assembler.adjustOrders();
		
		control.verify();
	}
	
	@Test
	public void testAdjustStopOrders() throws Exception {
		List<Order> orders = new Vector<Order>();
		orders.add(control.createMock(EditableOrder.class));
		orders.add(control.createMock(EditableOrder.class));
		
		List<StopOrderCache> entries = new Vector<StopOrderCache>();
		entries.add(control.createMock(StopOrderCache.class));
		expect(entries.get(0).getId()).andStubReturn(415L);
		entries.add(control.createMock(StopOrderCache.class));
		expect(entries.get(1).getId()).andStubReturn(118L);
		entries.add(control.createMock(StopOrderCache.class));
		expect(entries.get(2).getId()).andStubReturn(314L);
		
		expect(terminal.getStopOrders()).andReturn(orders);
		expect(middle.checkIfStopOrderRemoved((EditableOrder)
				same(orders.get(0)))).andReturn(false);
		expect(middle.checkIfStopOrderRemoved((EditableOrder)
				same(orders.get(1)))).andReturn(true);
		
		expect(cache.getAllStopOrders()).andReturn(entries);
		expect(terminal.isStopOrderExists(415L)).andReturn(false);
		expect(terminal.hasPendingStopOrders()).andReturn(false);
		middle.createNewStopOrder(entries.get(0));
		
		expect(terminal.isStopOrderExists(118L)).andReturn(false);
		expect(terminal.hasPendingStopOrders()).andReturn(true);
		
		expect(terminal.isStopOrderExists(314L)).andReturn(true);
		expect(middle.updateExistingStopOrder(entries.get(2))).andReturn(true);
		control.replay();
		
		assembler.adjustStopOrders();
		
		control.verify();
	}
	
	@Test
	public void testAdjustSecurities() throws Exception {
		SecurityEntry c1 = control.createMock(SecurityEntry.class),
			c2 = control.createMock(SecurityEntry.class),
			c3 = control.createMock(SecurityEntry.class);
		List<SecurityEntry> list = new Vector<SecurityEntry>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		expect(cache.getAllSecurities()).andReturn(list);
		middle.updateSecurity(same(c1));
		middle.updateSecurity(same(c2));
		middle.updateSecurity(same(c3));
		control.replay();
		
		assembler.adjustSecurities();
		
		control.verify();
	}
	
	@Test
	public void testAdjustPortfolios() throws Exception {
		PortfolioEntry c1 = control.createMock(PortfolioEntry.class),
			c2 = control.createMock(PortfolioEntry.class),
			c3 = control.createMock(PortfolioEntry.class);
		List<PortfolioEntry> list = new Vector<PortfolioEntry>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		expect(cache.getAllPortfoliosF()).andReturn(list);
		middle.updatePortfolioFORTS(same(c1));
		middle.updatePortfolioFORTS(same(c2));
		middle.updatePortfolioFORTS(same(c3));
		control.replay();
		
		assembler.adjustPortfolios();
		
		control.verify();
	}
	
	@Test
	public void testAdjustPositions() throws Exception {
		PositionEntry c1 = control.createMock(PositionEntry.class),
			c2 = control.createMock(PositionEntry.class),
			c3 = control.createMock(PositionEntry.class);
		List<PositionEntry> list = new Vector<PositionEntry>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		expect(cache.getAllPositionsF()).andReturn(list);
		middle.updatePositionFORTS(same(c1));
		middle.updatePositionFORTS(same(c2));
		middle.updatePositionFORTS(same(c3));
		control.replay();
		
		assembler.adjustPositions();
		
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
		assembler = new AssemblerHighLvl(t1, c1, middle);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<Cache> vCache = new Variant<Cache>(vTerm)
			.add(c1)
			.add(c2);
		Variant<AssemblerMidLvl> vMid = new Variant<AssemblerMidLvl>(vCache)
			.add(middle)
			.add(control.createMock(AssemblerMidLvl.class));
		Variant<?> iterator = vMid;
		int foundCnt = 0;
		AssemblerHighLvl x = null, found = null;
		do {
			x = new AssemblerHighLvl(vTerm.get(), vCache.get(), vMid.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(c1, found.getCache());
		assertSame(middle, found.getAssemblerMidLevel());
	}
	
	@Test
	public void testStart() throws Exception {
		middle.start();
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		middle.stop();
		control.replay();
		
		assembler.stop();
		
		control.verify();
	}

}
