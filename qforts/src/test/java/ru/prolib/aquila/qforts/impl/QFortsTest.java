package ru.prolib.aquila.qforts.impl;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
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
	
	static List<EditableOrder> toList(EditableOrder ...args) {
		List<EditableOrder> list = new ArrayList<>();
		for ( EditableOrder order : args ) {
			list.add(order);
		}
		return list;
	}
	
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
		registryMock = control.createMock(QFObjectRegistryImpl.class);
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
	public void testGetters() {
		assertEquals(QForts.LIQUIDITY_LIMITED, service.getLiquidityMode());
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
		transactionsMock.changeBalance(portfolio, CDecimalBD.ofRUB2("520"));
		control.replay();
		
		service.changeBalance(portfolio, CDecimalBD.ofRUB2("520"));
		
		control.verify();
	}
	
	@Test
	public void testHandleOrders() throws Exception {
		EditableOrder
			order1 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(10L)),
			order2 = (EditableOrder) terminal.createOrder(account2, symbol1, OrderAction.SELL, CDecimalBD.of(5L)),
			order3 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, CDecimalBD.of(20L)),
			order4 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(25L));
		List<EditableOrder> expectedOrders = new ArrayList<>();
		expectedOrders.add(order1);
		expectedOrders.add(order2);
		expectedOrders.add(order3);
		expectedOrders.add(order4);
		expect(registryMock.getOrderList(symbol1, CDecimalBD.of("54.26"))).andReturn(expectedOrders);
		transactionsMock.executeOrder(order1, CDecimalBD.of(10L), CDecimalBD.of("54.26"), "ICH/s0_c10");
		transactionsMock.executeOrder(order2,  CDecimalBD.of(5L), CDecimalBD.of("54.26"), "ICH/s10_c5");
		transactionsMock.executeOrder(order3,  CDecimalBD.of(3L), CDecimalBD.of("54.26"), "ICH/s15_c3");
		control.replay();
		
		service.handleOrders(terminal.getEditableSecurity(symbol1), of(18L), of("54.26"), "ICH");
		
		control.verify();
	}
	
	@Test
	public void testHandleOrders_TickInfoIsNull() throws Exception {
		EditableOrder
			order1 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(10L)),
			order2 = (EditableOrder) terminal.createOrder(account2, symbol1, OrderAction.SELL, CDecimalBD.of(5L)),
			order3 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, CDecimalBD.of(20L)),
			order4 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(25L));
		List<EditableOrder> expectedOrders = new ArrayList<>();
		expectedOrders.add(order1);
		expectedOrders.add(order2);
		expectedOrders.add(order3);
		expectedOrders.add(order4);
		expect(registryMock.getOrderList(symbol1, CDecimalBD.of("54.26"))).andReturn(expectedOrders);
		transactionsMock.executeOrder(order1, CDecimalBD.of(10L), CDecimalBD.of("54.26"), "/s0_c10");
		transactionsMock.executeOrder(order2,  CDecimalBD.of(5L), CDecimalBD.of("54.26"), "/s10_c5");
		transactionsMock.executeOrder(order3,  CDecimalBD.of(3L), CDecimalBD.of("54.26"), "/s15_c3");
		control.replay();
		
		service.handleOrders(terminal.getEditableSecurity(symbol1), of(18L), of("54.26"), null);
		
		control.verify();
	}
	
	@Test
	public void testHandleOrders_RejectOnFailure() throws Exception {
		EditableOrder
			order1 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(10L)),
			order2 = (EditableOrder) terminal.createOrder(account2, symbol1, OrderAction.SELL, CDecimalBD.of(5L)),
			order3 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, CDecimalBD.of(20L)),
			order4 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(25L));
		List<EditableOrder> expectedOrders = new ArrayList<>();
		expectedOrders.add(order1);
		expectedOrders.add(order2);
		expectedOrders.add(order3);
		expectedOrders.add(order4);
		expect(registryMock.getOrderList(symbol1, CDecimalBD.of("54.26"))).andReturn(expectedOrders);
		transactionsMock.executeOrder(order1, CDecimalBD.of(10L), CDecimalBD.of("54.26"), "TEX25/s0_c10");
		transactionsMock.executeOrder(order2,  CDecimalBD.of(5L), CDecimalBD.of("54.26"), "TEX25/s10_c5");
		expectLastCall().andThrow(new QFValidationException("Test error", QFResult.INSUFFICIENT_FUNDS));
		transactionsMock.rejectOrder(order2, "Test error");
		transactionsMock.executeOrder(order3,  CDecimalBD.of(3L), CDecimalBD.of("54.26"), "TEX25/s15_c3");
		control.replay();
		
		service.handleOrders(terminal.getEditableSecurity(symbol1), of(18L), of("54.26"), "TEX25");
		
		control.verify();
	}
	
	@Test
	public void testHandleOrders_LiquidityMode_ApplyToOrder() throws Exception {
		service = new QForts(registryMock, transactionsMock, QForts.LIQUIDITY_APPLY_TO_ORDER);
		EditableOrder
			order1 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(10L)),
			order2 = (EditableOrder) terminal.createOrder(account2, symbol1, OrderAction.SELL, CDecimalBD.of(5L)),
			order3 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, CDecimalBD.of(20L));
		expect(registryMock.getOrderList(symbol1, CDecimalBD.of("54.26"))).andReturn(toList(order1, order2, order3));
		transactionsMock.executeOrder(order1, of(10L), of("54.26"), "KAPPA-24");
		transactionsMock.executeOrder(order2,  of(5L), of("54.26"), "KAPPA-24");
		transactionsMock.executeOrder(order3, of(10L), of("54.26"), "KAPPA-24");
		control.replay();
		
		service.handleOrders(terminal.getEditableSecurity(symbol1), of(10L), of("54.26"), "KAPPA-24");

		control.verify();
	}
	
	@Test
	public void testHandleOrders_LiquidityMode_Unlimited() throws Exception {
		service = new QForts(registryMock, transactionsMock, QForts.LIQUIDITY_UNLIMITED);
		EditableOrder
			o1 = (EditableOrder) terminal.createOrder(account1, symbol1, OrderAction.BUY, CDecimalBD.of(10L)),
			o2 = (EditableOrder) terminal.createOrder(account2, symbol1, OrderAction.SELL, CDecimalBD.of(5L)),
			o3 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, CDecimalBD.of(20L)),
			o4 = (EditableOrder) terminal.createOrder(account3, symbol1, OrderAction.BUY, CDecimalBD.of(49088812L));
		expect(registryMock.getOrderList(symbol1, CDecimalBD.of("54.26"))).andReturn(toList(o1, o2, o3, o4));
		transactionsMock.executeOrder(o1, of(10L), of("54.26"), "KZ280");
		transactionsMock.executeOrder(o2,  of(5L), of("54.26"), "KZ280");
		transactionsMock.executeOrder(o3, of(20L), of("54.26"), "KZ280");
		transactionsMock.executeOrder(o4, of(49088812L), of("54.26"), "KZ280");
		control.replay();
		
		service.handleOrders(terminal.getEditableSecurity(symbol1), of(1L), of("54.26"), "KZ280");

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
		portfolio1.getEditablePosition(symbol1);
		portfolio3.getEditablePosition(symbol1);
		List<EditablePortfolio> expectedPortfolios = new ArrayList<>();
		expectedPortfolios.add(portfolio1);
		expectedPortfolios.add(portfolio2);
		expectedPortfolios.add(portfolio3);
		expect(registryMock.getPortfolioList()).andReturn(expectedPortfolios);
		transactionsMock.updateMargin(portfolio1.getEditablePosition(symbol1));
		transactionsMock.updateMargin(portfolio3.getEditablePosition(symbol1));
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
