package ru.prolib.aquila.utils.experimental.chart.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.utils.experimental.chart.data.ALOValidatorImpl;

public class ALOValidatorImplTest {
	private static Symbol symbol1, symbol2, symbol3;
	private static Account account1, account2, account3;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("MSFT");
		symbol2 = new Symbol("AAPL");
		symbol3 = new Symbol("VIX");
		account1 = new Account("TEST-1");
		account2 = new Account("TEST-2");
		account3 = new Account("TEST-3");
	}
	
	private EditableTerminal terminal;
	private ALOValidatorImpl service;
	private Set<Symbol> filterBySymbol;
	private Set<Account> filterByAccount;

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		terminal.getEditablePortfolio(account1);
		terminal.getEditablePortfolio(account2);
		terminal.getEditablePortfolio(account3);
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol3);
		filterBySymbol = new HashSet<>();
		filterByAccount = new HashSet<>();
		service = new ALOValidatorImpl(filterBySymbol, filterByAccount);
	}
	
	@Test
	public void testCtor0() {
		service = new ALOValidatorImpl();
		assertEquals(new HashSet<Symbol>(), service.getFiltersBySymbol());
		assertEquals(new HashSet<Account>(), service.getFiltersByAccount());
	}
	
	@Test
	public void testCtor1_Symbol() {
		service = new ALOValidatorImpl(symbol2);
		Set<Symbol> expectedFilterBySymbol = new HashSet<>();
		expectedFilterBySymbol.add(symbol2);
		assertEquals(expectedFilterBySymbol, service.getFiltersBySymbol());
		assertEquals(new HashSet<Account>(), service.getFiltersByAccount());
	}
	
	@Test
	public void testAddFilterBySymbol() {
		service.addFilterBySymbol(symbol1);
		service.addFilterBySymbol(symbol2);
		service.addFilterBySymbol(symbol3);
		
		Set<Symbol> expected = new HashSet<>();
		expected.add(symbol1);
		expected.add(symbol2);
		expected.add(symbol3);
		assertEquals(expected, filterBySymbol);
		assertEquals(expected, service.getFiltersBySymbol());
	}
	
	@Test
	public void testRemoveFilterBySymbol() {
		filterBySymbol.add(symbol1);
		filterBySymbol.add(symbol2);
		filterBySymbol.add(symbol3);
		
		service.removeFilterBySymbol(symbol2);
		service.removeFilterBySymbol(symbol1);		
		
		Set<Symbol> expected = new HashSet<>();
		expected.add(symbol3);
		assertEquals(expected, filterBySymbol);
		assertEquals(expected, service.getFiltersBySymbol());
	}
	
	@Test
	public void testRemoveFiltersBySymbol() {
		filterBySymbol.add(symbol1);
		filterBySymbol.add(symbol2);
		filterBySymbol.add(symbol3);

		service.removeFiltersBySymbol();
		
		Set<Symbol> expected = new HashSet<>();
		assertEquals(expected, filterBySymbol);
		assertEquals(expected, service.getFiltersBySymbol());
	}
	
	@Test
	public void testAddFilterByAccount() {
		service.addFilterByAccount(account1);
		service.addFilterByAccount(account2);
		service.addFilterByAccount(account3);
		
		Set<Account> expected = new HashSet<>();
		expected.add(account1);
		expected.add(account2);
		expected.add(account3);
		assertEquals(expected, filterByAccount);
		assertEquals(expected, service.getFiltersByAccount());
	}
	
	@Test
	public void testRemoveFilterByAccount() {
		filterByAccount.add(account1);
		filterByAccount.add(account2);
		filterByAccount.add(account3);
		
		service.removeFilterByAccount(account3);
		service.removeFilterByAccount(account1);
		
		Set<Account> expected = new HashSet<>();
		expected.add(account2);
		assertEquals(expected, filterByAccount);
		assertEquals(expected, service.getFiltersByAccount());
	}
	
	@Test
	public void testRemoveFiltersByAccount() {
		filterByAccount.add(account1);
		filterByAccount.add(account2);
		filterByAccount.add(account3);
		
		service.removeFiltersByAccount();
		
		Set<Account> expected = new HashSet<>();
		assertEquals(expected, filterByAccount);
		assertEquals(expected, service.getFiltersByAccount());
	}
	
	@Test
	public void testIsValid_NonLimitOrderIsNotValid() {
		Order order = terminal.createOrder(account1, symbol1, OrderAction.BUY, of(1L));
		
		assertFalse(service.isValid(order));
	}
	
	@Test
	public void testIsValid_InactiveOrderIsNotValid() {
		EditableOrder order = (EditableOrder)
				terminal.createOrder(account1, symbol1, OrderAction.BUY, of(1L), of("12.34"));
		order.update(OrderField.STATUS, OrderStatus.CANCELLED);
		
		assertFalse(service.isValid(order));
	}
	
	@Test
	public void testIsValid_ApplyFilterBySymbol() {
		EditableOrder order = (EditableOrder)
				terminal.createOrder(account1, symbol1, OrderAction.BUY, of(1L), of("12.34"));
		order.update(OrderField.STATUS, OrderStatus.ACTIVE);
		
		assertTrue(service.isValid(order));
		
		service.addFilterBySymbol(symbol2);
		
		assertFalse(service.isValid(order));
		
		service.addFilterBySymbol(symbol1);
		
		assertTrue(service.isValid(order));
	}
	
	@Test
	public void testIsValid_ApplyFilterByAccount() {
		EditableOrder order = (EditableOrder)
				terminal.createOrder(account2, symbol2, OrderAction.SELL, of(10L), of("25.91"));
		order.update(OrderField.STATUS, OrderStatus.ACTIVE);
		
		assertTrue(service.isValid(order));
		
		service.addFilterByAccount(account1);
		
		assertFalse(service.isValid(order));
		
		service.addFilterByAccount(account2);
		
		assertTrue(service.isValid(order));
	}

}
