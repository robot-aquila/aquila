package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class PortfolioAssemblerTest {
	private static Account account;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditablePortfolio portfolio;
	private Cache cache;
	private PortfolioFCache entry;
	private PortfolioAssembler assembler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("SPBFUT", "eqe01", "eqe01");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		portfolio = control.createMock(EditablePortfolio.class);
		cache = control.createMock(Cache.class);
		entry = new PortfolioFCache("eqe01", "SPBFUT", 100.0d, 80.0d, -10.0d);
		assembler = new PortfolioAssembler(terminal, cache);
	}
	
	@Test
	public void testAdjustByCache_New() throws Exception {
		expect(terminal.isPortfolioAvailable(account)).andReturn(false);
		expect(terminal.createPortfolio(account)).andReturn(portfolio);
		portfolio.setBalance(100.0d);
		portfolio.setCash(80.0d);
		portfolio.setVariationMargin(-10.0d);
		expect(portfolio.isAvailable()).andReturn(false);
		cache.registerAccount(account);
		terminal.firePortfolioAvailableEvent(portfolio);
		portfolio.setAvailable(true);
		portfolio.resetChanges();
		control.replay();
		
		assembler.adjustByCache(entry);
		
		control.verify();
	}
	
	@Test
	public void testAdjustByCache_UpdateExisting() throws Exception {
		expect(terminal.isPortfolioAvailable(account)).andReturn(true);
		expect(terminal.getEditablePortfolio(account)).andReturn(portfolio);
		portfolio.setBalance(100.0d);
		portfolio.setCash(80.0d);
		portfolio.setVariationMargin(-10.0d);
		expect(portfolio.isAvailable()).andReturn(true);
		portfolio.fireChangedEvent();
		portfolio.resetChanges();
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
		PortfolioAssembler x = null, found = null;
		do {
			x = new PortfolioAssembler(vTerm.get(), vCache.get());
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
