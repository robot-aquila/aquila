package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFObjectRegistryTest {
	private LinkedHashSet<EditablePortfolio> portfolios;
	private LinkedHashSet<EditableSecurity> securities;
	private LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> orders;
	private QFObjectRegistry registry;
	private static Account account1, account2, account3;
	private static Symbol symbol1, symbol2, symbol3;
	private EditableTerminal terminal;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account1 = new Account("TEST1");
		account2 = new Account("TEST2");
		account3 = new Account("TEST3");
		symbol1 = new Symbol("BEST");
		symbol2 = new Symbol("QUEST");
		symbol3 = new Symbol("GUEST");
	}

	@Before
	public void setUp() throws Exception {
		portfolios = new LinkedHashSet<>();
		securities = new LinkedHashSet<>();
		orders = new LinkedHashMap<>();
		registry = new QFObjectRegistry(portfolios, securities, orders);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol3);
		terminal.getEditablePortfolio(account1);
		terminal.getEditablePortfolio(account2);
		terminal.getEditablePortfolio(account3);
	}
	
	@Test
	public void testIsRegistered_Portfolio() {
		EditablePortfolio p1 = terminal.getEditablePortfolio(account1),
				p2 = terminal.getEditablePortfolio(account2),
				p3 = terminal.getEditablePortfolio(account3);
		
		assertFalse(registry.isRegistered(p1));
		assertFalse(registry.isRegistered(p2));
		assertFalse(registry.isRegistered(p3));
		
		portfolios.add(p1);
		portfolios.add(p3);

		assertTrue(registry.isRegistered(p1));
		assertFalse(registry.isRegistered(p2));
		assertTrue(registry.isRegistered(p3));
	}
	
	@Test
	public void testIsRegistered_Security() {
		EditableSecurity s1 = terminal.getEditableSecurity(symbol1),
				s2 = terminal.getEditableSecurity(symbol2),
				s3 = terminal.getEditableSecurity(symbol3);
		
		assertFalse(registry.isRegistered(s1));
		assertFalse(registry.isRegistered(s2));
		assertFalse(registry.isRegistered(s3));
		
		securities.add(s1);
		securities.add(s3);
		
		assertTrue(registry.isRegistered(s1));
		assertFalse(registry.isRegistered(s2));
		assertTrue(registry.isRegistered(s3));
	}
	
	@Test
	public void testIsRegistered_Order() {
		EditableOrder o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.BUY, 1L, FDecimal.of2(115.04)),
			o2 = (EditableOrder) terminal.createOrder(account2,
				symbol2, OrderAction.BUY, 1L, FDecimal.of2(86.19)),
			o3 = (EditableOrder) terminal.createOrder(account3,
				symbol3, OrderAction.SELL, 1L, FDecimal.of2(70.56));
		
		assertFalse(registry.isRegistered(o1));
		assertFalse(registry.isRegistered(o2));
		assertFalse(registry.isRegistered(o3));
		
		LinkedHashSet<EditableOrder> dummy = new LinkedHashSet<>();
		dummy.add(o1);
		orders.put(symbol1, dummy);
		dummy = new LinkedHashSet<>();
		dummy.add(o3);
		orders.put(symbol3, dummy);
		
		assertTrue(registry.isRegistered(o1));
		assertFalse(registry.isRegistered(o2));
		assertTrue(registry.isRegistered(o3));
	}
	
	@Test
	public void testRegister_Portfolio() {
		EditablePortfolio p1 = terminal.getEditablePortfolio(account1),
				p2 = terminal.getEditablePortfolio(account2),
				p3 = terminal.getEditablePortfolio(account3);
		
		registry.register(p1);
		registry.register(p2);
		registry.register(p3);
		
		LinkedHashSet<EditablePortfolio> expected = new LinkedHashSet<>();
		expected.add(p1);
		expected.add(p2);
		expected.add(p3);
		assertEquals(expected, portfolios);
	}
	
	@Test
	public void testRegister_Security() {
		EditableSecurity s1 = terminal.getEditableSecurity(symbol1),
				s3 = terminal.getEditableSecurity(symbol3);
		
		registry.register(s1);
		registry.register(s3);
		
		LinkedHashSet<EditableSecurity> expected = new LinkedHashSet<>();
		expected.add(s1);
		expected.add(s3);
		assertEquals(expected, securities);
	}
	
	@Test
	public void testRegister_Order() {
		EditableOrder s1o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.BUY, 1L, FDecimal.of2(115.04)),
			s1o2 = (EditableOrder) terminal.createOrder(account2,
				symbol1, OrderAction.BUY, 100L, FDecimal.of2(86.19)),
			s1o3 = (EditableOrder) terminal.createOrder(account3,
				symbol1, OrderAction.SELL, 10L, FDecimal.of2(70.56)),
			s2o1 = (EditableOrder) terminal.createOrder(account1,
				symbol2, OrderAction.SELL, 10L, FDecimal.of2(23.96)),
			s2o2 = (EditableOrder) terminal.createOrder(account3,
				symbol2, OrderAction.BUY, 5L, FDecimal.of2(11.05));
		
		registry.register(s1o1);
		registry.register(s1o2);
		registry.register(s1o3);
		registry.register(s2o1);
		registry.register(s2o2);
		
		LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> expected = new LinkedHashMap<>();
		expected.put(symbol1, new LinkedHashSet<>());
		expected.get(symbol1).add(s1o1);
		expected.get(symbol1).add(s1o2);
		expected.get(symbol1).add(s1o3);
		expected.put(symbol2, new LinkedHashSet<>());
		expected.get(symbol2).add(s2o1);
		expected.get(symbol2).add(s2o2);
		assertEquals(expected, orders);
	}
	
	@Test
	public void testGetSecurityList() {
		EditableSecurity s1 = terminal.getEditableSecurity(symbol1),
				s2 = terminal.getEditableSecurity(symbol2),
				s3 = terminal.getEditableSecurity(symbol3);
		securities.add(s2);
		securities.add(s1);
		securities.add(s3);
		
		List<EditableSecurity> actual = registry.getSecurityList();
		
		List<EditableSecurity> expected = new ArrayList<>();
		expected.add(s2);
		expected.add(s1);
		expected.add(s3);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPortfolioList() {
		EditablePortfolio p1 = terminal.getEditablePortfolio(account1),
				p2 = terminal.getEditablePortfolio(account2),
				p3 = terminal.getEditablePortfolio(account3);
		portfolios.add(p3);
		portfolios.add(p1);
		portfolios.add(p2);
		
		List<EditablePortfolio> actual = registry.getPortfolioList();
		
		List<EditablePortfolio> expected = new ArrayList<>();
		expected.add(p3);
		expected.add(p1);
		expected.add(p2);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetOrderList() {
		EditableOrder s1o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.BUY, 1L, FDecimal.of2(115.04)),
			s1o2 = (EditableOrder) terminal.createOrder(account2,
				symbol1, OrderAction.BUY, 100L, FDecimal.of2(86.19)),
			s1o3 = (EditableOrder) terminal.createOrder(account3,
				symbol1, OrderAction.SELL, 10L, FDecimal.of2(70.56)),
			s2o1 = (EditableOrder) terminal.createOrder(account1,
				symbol2, OrderAction.SELL, 10L, FDecimal.of2(23.96)),	// +
			s2o2 = (EditableOrder) terminal.createOrder(account3,
				symbol2, OrderAction.BUY, 5L, FDecimal.of2(11.05)), 	// -
			s2o3 = (EditableOrder) terminal.createOrder(account2,
				symbol2, OrderAction.BUY, 1L, FDecimal.of2(55.00)), 	// +
			s2o4 = (EditableOrder) terminal.createOrder(account1,
				symbol2, OrderAction.BUY, 5L, FDecimal.of2(55.29)), 	// +
			s2o5 = (EditableOrder) terminal.createOrder(account2,
				symbol2, OrderAction.SELL, 5L, FDecimal.of2(55.01)); 	// -
		registry.register(s1o1);
		registry.register(s1o2);
		registry.register(s1o3);
		registry.register(s2o1);
		registry.register(s2o2);
		registry.register(s2o3);
		registry.register(s2o4);
		registry.register(s2o5);
			
		List<EditableOrder> actual = registry.getOrderList(symbol2, FDecimal.of2(55.0));
		
		List<EditableOrder> expected = new ArrayList<>();
		expected.add(s2o1);
		expected.add(s2o3);
		expected.add(s2o4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPurgeOrder() {
		EditableOrder s1o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.BUY, 1L, FDecimal.of2(115.04)),
			s1o2 = (EditableOrder) terminal.createOrder(account2,
				symbol1, OrderAction.BUY, 100L, FDecimal.of2(86.19)),
			s1o3 = (EditableOrder) terminal.createOrder(account3,
				symbol1, OrderAction.SELL, 10L, FDecimal.of2(70.56)),
			s2o1 = (EditableOrder) terminal.createOrder(account1,
				symbol2, OrderAction.SELL, 10L, FDecimal.of2(23.96)),
			s2o2 = (EditableOrder) terminal.createOrder(account3,
				symbol2, OrderAction.BUY, 5L, FDecimal.of2(11.05));
		orders.put(symbol1, new LinkedHashSet<>());
		orders.get(symbol1).add(s1o1);
		orders.get(symbol1).add(s1o2);
		orders.get(symbol1).add(s1o3);
		orders.put(symbol2, new LinkedHashSet<>());
		orders.get(symbol2).add(s2o1);
		orders.get(symbol2).add(s2o2);

		registry.purgeOrder(s1o2);
		registry.purgeOrder(s2o1);
		
		LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> expected = new LinkedHashMap<>();
		expected.put(symbol1, new LinkedHashSet<>());
		expected.get(symbol1).add(s1o1);
		expected.get(symbol1).add(s1o3);
		expected.put(symbol2, new LinkedHashSet<>());
		expected.get(symbol2).add(s2o2);
		assertEquals(expected, orders);
	}

}
