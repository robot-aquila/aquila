package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

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

public class PositionParamsImplTest {
	private static Account account1, account2;
	private static Symbol symbol1, symbol2;
	private IMocksControl control;
	private Terminal terminalMock1, terminalMock2;
	private EventDispatcher dispatcherMock1, dispatcherMock2;
	private OSCController controllerMock1, controllerMock2;
	private PositionParamsImpl params;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account1 = new Account("TEST1");
		account2 = new Account("TEST2");
		symbol1 = new Symbol("GAZP");
		symbol2 = new Symbol("SBER");
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
		params = new PositionParamsImpl();
	}
	
	@Test
	public void testSettersAndGetters() {
		params.setAccount(account1);
		params.setController(controllerMock1);
		params.setEventDispatcher(dispatcherMock1);
		params.setID("foobar");
		params.setTerminal(terminalMock1);
		params.setSymbol(symbol1);
		
		assertEquals(account1, params.getAccount());
		assertSame(controllerMock1, params.getController());
		assertSame(dispatcherMock1, params.getEventDispatcher());
		assertEquals("foobar", params.getID());
		assertSame(terminalMock1, params.getTerminal());
		assertEquals(symbol1, params.getSymbol());
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
		params.setTerminal(terminalMock1);
		params.setSymbol(symbol1);

		Variant<Account> vAcc = new Variant<>(account1, account2);
		Variant<OSCController> vCtrl = new Variant<>(vAcc, controllerMock1, controllerMock2);
		Variant<EventDispatcher> vDisp = new Variant<>(vCtrl, dispatcherMock1, dispatcherMock2);
		Variant<String> vID = new Variant<>(vDisp, "foobar", "zulu24");
		Variant<Terminal> vTerm = new Variant<>(vID, terminalMock1, terminalMock2);
		Variant<Symbol> vSym = new Variant<>(vTerm, symbol1, symbol2);
		Variant<?> iterator = vSym;
		int foundCnt = 0;
		PositionParamsImpl x, found = null;
		do {
			x = new PositionParamsImpl();
			x.setAccount(vAcc.get());
			x.setController(vCtrl.get());
			x.setEventDispatcher(vDisp.get());
			x.setID(vID.get());
			x.setTerminal(vTerm.get());
			x.setSymbol(vSym.get());
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
		assertSame(terminalMock1, found.getTerminal());
		assertEquals(symbol1, found.getSymbol());
	}

}
