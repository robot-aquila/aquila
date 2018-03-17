package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStub;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFCalculatorTest {
	private static final CDecimal ZERO_MONEY2 = CDecimalBD.ZERO_RUB2;
	private static final CDecimal ZERO_MONEY5 = CDecimalBD.ZERO_RUB5;
	private static final CDecimal ZERO = CDecimalBD.ZERO;
	private static final CDecimal ZERO_PRICE = CDecimalBD.of("0.00");
	private static Account account1;
	private static Symbol symbol1, symbol2, symbol3, symbol4;
	private IMocksControl control;
	private QFCalcUtils utilsMock;
	private SchedulerStub schedulerStub;
	private QFCalculator service;
	private EditableTerminal terminal;
	private EditableSecurity security1;
	
	Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		account1 = new Account("TEST");
		symbol1 = new Symbol("BEST");
		symbol2 = new Symbol("QUEST");
		symbol3 = new Symbol("GUEST");
		symbol4 = new Symbol("LEAST");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		utilsMock = control.createMock(QFCalcUtils.class);
		schedulerStub = new SchedulerStub();
		service = new QFCalculator(utilsMock);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.withScheduler(schedulerStub)
			.buildTerminal();
		terminal.getEditablePortfolio(account1).consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("100000"))
			.buildUpdate());
		security1 = terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol3);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol4);
		terminal.getEditablePortfolio(account1);
	}
	
	@Test
	public void testChangePosition() throws Exception {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY, CDecimalBD.ofRUB2("5000"))
			.withToken(PortfolioField.FREE_MARGIN, CDecimalBD.ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN, CDecimalBD.ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("190"))
			.buildUpdate());
		Position pos = p.getEditablePosition(symbol1);
		QFPositionChangeUpdate pcuStub = new QFPositionChangeUpdate(account1, symbol1)
			.setChangeProfitAndLoss(CDecimalBD.ofRUB2("-15"))
			.setChangeUsedMargin(CDecimalBD.ofRUB2("72.15"))
			.setChangeVarMargin(CDecimalBD.ofRUB5("80.00015"))
			.setChangeVarMarginClose(CDecimalBD.ofRUB5("1"))
			.setChangeVarMarginInter(CDecimalBD.ofRUB5("99"));
		expect(utilsMock.changePosition(pos, CDecimalBD.of(150L), CDecimalBD.of("52.25")))
			.andReturn(pcuStub);
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.changePosition(p,
				security1,
				CDecimalBD.of(150L),
				CDecimalBD.of("52.25"));
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(CDecimalBD.ofRUB2("10000"))
			.setInitialEquity(CDecimalBD.ofRUB2("5000"))
			.setInitialFreeMargin(CDecimalBD.ofRUB2("4500"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("750"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("250"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("10.9"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("500"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("190"))
			.setChangeBalance(ZERO_MONEY2)
			.setChangeProfitAndLoss(CDecimalBD.ofRUB2("-15"))
			.setChangeUsedMargin(CDecimalBD.ofRUB2("72.15"))
			.setChangeVarMargin(CDecimalBD.ofRUB5("80.00015"))
			.setChangeVarMarginClose(CDecimalBD.ofRUB5("1"))
			.setChangeVarMarginInter(CDecimalBD.ofRUB5("99"))
			.setFinalEquity(CDecimalBD.ofRUB2("10735"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("10412.85"))
			.setPositionUpdate(pcuStub);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateByMarket() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY, CDecimalBD.ofRUB2("5000"))
			.withToken(PortfolioField.FREE_MARGIN, CDecimalBD.ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN, CDecimalBD.ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("190"))
			.buildUpdate());
		p.getEditablePosition(symbol1); // position should be skipped because of null volume
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
			// should be skipped because of zero volume
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(0L))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());
		QFPositionChangeUpdate puStub3 = null, puStub4 = null;
		for ( Position dummy : p.getPositions() ) {
			if ( dummy.getSymbol().equals(symbol3) ) {
				puStub3 = new QFPositionChangeUpdate(account1, symbol3)
					.setChangeProfitAndLoss(CDecimalBD.ofRUB2("-10"))
					.setChangeUsedMargin(CDecimalBD.ofRUB2("20"))
					.setChangeVarMargin(CDecimalBD.ofRUB5("-5"));
				expect(utilsMock.refreshByCurrentState(dummy)).andReturn(puStub3);
			} else
			if ( dummy.getSymbol().equals(symbol4) ) {
				puStub4 = new QFPositionChangeUpdate(account1, symbol4)
					.setChangeProfitAndLoss(CDecimalBD.ofRUB2("15"))
					.setChangeUsedMargin(CDecimalBD.ofRUB2("-40"))
					.setChangeVarMargin(CDecimalBD.ofRUB5("25"));
				expect(utilsMock.refreshByCurrentState(dummy)).andReturn(puStub4);
			}
		}
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.updateByMarket(p);
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(CDecimalBD.ofRUB2("10000"))
			.setInitialEquity(CDecimalBD.ofRUB2("5000"))
			.setInitialFreeMargin(CDecimalBD.ofRUB2("4500"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("750"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("250"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("10.9"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("500"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("190"))
			.setChangeBalance(ZERO_MONEY2)
			.setChangeUsedMargin(CDecimalBD.ofRUB2("-20"))
			.setChangeVarMargin(CDecimalBD.ofRUB5("20"))
			.setChangeProfitAndLoss(CDecimalBD.ofRUB2("5"))
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5)
			.setFinalEquity(CDecimalBD.ofRUB2("10755"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("10525"))
			.setPositionUpdate(puStub3)
			.setPositionUpdate(puStub4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateMargin() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY, CDecimalBD.ofRUB2("5000"))
			.withToken(PortfolioField.FREE_MARGIN, CDecimalBD.ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN, CDecimalBD.ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("190"))
			.buildUpdate());
		p.getEditablePosition(symbol1).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(12L))
			.buildUpdate());
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(95L))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());
		QFPositionChangeUpdate puStub = new QFPositionChangeUpdate(account1, symbol3)
			.setChangeProfitAndLoss(CDecimalBD.ofRUB2("15"))
			.setChangeUsedMargin(CDecimalBD.ofRUB2("-40"))
			.setChangeVarMargin(CDecimalBD.ofRUB5("25"));
		expect(utilsMock.refreshByCurrentState(p.getPosition(symbol3))).andReturn(puStub);
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.updateMargin(p.getPosition(symbol3));
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(CDecimalBD.ofRUB2("10000"))
			.setInitialEquity(CDecimalBD.ofRUB2("5000"))
			.setInitialFreeMargin(CDecimalBD.ofRUB2("4500"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("750"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("250"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("10.9"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("500"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("190"))
			.setChangeBalance(ZERO_MONEY2)
			.setChangeUsedMargin(CDecimalBD.ofRUB2("-40"))
			.setChangeVarMargin(CDecimalBD.ofRUB5("25"))
			.setChangeProfitAndLoss(CDecimalBD.ofRUB2("15"))
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5)
			.setFinalEquity(CDecimalBD.ofRUB2("10765"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("10555"))
			.setPositionUpdate(puStub);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateMargin_WhenZeroPosition() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY, CDecimalBD.ofRUB2("5000"))
			.withToken(PortfolioField.FREE_MARGIN, CDecimalBD.ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN, CDecimalBD.ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("190"))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(0L))
			.buildUpdate());
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.updateMargin(p.getPosition(symbol3));

		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(CDecimalBD.ofRUB2("10000"))
			.setInitialEquity(CDecimalBD.ofRUB2("5000"))
			.setInitialFreeMargin(CDecimalBD.ofRUB2("4500"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("750"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("250"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("10.9"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("500"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("190"))
			.setChangeBalance(ZERO_MONEY2)
			.setChangeUsedMargin(ZERO_MONEY2)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeProfitAndLoss(ZERO_MONEY2)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5)
			.setFinalEquity(CDecimalBD.ofRUB2("10750"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("10500"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangeBalance() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY, CDecimalBD.ofRUB2("5000"))
			.withToken(PortfolioField.FREE_MARGIN, CDecimalBD.ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN, CDecimalBD.ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("190"))
			.buildUpdate());
		control.replay();

		QFPortfolioChangeUpdate actual = service.changeBalance(p, CDecimalBD.ofRUB2("-4500"));
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(CDecimalBD.ofRUB2("10000"))
			.setInitialEquity(CDecimalBD.ofRUB2("5000"))
			.setInitialFreeMargin(CDecimalBD.ofRUB2("4500"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("750"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("250"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("10.9"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("500"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("190"))
			.setChangeBalance(CDecimalBD.ofRUB2("-4500"))
			.setChangeProfitAndLoss(ZERO_MONEY2)
			.setChangeUsedMargin(ZERO_MONEY2)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5)
			.setFinalEquity(CDecimalBD.ofRUB2("6250"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("6000"));
		assertEquals(expected, actual);
	}

	@Test
	public void testExecuteOrder_PartialExecution() throws Exception {
		schedulerStub.setFixedTime("2017-04-16T21:20:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.SELL,
				CDecimalBD.of(100L),
				CDecimalBD.of("49.15"));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(80L))
			.withToken(OrderField.EXECUTED_VALUE, CDecimalBD.ofRUB2("127.15"))
			.buildUpdate());
		expect(utilsMock.priceToMoney(security1, CDecimalBD.of(20L), CDecimalBD.of("49.20")))
			.andReturn(CDecimalBD.ofRUB2("128.19"));
		control.replay();
		
		QFOrderExecutionUpdate actual = service.executeOrder(order,
				CDecimalBD.of(20L),
				CDecimalBD.of("49.20"));
		
		control.verify();
		QFOrderExecutionUpdate expected = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(CDecimalBD.of(80L))
			.setInitialExecutedValue(CDecimalBD.ofRUB2("127.15"))
			.setInitialStatus(OrderStatus.ACTIVE)
			.setChangeCurrentVolume(CDecimalBD.of(-20L))
			.setChangeExecutedValue(CDecimalBD.ofRUB2("128.19"))
			.setFinalStatus(OrderStatus.ACTIVE)
			.setExecutionAction(OrderAction.SELL)
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(CDecimalBD.of("49.20"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-16T21:20:00Z"))
			.setExecutionValue(CDecimalBD.ofRUB2("128.19"))
			.setExecutionVolume(CDecimalBD.of(20L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testExecuteOrder_CompleteExecution() throws Exception {
		schedulerStub.setFixedTime("2017-04-16T21:28:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(20L),
				CDecimalBD.of("46.25"));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(15L))
			.withToken(OrderField.EXECUTED_VALUE, CDecimalBD.ofRUB2("34.24"))
			.buildUpdate());
		expect(utilsMock.priceToMoney(security1, CDecimalBD.of(15L), CDecimalBD.of("46.20")))
			.andReturn(CDecimalBD.ofRUB2("96.54"));
		control.replay();
		
		QFOrderExecutionUpdate actual = service.executeOrder(order,
				CDecimalBD.of(15L),
				CDecimalBD.of("46.20"));
		
		control.verify();
		QFOrderExecutionUpdate expected = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(CDecimalBD.of(15L))
			.setInitialExecutedValue(CDecimalBD.ofRUB2("34.24"))
			.setInitialStatus(OrderStatus.ACTIVE)
			.setChangeCurrentVolume(CDecimalBD.of(-15L))
			.setChangeExecutedValue(CDecimalBD.ofRUB2("96.54"))
			.setFinalStatus(OrderStatus.FILLED)
			.setFinalizationTime(T("2017-04-16T21:28:00Z"))
			.setExecutionAction(OrderAction.BUY)
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(CDecimalBD.of("46.20"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-16T21:28:00Z"))
			.setExecutionValue(CDecimalBD.ofRUB2("96.54"))
			.setExecutionVolume(CDecimalBD.of(15L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateOrderStatus() {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(20L),
				CDecimalBD.of("46.25"));
		control.replay();
		
		QFOrderStatusUpdate actual = service.updateOrderStatus(order, OrderStatus.ACTIVE, null);
		
		control.verify();
		QFOrderStatusUpdate expected = new QFOrderStatusUpdate()
			.setInitialStatus(OrderStatus.PENDING)
			.setFinalStatus(OrderStatus.ACTIVE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateOrderStatus_Finalization() {
		schedulerStub.setFixedTime("2017-04-16T22:14:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(20L),
				CDecimalBD.of("46.25"));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.buildUpdate());
		control.replay();
		
		QFOrderStatusUpdate actual = service.updateOrderStatus(order,
				OrderStatus.REJECTED, "Insufficient funds");
		
		control.verify();
		QFOrderStatusUpdate expected = new QFOrderStatusUpdate()
			.setInitialStatus(OrderStatus.ACTIVE)
			.setFinalStatus(OrderStatus.REJECTED)
			.setSystemMessage("Insufficient funds")
			.setFinalizationTime(T("2017-04-16T22:14:00Z"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMidClearing() throws Exception {
		service = new QFCalculator();
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY, ofRUB2( "5000"))
			.withToken(PortfolioField.FREE_MARGIN, ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN, ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, ofRUB5("190"))
			.buildUpdate());
		// position#1
		terminal.getEditableSecurity(symbol1).consume(new DeltaUpdateBuilder() // available security is required
			.withToken(SecurityField.TICK_SIZE, of("0.01"))
			.withToken(SecurityField.TICK_VALUE, ofRUB5("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("40.00"))
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("10.00"))
			.buildUpdate());
		p.getEditablePosition(symbol1); // uninitialized position should be OK
		// position#2
		terminal.getEditableSecurity(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of("0.01"))
			.withToken(SecurityField.TICK_VALUE, ofRUB2("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("12.00"))
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("4.00"))
			.buildUpdate());
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder() // should process closed position 
			.withToken(PositionField.CURRENT_VOLUME, ZERO)
			.withToken(PositionField.OPEN_PRICE, ZERO_PRICE)
			.withToken(PositionField.CURRENT_PRICE, ZERO_PRICE)
			.withToken(PositionField.USED_MARGIN, ZERO_MONEY2)
			.withToken(PositionField.PROFIT_AND_LOSS, ofRUB2("60.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN, ZERO_MONEY5)
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("50.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("10.00000"))
			.buildUpdate());
		// position#3
		terminal.getEditableSecurity(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of("0.005"))
			.withToken(SecurityField.TICK_VALUE, ofRUB2("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("25.785"))	// @25.785
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("50.00"))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, of(10L))
			.withToken(PositionField.OPEN_PRICE, of("250.000")) 		// @25.000
			.withToken(PositionField.CURRENT_PRICE, of("252.500"))		// @25.250
			.withToken(PositionField.USED_MARGIN, ofRUB2("490.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, ofRUB2("9.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN, ofRUB5("5.00000")) // (252.500-250.000)/0.005*0.01=5.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("3.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.buildUpdate());
		// position#4
		terminal.getEditableSecurity(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of("10"))
			.withToken(SecurityField.TICK_VALUE, ofRUB2("2.00"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("160"))		// @160
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("5.00"))
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.withToken(PositionField.OPEN_PRICE, of("-750"))			// @150
			.withToken(PositionField.CURRENT_PRICE, of("-850"))			// @170
			.withToken(PositionField.USED_MARGIN, ofRUB2("25.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, ofRUB2("-14.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN, ofRUB5("-20.00000")) // (-850--750)/10*2.00=-20.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("5.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.buildUpdate());
		control.replay();

		QFPortfolioChangeUpdate actual = service.midClearing(p);
		
		control.verify();
		// position update#1
		QFPositionChangeUpdate expectedPU1 = new QFPositionChangeUpdate(account1, symbol1)
				.setChangeBalance(ZERO_MONEY2)
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY2)
				.setInitialProfitAndLoss(ZERO_MONEY2)
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ZERO_MONEY5)
				.setInitialVarMarginInter(ZERO_MONEY5)
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY2)
				.setFinalProfitAndLoss(ZERO_MONEY2)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU1, actual.getPositionUpdate(symbol1));
		// position update#2
		QFPositionChangeUpdate expectedPU2 = new QFPositionChangeUpdate(account1, symbol2)
				.setChangeBalance(ZERO_MONEY2)
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY2)
				.setInitialProfitAndLoss(ofRUB2("60.00"))
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ofRUB5("50.00000"))
				.setInitialVarMarginInter(ofRUB5("10.00000"))
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY2)
				.setFinalProfitAndLoss(ofRUB2("60.00"))
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ofRUB5("60.00000"));
		assertEquals(expectedPU2, actual.getPositionUpdate(symbol2));
		// position update#3
		QFPositionChangeUpdate expectedPU3 = new QFPositionChangeUpdate(account1, symbol3)
				.setChangeBalance(ZERO_MONEY2)
				.setChangeVolume(ZERO)
				.setInitialVolume(of(10L))
				.setInitialOpenPrice(of("250.000"))
				.setInitialCurrentPrice(of("252.500"))
				.setInitialUsedMargin(ofRUB2("490.00"))
				.setInitialProfitAndLoss(ofRUB2("9.00"))
				.setInitialVarMargin(ofRUB5("5.00000"))
				.setInitialVarMarginClose(ofRUB5("3.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setFinalOpenPrice(of("257.850"))				// @25.785
				.setFinalCurrentPrice(of("257.850"))			// @25.785
				.setFinalUsedMargin(ofRUB2("500.00"))
				.setFinalProfitAndLoss(ofRUB2("19.70"))
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ofRUB5("19.70000"));	// prev.VM=(257.850-250.000)/0.005*0.01=15.70000
																// prev.VMC=3.00000
																// prev.VMI=1.00000
																// total=15.70000+3.00000+1.00000=19.70000
		assertEquals(expectedPU3, actual.getPositionUpdate(symbol3));
		// position update#4
		QFPositionChangeUpdate expectedPU4 = new QFPositionChangeUpdate(account1, symbol4)
				.setChangeBalance(ZERO_MONEY2)
				.setChangeVolume(ZERO)
				.setInitialVolume(of(-5L))
				.setInitialOpenPrice(of("-750"))
				.setInitialCurrentPrice(of("-850"))
				.setInitialUsedMargin(ofRUB2("25.00"))
				.setInitialProfitAndLoss(ofRUB2("-14.00"))
				.setInitialVarMargin(ofRUB5("-20.00000"))
				.setInitialVarMarginClose(ofRUB5("5.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setFinalOpenPrice(of("-800"))					// @160
				.setFinalCurrentPrice(of("-800"))				// @160
				.setFinalUsedMargin(ofRUB2("25.00"))
				.setFinalProfitAndLoss(ofRUB2("-4.00"))
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ofRUB5("-4.00000"));	// prev.VM=(-800--750)/10*2.00=-10.00000
																// prev.VMC=5.00000
																// prev.VMI=1.00000
																// total=-10.00000+5.00000+1.00000=-4.00000
		assertEquals(expectedPU4, actual.getPositionUpdate(symbol4));
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(ofRUB2("10000"))
			.setInitialEquity(ofRUB2("5000"))
			.setInitialFreeMargin(ofRUB2("4500"))
			.setInitialProfitAndLoss(ofRUB2("750"))
			.setInitialUsedMargin(ofRUB2("250"))
			.setInitialVarMargin(ofRUB5("10.9"))
			.setInitialVarMarginClose(ofRUB5("500"))
			.setInitialVarMarginInter(ofRUB5("190"))
			.setChangeBalance(ZERO_MONEY2)
			.setFinalVarMarginInter(ofRUB5("75.70"))	// 0.00 + 60.00 + 19.70 + -4.00 = 75.70
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalProfitAndLoss(ofRUB2("75.70"))		
			.setFinalUsedMargin(ofRUB2("525.00"))		// 0.00 + 0.00 + 500.00 + 25.00 = 525.00
			.setFinalEquity(ofRUB2("10075.70"))			// Balance + PL = 10000.00 - 75.70 = 10075.70
			.setFinalFreeMargin(ofRUB2("9550.70"));		// Equity - Used Margin = 10075.70 - 525.00 = 9550.70
		// We have to make the same order as in actual update. It's hard to predict. Let's use existing order.
		assertEquals(4, actual.getPositionUpdates().size());
		for ( QFPositionChangeUpdate x : actual.getPositionUpdates() ) {
			if ( symbol1.equals(x.getSymbol()) ) {
				expected.setPositionUpdate(expectedPU1);
			} else
			if ( symbol2.equals(x.getSymbol()) ) {
				expected.setPositionUpdate(expectedPU2);
			} else
			if ( symbol3.equals(x.getSymbol()) ) {
				expected.setPositionUpdate(expectedPU3);
			} else
			if ( symbol4.equals(x.getSymbol()) ) {
				expected.setPositionUpdate(expectedPU4);
			}
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClearing() {
		service = new QFCalculator();
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY, ofRUB2( "5000"))
			.withToken(PortfolioField.FREE_MARGIN, ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN, ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, ofRUB5("190"))
			.buildUpdate());
		// position#1
		terminal.getEditableSecurity(symbol1).consume(new DeltaUpdateBuilder() // available security is required
			.withToken(SecurityField.TICK_SIZE, of("0.01"))
			.withToken(SecurityField.TICK_VALUE, ofRUB5("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("40.00"))
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("10.00"))
			.buildUpdate());
		p.getEditablePosition(symbol1); // uninitialized position should be OK
		// position#2
		terminal.getEditableSecurity(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of("0.01"))
			.withToken(SecurityField.TICK_VALUE, ofRUB2("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("12.00"))
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("4.00"))
			.buildUpdate());
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder() // should process closed position 
			.withToken(PositionField.CURRENT_VOLUME, ZERO)
			.withToken(PositionField.OPEN_PRICE, ZERO_PRICE)
			.withToken(PositionField.CURRENT_PRICE, ZERO_PRICE)
			.withToken(PositionField.USED_MARGIN, ZERO_MONEY2)
			.withToken(PositionField.PROFIT_AND_LOSS, ofRUB2("60.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN, ZERO_MONEY5)
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("50.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("10.00000"))
			.buildUpdate());
		// position#3
		terminal.getEditableSecurity(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of("0.005"))
			.withToken(SecurityField.TICK_VALUE, ofRUB2("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("25.785"))	// @25.785
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("50.00"))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, of(10L))
			.withToken(PositionField.OPEN_PRICE, of("250.000")) 		// @25.000
			.withToken(PositionField.CURRENT_PRICE, of("252.500"))		// @25.250
			.withToken(PositionField.USED_MARGIN, ofRUB2("490.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, ofRUB2("9.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN, ofRUB5("5.00000")) // (252.500-250.000)/0.005*0.01=5.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("3.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.buildUpdate());
		// position#4
		terminal.getEditableSecurity(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of("10"))
			.withToken(SecurityField.TICK_VALUE, ofRUB2("2.00"))
			.withToken(SecurityField.SETTLEMENT_PRICE, of("160"))		// @160
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("5.00"))
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.withToken(PositionField.OPEN_PRICE, of("-750"))			// @150
			.withToken(PositionField.CURRENT_PRICE, of("-850"))			// @170
			.withToken(PositionField.USED_MARGIN, ofRUB2("25.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, ofRUB2("-14.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN, ofRUB5("-20.00000")) // (-850--750)/10*2.00=-20.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("5.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.buildUpdate());
		control.replay();

		QFPortfolioChangeUpdate actual = service.clearing(p);
		
		control.verify();
		// position update#1
		QFPositionChangeUpdate expectedPU1 = new QFPositionChangeUpdate(account1, symbol1)
				.setChangeBalance(ZERO_MONEY2)
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY2)
				.setInitialProfitAndLoss(ZERO_MONEY2)
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ZERO_MONEY5)
				.setInitialVarMarginInter(ZERO_MONEY5)
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY2)
				.setFinalProfitAndLoss(ZERO_MONEY2)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU1, actual.getPositionUpdate(symbol1));
		// position update#2
		QFPositionChangeUpdate expectedPU2 = new QFPositionChangeUpdate(account1, symbol2)
				.setChangeBalance(ofRUB2("60.00"))
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY2)
				.setInitialProfitAndLoss(ofRUB2("60.00"))
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ofRUB5("50.00000"))
				.setInitialVarMarginInter(ofRUB5("10.00000"))
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY2)
				.setFinalProfitAndLoss(ZERO_MONEY2)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU2, actual.getPositionUpdate(symbol2));
		// position update#3
		QFPositionChangeUpdate expectedPU3 = new QFPositionChangeUpdate(account1, symbol3)
				.setChangeBalance(ofRUB2("19.70"))
				.setChangeVolume(ZERO)
				.setInitialVolume(of(10L))
				.setInitialOpenPrice(of("250.000"))
				.setInitialCurrentPrice(of("252.500"))
				.setInitialUsedMargin(ofRUB2("490.00"))
				.setInitialProfitAndLoss(ofRUB2("9.00"))
				.setInitialVarMargin(ofRUB5("5.00000"))
				.setInitialVarMarginClose(ofRUB5("3.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setFinalOpenPrice(of("257.850"))				// @25.785
				.setFinalCurrentPrice(of("257.850"))			// @25.785
				.setFinalUsedMargin(ofRUB2("500.00"))
				.setFinalProfitAndLoss(ZERO_MONEY2)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU3, actual.getPositionUpdate(symbol3));
		// position update#4
		QFPositionChangeUpdate expectedPU4 = new QFPositionChangeUpdate(account1, symbol4)
				.setChangeBalance(ofRUB2("-4.00"))
				.setChangeVolume(ZERO)
				.setInitialVolume(of(-5L))
				.setInitialOpenPrice(of("-750"))
				.setInitialCurrentPrice(of("-850"))
				.setInitialUsedMargin(ofRUB2("25.00"))
				.setInitialProfitAndLoss(ofRUB2("-14.00"))
				.setInitialVarMargin(ofRUB5("-20.00000"))
				.setInitialVarMarginClose(ofRUB5("5.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setFinalOpenPrice(of("-800"))					// @160
				.setFinalCurrentPrice(of("-800"))				// @160
				.setFinalUsedMargin(ofRUB2("25.00"))
				.setFinalProfitAndLoss(ZERO_MONEY2)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU4, actual.getPositionUpdate(symbol4));

		
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
				.setInitialBalance(ofRUB2("10000"))
				.setInitialEquity(ofRUB2("5000"))
				.setInitialFreeMargin(ofRUB2("4500"))
				.setInitialProfitAndLoss(ofRUB2("750"))
				.setInitialUsedMargin(ofRUB2("250"))
				.setInitialVarMargin(ofRUB5("10.9"))
				.setInitialVarMarginClose(ofRUB5("500"))
				.setInitialVarMarginInter(ofRUB5("190"))
				.setChangeBalance(ofRUB2("75.70"))
				.setFinalVarMarginInter(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalProfitAndLoss(ZERO_MONEY2)		
				.setFinalUsedMargin(ofRUB2("525.00"))		// 0.00 + 0.00 + 500.00 + 25.00 = 525.00
				.setFinalEquity(ofRUB2("10075.70"))
				.setFinalFreeMargin(ofRUB2("9550.70"));
			assertEquals(4, actual.getPositionUpdates().size());
			for ( QFPositionChangeUpdate x : actual.getPositionUpdates() ) {
				if ( symbol1.equals(x.getSymbol()) ) {
					expected.setPositionUpdate(expectedPU1);
				} else
				if ( symbol2.equals(x.getSymbol()) ) {
					expected.setPositionUpdate(expectedPU2);
				} else
				if ( symbol3.equals(x.getSymbol()) ) {
					expected.setPositionUpdate(expectedPU3);
				} else
				if ( symbol4.equals(x.getSymbol()) ) {
					expected.setPositionUpdate(expectedPU4);
				}
			}
			assertEquals(expected, actual);
	}
	
}
