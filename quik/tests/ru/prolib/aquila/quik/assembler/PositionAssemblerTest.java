package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class PositionAssemblerTest {
	private static Account account;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditablePortfolio portfolio;
	private EditablePosition position;
	private Security security;
	private Cache cache;
	private PositionFCache entry;
	private PositionAssembler assembler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account = new Account("TEST", "eqe01");
		descr = new SecurityDescriptor("RIM3", "SPBF", "USD", SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		portfolio = control.createMock(EditablePortfolio.class);
		position = control.createMock(EditablePosition.class);
		security = control.createMock(Security.class);
		cache = control.createMock(Cache.class);
		entry = new PositionFCache("eqe01", "TEST", "RIM3", 10L, 0L, 10.0d);
		assembler = new PositionAssembler(terminal, cache);
	}
	
	@Test
	public void testAdjustByCache_SkipIfPortfolioNotExists() throws Exception {
		expect(terminal.isPortfolioAvailable(account)).andReturn(false);
		control.replay();
		
		assembler.adjustByCache(entry);
		
		control.verify();
	}
	
	@Test
	public void testAdjustByCache_SkipIfDescrNotRegistered() throws Exception {
		expect(terminal.isPortfolioAvailable(account)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(false);
		control.replay();
		
		assembler.adjustByCache(entry);
		
		control.verify();
	}
	
	@Test
	public void testAdjustByCache_SkipIfNoSecurity() throws Exception {
		expect(terminal.isPortfolioAvailable(account)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(true);
		expect(cache.getSecurityDescriptorByName("RIM3")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(false);
		control.replay();
		
		assembler.adjustByCache(entry);
		
		control.verify();
	}
	
	@Test
	public void testAdjustByCache_New() throws Exception {
		expect(terminal.isPortfolioAvailable(account)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(true);
		expect(cache.getSecurityDescriptorByName("RIM3")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(account)).andReturn(portfolio);
		expect(terminal.getSecurity(descr)).andReturn(security);
		expect(portfolio.getEditablePosition(security)).andReturn(position);
		position.setCurrQty(0L);
		position.setOpenQty(10L);
		position.setVarMargin(10.0d);
		expect(position.isAvailable()).andReturn(false);
		portfolio.firePositionAvailableEvent(position);
		position.setAvailable(true);
		position.resetChanges();
		control.replay();
		
		assembler.adjustByCache(entry);
		
		control.verify();
	}

	@Test
	public void testAdjustByCache_UpdateExisting() throws Exception {
		expect(terminal.isPortfolioAvailable(account)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(true);
		expect(cache.getSecurityDescriptorByName("RIM3")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(account)).andReturn(portfolio);
		expect(terminal.getSecurity(descr)).andReturn(security);
		expect(portfolio.getEditablePosition(security)).andReturn(position);
		position.setCurrQty(0L);
		position.setOpenQty(10L);
		position.setVarMargin(10.0d);
		expect(position.isAvailable()).andReturn(true);
		position.fireChangedEvent();
		position.resetChanges();
		control.replay();
		
		assembler.adjustByCache(entry);
		
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
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<Cache> vCache = new Variant<Cache>(vTerm)
			.add(cache)
			.add(control.createMock(Cache.class));
		Variant<?> iterator = vCache;
		int foundCnt = 0;
		PositionAssembler x = null, found = null;
		do {
			x = new PositionAssembler(vTerm.get(), vCache.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(cache, found.getCache());
	}


}
