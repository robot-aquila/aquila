package ru.prolib.aquila.qforts.impl;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
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
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFortsTest {
	private static Account account1, account2, account3;
	private static Symbol symbol1;
	private IMocksControl control;
	private QFObjectRegistry registryMock;
	private QFTransactionService transactionsMock;
	private EditableTerminal terminal;
	private QForts service;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		account1 = new Account("TEST1");
		account2 = new Account("TEST2");
		account3 = new Account("TEST3");
		symbol1 = new Symbol("BEST1");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		registryMock = control.createMock(QFObjectRegistry.class);
		transactionsMock = control.createMock(QFTransactionService.class);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		service = new QForts(registryMock, transactionsMock);
		terminal.getEditableSecurity(symbol1);
		terminal.getEditablePortfolio(account1);
		terminal.getEditablePortfolio(account2);
		terminal.getEditablePortfolio(account3);
	}
	
	@Test
	public void testRegisterPortfolio() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account1);
		registryMock.register(portfolio);
		control.replay();
		
		service.registerPortfolio(portfolio);
		
		control.verify();
	}
	
	@Test
	public void testRegisterSecurity() throws Exception {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		registryMock.register(security);
		control.replay();
		
		service.registerSecurity(security);
		
		control.verify();
	}
	
	@Test
	public void testRegisterOrder() throws Exception {
		EditableOrder order = terminal.createOrder(account1, symbol1);
		transactionsMock.registerOrder(order);
		control.replay();
		
		service.registerOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		EditableOrder order = terminal.createOrder(account1, symbol1);
		transactionsMock.cancelOrder(order);
		control.replay();
		
		service.cancelOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testChangeBalance() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account1);
		transactionsMock.changeBalance(portfolio, FMoney.ofRUB2(520.0));
		control.replay();
		
		service.changeBalance(portfolio, FMoney.ofRUB2(520.0));
		
		control.verify();
	}
	
	@Test
	public void testHandleOrders() throws Exception {
		EditableOrder
			order1 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, 10L),
			order2 = (EditableOrder) terminal.createOrder(account2, symbol1, OrderAction.SELL, 5L),
			order3 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, 20L),
			order4 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, 25L);
		List<EditableOrder> expectedOrders = new ArrayList<>();
		expectedOrders.add(order1);
		expectedOrders.add(order2);
		expectedOrders.add(order3);
		expectedOrders.add(order4);
		expect(registryMock.getOrderList(symbol1, FDecimal.of2(54.26))).andReturn(expectedOrders);
		transactionsMock.executeOrder(order1, 10L, FDecimal.of2(54.26));
		transactionsMock.executeOrder(order2,  5L, FDecimal.of2(54.26));
		transactionsMock.executeOrder(order3,  3L, FDecimal.of2(54.26));
		control.replay();
		
		service.handleOrders(terminal.getEditableSecurity(symbol1), 18L, FDecimal.of2(54.26));
		
		control.verify();
	}
	
	@Test
	public void testHandleOrders_RejectOnFailure() throws Exception {
		EditableOrder
			order1 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, 10L),
			order2 = (EditableOrder) terminal.createOrder(account2, symbol1, OrderAction.SELL, 5L),
			order3 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, 20L),
			order4 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, 25L);
		List<EditableOrder> expectedOrders = new ArrayList<>();
		expectedOrders.add(order1);
		expectedOrders.add(order2);
		expectedOrders.add(order3);
		expectedOrders.add(order4);
		expect(registryMock.getOrderList(symbol1, FDecimal.of2(54.26))).andReturn(expectedOrders);
		transactionsMock.executeOrder(order1, 10L, FDecimal.of2(54.26));
		transactionsMock.executeOrder(order2,  5L, FDecimal.of2(54.26));
		expectLastCall().andThrow(new QFValidationException("Test error", QFResult.INSUFFICIENT_FUNDS));
		transactionsMock.rejectOrder(order2, "Test error");
		transactionsMock.executeOrder(order3,  3L, FDecimal.of2(54.26));
		control.replay();
		
		service.handleOrders(terminal.getEditableSecurity(symbol1), 18L, FDecimal.of2(54.26));
		
		control.verify();
	}
	
	@Test
	public void testUpdateByMarket() throws Exception {
		EditablePortfolio
			portfolio1 = terminal.getEditablePortfolio(account1),
			portfolio2 = terminal.getEditablePortfolio(account2),
			portfolio3 = terminal.getEditablePortfolio(account3);
		List<EditablePortfolio> expectedPortfolios = new ArrayList<>();
		expectedPortfolios.add(portfolio1);
		expectedPortfolios.add(portfolio2);
		expectedPortfolios.add(portfolio3);
		expect(registryMock.getPortfolioList()).andReturn(expectedPortfolios);
		transactionsMock.updateByMarket(portfolio1);
		transactionsMock.updateByMarket(portfolio2);
		transactionsMock.updateByMarket(portfolio3);
		control.replay();
		
		service.updateByMarket();
		
		control.verify();
	}
	
	@Test
	public void testUpdateMargin() throws Exception {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		EditablePortfolio
			portfolio1 = terminal.getEditablePortfolio(account1),
			portfolio2 = terminal.getEditablePortfolio(account2),
			portfolio3 = terminal.getEditablePortfolio(account3);
		List<EditablePortfolio> expectedPortfolios = new ArrayList<>();
		expectedPortfolios.add(portfolio1);
		expectedPortfolios.add(portfolio2);
		expectedPortfolios.add(portfolio3);
		expect(registryMock.getPortfolioList()).andReturn(expectedPortfolios);
		transactionsMock.updateMargin(portfolio1, security);
		transactionsMock.updateMargin(portfolio2, security);
		transactionsMock.updateMargin(portfolio3, security);
		control.replay();
		
		service.updateMargin(security);
		
		control.verify();
	}
	
	@Test
	public void testMidClearing() throws Exception {
		EditablePortfolio
			portfolio1 = terminal.getEditablePortfolio(account1),
			portfolio2 = terminal.getEditablePortfolio(account2),
			portfolio3 = terminal.getEditablePortfolio(account3);
		List<EditablePortfolio> expectedPortfolios = new ArrayList<>();
		expectedPortfolios.add(portfolio1);
		expectedPortfolios.add(portfolio2);
		expectedPortfolios.add(portfolio3);
		expect(registryMock.getPortfolioList()).andReturn(expectedPortfolios);
		transactionsMock.midClearing(portfolio1);
		transactionsMock.midClearing(portfolio2);
		transactionsMock.midClearing(portfolio3);
		control.replay();
		
		service.midClearing();
		
		control.verify();
	}

	@Test
	public void testClearing() throws Exception {
		EditablePortfolio
			portfolio1 = terminal.getEditablePortfolio(account1),
			portfolio2 = terminal.getEditablePortfolio(account2),
			portfolio3 = terminal.getEditablePortfolio(account3);
		List<EditablePortfolio> expectedPortfolios = new ArrayList<>();
		expectedPortfolios.add(portfolio1);
		expectedPortfolios.add(portfolio2);
		expectedPortfolios.add(portfolio3);
		expect(registryMock.getPortfolioList()).andReturn(expectedPortfolios);
		transactionsMock.clearing(portfolio1);
		transactionsMock.clearing(portfolio2);
		transactionsMock.clearing(portfolio3);
		control.replay();
		
		service.clearing();
		
		control.verify();
	}

}
