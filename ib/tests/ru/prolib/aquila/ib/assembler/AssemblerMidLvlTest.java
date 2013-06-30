package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.assembler.cache.*;

public class AssemblerMidLvlTest {
	private IMocksControl control;
	private AssemblerLowLvl low;
	private EditablePortfolio port;
	private EditableSecurity security;
	private EditablePosition position;
	private IBEditableTerminal term;
	private AssemblerMidLvl asm;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		low = control.createMock(AssemblerLowLvl.class);
		port = control.createMock(EditablePortfolio.class);
		security = control.createMock(EditableSecurity.class);
		position = control.createMock(EditablePosition.class);
		term = control.createMock(IBEditableTerminal.class);
		asm = new AssemblerMidLvl(low);
		expect(low.getTerminal()).andStubReturn(term);
	}
	
	@Test
	public void testGetTerminal() throws Exception {
		IBEditableTerminal term = control.createMock(IBEditableTerminal.class);
		expect(low.getTerminal()).andReturn(term);
		control.replay();
		
		assertSame(term, asm.getTerminal());
		
		control.verify();
	}
	
	@Test
	public void testGetCache() throws Exception {
		Cache cache = control.createMock(Cache.class);
		expect(low.getCache()).andReturn(cache);
		control.replay();
		
		assertSame(cache, asm.getCache());
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Contract_IfAvailable() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(low.getSecurity(entry)).andReturn(security);
		low.update(same(security), same(entry));
		expect(security.isAvailable()).andReturn(true);
		low.fireEvents(same(security));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Contract_IfNotAvailable() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(low.getSecurity(entry)).andReturn(security);
		low.update(same(security), same(entry));
		expect(security.isAvailable()).andReturn(false);
		low.startMktData(same(security), same(entry));
		low.fireEvents(same(security));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Portfolio_IfAvailable() throws Exception {
		PortfolioValueEntry e = new PortfolioValueEntry("TEST", "k", "c", "v");
		expect(low.getPortfolio(eq(new Account("TEST")))).andReturn(port);
		low.update(same(port), same(e));
		expect(low.isAvailable(same(port))).andReturn(true);
		low.fireEvents(same(port));
		control.replay();
		
		asm.update(e);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Portfolio_IfNotAvailable() throws Exception {
		PortfolioValueEntry e = new PortfolioValueEntry("TEST", "k", "c", "v");
		expect(low.getPortfolio(eq(new Account("TEST")))).andReturn(port);
		low.update(same(port), same(e));
		expect(low.isAvailable(same(port))).andReturn(false);
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
		Variant<AssemblerLowLvl> vLow = new Variant<AssemblerLowLvl>()
			.add(low)
			.add(control.createMock(AssemblerLowLvl.class));
		Variant<?> iterator = vLow;
		int foundCnt = 0;
		AssemblerMidLvl x, found = null;
		do {
			x = new AssemblerMidLvl(vLow.get());
			if ( asm.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(low, found.getLowLevelAssembler());
	}
	
	@Test
	public void testConstruct1_UsingTerminal() throws Exception {
		AssemblerMidLvl expect = new AssemblerMidLvl(new AssemblerLowLvl(term));
		assertEquals(expect, new AssemblerMidLvl(term));
	}
	
	@Test
	public void testUpdate_Position_NoSecurity() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(entry.getContractId()).andReturn(91215);
		expect(low.getSecurity(91215)).andReturn(null);
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Position() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(entry.getContractId()).andReturn(91215);
		expect(low.getSecurity(91215)).andReturn(security);
		expect(entry.getAccount()).andReturn(new Account("TEST"));
		expect(low.getPortfolio(eq(new Account("TEST")))).andReturn(port);
		expect(port.getEditablePosition(same(security))).andReturn(position);
		low.update(same(position), same(entry));
		low.fireEvents(same(position));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Order() throws Exception {
		fail("TODO: incomplete");
	}

}
