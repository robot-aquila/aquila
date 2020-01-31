package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SymbolSubscrHandlerTest {
	private IMocksControl control;
	private SymbolSubscrHandler.Owner ownerMock1, ownerMock2;
	private Symbol symbol1, symbol2;
	private CompletableFuture<Boolean> confirm1, confirm2;
	private SymbolSubscrHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		ownerMock1 = control.createMock(SymbolSubscrHandler.Owner.class);
		ownerMock2 = control.createMock(SymbolSubscrHandler.Owner.class);
		symbol1 = new Symbol("MSFT");
		symbol2 = new Symbol("AAPL");
		confirm1 = new CompletableFuture<>();
		confirm2 = new CompletableFuture<>();
		service = new SymbolSubscrHandler(ownerMock1, symbol1, MDLevel.L2, confirm1);
	}
	
	@Test
	public void testGetters() {
		assertEquals(ownerMock1, service.getOwner());
		assertEquals(symbol1, service.getSymbol());
		assertEquals(MDLevel.L2, service.getLevel());
		assertFalse(service.isClosed());
	}
	
	@Test
	public void testClose() {
		ownerMock1.onUnsubscribe(symbol1, MDLevel.L2);
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
	public void testEquals() throws Exception {
		Variant<SymbolSubscrHandler.Owner> vOwn = new Variant<>(ownerMock1, ownerMock2);
		Variant<Symbol> vSym = new Variant<>(vOwn, symbol1, symbol2);
		Variant<MDLevel> vLev = new Variant<>(vSym, MDLevel.L2, MDLevel.L1_BBO);
		Variant<Boolean> vCls = new Variant<>(vLev, false, true);
		Variant<CompletableFuture<Boolean>> vCnf = new Variant<>(vCls, confirm1, confirm2);
		Variant<?> iterator = vCnf;
		int found_cnt = 0;
		SymbolSubscrHandler x, found = null;
		do {
			x = new SymbolSubscrHandler(vOwn.get(), vSym.get(), vLev.get(), vCnf.get());
			if ( vCls.get() ) {
				x.close();
			}
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(ownerMock1, found.getOwner());
		assertEquals(symbol1, found.getSymbol());
		assertEquals(MDLevel.L2, found.getLevel());
		assertFalse(found.isClosed());
		assertSame(confirm1, found.getConfirmation());
	}

}
