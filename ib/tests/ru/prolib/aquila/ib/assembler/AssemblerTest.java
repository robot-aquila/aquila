package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.assembler.cache.*;

public class AssemblerTest {
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private Cache cache;
	private AssemblerHighLvl high;
	private Assembler asm; 

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		cache = control.createMock(Cache.class);
		high = control.createMock(AssemblerHighLvl.class);
		asm = new Assembler(terminal, high);
		expect(terminal.getCache()).andStubReturn(cache);
	}
	
	@Test
	public void testUpdate_Contract() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		cache.update(same(entry));
		high.update(same(entry));
		high.assembleOrders();
		high.assemblePositions();
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Order_ContractNotExists() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		expect(entry.getContractId()).andStubReturn(215);
		cache.update(same(entry));
		expect(cache.getContract(215)).andReturn(null);
		terminal.requestContract(215);
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Order_ContractExists() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		ContractEntry contract = control.createMock(ContractEntry.class);
		expect(entry.getContractId()).andStubReturn(215);
		cache.update(entry);
		expect(cache.getContract(215)).andReturn(contract);
		high.assembleOrder(same(entry));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_OrderStatus() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		cache.update(same(entry));
		high.assembleOrder(same(entry));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Position_ContractNotExists() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(entry.getContractId()).andStubReturn(814);
		cache.update(same(entry));
		expect(cache.getContract(eq(814))).andReturn(null);
		terminal.requestContract(eq(814));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Position_ContractExists() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		ContractEntry contract = control.createMock(ContractEntry.class);
		expect(entry.getContractId()).andStubReturn(814);
		cache.update(same(entry));
		expect(cache.getContract(eq(814))).andReturn(contract);
		high.assemblePosition(same(entry));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Exec() throws Exception {
		ExecEntry entry = control.createMock(ExecEntry.class);
		cache.update(same(entry));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Portfolio() throws Exception {
		PortfolioValueEntry e = control.createMock(PortfolioValueEntry.class);
		high.update(same(e));
		control.replay();
		
		asm.update(e);
		
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
		IBTerminalBuilder tb = new IBTerminalBuilder();
		IBEditableTerminal t1 = (IBEditableTerminal) tb.createTerminal("foo"),
						   t2 = (IBEditableTerminal) tb.createTerminal("foo");
		asm = new Assembler(t1, high);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<AssemblerHighLvl> vHigh = new Variant<AssemblerHighLvl>(vTerm)
			.add(high)
			.add(control.createMock(AssemblerHighLvl.class));
		Variant<?> iterator = vHigh;
		int foundCnt = 0;
		Assembler x, found = null;
		do {
			x = new Assembler(vTerm.get(), vHigh.get());
			if ( asm.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(high, found.getHighLevelAssembler());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		Assembler exp = new Assembler(terminal, new AssemblerHighLvl(terminal));
		assertEquals(exp, new Assembler(terminal));
	}

}
