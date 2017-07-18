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
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
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
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(100000.0))
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
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(5000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(4500.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(750.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(250.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(10.9))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(500.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(190.0))
			.buildUpdate());
		Position pos = p.getEditablePosition(symbol1);
		QFPositionChangeUpdate pcuStub = new QFPositionChangeUpdate(account1, symbol1)
			.setChangeProfitAndLoss(FMoney.ofRUB2(-15.0))
			.setChangeUsedMargin(FMoney.ofRUB2(72.15))
			.setChangeVarMargin(FMoney.ofRUB5(80.00015))
			.setChangeVarMarginClose(FMoney.ofRUB5(1.0))
			.setChangeVarMarginInter(FMoney.ofRUB5(99.0));
		expect(utilsMock.changePosition(pos, 150L, FDecimal.of2(52.25))).andReturn(pcuStub);
		control.replay();
		
		QFPortfolioChangeUpdate actual =
				service.changePosition(p, security1, 150L, FDecimal.of2(52.25));
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setInitialEquity(FMoney.ofRUB2(5000.0))
			.setInitialFreeMargin(FMoney.ofRUB2(4500.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(750.0))
			.setInitialUsedMargin(FMoney.ofRUB2(250.0))
			.setInitialVarMargin(FMoney.ofRUB5(10.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(500.0))
			.setInitialVarMarginInter(FMoney.ofRUB5(190.0))
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeProfitAndLoss(FMoney.ofRUB2(-15.0))
			.setChangeUsedMargin(FMoney.ofRUB2(72.15))
			.setChangeVarMargin(FMoney.ofRUB5(80.00015))
			.setChangeVarMarginClose(FMoney.ofRUB5(1.0))
			.setChangeVarMarginInter(FMoney.ofRUB5(99.0))
			.setFinalEquity(FMoney.ofRUB2(10735.0))
			.setFinalFreeMargin(FMoney.ofRUB2(10412.85))
			.setPositionUpdate(pcuStub);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateByMarket() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(5000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(4500.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(750.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(250.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(10.9))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(500.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(190.0))
			.buildUpdate());
		p.getEditablePosition(symbol1); // position should be skipped because of null volume
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
			// should be skipped because of zero volume
			.withToken(PositionField.CURRENT_VOLUME, 0L)
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		QFPositionChangeUpdate puStub3 = null, puStub4 = null;
		for ( Position dummy : p.getPositions() ) {
			if ( dummy.getSymbol().equals(symbol3) ) {
				puStub3 = new QFPositionChangeUpdate(account1, symbol3)
					.setChangeProfitAndLoss(FMoney.ofRUB2(-10.0))
					.setChangeUsedMargin(FMoney.ofRUB2(20.0))
					.setChangeVarMargin(FMoney.ofRUB5(-5.0));
				expect(utilsMock.refreshByCurrentState(dummy)).andReturn(puStub3);
			} else
			if ( dummy.getSymbol().equals(symbol4) ) {
				puStub4 = new QFPositionChangeUpdate(account1, symbol4)
					.setChangeProfitAndLoss(FMoney.ofRUB2(15.0))
					.setChangeUsedMargin(FMoney.ofRUB2(-40.0))
					.setChangeVarMargin(FMoney.ofRUB5(25.0));
				expect(utilsMock.refreshByCurrentState(dummy)).andReturn(puStub4);
			}
		}
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.updateByMarket(p);
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setInitialEquity(FMoney.ofRUB2(5000.0))
			.setInitialFreeMargin(FMoney.ofRUB2(4500.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(750.0))
			.setInitialUsedMargin(FMoney.ofRUB2(250.0))
			.setInitialVarMargin(FMoney.ofRUB5(10.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(500.0))
			.setInitialVarMarginInter(FMoney.ofRUB5(190.0))
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeUsedMargin(FMoney.ofRUB2(-20.0))
			.setChangeVarMargin(FMoney.ofRUB5(20.0))
			.setChangeProfitAndLoss(FMoney.ofRUB2(5.0))
			.setChangeVarMarginClose(FMoney.ZERO_RUB5)
			.setChangeVarMarginInter(FMoney.ZERO_RUB5)
			.setFinalEquity(FMoney.ofRUB2(10755.0))
			.setFinalFreeMargin(FMoney.ofRUB2(10525.0))
			.setPositionUpdate(puStub3)
			.setPositionUpdate(puStub4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateMargin() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(5000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(4500.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(750.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(250.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(10.9))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(500.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(190.0))
			.buildUpdate());
		p.getEditablePosition(symbol1).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 12L)
			.buildUpdate());
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 95L)
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		QFPositionChangeUpdate puStub = new QFPositionChangeUpdate(account1, symbol3)
			.setChangeProfitAndLoss(FMoney.ofRUB2(15.0))
			.setChangeUsedMargin(FMoney.ofRUB2(-40.0))
			.setChangeVarMargin(FMoney.ofRUB5(25.0));
		expect(utilsMock.refreshByCurrentState(p.getPosition(symbol3))).andReturn(puStub);
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.updateMargin(p.getPosition(symbol3));
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setInitialEquity(FMoney.ofRUB2(5000.0))
			.setInitialFreeMargin(FMoney.ofRUB2(4500.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(750.0))
			.setInitialUsedMargin(FMoney.ofRUB2(250.0))
			.setInitialVarMargin(FMoney.ofRUB5(10.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(500.0))
			.setInitialVarMarginInter(FMoney.ofRUB5(190.0))
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeUsedMargin(FMoney.ofRUB2(-40.0))
			.setChangeVarMargin(FMoney.ofRUB5(25.0))
			.setChangeProfitAndLoss(FMoney.ofRUB2(15.0))
			.setChangeVarMarginClose(FMoney.ZERO_RUB5)
			.setChangeVarMarginInter(FMoney.ZERO_RUB5)
			.setFinalEquity(FMoney.ofRUB2(10765.0))
			.setFinalFreeMargin(FMoney.ofRUB2(10555.0))
			.setPositionUpdate(puStub);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateMargin_WhenZeroPosition() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(5000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(4500.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(750.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(250.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(10.9))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(500.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(190.0))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 0L)
			.buildUpdate());
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.updateMargin(p.getPosition(symbol3));

		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setInitialEquity(FMoney.ofRUB2(5000.0))
			.setInitialFreeMargin(FMoney.ofRUB2(4500.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(750.0))
			.setInitialUsedMargin(FMoney.ofRUB2(250.0))
			.setInitialVarMargin(FMoney.ofRUB5(10.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(500.0))
			.setInitialVarMarginInter(FMoney.ofRUB5(190.0))
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeUsedMargin(FMoney.ZERO_RUB2)
			.setChangeVarMargin(FMoney.ZERO_RUB5)
			.setChangeProfitAndLoss(FMoney.ZERO_RUB2)
			.setChangeVarMarginClose(FMoney.ZERO_RUB5)
			.setChangeVarMarginInter(FMoney.ZERO_RUB5)
			.setFinalEquity(FMoney.ofRUB2(10750))
			.setFinalFreeMargin(FMoney.ofRUB2(10500.0));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangeBalance() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(5000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(4500.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(750.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(250.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(10.9))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(500.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(190.0))
			.buildUpdate());
		control.replay();

		QFPortfolioChangeUpdate actual = service.changeBalance(p, FMoney.ofRUB2(-4500.0));
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setInitialEquity(FMoney.ofRUB2(5000.0))
			.setInitialFreeMargin(FMoney.ofRUB2(4500.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(750.0))
			.setInitialUsedMargin(FMoney.ofRUB2(250.0))
			.setInitialVarMargin(FMoney.ofRUB5(10.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(500.0))
			.setInitialVarMarginInter(FMoney.ofRUB5(190.0))
			.setChangeBalance(FMoney.ofRUB2(-4500.0))
			.setChangeProfitAndLoss(FMoney.ZERO_RUB2)
			.setChangeUsedMargin(FMoney.ZERO_RUB2)
			.setChangeVarMargin(FMoney.ZERO_RUB5)
			.setChangeVarMarginClose(FMoney.ZERO_RUB5)
			.setChangeVarMarginInter(FMoney.ZERO_RUB5)
			.setFinalEquity(FMoney.ofRUB2(6250.0))
			.setFinalFreeMargin(FMoney.ofRUB2(6000.0));
		assertEquals(expected, actual);
	}

	@Test
	public void testExecuteOrder_PartialExecution() throws Exception {
		schedulerStub.setFixedTime("2017-04-16T21:20:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.SELL, 100L, FDecimal.of2(49.15));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.withToken(OrderField.CURRENT_VOLUME, 80L)
			.withToken(OrderField.EXECUTED_VALUE, FMoney.ofRUB2(127.15))
			.buildUpdate());
		expect(utilsMock.priceToMoney(security1, 20L, FDecimal.of2(49.20)))
			.andReturn(FMoney.ofRUB2(128.19));
		control.replay();
		
		QFOrderExecutionUpdate actual = service.executeOrder(order, 20L, FDecimal.of2(49.20));
		
		control.verify();
		QFOrderExecutionUpdate expected = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(80L)
			.setInitialExecutedValue(FMoney.ofRUB2(127.15))
			.setInitialStatus(OrderStatus.ACTIVE)
			.setChangeCurrentVolume(-20L)
			.setChangeExecutedValue(FMoney.ofRUB2(128.19))
			.setFinalStatus(OrderStatus.ACTIVE)
			.setExecutionAction(OrderAction.SELL)
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(FDecimal.of2(49.20))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-16T21:20:00Z"))
			.setExecutionValue(FMoney.ofRUB2(128.19))
			.setExecutionVolume(20L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testExecuteOrder_CompleteExecution() throws Exception {
		schedulerStub.setFixedTime("2017-04-16T21:28:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.BUY, 20L, FDecimal.of2(46.25));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.withToken(OrderField.CURRENT_VOLUME, 15L)
			.withToken(OrderField.EXECUTED_VALUE, FMoney.ofRUB2(34.24))
			.buildUpdate());
		expect(utilsMock.priceToMoney(security1, 15L, FDecimal.of2(46.20)))
			.andReturn(FMoney.ofRUB2(96.54));
		control.replay();
		
		QFOrderExecutionUpdate actual = service.executeOrder(order, 15L, FDecimal.of2(46.20));
		
		control.verify();
		QFOrderExecutionUpdate expected = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(15L)
			.setInitialExecutedValue(FMoney.ofRUB2(34.24))
			.setInitialStatus(OrderStatus.ACTIVE)
			.setChangeCurrentVolume(-15L)
			.setChangeExecutedValue(FMoney.ofRUB2(96.54))
			.setFinalStatus(OrderStatus.FILLED)
			.setFinalizationTime(T("2017-04-16T21:28:00Z"))
			.setExecutionAction(OrderAction.BUY)
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(FDecimal.of2(46.20))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-16T21:28:00Z"))
			.setExecutionValue(FMoney.ofRUB2(96.54))
			.setExecutionVolume(15L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateOrderStatus() {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1, OrderAction.BUY, 20L, FDecimal.of2(46.25));
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
				symbol1, OrderAction.BUY, 20L, FDecimal.of2(46.25));
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
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(5000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(4500.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(750.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(250.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(10.9))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(500.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(190.0))
			.buildUpdate());
		// This position should be skipped because of null volume
		p.getEditablePosition(symbol1);
		// This position should be skipped because of zero volume
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 0L)
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		QFPositionChangeUpdate puStub3 = null, puStub4 = null;
		for ( Position dummy : p.getPositions() ) {
			if ( dummy.getSymbol().equals(symbol3) ) {
				puStub3 = new QFPositionChangeUpdate(account1, symbol3)
					.setChangeProfitAndLoss(FMoney.ofRUB2(86.19))
					.setChangeUsedMargin(FMoney.ofRUB2(14.05))
					.setChangeVarMarginInter(FMoney.ofRUB5(14.05012));
				expect(utilsMock.midClearing(dummy)).andReturn(puStub3);
			} else
			if ( dummy.getSymbol().equals(symbol4) ) {
				puStub4 = new QFPositionChangeUpdate(account1, symbol4)
					.setChangeProfitAndLoss(FMoney.ofRUB2(-15.0))
					.setChangeUsedMargin(FMoney.ofRUB2(-40.0))
					.setChangeVarMarginInter(FMoney.ofRUB5(-15.02159));
				expect(utilsMock.midClearing(dummy)).andReturn(puStub4);
			}
		}
		control.replay();

		QFPortfolioChangeUpdate actual = service.midClearing(p);
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setInitialEquity(FMoney.ofRUB2(5000.0))
			.setInitialFreeMargin(FMoney.ofRUB2(4500.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(750.0))
			.setInitialUsedMargin(FMoney.ofRUB2(250.0))
			.setInitialVarMargin(FMoney.ofRUB5(10.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(500.0))
			.setInitialVarMarginInter(FMoney.ofRUB5(190.0))
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeProfitAndLoss(FMoney.ofRUB2(71.19))
			.setChangeUsedMargin(FMoney.ofRUB2(-25.95))
			.setFinalEquity(FMoney.ofRUB2(10821.19))
			.setFinalFreeMargin(FMoney.ofRUB2(10597.14))
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5)
			.setFinalVarMarginInter(FMoney.ofRUB5(-0.97147))
			.setPositionUpdate(puStub3)
			.setPositionUpdate(puStub4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClearing() throws Exception {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(10000.0))
			.withToken(PortfolioField.EQUITY, FMoney.ofRUB2(5000.0))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofRUB2(4500.0))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofRUB2(750.0))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofRUB2(250.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN, FMoney.ofRUB5(10.9))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(500.0))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(190.0))
			.buildUpdate());
		// This position should be skipped because of null volume
		p.getEditablePosition(symbol1);
		// This position should be skipped because of zero volume
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 0L)
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		QFPositionChangeUpdate puStub3 = null, puStub4 = null;
		for ( Position dummy : p.getPositions() ) {
			if ( dummy.getSymbol().equals(symbol3) ) {
				puStub3 = new QFPositionChangeUpdate(account1, symbol3)
					.setChangeBalance(FMoney.ofRUB2(-115.28))
					.setChangeUsedMargin(FMoney.ofRUB2(26.19));
				expect(utilsMock.clearing(dummy)).andReturn(puStub3);
			} else
			if ( dummy.getSymbol().equals(symbol4) ) {
				puStub4 = new QFPositionChangeUpdate(account1, symbol4)
					.setChangeBalance(FMoney.ofRUB2(-26.0))
					.setChangeUsedMargin(FMoney.ofRUB2(42.0));
				expect(utilsMock.clearing(dummy)).andReturn(puStub4);
			}
		}
		control.replay();

		QFPortfolioChangeUpdate actual = service.clearing(p);
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setInitialEquity(FMoney.ofRUB2(5000.0))
			.setInitialFreeMargin(FMoney.ofRUB2(4500.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(750.0))
			.setInitialUsedMargin(FMoney.ofRUB2(250.0))
			.setInitialVarMargin(FMoney.ofRUB5(10.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(500.0))
			.setInitialVarMarginInter(FMoney.ofRUB5(190.0))
			.setChangeBalance(FMoney.ofRUB2(-141.28)) // =9858.72
			.setChangeUsedMargin(FMoney.ofRUB2(68.19)) // =318.19
			.setFinalProfitAndLoss(FMoney.ZERO_RUB2)
			.setFinalEquity(FMoney.ofRUB2(9858.72))
			.setFinalFreeMargin(FMoney.ofRUB2(9540.53))
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5)
			.setFinalVarMarginInter(FMoney.ZERO_RUB5)
			.setPositionUpdate(puStub3)
			.setPositionUpdate(puStub4);
		assertEquals(expected, actual);
		assertEquals(FMoney.ofRUB2(9858.72), actual.getFinalBalance());
		assertEquals(FMoney.ofRUB2(318.19), actual.getFinalUsedMargin());
	}

}
