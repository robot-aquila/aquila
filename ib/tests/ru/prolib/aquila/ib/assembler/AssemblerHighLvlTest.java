package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.assembler.cache.*;

public class AssemblerHighLvlTest {
	private IMocksControl control;
	private AssemblerMidLvl middle;
	private Cache cache;
	private AssemblerHighLvl asm;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		middle = control.createMock(AssemblerMidLvl.class);
		cache = control.createMock(Cache.class);
		asm = new AssemblerHighLvl(middle);
		
		expect(middle.getCache()).andStubReturn(cache);
	}
	
	@Test
	public void testUpdate_Portfolio() throws Exception {
		PortfolioValueEntry e = control.createMock(PortfolioValueEntry.class);
		middle.update(same(e));
		control.replay();
		
		asm.update(e);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Contract() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		middle.update(same(entry));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(asm.equals(asm));
		assertFalse(asm.equals(null));
		assertFalse(asm.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<AssemblerMidLvl> vMid = new Variant<AssemblerMidLvl>()
			.add(middle)
			.add(control.createMock(AssemblerMidLvl.class));
		Variant<?> iterator = vMid;
		int foundCnt = 0;
		AssemblerHighLvl x, found = null;
		do {
			x = new AssemblerHighLvl(vMid.get());
			if ( asm.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(middle, found.getMiddleLevelAssembler());
	}
	
	@Test
	public void testConstruct1_UsingTerminal() throws Exception {
		IBEditableTerminal term = control.createMock(IBEditableTerminal.class);
		AssemblerHighLvl exp = new AssemblerHighLvl(new AssemblerMidLvl(term));
		assertEquals(exp, new AssemblerHighLvl(term));
	}
	
	@Test
	public void testGetTerminal() throws Exception {
		IBEditableTerminal term = control.createMock(IBEditableTerminal.class);
		expect(middle.getTerminal()).andReturn(term);
		control.replay();
		
		assertSame(term, asm.getTerminal());
		
		control.verify();
	}
	
	@Test
	public void testGetCache() throws Exception {
		Cache cache = control.createMock(Cache.class);
		expect(middle.getCache()).andReturn(cache);
		control.replay();
		
		assertSame(cache, asm.getCache());
		
		control.verify();
	}
	
	@Test
	public void testAssembleOrders() throws Exception {
		List<OrderEntry> list = new Vector<OrderEntry>();
		list.add(control.createMock(OrderEntry.class));
		list.add(control.createMock(OrderEntry.class));
		list.add(control.createMock(OrderEntry.class));
		expect(cache.getOrderEntries()).andReturn(list);
		for ( int i = 0; i < list.size(); i ++ ) {
			middle.update(same(list.get(i)));
		}
		control.replay();
		
		asm.assembleOrders();
		
		control.verify();
	}
	
	@Test
	public void testAssembleOrder_Order() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		middle.update(same(entry));
		control.replay();
		
		asm.assembleOrder(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssembleOrder_OrderStatus_OrderFound() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(entry.getId()).andReturn(254L);
		OrderEntry order = control.createMock(OrderEntry.class);
		expect(cache.getOrder(eq(254L))).andReturn(order);
		middle.update(same(order));
		control.replay();
		
		asm.assembleOrder(entry);
		
		control.verify();
	}
	
	@Test
	public void testAssembleOrder_OrderStatus_OrderNotFound() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(entry.getId()).andReturn(254L);
		expect(cache.getOrder(eq(254L))).andReturn(null);
		control.replay();
		
		asm.assembleOrder(entry);
		
		control.verify();
	}

	@Test
	public void testAssemblePositions() throws Exception {
		List<PositionEntry> list = new Vector<PositionEntry>();
		list.add(control.createMock(PositionEntry.class));
		list.add(control.createMock(PositionEntry.class));
		list.add(control.createMock(PositionEntry.class));
		list.add(control.createMock(PositionEntry.class));
		expect(cache.getPositionEntries()).andReturn(list);
		for ( int i = 0; i < list.size(); i ++ ) {
			middle.update(same(list.get(i)));
		}
		control.replay();
		
		asm.assemblePositions();
		
		control.verify();
	}
	
	@Test
	public void testAssemblePosition() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		middle.update(same(entry));
		control.replay();
		
		asm.assemblePosition(entry);
		
		control.verify();
	}

}
