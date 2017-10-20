package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.assembler.cache.*;

public class AssemblerMidLvlTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private AssemblerLowLvl low;
	private EditablePortfolio port;
	private EditableSecurity security;
	private EditablePosition position;
	private IBEditableTerminal term;
	private AssemblerMidLvl asm;
	private Cache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		descr = new SecurityDescriptor("SBER", "EQBR", "SUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		low = control.createMock(AssemblerLowLvl.class);
		port = control.createMock(EditablePortfolio.class);
		security = control.createMock(EditableSecurity.class);
		position = control.createMock(EditablePosition.class);
		term = control.createMock(IBEditableTerminal.class);
		cache = control.createMock(Cache.class);
		asm = new AssemblerMidLvl(low);
		expect(low.getTerminal()).andStubReturn(term);
		expect(low.getCache()).andStubReturn(cache);
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
		expect(low.getCache()).andReturn(cache);
		control.replay();
		
		assertSame(cache, asm.getCache());
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Contract_ExistingSecurity() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(entry.getSecurityDescriptor()).andReturn(descr);		
		expect(term.getEditableSecurity(descr)).andReturn(security);
		low.update(same(security), same(entry));
		expect(security.isAvailable()).andReturn(true);
		term.fireEvents(same(security));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Contract_NewSecurity() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(entry.getSecurityDescriptor()).andReturn(descr);		
		expect(term.getEditableSecurity(descr)).andReturn(security);
		low.update(same(security), same(entry));
		expect(security.isAvailable()).andReturn(false);
		low.startMktData(same(security), same(entry));
		term.fireEvents(same(security));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Portfolio_IfAvailable() throws Exception {
		PortfolioValueEntry e = new PortfolioValueEntry("TEST", "k", "c", "v");
		Account a = new Account("TEST");
		expect(term.getEditablePortfolio(eq(a))).andReturn(port);
		low.update(same(port), same(e));
		expect(low.isAvailable(same(port))).andReturn(true);
		term.fireEvents(same(port));
		control.replay();
		
		asm.update(e);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Portfolio_IfNotAvailable() throws Exception {
		PortfolioValueEntry e = new PortfolioValueEntry("TEST", "k", "c", "v");
		Account a = new Account("TEST");
		expect(term.getEditablePortfolio(eq(a))).andReturn(port);
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
	public void testUpdate_Position_NoContractEntry() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(entry.getContractId()).andReturn(91215);
		expect(cache.getContract(eq(91215))).andReturn(null);
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Position_NoSecurity() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		ContractEntry eCont = control.createMock(ContractEntry.class);
		expect(entry.getContractId()).andReturn(91215);
		expect(cache.getContract(eq(91215))).andReturn(eCont);
		expect(eCont.getSecurityDescriptor()).andReturn(descr);
		expect(term.getSecurity(descr)).andThrow(new SecurityException("test"));
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Position() throws Exception {
		Account a = new Account("foo");
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(entry.getContractId()).andStubReturn(91215);
		expect(entry.getAccount()).andStubReturn(a);
		
		ContractEntry eCont = control.createMock(ContractEntry.class);
		expect(eCont.getSecurityDescriptor()).andStubReturn(descr);
		
		expect(cache.getContract(eq(91215))).andReturn(eCont);
		expect(term.getSecurity(descr)).andReturn(security);
		expect(term.getEditablePortfolio(eq(a))).andReturn(port);
		expect(port.getEditablePosition(security)).andReturn(position);
		low.update(position, entry);
		port.fireEvents(position);
		control.replay();
		
		asm.update(entry);
		
		control.verify();
	}

}
