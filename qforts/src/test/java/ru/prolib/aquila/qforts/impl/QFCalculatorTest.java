package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

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
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFCalculatorTest {
	private static final CDecimal ZERO_MONEY2 = CDecimalBD.ZERO_RUB2;
	private static final CDecimal ZERO_MONEY5 = CDecimalBD.ZERO_RUB5;
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
		// This position should be skipped because of null volume
		p.getEditablePosition(symbol1);
		// This position should be skipped because of zero volume
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
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
					.setChangeProfitAndLoss(CDecimalBD.ofRUB2("86.19"))
					.setChangeUsedMargin(CDecimalBD.ofRUB2("14.05"))
					.setChangeVarMarginInter(CDecimalBD.ofRUB5("14.05012"));
				expect(utilsMock.midClearing(dummy)).andReturn(puStub3);
			} else
			if ( dummy.getSymbol().equals(symbol4) ) {
				puStub4 = new QFPositionChangeUpdate(account1, symbol4)
					.setChangeProfitAndLoss(CDecimalBD.ofRUB2("-15"))
					.setChangeUsedMargin(CDecimalBD.ofRUB2("-40"))
					.setChangeVarMarginInter(CDecimalBD.ofRUB5("-15.02159"));
				expect(utilsMock.midClearing(dummy)).andReturn(puStub4);
			}
		}
		control.replay();

		QFPortfolioChangeUpdate actual = service.midClearing(p);
		
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
			.setChangeProfitAndLoss(CDecimalBD.ofRUB2("71.19"))
			.setChangeUsedMargin(CDecimalBD.ofRUB2("-25.95"))
			.setFinalEquity(CDecimalBD.ofRUB2("10821.19"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("10597.14"))
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("-0.97147"))
			.setPositionUpdate(puStub3)
			.setPositionUpdate(puStub4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClearing() throws Exception {
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
		// This position should be skipped because of null volume
		p.getEditablePosition(symbol1);
		// This position should be skipped because of zero volume
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
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
					.setChangeBalance(CDecimalBD.ofRUB2("-115.28"))
					.setChangeUsedMargin(CDecimalBD.ofRUB2("26.19"));
				expect(utilsMock.clearing(dummy)).andReturn(puStub3);
			} else
			if ( dummy.getSymbol().equals(symbol4) ) {
				puStub4 = new QFPositionChangeUpdate(account1, symbol4)
					.setChangeBalance(CDecimalBD.ofRUB2("-26.0"))
					.setChangeUsedMargin(CDecimalBD.ofRUB2("42.0"));
				expect(utilsMock.clearing(dummy)).andReturn(puStub4);
			}
		}
		control.replay();

		QFPortfolioChangeUpdate actual = service.clearing(p);
		
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
			.setChangeBalance(CDecimalBD.ofRUB2("-141.28")) // =9858.72
			.setChangeUsedMargin(CDecimalBD.ofRUB2("68.19")) // =318.19
			.setFinalProfitAndLoss(ZERO_MONEY2)
			.setFinalEquity(CDecimalBD.ofRUB2("9858.72"))
			.setFinalFreeMargin(CDecimalBD.ofRUB2("9540.53"))
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(ZERO_MONEY5)
			.setPositionUpdate(puStub3)
			.setPositionUpdate(puStub4);
		assertEquals(expected, actual);
		assertEquals(CDecimalBD.ofRUB2("9858.72"), actual.getFinalBalance());
		assertEquals(CDecimalBD.ofRUB2("318.19"), actual.getFinalUsedMargin());
	}

}
