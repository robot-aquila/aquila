package ru.prolib.aquila.qforts.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.easymock.EasyMock.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.BusinessEntity;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.concurrency.Multilock;
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
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		multilockMock.unlock();
		control.replay();
		
		service.registerOrder(order);
	}

	@Test
	public void testCancelOrder() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
	public void testCancelOrder_ThrowsIfNotRegistered() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expectedLockable.add(portfolio);
		expectedLockable.add(security);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderExecutionUpdate oeuStub = new QFOrderExecutionUpdate()
				.setExecutionAction(OrderAction.BUY)
				.setExecutionVolume(10L)
				.setFinalStatus(OrderStatus.ACTIVE);
		expect(calculatorMock.executeOrder(order, 10L, FDecimal.of2(100.05))).andReturn(oeuStub);
		QFPortfolioChangeUpdate pcuMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changePosition(portfolio, security, 10L, FDecimal.of2(100.05)))
			.andReturn(pcuMock);
		expect(validatorMock.canChangePositon(pcuMock)).andReturn(QFResult.OK);
		assemblerMock.update(order, oeuStub, 1001L);
		assemblerMock.update(portfolio, pcuMock);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, 10L, FDecimal.of2(100.05));
		
		control.verify();
	}
	
	@Test
	public void testExecuteOrder_ForSeller() throws Exception {
		seqExecutionID.set(1050L);
		EditableOrder order = terminal.createOrder(account, symbol1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expectedLockable.add(portfolio);
		expectedLockable.add(security);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderExecutionUpdate oeuStub = new QFOrderExecutionUpdate()
				.setExecutionAction(OrderAction.SELL)
				.setExecutionVolume(20L)
				.setFinalStatus(OrderStatus.ACTIVE);
		expect(calculatorMock.executeOrder(order, 20L, FDecimal.of2(92.14))).andReturn(oeuStub);
		QFPortfolioChangeUpdate pcuMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changePosition(portfolio, security, -20L, FDecimal.of2(92.14)))
			.andReturn(pcuMock);
		expect(validatorMock.canChangePositon(pcuMock)).andReturn(QFResult.OK);
		assemblerMock.update(order, oeuStub, 1051L);
		assemblerMock.update(portfolio, pcuMock);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, 20L, FDecimal.of2(92.14));
		
		control.verify();
	}

	@Test (expected=QFTransactionException.class)
	public void testExecuteOrder_ThrowsIfNotRegistered() throws Exception {
		EditableOrder order = terminal.createOrder(account, symbol1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expectedLockable.add(portfolio);
		expectedLockable.add(security);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, 10L, FDecimal.of2(100.05));
		
		control.verify();
	}
	
	@Test (expected=QFValidationException.class)
	public void testExecuteOrder_InsufficientFunds() throws Exception {
		seqExecutionID.set(1050L);
		EditableOrder order = terminal.createOrder(account, symbol1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expectedLockable.add(portfolio);
		expectedLockable.add(security);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderExecutionUpdate oeuStub = new QFOrderExecutionUpdate()
				.setExecutionAction(OrderAction.SELL)
				.setExecutionVolume(20L)
				.setFinalStatus(OrderStatus.ACTIVE);
		expect(calculatorMock.executeOrder(order, 20L, FDecimal.of2(92.14))).andReturn(oeuStub);
		QFPortfolioChangeUpdate pcuMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changePosition(portfolio, security, -20L, FDecimal.of2(92.14)))
			.andReturn(pcuMock);
		expect(validatorMock.canChangePositon(pcuMock)).andReturn(QFResult.INSUFFICIENT_FUNDS);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, 20L, FDecimal.of2(92.14));
	}
	
	@Test
	public void testExecuteOrder_PurgeFilled() throws Exception {
		seqExecutionID.set(1005L);
		EditableOrder order = terminal.createOrder(account, symbol1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(order);
		expectedLockable.add(portfolio);
		//expectedLockable.add(portfolio.getEditablePosition(symbol1));
		expectedLockable.add(security);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(order)).andReturn(true);
		QFOrderExecutionUpdate oeuStub = new QFOrderExecutionUpdate()
				.setExecutionAction(OrderAction.SELL)
				.setExecutionVolume(5L)
				.setFinalStatus(OrderStatus.FILLED);
		expect(calculatorMock.executeOrder(order, 5L, FDecimal.of2(100.05))).andReturn(oeuStub);
		QFPortfolioChangeUpdate pcuMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changePosition(portfolio, security, -5L, FDecimal.of2(100.05)))
			.andReturn(pcuMock);
		expect(validatorMock.canChangePositon(pcuMock)).andReturn(QFResult.OK);
		assemblerMock.update(order, oeuStub, 1006L);
		assemblerMock.update(portfolio, pcuMock);
		registryMock.purgeOrder(order);
		multilockMock.unlock();
		control.replay();
		
		service.executeOrder(order, 5L, FDecimal.of2(100.05));
		
		control.verify();
	}
	
	@Test
	public void testUpdateByMarket() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		portfolio.getEditablePosition(symbol1);
		portfolio.getEditablePosition(symbol2);
		portfolio.getEditablePosition(symbol3);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(terminal.getEditableSecurity(symbol1));
		expectedLockable.add(terminal.getEditableSecurity(symbol2));
		expectedLockable.add(terminal.getEditableSecurity(symbol3));
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		portfolio.getEditablePosition(symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(terminal.getEditableSecurity(symbol1));
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.updateByMarket(portfolio);
	}
	
	@Test
	public void testChangeBalance() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(true);
		QFPortfolioChangeUpdate updateMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.changeBalance(portfolio, FMoney.ofRUB2(-1000.0))).andReturn(updateMock);
		assemblerMock.update(portfolio, updateMock);
		multilockMock.unlock();
		control.replay();
		
		service.changeBalance(portfolio, FMoney.ofRUB2(-1000.0));
		
		control.verify();
	}

	@Test (expected=QFTransactionException.class)
	public void testChangeBalance_ThrowsIfNotRegistered() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.changeBalance(portfolio, FMoney.ofRUB2(-1000.0));
	}
	
	@Test
	public void testUpdateMargin() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		portfolio.getEditablePosition(symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(security);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(true);
		QFPortfolioChangeUpdate updateMock = control.createMock(QFPortfolioChangeUpdate.class);
		expect(calculatorMock.updateMargin(portfolio, security)).andReturn(updateMock);
		assemblerMock.update(portfolio, updateMock);
		multilockMock.unlock();
		control.replay();
		
		service.updateMargin(portfolio, security);
		
		control.verify();
	}
	
	@Test (expected=QFTransactionException.class)
	public void testUpdateMargin_ThrowsIfNotRegistered() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		portfolio.getEditablePosition(symbol1);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(security);
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.updateMargin(portfolio, security);
	}
	
	@Test
	public void testUpdateMargin_DoNothingIfNoOpenPosition() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		control.replay();
		
		service.updateMargin(portfolio, terminal.getEditableSecurity(symbol1));
		
		control.verify();
	}
	
	@Test
	public void testMidClearing() throws Exception {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		portfolio.getEditablePosition(symbol1);
		portfolio.getEditablePosition(symbol2);
		portfolio.getEditablePosition(symbol3);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(terminal.getEditableSecurity(symbol1));
		expectedLockable.add(terminal.getEditableSecurity(symbol2));
		expectedLockable.add(terminal.getEditableSecurity(symbol3));
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		portfolio.getEditablePosition(symbol3);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(terminal.getEditableSecurity(symbol3));
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		portfolio.getEditablePosition(symbol1);
		portfolio.getEditablePosition(symbol2);
		portfolio.getEditablePosition(symbol3);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(terminal.getEditableSecurity(symbol1));
		expectedLockable.add(terminal.getEditableSecurity(symbol2));
		expectedLockable.add(terminal.getEditableSecurity(symbol3));
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
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
		portfolio.getEditablePosition(symbol3);
		Set<BusinessEntity> expectedLockable = new HashSet<>();
		expectedLockable.add(portfolio);
		expectedLockable.add(terminal.getEditableSecurity(symbol3));
		expect(assemblerMock.createMultilock(expectedLockable)).andReturn(multilockMock);
		multilockMock.lock();
		expect(registryMock.isRegistered(portfolio)).andReturn(false);
		multilockMock.unlock();
		control.replay();
		
		service.clearing(portfolio);
		
		control.verify();
	}

}
