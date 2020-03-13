package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionEvent;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.concurrency.EventSuppressor;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.concurrency.MultilockBuilderBE;
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
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
	}
	
	@Test
	public void testUpdate_Order_OEU() throws Exception {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("15.34"));
		QFOrderExecutionUpdate update = new QFOrderExecutionUpdate()
			.setFinalCurrentVolume(CDecimalBD.of(0L))
			.setFinalExecutedValue(CDecimalBD.ofRUB5("30.68"))
			.setFinalizationTime(T("2017-04-18T08:33:00Z"))
			.setFinalStatus(OrderStatus.FILLED)
			.setExecutionAction(OrderAction.SELL)
			.setExecutionOrderID(4429L)
			.setExecutionPrice(CDecimalBD.of("15.36"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-18T08:33:00.001Z"))
			.setExecutionValue(CDecimalBD.ofRUB5("30.70"))
			.setExecutionVolume(CDecimalBD.of(10L))
			.setExecutionExternalID("TX5268812-T2");
		OrderExecution expectedExec = new OrderExecutionImpl(terminal, 240L,
				"TX5268812-T2", symbol1, OrderAction.SELL, 4429L,
				T("2017-04-18T08:33:00.001Z"),
				CDecimalBD.of("15.36"),
				CDecimalBD.of(10L),
				CDecimalBD.ofRUB5("30.70"));
		CountDownLatch testPassed = new CountDownLatch(1);
		order.onExecution().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expectedExec, ((OrderExecutionEvent) event).getExecution());
				assertEquals(new DeltaUpdateBuilder()
					.withToken(OrderField.ACTION, OrderAction.SELL)
					.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(0L))
					.withToken(OrderField.EXECUTED_VALUE, CDecimalBD.ofRUB5("30.68"))
					.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L))
					.withToken(OrderField.PRICE, CDecimalBD.of("15.34"))
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
				symbol1,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("15.34"));
		QFOrderStatusUpdate update = new QFOrderStatusUpdate()
			.setFinalizationTime(T("2017-04-18T09:30:00Z"))
			.setFinalStatus(OrderStatus.REJECTED)
			.setSystemMessage("Test message");
		
		service.update(order, update);
		
		assertEquals(new DeltaUpdateBuilder()
			.withToken(OrderField.ACTION, OrderAction.SELL)
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L))
			.withToken(OrderField.PRICE, CDecimalBD.of("15.34"))
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
			.setFinalBalance(CDecimalBD.ofRUB2("10000"))
			.setFinalEquity(CDecimalBD.ofRUB2("8000"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("2000"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("-200"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("150"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("98.25412"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("1.09882"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("5.88721"));
		update.getOrCreatePositionUpdate(symbol1)
			.setFinalCurrentPrice(CDecimalBD.of("100.15"))
			.setFinalOpenPrice(CDecimalBD.of("94.12"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB5("156.12"))
			.setFinalUsedMargin(CDecimalBD.ofRUB5("65.02"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-632"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("-20"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("118.1"))
			.setFinalVolume(CDecimalBD.of(1L))
			.setFinalTickValue(ofRUB5("12.17601"));
		update.getOrCreatePositionUpdate(symbol2)
			.setFinalCurrentPrice(CDecimalBD.of("12.82"))
			.setFinalOpenPrice(CDecimalBD.of("532.81"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB5("11.02"))
			.setFinalUsedMargin(CDecimalBD.ofRUB5("4.1"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-9"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("1002"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("18.9"))
			.setFinalVolume(CDecimalBD.of(10L))
			.setFinalTickValue(ofUSD2("14.26"));
		
		service.update(portfolio, update);
		
		assertEquals(new DeltaUpdateBuilder()
			.withToken(PortfolioField.CURRENCY, "RUB")
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB5("10000"))
			.withToken(PortfolioField.EQUITY, CDecimalBD.ofRUB5("8000"))
			.withToken(PortfolioField.FREE_MARGIN, CDecimalBD.ofRUB5("2000"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, CDecimalBD.ofRUB5("-200"))
			.withToken(PortfolioField.USED_MARGIN, CDecimalBD.ofRUB5("150"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("98.25412"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("1.09882"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("5.88721"))
			.buildUpdate().getContents(), portfolio.getContents());
		assertEquals(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("100.15"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(1L))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("94.12"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB5("156.12"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB5("65.02"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-632"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("-20"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("118.1"))
			.withToken(QFPositionField.QF_TICK_VALUE, ofRUB5("12.17601"))
			.buildUpdate().getContents(), portfolio.getPosition(symbol1).getContents());
		assertEquals(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("12.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("532.81"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB5("11.02"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB5("4.1"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-9"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("1002"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("18.9"))
			.withToken(QFPositionField.QF_TICK_VALUE, ofUSD2("14.26"))
			.buildUpdate().getContents(), portfolio.getPosition(symbol2).getContents());
	}
	
	@Test
	public void testCreateMultilock_B() {
		MultilockBuilderBE builder = new MultilockBuilderBE()
				.add(terminal.getEditablePortfolio(account1))
				.add(terminal.getEditableSecurity(symbol1))
				.add(terminal.getEditableSecurity(symbol2));
		
		EventSuppressor actual = (EventSuppressor) service.createMultilock(builder);
		
		Multilock mlock = new Multilock(actual.getMultilock().getLID(), builder.getObjects());
		EventSuppressor expected = new EventSuppressor(actual.getLID(), builder.getObjects(), mlock);
		assertEquals(expected, actual);
	}

}
