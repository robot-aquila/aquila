package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.data.S;
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
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Order() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		cache.update(same(entry));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_OrderStatus() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		cache.update(same(entry));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Position() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		cache.update(same(entry));
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdatePortfolio_NullCash() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(high.getPortfolio(eq("BEST"))).andReturn(port);
		setter.set(same(port), eq(24.12d));
		expect(port.getCash()).andReturn(null);
		control.replay();
		
		asm.updatePortfolio("BEST", setter, 24.12d);
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdatePortfolio_NullBalance() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(high.getPortfolio(eq("BEST"))).andReturn(port);
		setter.set(same(port), eq(24.12d));
		expect(port.getCash()).andReturn(81.21d);
		expect(port.getBalance()).andReturn(null);
		control.replay();
		
		asm.updatePortfolio("BEST", setter, 24.12d);
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdatePortfolio() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(high.getPortfolio(eq("BEST"))).andReturn(port);
		setter.set(same(port), eq(24.12d));
		expect(port.getCash()).andReturn(81.21d);
		expect(port.getBalance()).andReturn(15.43d);
		high.fireEvents(same(port));
		control.replay();
		
		asm.updatePortfolio("BEST", setter, 24.12d);
		
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
		asm = new Assembler(t1);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		Assembler x, found = null;
		do {
			x = new Assembler(vTerm.get());
			if ( asm.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
	}

}
