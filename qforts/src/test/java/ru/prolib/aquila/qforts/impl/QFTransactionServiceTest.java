package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.concurrency.MultilockBuilderBE;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFTransactionServiceTest {
	private static Account account;
	private static Symbol symbol1, symbol2, symbol3;
	private IMocksControl control;
	private QFObjectRegistry registryMock;
	private QFAssembler assemblerMock;
	private QFCalculator calculatorMock;
	private Multilock multilockMock;
	private QFValidator validatorMock;
	private AtomicLong seqExecutionID;
	private EditableTerminal terminal;
	private QFTransactionService service;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account = new Account("TEST");
		symbol1 = new Symbol("BEST1");
		symbol2 = new Symbol("BEST2");
		symbol3 = new Symbol("BEST3");
	}

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		control = createStrictControl();
		registryMock = control.createMock(QFObjectRegistry.class);
		assemblerMock = control.createMock(QFAssembler.class);
		calculatorMock = control.createMock(QFCalculator.class);
		multilockMock = control.createMock(Multilock.class);
		validatorMock = control.createMock(QFValidator.class);
		seqExecutionID = new AtomicLong();
		service = new QFTransactionService(registryMock, seqExecutionID,
				calculatorMock, assemblerMock, validatorMock);
		terminal.getEditablePortfolio(account);
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol3);
	}
	
	@Test
	public void testRegisterOrder() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(order)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(false);
		QFOrderStatusUpdate updateMock = control.createMock(QFOrderStatusUpdate.class);
		expect(calculatorMock.updateOrderStatus(order, OrderStatus.ACTIVE, null)).andReturn(updateMock);
		assemblerMock.update(order, updateMock);
		registryMock.register(order);
		multilockMock.unlock();
		control.replay();
		
		service.registerOrder(order);
		
		control.verify();
	}
	
	@Test (expected=QFTransactionException.class)
	public void testRegisterOrder_ThrowsIfRegistered() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(order)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		multilockMock.unlock();
		control.replay();
		
		service.registerOrder(order);
	}

	@Test
	public void testCancelOrder() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(order)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderStatusUpdate updateMock = control.createMock(QFOrderStatusUpdate.class);
		expect(calculatorMock.updateOrderStatus(order, OrderStatus.CANCELLED, null)).andReturn(updateMock);
		assemblerMock.update(order, updateMock);
		registryMock.purgeOrder(order);
		multilockMock.unlock();
		control.replay();
		
		service.cancelOrder(order);
		
		control.verify();
	}
	
	@Test (expected=QFTransactionException.class)
	public void testCancelOrder_ThrowsIfNotRegisteredAndNotFinished() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		order.consume(new DeltaUpdateBuilder().withToken(OrderField.STATUS, OrderStatus.PENDING).buildUpdate());
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(order)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.cancelOrder(order);
		
		control.verify();		
	}
	
	@Test
	public void testCacnelOrder_SkipsIfNotRegisteredButFinished() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.STATUS, OrderStatus.CANCELLED)
				.buildUpdate());
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(order)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.cancelOrder(order);
		
		control.verify();		
	}
	
	@Test
	public void testRejectOrder() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(order)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderStatusUpdate updateMock = control.createMock(QFOrderStatusUpdate.class);
		expect(calculatorMock.updateOrderStatus(order, OrderStatus.REJECTED, "test message"))
			.andReturn(updateMock);
		assemblerMock.update(order, updateMock);
		registryMock.purgeOrder(order);
		multilockMock.unlock();
		control.replay();
		
		service.rejectOrder(order, "test message");
		
		control.verify();
	}
	
	@Test (expected=QFTransactionException.class)
	public void testRejectOrder_ThrowsIfNotRegistered() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(order)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.rejectOrder(order, "test message");
		
		control.verify();		
	}
	
	@Test
	public void testExecuteOrder_ForBuyer() throws Exception {
		seqExecutionID.set(1000L);
		EditableOrder order = terminal.createOrder(account, symbol1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(order)
				.add(order.getPortfolio())
				.add(order.getPosition())
				.add(order.getSecurity()))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderExecutionUpdate oeuStub = new QFOrderExecutionUpdate()
				.setExecutionAction(OrderAction.BUY)
				.setExecutionVolume(CDecimalBD.of(10L))
				.setFinalStatus(OrderStatus.ACTIVE);
		expect(calculatorMock.executeOrder(order, CDecimalBD.of(10L), CDecimalBD.of("100.05")))
			.andReturn(oeuStub);
		QFPortfolioChangeUpdate pcuMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changePosition(portfolio, security, CDecimalBD.of(10L), CDecimalBD.of("100.05")))
			.andReturn(pcuMock);
		expect(validatorMock.canChangePositon(pcuMock)).andReturn(QFResult.OK);
		assemblerMock.update(order, oeuStub, 1001L);
		assemblerMock.update(portfolio, pcuMock);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, CDecimalBD.of(10L), CDecimalBD.of("100.05"));
		
		control.verify();
	}
	
	@Test
	public void testExecuteOrder_ForSeller() throws Exception {
		seqExecutionID.set(1050L);
		EditableOrder order = terminal.createOrder(account, symbol1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(order)
				.add(order.getPortfolio())
				.add(order.getPosition())
				.add(order.getSecurity()))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderExecutionUpdate oeuStub = new QFOrderExecutionUpdate()
				.setExecutionAction(OrderAction.SELL)
				.setExecutionVolume(CDecimalBD.of(20L))
				.setFinalStatus(OrderStatus.ACTIVE);
		expect(calculatorMock.executeOrder(order, CDecimalBD.of(20L), CDecimalBD.of("92.14")))
			.andReturn(oeuStub);
		QFPortfolioChangeUpdate pcuMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changePosition(portfolio, security, CDecimalBD.of(-20L), CDecimalBD.of("92.14")))
			.andReturn(pcuMock);
		expect(validatorMock.canChangePositon(pcuMock)).andReturn(QFResult.OK);
		assemblerMock.update(order, oeuStub, 1051L);
		assemblerMock.update(portfolio, pcuMock);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, CDecimalBD.of(20L), CDecimalBD.of("92.14"));
		
		control.verify();
	}

	@Test (expected=QFTransactionException.class)
	public void testExecuteOrder_ThrowsIfNotRegistered() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(order)
				.add(order.getPortfolio())
				.add(order.getPosition())
				.add(order.getSecurity()))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, CDecimalBD.of(10L), CDecimalBD.of("100.05"));
		
		control.verify();
	}
	
	@Test
	public void testExecuteOrder_Complete_InsufficientFunds() throws Exception {
		QFObjectRegistry obj_reg = new QFObjectRegistry();
		service = new QFTransactionService(obj_reg, seqExecutionID);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.SETTLEMENT_PRICE, of(110000L))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("14937.51"))
				.withToken(SecurityField.TICK_SIZE, of(10L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.65512"))
				.buildUpdate());
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		obj_reg.register(portfolio);
		new QForts(obj_reg, service).changeBalance(portfolio, ofRUB2("100000.00"));
		EditableOrder order = (EditableOrder) terminal.createOrder(
				account, symbol1, OrderAction.BUY, of(20L), of(120000L)
			);
		obj_reg.register(order);
		
		service.executeOrder(order, of(20L), of(105000L));

		assertEquals(ZERO_RUB2, portfolio.getUsedMargin());
		assertEquals(ZERO_RUB2, portfolio.getProfitAndLoss());
		assertEquals(ofRUB2("100000"), portfolio.getEquity());
		assertEquals(ofRUB2("100000"), portfolio.getFreeMargin());
		assertEquals(of(20L), order.getCurrentVolume());
		assertNull(order.getExecutedValue());
		assertEquals(OrderStatus.CANCELLED, order.getStatus());
		assertEquals("Execution rejected (code 1)", order.getSystemMessage());
		assertFalse(obj_reg.isRegistered(order));
	}
	
	@Test
	public void testExecuteOrder_Partial_InsufficientFunds() throws Exception {
		QFObjectRegistry obj_reg = new QFObjectRegistry();
		service = new QFTransactionService(obj_reg, seqExecutionID);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.SETTLEMENT_PRICE, of(110000L))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("14937.51"))
				.withToken(SecurityField.TICK_SIZE, of(10L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.65512"))
				.buildUpdate());
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		obj_reg.register(portfolio);
		new QForts(obj_reg, service).changeBalance(portfolio, ofRUB2("100000.00"));
		EditableOrder order = (EditableOrder) terminal.createOrder(
				account, symbol1, OrderAction.BUY, of(20L), of(120000L)
			);
		obj_reg.register(order);
		service.executeOrder(order, of(5L), of(105000L));
		
		assertEquals(ofRUB2( "74687.55"), portfolio.getUsedMargin());
		assertEquals(ofRUB2( "34137.80"), portfolio.getProfitAndLoss());
		assertEquals(ofRUB2("134137.80"), portfolio.getEquity());
		assertEquals(ofRUB2( "59450.25"), portfolio.getFreeMargin());
		assertEquals(of(15L), order.getCurrentVolume());
		assertEquals(ofRUB5("716893.80"), order.getExecutedValue());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertNull(order.getSystemMessage());
		assertTrue(obj_reg.isRegistered(order));
		
		service.executeOrder(order, of(5L), of(112000L));
		
		// All properties unchanged, except order status and sys. message 
		assertEquals(ofRUB2( "74687.55"), portfolio.getUsedMargin());
		assertEquals(ofRUB2( "34137.80"), portfolio.getProfitAndLoss());
		assertEquals(ofRUB2("134137.80"), portfolio.getEquity());
		assertEquals(ofRUB2( "59450.25"), portfolio.getFreeMargin());
		assertEquals(of(15L), order.getCurrentVolume());
		assertEquals(ofRUB5("716893.80"), order.getExecutedValue());
		assertEquals(OrderStatus.CANCELLED, order.getStatus());
		assertEquals("Execution rejected (code 1)", order.getSystemMessage());
		assertFalse(obj_reg.isRegistered(order));
	}
	
	@Test
	public void testExecuteOrder_PurgeFilled() throws Exception {
		seqExecutionID.set(1005L);
		EditableOrder order = terminal.createOrder(account, symbol1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(order)
				.add(order.getPortfolio())
				.add(order.getPosition())
				.add(order.getSecurity()))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderExecutionUpdate oeuStub = new QFOrderExecutionUpdate()
				.setExecutionAction(OrderAction.SELL)
				.setExecutionVolume(CDecimalBD.of(5L))
				.setFinalStatus(OrderStatus.FILLED);
		expect(calculatorMock.executeOrder(order, CDecimalBD.of(5L), CDecimalBD.of("100.05")))
			.andReturn(oeuStub);
		QFPortfolioChangeUpdate pcuMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changePosition(portfolio, security, CDecimalBD.of(-5L), CDecimalBD.of("100.05")))
			.andReturn(pcuMock);
		expect(validatorMock.canChangePositon(pcuMock)).andReturn(QFResult.OK);
		assemblerMock.update(order, oeuStub, 1006L);
		assemblerMock.update(portfolio, pcuMock);
		registryMock.purgeOrder(order);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, CDecimalBD.of(5L), CDecimalBD.of("100.05"));
		
		control.verify();
	}
	
	@Test
	public void testUpdateByMarket() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(portfolio)
				.add(portfolio.getEditablePosition(symbol1))
				.add(portfolio.getEditablePosition(symbol2))
				.add(portfolio.getEditablePosition(symbol3))
				.add(terminal.getEditableSecurity(symbol1))
				.add(terminal.getEditableSecurity(symbol2))
				.add(terminal.getEditableSecurity(symbol3)))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(true);
		QFPortfolioChangeUpdate updateMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.updateByMarket(portfolio)).andReturn(updateMock);
		assemblerMock.update(portfolio, updateMock);
		multilockMock.unlock();
		control.replay();
		
		service.updateByMarket(portfolio);
		
		control.verify();
	}
	
	@Test (expected=QFTransactionException.class)
	public void testUpdateByMarket_ThrowsIfNotRegistered() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(portfolio)
				.add(portfolio.getEditablePosition(symbol1))
				.add(terminal.getEditableSecurity(symbol1)))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.updateByMarket(portfolio);
	}
	
	@Test
	public void testChangeBalance() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(portfolio)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(true);
		QFPortfolioChangeUpdate updateMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changeBalance(portfolio, CDecimalBD.ofRUB2("-1000"))).andReturn(updateMock);
		assemblerMock.update(portfolio, updateMock);
		multilockMock.unlock();
		control.replay();
		
		service.changeBalance(portfolio, CDecimalBD.ofRUB2("-1000"));
		
		control.verify();
	}

	@Test (expected=QFTransactionException.class)
	public void testChangeBalance_ThrowsIfNotRegistered() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE().add(portfolio)))
			.andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.changeBalance(portfolio, CDecimalBD.ofRUB2("-1000"));
	}
	
	@Test
	public void testUpdateMargin() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		EditablePosition position = portfolio.getEditablePosition(symbol1);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(position)
				.add(portfolio)
				.add(security))).andReturn(multilockMock);
		multilockMock.lock();
		QFPortfolioChangeUpdate updateMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.updateMargin(position)).andReturn(updateMock);
		assemblerMock.update(portfolio, updateMock);
		multilockMock.unlock();
		control.replay();
		
		service.updateMargin(position);
		
		control.verify();
	}
	
	@Test
	public void testMidClearing() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(portfolio)
				.add(portfolio.getPosition(symbol1))
				.add(terminal.getSecurity(symbol1))
				.add(portfolio.getPosition(symbol2))
				.add(terminal.getSecurity(symbol2))
				.add(portfolio.getPosition(symbol3))
				.add(terminal.getSecurity(symbol3)))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(true);
		QFPortfolioChangeUpdate updateMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.midClearing(portfolio)).andReturn(updateMock);
		assemblerMock.update(portfolio, updateMock);
		multilockMock.unlock();
		control.replay();
		
		service.midClearing(portfolio);
		
		control.verify();
	}

	@Test (expected=QFTransactionException.class)
	public void testMidClearing_ThrowsIfNotRegistered() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(portfolio)
				.add(portfolio.getEditablePosition(symbol3))
				.add(terminal.getEditableSecurity(symbol3)))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.midClearing(portfolio);
		
		control.verify();
	}

	@Test
	public void testClearing() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(portfolio)
				.add(portfolio.getEditablePosition(symbol1))
				.add(portfolio.getEditablePosition(symbol2))
				.add(portfolio.getEditablePosition(symbol3))
				.add(terminal.getEditableSecurity(symbol1))
				.add(terminal.getEditableSecurity(symbol2))
				.add(terminal.getEditableSecurity(symbol3)))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(true);
		QFPortfolioChangeUpdate updateMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.clearing(portfolio)).andReturn(updateMock);
		assemblerMock.update(portfolio, updateMock);
		multilockMock.unlock();
		control.replay();
		
		service.clearing(portfolio);
		
		control.verify();
	}

	@Test (expected=QFTransactionException.class)
	public void testClearing_ThrowsIfNotRegistered() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		expect(assemblerMock.createMultilock(new MultilockBuilderBE()
				.add(portfolio)
				.add(portfolio.getEditablePosition(symbol3))
				.add(terminal.getEditableSecurity(symbol3)))).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.clearing(portfolio);
		
		control.verify();
	}

}
