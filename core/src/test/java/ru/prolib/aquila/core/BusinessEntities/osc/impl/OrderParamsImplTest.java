package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.utils.Variant;

public class OrderParamsImplTest {
	private static Account account1, account2;
	private static Symbol symbol1, symbol2;
	private IMocksControl control;
	private Terminal terminalMock1, terminalMock2;
	private EventDispatcher dispatcherMock1, dispatcherMock2;
	private OSCController controllerMock1, controllerMock2;
	private OrderParamsImpl params;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account1 = new Account("TEST1");
		account2 = new Account("TEST2");
		symbol1 = new Symbol("XYZ1");
		symbol2 = new Symbol("XYZ2");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock1 = control.createMock(Terminal.class);
		terminalMock2 = control.createMock(Terminal.class);
		dispatcherMock1 = control.createMock(EventDispatcher.class);
		dispatcherMock2 = control.createMock(EventDispatcher.class);
		controllerMock1 = control.createMock(OSCController.class);
		controllerMock2 = control.createMock(OSCController.class);
		params = new OrderParamsImpl();
	}
	
	@Test
	public void testSettersAndGetters() {
		params.setAccount(account1);
		params.setController(controllerMock1);
		params.setEventDispatcher(dispatcherMock1);
		params.setID("foobar");
		params.setOrderID(286L);
		params.setSymbol(symbol1);
		params.setTerminal(terminalMock1);
		
		assertEquals(account1, params.getAccount());
		assertSame(controllerMock1, params.getController());
		assertSame(dispatcherMock1, params.getEventDispatcher());
		assertEquals("foobar", params.getID());
		assertEquals(286L, params.getOrderID());
		assertEquals(symbol1, params.getSymbol());
		assertSame(terminalMock1, params.getTerminal());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetID_ThrowsUndefined() {
		params.getID();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetEventDispatcher_ThrowsUndefined() {
		params.getEventDispatcher();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetController_ThrowsUndefined() {
		params.getController();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetTerminal_ThrowsUndefined() {
		params.getTerminal();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetAccount_ThrowsUndefined() {
		params.getAccount();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetSymbol_ThrowsUndefined() {
		params.getSymbol();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetOrderID_ThrowsUndefined() {
		params.getOrderID();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(params.equals(params));
		assertFalse(params.equals(null));
		assertFalse(params.equals(this));
	}
	
	@Test
	public void testEquals() {
		params.setAccount(account1);
		params.setController(controllerMock1);
		params.setEventDispatcher(dispatcherMock1);
		params.setID("foobar");
		params.setOrderID(286L);
		params.setSymbol(symbol1);
		params.setTerminal(terminalMock1);

		Variant<Account> vAcc = new Variant<>(account1, account2);
		Variant<OSCController> vCtrl = new Variant<>(vAcc, controllerMock1, controllerMock2);
		Variant<EventDispatcher> vDisp = new Variant<>(vCtrl, dispatcherMock1, dispatcherMock2);
		Variant<String> vID = new Variant<>(vDisp, "foobar", "zulu24");
		Variant<Long> vOrdID = new Variant<>(vID, 286L, 512L);
		Variant<Symbol> vSym = new Variant<>(vOrdID, symbol1, symbol2);
		Variant<Terminal> vTerm = new Variant<>(vSym, terminalMock1, terminalMock2);
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		OrderParamsImpl x, found = null;
		do {
			x = new OrderParamsImpl();
			x.setAccount(vAcc.get());
			x.setController(vCtrl.get());
			x.setEventDispatcher(vDisp.get());
			x.setID(vID.get());
			x.setOrderID(vOrdID.get());
			x.setSymbol(vSym.get());
			x.setTerminal(vTerm.get());
			if ( params.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(account1, found.getAccount());
		assertSame(controllerMock1, found.getController());
		assertSame(dispatcherMock1, found.getEventDispatcher());
		assertEquals("foobar", found.getID());
		assertEquals(286L, found.getOrderID());
		assertEquals(symbol1, found.getSymbol());
		assertSame(terminalMock1, found.getTerminal());
	}

}
