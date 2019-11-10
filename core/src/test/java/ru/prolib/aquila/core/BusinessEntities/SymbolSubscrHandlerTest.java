package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SymbolSubscrHandlerTest {
	private static Symbol symbol1, symbol2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol1 = new Symbol("AAPL");
		symbol2 = new Symbol("MSFT");
	}
	
	private IMocksControl control;
	private SymbolSubscrRepository repoMock1, repoMock2;
	private SymbolSubscrHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock1 = control.createMock(SymbolSubscrRepository.class);
		repoMock2 = control.createMock(SymbolSubscrRepository.class);
		service = new SymbolSubscrHandler(repoMock1, symbol1, MDLevel.L1_BBO);
	}
	
	@Test
	public void testGetters() {
		assertEquals(repoMock1, service.getRepository());
		assertEquals(symbol1, service.getSymbol());
		assertEquals(MDLevel.L1_BBO, service.getLevel());
		assertFalse(service.isClosed());
	}

	@Test
	public void testClose() {
		expect(repoMock1.unsubscribe(symbol1, MDLevel.L1_BBO)).andReturn(null);
		control.replay();
		
		service.close();
		
		control.verify();
		
		service.close();
		service.close();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		control.resetToNice();
		control.replay();
		Variant<SymbolSubscrRepository> vRep = new Variant<>(repoMock1, repoMock2);
		Variant<Symbol> vSym = new Variant<>(vRep, symbol1, symbol2);
		Variant<MDLevel> vLev = new Variant<>(vSym, MDLevel.L1_BBO, MDLevel.L2);
		Variant<Boolean> vCls = new Variant<>(vLev, false, true);
		Variant<?> iterator = vCls;
		int found_cnt = 0;
		SymbolSubscrHandler x, found = null;
		do {
			x = new SymbolSubscrHandler(vRep.get(), vSym.get(), vLev.get());
			if ( vCls.get() ) {
				x.close();
			}
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(repoMock1, found.getRepository());
		assertEquals(symbol1, found.getSymbol());
		assertEquals(MDLevel.L1_BBO, found.getLevel());
		assertFalse(found.isClosed());
	}

}
