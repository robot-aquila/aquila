package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventProducer;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.BusinessEntity;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.EventSuppressor;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionEvent;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFAssemblerTest {
	private static Account account1;
	private static Symbol symbol1, symbol2;
	private EditableTerminal terminal;
	private EditablePortfolio portfolio;
	private QFAssembler service;
	
	Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account1 = new Account("TEST");
		symbol1 = new Symbol("BEST");
		symbol2 = new Symbol("QUEST");
	}

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		portfolio = terminal.getEditablePortfolio(account1);
		service = new QFAssembler();
	}
	
	@Test
	public void testUpdate_Order_OEU() throws Exception {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.SELL, 10L, FDecimal.of2(15.34));
		QFOrderExecutionUpdate update = new QFOrderExecutionUpdate()
			.setFinalCurrentVolume(0L)
			.setFinalExecutedValue(FMoney.ofRUB2(30.68))
			.setFinalizationTime(T("2017-04-18T08:33:00Z"))
			.setFinalStatus(OrderStatus.FILLED)
			.setExecutionAction(OrderAction.SELL)
			.setExecutionOrderID(4429L)
			.setExecutionPrice(FDecimal.of2(15.36))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-18T08:33:00.001Z"))
			.setExecutionValue(FMoney.ofRUB2(30.70))
			.setExecutionVolume(10L);
		OrderExecution expectedExec = new OrderExecutionImpl(terminal, 240L,
				null, symbol1, OrderAction.SELL, 4429L,
				T("2017-04-18T08:33:00.001Z"), FDecimal.of2(15.36),
				10L, FMoney.ofRUB2(30.70));
		CountDownLatch testPassed = new CountDownLatch(1);
		order.onExecution().addSyncListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expectedExec, ((OrderExecutionEvent) event).getExecution());
				assertEquals(new DeltaUpdateBuilder()
					.withToken(OrderField.ACTION, OrderAction.SELL)
					.withToken(OrderField.CURRENT_VOLUME, 0L)
					.withToken(OrderField.EXECUTED_VALUE, FMoney.ofRUB2(30.68))
					.withToken(OrderField.INITIAL_VOLUME, 10L)
					.withToken(OrderField.PRICE, FDecimal.of2(15.34))
					.withToken(OrderField.STATUS, OrderStatus.FILLED)
					.withToken(OrderField.TIME, order.getTime())
					.withToken(OrderField.TIME_DONE, T("2017-04-18T08:33:00Z"))
					.withToken(OrderField.TYPE, OrderType.LMT)
					.buildUpdate().getContents(), order.getContents());
				testPassed.countDown();
			}
		});
		
		service.update(order, update, 240L);
		
		assertTrue(testPassed.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testUpdate_Order_OSU() {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.SELL, 10L, FDecimal.of2(15.34));
		QFOrderStatusUpdate update = new QFOrderStatusUpdate()
			.setFinalizationTime(T("2017-04-18T09:30:00Z"))
			.setFinalStatus(OrderStatus.REJECTED)
			.setSystemMessage("Test message");
		
		service.update(order, update);
		
		assertEquals(new DeltaUpdateBuilder()
			.withToken(OrderField.ACTION, OrderAction.SELL)
			.withToken(OrderField.CURRENT_VOLUME, 10L)
			.withToken(OrderField.INITIAL_VOLUME, 10L)
			.withToken(OrderField.PRICE, FDecimal.of2(15.34))
			.withToken(OrderField.STATUS, OrderStatus.REJECTED)
			.withToken(OrderField.TIME, order.getTime())
			.withToken(OrderField.TIME_DONE, T("2017-04-18T09:30:00Z"))
			.withToken(OrderField.TYPE, OrderType.LMT)
			.withToken(OrderField.SYSTEM_MESSAGE, "Test message")
			.buildUpdate().getContents(), order.getContents());
	}
	
	@Test
	public void testUpdate_Portfolio_PCU() {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(account1)
			.setFinalBalance(FMoney.ofRUB2(10000.0))
			.setFinalEquity(FMoney.ofRUB2(8000.0))
			.setFinalFreeMargin(FMoney.ofRUB2(2000.0))
			.setFinalProfitAndLoss(FMoney.ofRUB2(-200.0))
			.setFinalUsedMargin(FMoney.ofRUB2(150.0))
			.setFinalVarMargin(FMoney.ofRUB5(98.25412))
			.setFinalVarMarginClose(FMoney.ofRUB5(1.09882))
			.setFinalVarMarginInter(FMoney.ofRUB5(5.88721));
		update.getOrCreatePositionUpdate(symbol1)
			.setFinalCurrentPrice(FDecimal.of2(100.15))
			.setFinalOpenPrice(FDecimal.of2(94.12))
			.setFinalProfitAndLoss(FMoney.ofRUB2(156.12))
			.setFinalUsedMargin(FMoney.ofRUB2(65.02))
			.setFinalVarMargin(FMoney.ofRUB5(-632.0))
			.setFinalVarMarginClose(FMoney.ofRUB5(-20.0))
			.setFinalVarMarginInter(FMoney.ofRUB5(118.1))
			.setFinalVolume(1L);
		update.getOrCreatePositionUpdate(symbol2)
			.setFinalCurrentPrice(FDecimal.of2(12.82))
			.setFinalOpenPrice(FDecimal.of2(532.81))
			.setFinalProfitAndLoss(FMoney.ofRUB2(11.02))
			.setFinalUsedMargin(FMoney.ofRUB2(4.1))
			.setFinalVarMargin(FMoney.ofRUB5(-9.0))
			.setFinalVarMarginClose(FMoney.ofRUB5(1002.0))
			.setFinalVarMarginInter(FMoney.ofRUB5(18.9))
			.setFinalVolume(10L);
		
		service.update(portfolio, update);
		
		assertEquals(new DeltaUpdateBuilder()
			.withToken(PortfolioField.CURRENCY, FMoney.RUB)
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(8000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(2000.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(-200.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(150.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(98.25412))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(1.09882))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(5.88721))
			.buildUpdate().getContents(), portfolio.getContents());
		assertEquals(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(100.15))
			.withToken(PositionField.CURRENT_VOLUME, 1L)
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(94.12))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(156.12))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(65.02))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-632.0))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(-20.0))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(118.1))
			.buildUpdate().getContents(), portfolio.getPosition(symbol1).getContents());
		assertEquals(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(12.82))
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(532.81))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(11.02))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(4.1))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-9.0))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(1002.0))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(18.9))
			.buildUpdate().getContents(), portfolio.getPosition(symbol2).getContents());
	}
	
	@Test
	public void testCreateMultilock() {
		Portfolio portfolio = terminal.getEditablePortfolio(account1);
		Security security1 = terminal.getEditableSecurity(symbol1),
				 security2 = terminal.getEditableSecurity(symbol2);
		Set<BusinessEntity> objects = new HashSet<>();
		objects.add(security1);
		objects.add(security2);
		objects.add(portfolio);
		
		Lockable actual = service.createMultilock(objects);
		
		assertNotNull(actual);
		Multilock lock = (Multilock) actual;
		List<Lockable> actualObjects = lock.getObjects();
		Set<EventProducer> dummy = new HashSet<>();
		dummy.add(security1);
		dummy.add(security2);
		dummy.add(portfolio);		
		List<Lockable> expectedObjects = new ArrayList<>();
		expectedObjects.add(portfolio);
		expectedObjects.add(security1);
		expectedObjects.add(security2);
		expectedObjects.add(new EventSuppressor(actualObjects.get(3).getLID(), dummy));
		assertEquals(expectedObjects, lock.getObjects());
	}

}
