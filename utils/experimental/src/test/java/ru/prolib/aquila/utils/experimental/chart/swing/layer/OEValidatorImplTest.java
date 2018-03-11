package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionImpl;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class OEValidatorImplTest {
	private static Symbol symbol1, symbol2;
	private static Account account1, account2;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol1 = new Symbol("MSFT");
		symbol2 = new Symbol("AAPL");
		account1 = new Account("TEST-1");
		account2 = new Account("TEST-2");
	}
	
	private IMocksControl control;
	private Terminal terminalMock;
	private Order orderMock;
	private Set<Symbol> symbolsStub;
	private Set<Account> accountsStub;
	private OEValidatorImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(Terminal.class);
		orderMock = control.createMock(Order.class);
		symbolsStub = new LinkedHashSet<>();
		accountsStub = new LinkedHashSet<>();
		service = new OEValidatorImpl(symbolsStub, accountsStub);
	}
	
	@Test
	public void testCtor2_Service() {
		assertSame(symbolsStub, service.getFiltersBySymbol());
		assertSame(accountsStub, service.getFiltersByAccount());
	}
	
	@Test
	public void testCtor0() {
		service = new OEValidatorImpl();
		assertEquals(new HashSet<>(), service.getFiltersBySymbol());
		assertEquals(new HashSet<>(), service.getFiltersByAccount());
	}
	
	@Test
	public void testCtor1() {
		service = new OEValidatorImpl(symbol2);
		Set<Symbol> expectedSymbols = new HashSet<>();
		expectedSymbols.add(symbol2);
		assertEquals(expectedSymbols, service.getFiltersBySymbol());
		assertEquals(new HashSet<>(), service.getFiltersByAccount());
	}
	
	@Test
	public void testAddFilterBySymbol() {
		service.addFilterBySymbol(symbol1);
		service.addFilterBySymbol(symbol2);
		
		Set<Symbol> expected = new LinkedHashSet<>();
		expected.add(symbol1);
		expected.add(symbol2);
		assertEquals(expected, symbolsStub);
		assertEquals(expected, service.getFiltersBySymbol());
	}
	
	@Test
	public void testRemoveFilterBySymbol() {
		symbolsStub.add(symbol1);
		symbolsStub.add(symbol2);
		
		service.removeFilterBySymbol(symbol1);
		
		Set<Symbol> expected = new LinkedHashSet<>();
		expected.add(symbol2);
		assertEquals(expected, symbolsStub);
		assertEquals(expected, service.getFiltersBySymbol());
	}
	
	@Test
	public void testRemoveFiltersBySymbol() {
		symbolsStub.add(symbol1);
		symbolsStub.add(symbol2);
		
		service.removeFiltersBySymbol();

		assertEquals(new LinkedHashSet<>(), symbolsStub);
		assertEquals(new LinkedHashSet<>(), service.getFiltersBySymbol());
	}
	
	@Test
	public void testAddFilterByAccount() {
		service.addFilterByAccount(account1);
		service.addFilterByAccount(account2);
		
		Set<Account> expected = new LinkedHashSet<>();
		expected.add(account1);
		expected.add(account2);
		assertEquals(expected, accountsStub);
		assertEquals(expected, service.getFiltersByAccount());
	}
	
	@Test
	public void testRemoveFilterByAccount() {
		accountsStub.add(account1);
		accountsStub.add(account2);
		
		service.removeFilterByAccount(account1);
		
		Set<Account> expected = new LinkedHashSet<>();
		expected.add(account2);
		assertEquals(expected, accountsStub);
		assertEquals(expected, service.getFiltersByAccount());
	}
	
	@Test
	public void testRemoveFiltersByAccount() {
		accountsStub.add(account1);
		accountsStub.add(account2);
		
		service.removeFiltersByAccount();
		
		assertEquals(new LinkedHashSet<>(), accountsStub);
		assertEquals(new LinkedHashSet<>(), service.getFiltersByAccount());
	}
	
	@Test
	public void testIsValid_NoFilters() {
		OrderExecution oeStub = new OrderExecutionImpl(terminalMock, 1000L, "FOO-1000", new Symbol("MSFT"),
				OrderAction.BUY, 250L, T("2018-03-10T22:12:00Z"), CDecimalBD.of("251.34"), CDecimalBD.of(10L),
				CDecimalBD.of("5.15"));
		control.replay();
		
		assertTrue(service.isValid(oeStub));
		
		control.verify();
	}
	
	@Test
	public void testIsValid_FilterBySymbol() {
		OrderExecution oeStub = new OrderExecutionImpl(terminalMock, 1000L, "FOO-1000", new Symbol("MSFT"),
				OrderAction.BUY, 250L, T("2018-03-10T22:12:00Z"), CDecimalBD.of("251.34"), CDecimalBD.of(10L),
				CDecimalBD.of("5.15"));
		control.replay();
		service.addFilterBySymbol(symbol2);
		
		assertFalse(service.isValid(oeStub));
		
		control.verify();
	}
	
	@Test
	public void testIsValid_FilterByAccount() throws Exception {
		OrderExecution oeStub = new OrderExecutionImpl(terminalMock, 1000L, "FOO-1000", new Symbol("MSFT"),
				OrderAction.BUY, 250L, T("2018-03-10T22:12:00Z"), CDecimalBD.of("251.34"), CDecimalBD.of(10L),
				CDecimalBD.of("5.15"));
		expect(terminalMock.getOrder(250L)).andReturn(orderMock);
		expect(orderMock.getAccount()).andReturn(account1);
		control.replay();
		service.addFilterByAccount(account2);
		
		assertFalse(service.isValid(oeStub));
		
		control.verify();
	}

}
