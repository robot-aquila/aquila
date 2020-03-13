package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.aquila.core.BusinessEntities.OrderAction.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
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
	private static final CDecimal ZERO_MONEY5 = CDecimalBD.ZERO_RUB5;
	private static final CDecimal ZERO = CDecimalBD.ZERO;
	private static final CDecimal ZERO_PRICE = CDecimalBD.of("0.00");
	private static Account account1;
	private static Symbol symbol1, symbol2, symbol3, symbol4;
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
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
		symbol1 = new Symbol("BEST1");
		symbol2 = new Symbol("QUEST2");
		symbol3 = new Symbol("GUEST3");
		symbol4 = new Symbol("LEAST4");
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
		expect(utilsMock.changePosition(pos, CDecimalBD.of(150L), CDecimalBD.of("52.25"))).andReturn(pcuStub);
		control.replay();
		
		QFPortfolioChangeUpdate actual = service.changePosition(p, security1, of(150L), of("52.25"));
		
		control.verify();
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(ofRUB5("10000"))
			.setInitialEquity(ofRUB5("5000"))
			.setInitialFreeMargin(ofRUB5("4500"))
			.setInitialProfitAndLoss(ofRUB5("750"))
			.setInitialUsedMargin(ofRUB5("250"))
			.setInitialVarMargin(ofRUB5("10.9"))
			.setInitialVarMarginClose(ofRUB5("500"))
			.setInitialVarMarginInter(ofRUB5("190"))
			.setChangeBalance(ZERO_MONEY5)
			.setChangeProfitAndLoss(ofRUB5("-15"))
			.setChangeUsedMargin(ofRUB5("72.15"))
			.setChangeVarMargin(ofRUB5("80.00015"))
			.setChangeVarMarginClose(ofRUB5("1"))
			.setChangeVarMarginInter(ofRUB5("99"))
			.setFinalEquity(ofRUB5("10735"))
			.setFinalFreeMargin(ofRUB5("10412.85"))
			.setPositionUpdate(pcuStub);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_ThrowsIfTickValueChanged() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		Position pos = p.getEditablePosition(symbol1);
		expect(utilsMock.changePosition(pos, of(-10L), of("154.26")))
			.andReturn(new QFPositionChangeUpdate(account1, symbol1)
				.setInitialTickValue(ofRUB5("26.99024"))
				.setFinalTickValue(ofRUB2("12.36")));
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Tick value changed during position update: symbol=BEST1 expected=26.99024 RUB actual=12.36 RUB");
		
		service.changePosition(p, security1, of(-10L), of("154.26"));
	}
	
	@Test
	public void testUpdateByMarket_ThrowsIfTickValueNotSpecifiedForPosition() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE,					ofRUB5("10000"))
			.withToken(PortfolioField.EQUITY,					ofRUB5("5000"))
			.withToken(PortfolioField.FREE_MARGIN,				ofRUB5("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS,			ofRUB5("750"))
			.withToken(PortfolioField.USED_MARGIN,				ofRUB5("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN,			ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE,	ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER,	ofRUB5("190"))
			.buildUpdate());
		p.getEditablePosition(symbol1).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME, of(1L))	
			.buildUpdate());
		service = new QFCalculator();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("No more candidates to determine current price");
		
		service.updateByMarket(p);
	}

	@Test
	public void testUpdateByMarket_RealData() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE,					ofRUB5("10000.0"))
			.withToken(PortfolioField.EQUITY,					ofRUB5( "5000.0"))
			.withToken(PortfolioField.FREE_MARGIN,				ofRUB5( "4500.0"))
			.withToken(PortfolioField.PROFIT_AND_LOSS,			ofRUB5(  "750.0"))
			.withToken(PortfolioField.USED_MARGIN,				ofRUB5(  "250.0"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN,			ofRUB5(   "10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE,	ofRUB5(  "500.0"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER,	ofRUB5(  "190.0"))
			.buildUpdate());
		p.getEditablePosition(symbol1); // position should be skipped because of null volume
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder()
			// should be skipped because of zero volume
			.withToken(PositionField.CURRENT_VOLUME,		of(0L))
			.buildUpdate());
		terminal.getEditableSecurity(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("0.02"))
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB5("10.0"))
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("27.00"))		// or last price
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(10L))
			.withToken(PositionField.OPEN_PRICE,			of("269.20"))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.025"))
			// without vm.close & vm.inter should be OK
			.buildUpdate());
		terminal.getEditableSecurity(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("0.05"))
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB5("25.0"))
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("16.10"))		// the last price is highest priority
			.buildUpdate());
		terminal.getEditableSecurity(symbol4).consume(new L1UpdateBuilder(symbol4) // or settlement price
			.withTrade()
			.withTime("2020-03-12T14:49:00Z")
			.withPrice("16.25")
			.withSize(1L)
			.buildL1Update());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(PositionField.OPEN_PRICE,			of("-84.20"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("120.0"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("-10.0"))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB2("0.5"))
			.buildUpdate());
		service = new QFCalculator();
		
		QFPortfolioChangeUpdate actual = service.updateByMarket(p);
		
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(ofRUB5("10000.0"))
			.setInitialEquity(ofRUB5("5000.0"))
			.setInitialFreeMargin(ofRUB5("4500.0"))
			.setInitialProfitAndLoss(ofRUB5("750.0"))
			.setInitialUsedMargin(ofRUB5("250.0"))
			.setInitialVarMargin(ofRUB5("10.9"))
			.setInitialVarMarginClose(ofRUB5("500.0"))
			.setInitialVarMarginInter(ofRUB5("190.0"))
			.setChangeBalance(ZERO_MONEY5)
			.setFinalUsedMargin(ofRUB5("225"))	// sum up used margin of all positions: 125.0 + 100.0 = 225.0
												// change == 225.0 - 250.0 = -25.0
			.setFinalVarMargin(ofRUB5("30.5"))	// sum up margin change of all positions: 30.5
												// ^^ this case it is same with total var margin
												// because it was not calculated initially
			.setFinalVarMarginClose(ofRUB5("120.0"))
			.setFinalVarMarginInter(ofRUB5("-10.0"))

			.setFinalProfitAndLoss(ofRUB5("140.5"))
			
			.setFinalEquity(ofRUB5("10140.5")) // equity = balance + pl
			
			.setFinalFreeMargin(ofRUB5("9915.5")) // free margin = equity - used margin
					
			.setPositionUpdate(new QFPositionChangeUpdate(account1, symbol3)
					.setChangeBalance(ZERO_MONEY5)
					.setInitialCurrentPrice(of("0.00")).setFinalCurrentPrice(of("270.00")) // 27.00 * 10
					.setInitialOpenPrice(of("269.20")).setFinalOpenPrice(of("269.20"))
					.setInitialProfitAndLoss(ZERO_MONEY5).setFinalProfitAndLoss(ofRUB5("1.0"))
					.setInitialUsedMargin(ZERO_MONEY5).setFinalUsedMargin(ofRUB5("100.0")) // 10.0 * 10
					.setInitialVarMargin(ZERO_MONEY5).setFinalVarMargin(ofRUB5("1.0")) // (270.0-269.2)/0.02*0.025=1
					.setInitialVarMarginClose(ZERO_MONEY5).setFinalVarMarginClose(ZERO_MONEY5)
					.setInitialVarMarginInter(ZERO_MONEY5).setFinalVarMarginInter(ZERO_MONEY5)
					.setInitialVolume(of(10L)).setFinalVolume(of(10L))
					.setInitialTickValue(ofRUB5("0.025")).setFinalTickValue(ofRUB5("0.025"))
				)
			.setPositionUpdate(new QFPositionChangeUpdate(account1, symbol4)
					.setChangeBalance(ZERO_MONEY5)
					.setInitialCurrentPrice(of("0.00")).setFinalCurrentPrice(of("-81.25")) // 16.25 * -5
					.setInitialOpenPrice(of("-84.20")).setFinalOpenPrice(of("-84.20"))
					.setInitialProfitAndLoss(ZERO_MONEY5).setFinalProfitAndLoss(ofRUB5("139.5"))
					.setInitialUsedMargin(ZERO_MONEY5).setFinalUsedMargin(ofRUB5("125.0")) // 25.0 * 5
					.setInitialVarMargin(ZERO_MONEY5).setFinalVarMargin(ofRUB5("29.5")) // (-81.25--84.20)/0.05*0.5=29.5
					.setInitialVarMarginClose(ofRUB5("120")).setFinalVarMarginClose(ofRUB5("120"))
					.setInitialVarMarginInter(ofRUB5("-10")).setFinalVarMarginInter(ofRUB5("-10"))
					.setInitialVolume(of(-5L)).setFinalVolume(of(-5L))
					.setInitialTickValue(ofRUB2("0.50")).setFinalTickValue(ofRUB2("0.50"))
				);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateMargin_RealData() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE,				ofRUB5("10000.01156"))
			.withToken(PortfolioField.EQUITY,				ofRUB5( "5000.00240"))
			.withToken(PortfolioField.FREE_MARGIN,			ofRUB5( "4500.00001"))
			.withToken(PortfolioField.PROFIT_AND_LOSS,		ofRUB5(  "750.99901"))
			.withToken(PortfolioField.USED_MARGIN,			ofRUB5(  "250.10001"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN,		ofRUB5(   "10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE,ofRUB5(  "500.0"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER,ofRUB5(  "190.0"))
			.buildUpdate());
		p.getEditablePosition(symbol1).consume(new DeltaUpdateBuilder() // should be ignored
			.withToken(PositionField.CURRENT_VOLUME,		of(12L))
			.buildUpdate());
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder() // should be ignored
			.withToken(PositionField.CURRENT_VOLUME,		of(95L))
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder() // should be ignored
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.buildUpdate());
		terminal.getEditableSecurity(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("0.015"))
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB5("5402.97"))
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("426.0"))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(10L))
			.withToken(PositionField.OPEN_PRICE,			of("4500.015"))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.35"))
			.buildUpdate());
		service = new QFCalculator();
		
		QFPortfolioChangeUpdate actual = service.updateMargin(p.getPosition(symbol3));
		
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(ofRUB5("10000.01156"))
			.setInitialEquity(ofRUB5("5000.00240"))
			.setInitialFreeMargin(ofRUB5("4500.00001"))
			.setInitialProfitAndLoss(ofRUB5("750.99901"))
			.setInitialUsedMargin(ofRUB5("250.10001"))
			.setInitialVarMargin(ofRUB5("10.9"))
			.setInitialVarMarginClose(ofRUB5("500"))
			.setInitialVarMarginInter(ofRUB5("190"))
			.setChangeBalance(ZERO_MONEY5)
			.setChangeProfitAndLoss(ofRUB5("-5600.35"))
			.setChangeUsedMargin(ofRUB5("54029.7"))
			.setChangeVarMargin(ofRUB5("-5600.35"))
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5)
			.setFinalEquity(ofRUB5("5150.66057")) // 10000.01156 + -5600.35 + 750.99901 = 5150.66057
			.setFinalFreeMargin(ofRUB5("-49129.13944")) // 5150.66057 - 54029.7 - 250.10001 = -49129.13944
			.setPositionUpdate(new QFPositionChangeUpdate(account1, symbol3)
					.setChangeBalance(ZERO_MONEY5)
					.setInitialCurrentPrice(of("0.000")).setFinalCurrentPrice(of("4260.000"))
					.setInitialOpenPrice(of("4500.015")).setFinalOpenPrice(of("4500.015"))
					.setInitialProfitAndLoss(ZERO_MONEY5).setFinalProfitAndLoss(ofRUB5("-5600.35"))
					.setInitialUsedMargin(ZERO_MONEY5).setFinalUsedMargin(ofRUB5("54029.7")) // 10 * 5402.97
					.setInitialVarMargin(ZERO_MONEY5)
						// (10 * 426.0) - 4500.015 = -240.015 / 0.015 * 0.35 = -5600.35
						.setFinalVarMargin(ofRUB5("-5600.35"))
					.setInitialVarMarginClose(ZERO_MONEY5).setFinalVarMarginClose(ZERO_MONEY5)
					.setInitialVarMarginInter(ZERO_MONEY5).setFinalVarMarginInter(ZERO_MONEY5)
					.setInitialVolume(of(10L)).setFinalVolume(of(10L))
					.setInitialTickValue(ofRUB5("0.35")).setFinalTickValue(ofRUB5("0.35"))
				)
			;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateMargin_ZeroPosition() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE,				ofRUB5("10000.0"))
			.withToken(PortfolioField.EQUITY,				ofRUB5( "5000.0"))
			.withToken(PortfolioField.FREE_MARGIN,			ofRUB5( "4500.0"))
			.withToken(PortfolioField.PROFIT_AND_LOSS,		ofRUB5(  "750.0"))
			.withToken(PortfolioField.USED_MARGIN,			ofRUB5(  "250.0"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN,		ofRUB5(   "10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE,ofRUB5(  "500.0"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER,ofRUB5(  "190.0"))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(0L))
			.buildUpdate());
		service = new QFCalculator();
		
		QFPortfolioChangeUpdate actual = service.updateMargin(p.getPosition(symbol3));

		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(ofRUB5("10000.0"))
			.setInitialEquity(ofRUB5("5000.0"))
			.setInitialFreeMargin(ofRUB5("4500.0"))
			.setInitialProfitAndLoss(ofRUB5("750.0"))
			.setInitialUsedMargin(ofRUB5("250.0"))
			.setInitialVarMargin(ofRUB5("10.9"))
			.setInitialVarMarginClose(ofRUB5("500.0"))
			.setInitialVarMarginInter(ofRUB5("190.0"))
			.setChangeBalance(ZERO_MONEY5)
			.setChangeUsedMargin(ZERO_MONEY5)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeProfitAndLoss(ZERO_MONEY5)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5)
			.setFinalEquity(ofRUB5("10750.0"))
			.setFinalFreeMargin(ofRUB5("10500.0"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangeBalance() {
		EditablePortfolio p = terminal.getEditablePortfolio(account1);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE,				ofRUB5("10000.86"))
			.withToken(PortfolioField.EQUITY,				ofRUB5( "5000.24"))
			.withToken(PortfolioField.FREE_MARGIN,			ofRUB5( "4500.16"))
			.withToken(PortfolioField.PROFIT_AND_LOSS,		ofRUB5(  "750.45"))
			.withToken(PortfolioField.USED_MARGIN,			ofRUB5(  "250.11"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN,		ofRUB5(   "10.90"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE,ofRUB5(  "500.01"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER,ofRUB5(  "190.22"))
			.buildUpdate());
		service = new QFCalculator();

		QFPortfolioChangeUpdate actual = service.changeBalance(p, ofRUB2("-4500.00"));
		
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
			.setInitialBalance(ofRUB5("10000.86"))
			.setInitialEquity(ofRUB5("5000.24"))
			.setInitialFreeMargin(ofRUB5("4500.16"))
			.setInitialProfitAndLoss(ofRUB5("750.45"))
			.setInitialUsedMargin(ofRUB5("250.11"))
			.setInitialVarMargin(ofRUB5("10.90"))
			.setInitialVarMarginClose(ofRUB5("500.01"))
			.setInitialVarMarginInter(ofRUB5("190.22"))
			.setChangeBalance(ofRUB2("-4500.00")) // final balance = 5500.86
			.setChangeProfitAndLoss(ZERO_MONEY5)
			.setChangeUsedMargin(ZERO_MONEY5)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5)
			.setFinalEquity(ofRUB5("6251.31")) 
			.setFinalFreeMargin(ofRUB5("6001.2"));
		assertEquals(expected, actual);
	}

	@Test
	public void testExecuteOrder_PartialExecution_RealData() throws Exception {
		schedulerStub.setFixedTime("2017-04-16T21:20:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1, symbol1, SELL, of(100L), of("49.15"));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.withToken(OrderField.CURRENT_VOLUME, of(80L))
			.withToken(OrderField.EXECUTED_VALUE, ofRUB5("127.15"))
			.buildUpdate());
		terminal.getEditableSecurity(symbol1).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of("0.05"))
			.withToken(SecurityField.TICK_VALUE, ofRUB5("12.26501"))
			.buildUpdate());
		service = new QFCalculator();
		
		QFOrderExecutionUpdate actual = service.executeOrder(order, of(20L), of("49.20"));
		
		QFOrderExecutionUpdate expected = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(of(80L))
			.setInitialExecutedValue(ofRUB5("127.15"))
			.setInitialStatus(OrderStatus.ACTIVE)
			.setChangeCurrentVolume(of(-20L))
			.setChangeExecutedValue(ofRUB5("241375.3968"))
			.setFinalStatus(OrderStatus.ACTIVE)
			.setExecutionAction(SELL)
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(of("49.20"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-16T21:20:00Z"))
			.setExecutionValue(ofRUB5("241375.3968"))
			.setExecutionVolume(of(20L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testExecuteOrder_CompleteExecution() throws Exception {
		schedulerStub.setFixedTime("2017-04-16T21:28:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1, symbol1, BUY, of(20L), of("46.25"));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.withToken(OrderField.CURRENT_VOLUME, of(15L))
			.withToken(OrderField.EXECUTED_VALUE, ofRUB2("34.24"))
			.buildUpdate());
		terminal.getEditableSecurity(symbol1).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,	of("0.05"))
			.withToken(SecurityField.TICK_VALUE, ofRUB5("12.26501"))
			.buildUpdate());
		service = new QFCalculator();
		
		QFOrderExecutionUpdate actual = service.executeOrder(order, of(15L), of("46.20"));
		
		QFOrderExecutionUpdate expected = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(of(15L))
			.setInitialExecutedValue(ofRUB5("34.24"))
			.setInitialStatus(OrderStatus.ACTIVE)
			.setChangeCurrentVolume(of(-15L))
			.setChangeExecutedValue(ofRUB5("169993.0386"))
			.setFinalStatus(OrderStatus.FILLED)
			.setFinalizationTime(T("2017-04-16T21:28:00Z"))
			.setExecutionAction(BUY)
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(of("46.20"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-16T21:28:00Z"))
			.setExecutionValue(ofRUB5("169993.0386"))
			.setExecutionVolume(of(15L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testExecuteOrder_Bugfix_ScaleAndRound() throws Exception {
		// Like for RTS-X.XX
		service = new QFCalculator();
		schedulerStub.setFixedTime("2017-04-16T21:28:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1, symbol1, BUY, of(20L), of("120750"));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.withToken(OrderField.CURRENT_VOLUME, of(5L))
			// 15@120700=120700/10*13.14734*15=2380325.90700
			.withToken(OrderField.EXECUTED_VALUE, ofRUB5("2380325.90700"))
			.buildUpdate());
		security1.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, of(10L))
			.withToken(SecurityField.TICK_VALUE, ofRUB5("13.14734"))
			.buildUpdate());
		
		QFOrderExecutionUpdate actual = service.executeOrder(order, of(5L),
			of("120650")); // 5@120650=120650/10*13.14734*5=793113.28550
		
		QFOrderExecutionUpdate expected = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(of(5L))
			.setInitialExecutedValue(ofRUB5("2380325.90700"))
			.setInitialStatus(OrderStatus.ACTIVE)
			.setChangeCurrentVolume(of(-5L))
			.setChangeExecutedValue(ofRUB5("793113.28550"))
			.setFinalStatus(OrderStatus.FILLED)
			.setFinalizationTime(T("2017-04-16T21:28:00Z"))
			.setExecutionAction(BUY)
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(of("120650"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2017-04-16T21:28:00Z"))
			.setExecutionValue(ofRUB5("793113.28550"))
			.setExecutionVolume(of(5L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateOrderStatus() {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1, symbol1, BUY, of(20L), of("46.25"));
		service = new QFCalculator();
		
		QFOrderStatusUpdate actual = service.updateOrderStatus(order, OrderStatus.ACTIVE, null);
		
		QFOrderStatusUpdate expected = new QFOrderStatusUpdate()
			.setInitialStatus(OrderStatus.PENDING)
			.setFinalStatus(OrderStatus.ACTIVE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateOrderStatus_Finalization() {
		schedulerStub.setFixedTime("2017-04-16T22:14:00Z");
		EditableOrder order = (EditableOrder) terminal.createOrder(account1, symbol1, BUY, of(20L), of("46.25"));
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, OrderStatus.ACTIVE)
			.buildUpdate());
		service = new QFCalculator();
		
		QFOrderStatusUpdate actual = service.updateOrderStatus(order, OrderStatus.REJECTED, "Insufficient funds");
		
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
			.withToken(PortfolioField.BALANCE,				ofRUB5("10000"))
			.withToken(PortfolioField.EQUITY,				ofRUB5( "5000"))
			.withToken(PortfolioField.FREE_MARGIN,			ofRUB5( "4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS,		ofRUB5(  "750"))
			.withToken(PortfolioField.USED_MARGIN,			ofRUB5(  "250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN,		ofRUB5(   "10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE,ofRUB5(  "500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER,ofRUB5(  "190"))
			.buildUpdate());
		// position#1
		terminal.getEditableSecurity(symbol1).consume(new DeltaUpdateBuilder() // available security is required
			.withToken(SecurityField.TICK_SIZE,				of("0.01"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB5("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("40.00"))
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("10.00"))
			.buildUpdate());
		p.getEditablePosition(symbol1); // uninitialized position should be OK
		// position#2
		terminal.getEditableSecurity(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("0.01"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB2("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("12.00"))
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("4.00"))
			.buildUpdate());
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder() // should process closed position 
			.withToken(PositionField.CURRENT_VOLUME,		ZERO)
			.withToken(PositionField.OPEN_PRICE,			ZERO_PRICE)
			.withToken(PositionField.CURRENT_PRICE,			ZERO_PRICE)
			.withToken(PositionField.USED_MARGIN,			ZERO_MONEY5)
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("60.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ZERO_MONEY5)
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("50.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("10.00000"))
			.buildUpdate());
		// position#3
		terminal.getEditableSecurity(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("0.005"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB2("0.02")) // non-zero pos -> QF_TICK_VALUE reqd
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("25.785"))		// @25.785
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("50.00"))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(10L))
			.withToken(PositionField.OPEN_PRICE,			of("250.000")) 		// @25.000
			.withToken(PositionField.CURRENT_PRICE,			of("252.500"))		// @25.250
			.withToken(PositionField.USED_MARGIN,			ofRUB5("490.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("9.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("5.00000"))	// (252.500-250.000)/0.005*0.01=5.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("3.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB2("0.01"))
			.buildUpdate());
		// position#4
		terminal.getEditableSecurity(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("10"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB2("1.80")) // non-zero pos -> QF_TICK_VALUE reqd
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("160"))			// @160
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("5.00"))
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(PositionField.OPEN_PRICE,			of("-750"))			// @150
			.withToken(PositionField.CURRENT_PRICE,			of("-850"))			// @170
			.withToken(PositionField.USED_MARGIN,			ofRUB5("25.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("-14.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-20.00000")) // (-850--750)/10*2.00=-20.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("5.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB2("2.00"))
			.buildUpdate());

		QFPortfolioChangeUpdate actual = service.midClearing(p);
		
		// position update#1
		QFPositionChangeUpdate expectedPU1 = new QFPositionChangeUpdate(account1, symbol1)
				.setChangeBalance(ZERO_MONEY5)
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY5)
				.setInitialProfitAndLoss(ZERO_MONEY5)
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ZERO_MONEY5)
				.setInitialVarMarginInter(ZERO_MONEY5)
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY5)
				.setFinalProfitAndLoss(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU1, actual.getPositionUpdate(symbol1));
		// position update#2
		QFPositionChangeUpdate expectedPU2 = new QFPositionChangeUpdate(account1, symbol2)
				.setChangeBalance(ZERO_MONEY5)
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY5)
				.setInitialProfitAndLoss(ofRUB2("60.00"))
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ofRUB5("50.00000"))
				.setInitialVarMarginInter(ofRUB5("10.00000"))
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY5)
				.setFinalProfitAndLoss(ofRUB2("60.00"))
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ofRUB5("60.00000"));
		assertEquals(expectedPU2, actual.getPositionUpdate(symbol2));
		// position update#3
		QFPositionChangeUpdate expectedPU3 = new QFPositionChangeUpdate(account1, symbol3)
				.setChangeBalance(ZERO_MONEY5)
				.setChangeVolume(ZERO)
				.setInitialVolume(of(10L))
				.setInitialOpenPrice(of("250.000"))
				.setInitialCurrentPrice(of("252.500"))
				.setInitialUsedMargin(ofRUB2("490.00"))
				.setInitialProfitAndLoss(ofRUB2("9.00"))
				.setInitialVarMargin(ofRUB5("5.00000"))
				.setInitialVarMarginClose(ofRUB5("3.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setInitialTickValue(ofRUB2("0.01"))
				.setFinalOpenPrice(of("257.850"))				// @25.785
				.setFinalCurrentPrice(of("257.850"))			// @25.785
				.setFinalUsedMargin(ofRUB2("500.00"))
				.setFinalProfitAndLoss(ofRUB2("19.70"))
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ofRUB5("19.70000"))	// prev.VM=(257.850-250.000)/0.005*0.01=15.70000
																// prev.VMC=3.00000
																// prev.VMI=1.00000
																// total=15.70000+3.00000+1.00000=19.70000
				.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expectedPU3, actual.getPositionUpdate(symbol3));
		// position update#4
		QFPositionChangeUpdate expectedPU4 = new QFPositionChangeUpdate(account1, symbol4)
				.setChangeBalance(ZERO_MONEY5)
				.setChangeVolume(ZERO)
				.setInitialVolume(of(-5L))
				.setInitialOpenPrice(of("-750"))
				.setInitialCurrentPrice(of("-850"))
				.setInitialUsedMargin(ofRUB2("25.00"))
				.setInitialProfitAndLoss(ofRUB2("-14.00"))
				.setInitialVarMargin(ofRUB5("-20.00000"))
				.setInitialVarMarginClose(ofRUB5("5.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setInitialTickValue(ofRUB2("2.00"))
				.setFinalOpenPrice(of("-800"))					// @160
				.setFinalCurrentPrice(of("-800"))				// @160
				.setFinalUsedMargin(ofRUB2("25.00"))
				.setFinalProfitAndLoss(ofRUB2("-4.00"))
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ofRUB5("-4.00000")) // prev.VM=(-800--750)/10*2.00=-10.00000
																// prev.VMC=5.00000
																// prev.VMI=1.00000
																// total=-10.00000+5.00000+1.00000=-4.00000
				.setFinalTickValue(ofRUB2("1.80"));
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
			.setChangeBalance(ZERO_MONEY5)
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
			.withToken(PortfolioField.BALANCE,				ofRUB2("10000"))
			.withToken(PortfolioField.EQUITY,				ofRUB2("5000"))
			.withToken(PortfolioField.FREE_MARGIN,			ofRUB2("4500"))
			.withToken(PortfolioField.PROFIT_AND_LOSS,		ofRUB2("750"))
			.withToken(PortfolioField.USED_MARGIN,			ofRUB2("250"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN,		ofRUB5("10.9"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE,ofRUB5("500"))
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER,ofRUB5("190"))
			.buildUpdate());
		// position#1
		terminal.getEditableSecurity(symbol1).consume(new DeltaUpdateBuilder() // available security is required
			.withToken(SecurityField.TICK_SIZE,				of("0.01"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB5("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("40.00"))
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("10.00"))
			.buildUpdate());
		p.getEditablePosition(symbol1); // uninitialized position should be OK
		// position#2
		terminal.getEditableSecurity(symbol2).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("0.01"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB2("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("12.00"))
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("4.00"))
			.buildUpdate());
		p.getEditablePosition(symbol2).consume(new DeltaUpdateBuilder() // should process closed position 
			.withToken(PositionField.CURRENT_VOLUME,		ZERO)
			.withToken(PositionField.OPEN_PRICE,			ZERO_PRICE)
			.withToken(PositionField.CURRENT_PRICE,			ZERO_PRICE)
			.withToken(PositionField.USED_MARGIN,			ZERO_MONEY5)
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB2("60.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ZERO_MONEY5)
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("50.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("10.00000"))
			.buildUpdate());
		// position#3
		terminal.getEditableSecurity(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("0.005"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB2("0.07")) // non-zero pos -> QF_TICK_VALUE reqd
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("25.785"))	// @25.785
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("50.00"))
			.buildUpdate());
		p.getEditablePosition(symbol3).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(10L))
			.withToken(PositionField.OPEN_PRICE,			of("250.000")) 		// @25.000
			.withToken(PositionField.CURRENT_PRICE,			of("252.500"))		// @25.250
			.withToken(PositionField.USED_MARGIN,			ofRUB2("490.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB2("9.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("5.00000")) // (252.500-250.000)/0.005*0.01=5.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("3.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB2("0.01"))
			.buildUpdate());
		// position#4
		terminal.getEditableSecurity(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE,				of("10"))
			.withToken(SecurityField.TICK_VALUE,			ofRUB2("1.40")) // non-zero pos -> QF_TICK_VALUE reqd
			.withToken(SecurityField.SETTLEMENT_PRICE,		of("160"))		// @160
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("5.00"))
			.buildUpdate());
		p.getEditablePosition(symbol4).consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(PositionField.OPEN_PRICE,			of("-750"))			// @150
			.withToken(PositionField.CURRENT_PRICE,			of("-850"))			// @170
			.withToken(PositionField.USED_MARGIN,			ofRUB2("25.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB2("-14.00"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-20.00000")) // (-850--750)/10*2.00=-20.00000
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("5.00000"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("1.00000"))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB2("2.00"))
			.buildUpdate());

		QFPortfolioChangeUpdate actual = service.clearing(p);
		
		// position update#1
		QFPositionChangeUpdate expectedPU1 = new QFPositionChangeUpdate(account1, symbol1)
				.setChangeBalance(ZERO_MONEY5)
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY5)
				.setInitialProfitAndLoss(ZERO_MONEY5)
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ZERO_MONEY5)
				.setInitialVarMarginInter(ZERO_MONEY5)
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY5)
				.setFinalProfitAndLoss(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU1, actual.getPositionUpdate(symbol1));
		// position update#2
		QFPositionChangeUpdate expectedPU2 = new QFPositionChangeUpdate(account1, symbol2)
				.setChangeBalance(ofRUB5("60.00"))
				.setChangeVolume(ZERO)
				.setInitialVolume(ZERO)
				.setInitialOpenPrice(of("0.00"))
				.setInitialCurrentPrice(of("0.00"))
				.setInitialUsedMargin(ZERO_MONEY5)
				.setInitialProfitAndLoss(ofRUB5("60.00"))
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ofRUB5("50.00000"))
				.setInitialVarMarginInter(ofRUB5("10.00000"))
				.setFinalOpenPrice(of("0.00"))
				.setFinalCurrentPrice(of("0.00"))
				.setFinalUsedMargin(ZERO_MONEY5)
				.setFinalProfitAndLoss(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expectedPU2, actual.getPositionUpdate(symbol2));
		// position update#3
		QFPositionChangeUpdate expectedPU3 = new QFPositionChangeUpdate(account1, symbol3)
				.setChangeBalance(ofRUB5("19.70"))
				.setChangeVolume(ZERO)
				.setInitialVolume(of(10L))
				.setInitialOpenPrice(of("250.000"))
				.setInitialCurrentPrice(of("252.500"))
				.setInitialUsedMargin(ofRUB5("490.00"))
				.setInitialProfitAndLoss(ofRUB5("9.00"))
				.setInitialVarMargin(ofRUB5("5.00000"))
				.setInitialVarMarginClose(ofRUB5("3.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setInitialTickValue(ofRUB2("0.01"))
				.setFinalOpenPrice(of("257.850"))				// @25.785
				.setFinalCurrentPrice(of("257.850"))			// @25.785
				.setFinalUsedMargin(ofRUB5("500.00"))
				.setFinalProfitAndLoss(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5)
				.setFinalTickValue(ofRUB2("0.07"));
		assertEquals(expectedPU3, actual.getPositionUpdate(symbol3));
		// position update#4
		QFPositionChangeUpdate expectedPU4 = new QFPositionChangeUpdate(account1, symbol4)
				.setChangeBalance(ofRUB5("-4.00"))
				.setChangeVolume(ZERO)
				.setInitialVolume(of(-5L))
				.setInitialOpenPrice(of("-750"))
				.setInitialCurrentPrice(of("-850"))
				.setInitialUsedMargin(ofRUB5("25.00"))
				.setInitialProfitAndLoss(ofRUB5("-14.00"))
				.setInitialVarMargin(ofRUB5("-20.00000"))
				.setInitialVarMarginClose(ofRUB5("5.00000"))
				.setInitialVarMarginInter(ofRUB5("1.00000"))
				.setInitialTickValue(ofRUB2("2.00"))
				.setFinalOpenPrice(of("-800"))					// @160
				.setFinalCurrentPrice(of("-800"))				// @160
				.setFinalUsedMargin(ofRUB5("25.00"))
				.setFinalProfitAndLoss(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ZERO_MONEY5)
				.setFinalTickValue(ofRUB2("1.40"));
		assertEquals(expectedPU4, actual.getPositionUpdate(symbol4));

		
		QFPortfolioChangeUpdate expected = new QFPortfolioChangeUpdate(account1)
				.setInitialBalance(ofRUB5("10000"))
				.setInitialEquity(ofRUB5("5000"))
				.setInitialFreeMargin(ofRUB5("4500"))
				.setInitialProfitAndLoss(ofRUB5("750"))
				.setInitialUsedMargin(ofRUB5("250"))
				.setInitialVarMargin(ofRUB5("10.9"))
				.setInitialVarMarginClose(ofRUB5("500"))
				.setInitialVarMarginInter(ofRUB5("190"))
				.setChangeBalance(ofRUB5("75.70"))
				.setFinalVarMarginInter(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalProfitAndLoss(ZERO_MONEY5)		
				.setFinalUsedMargin(ofRUB5("525.00"))		// 0.00 + 0.00 + 500.00 + 25.00 = 525.00
				.setFinalEquity(ofRUB5("10075.70"))
				.setFinalFreeMargin(ofRUB5("9550.70"));
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
