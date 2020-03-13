package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

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
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFCalcUtilsTest {
	private static final CDecimal ZERO = CDecimalBD.ZERO;
	private static final CDecimal ZERO_PRICE = ZERO.withScale(2);
	private static final CDecimal ZERO_MONEY2 = CDecimalBD.ZERO_RUB2;
	private static final CDecimal ZERO_MONEY5 = CDecimalBD.ZERO_RUB5;
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private static Account account;
	private static Symbol symbol;
	private EditableTerminal terminal;
	private QFCalcUtils service;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		account = new Account("TEST");
		symbol = new Symbol("BEST");
	}

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		terminal.getEditablePortfolio(account).consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.BALANCE, ofRUB2("100000"))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, 		of("0.01"))
			.withToken(SecurityField.TICK_VALUE,		ofRUB2("0.02"))
			.withToken(SecurityField.SETTLEMENT_PRICE,	of("50.00"))
			.withToken(SecurityField.INITIAL_MARGIN,	ofRUB2("12.00"))
			.buildUpdate());

		service = new QFCalcUtils();
	}
	
	@Test
	public void testChangePosition_OpenLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("100.05015"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, of(10L), of("45.00"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY5)
			.setInitialCurrentPrice(ZERO_PRICE)
			.setInitialOpenPrice(ZERO_PRICE)
			.setInitialProfitAndLoss(ZERO_MONEY5)
			.setInitialUsedMargin(ZERO_MONEY5)
			.setInitialVarMargin(ZERO_MONEY5)
			.setInitialVarMarginClose(ofRUB5("26.86193"))
			.setInitialVarMarginInter(ofRUB5("100.05015"))
			.setInitialVolume(ZERO)
			.setInitialTickValue(null)
			.setFinalCurrentPrice(of("500.00")) // by settlement price 50.0
			.setFinalOpenPrice(of("450.00"))
			.setFinalProfitAndLoss(ofRUB5("226.91208"))
			.setFinalUsedMargin(ofRUB5("120"))
			.setFinalVarMargin(ofRUB5("100")) // x2 because of tick value 0.02
			.setFinalVarMarginClose(ofRUB5("26.86193"))
			.setFinalVarMarginInter(ofRUB5("100.05015"))
			.setFinalVolume(of(10L))
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_OpenShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("52.82"))
			.buildUpdate());
		// should use the last price instead of settlement price
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("49.02")
			.withSize(1L)
			.buildL1Update());
		
		QFPositionChangeUpdate actual = service.changePosition(p, of(-5L), of("48.00"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY5)
			.setInitialCurrentPrice(ZERO_PRICE)
			.setInitialOpenPrice(ZERO_PRICE)
			.setInitialProfitAndLoss(ZERO_MONEY5)
			.setInitialUsedMargin(ZERO_MONEY5)
			.setInitialVarMargin(ZERO_MONEY5)
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(ZERO)
			.setInitialTickValue(null)
			.setFinalCurrentPrice(of("-245.10")) // by last price 49.02
			.setFinalOpenPrice(of("-240.00"))
			.setFinalProfitAndLoss(ofRUB5("52.65"))
			.setFinalUsedMargin(ofRUB5("60"))
			.setFinalVarMargin(ofRUB5("-10.2")) // x2 because of tick value 0.02
			.setFinalVarMarginClose(ofRUB5("10.03"))
			.setFinalVarMarginInter(ofRUB5("52.82"))
			.setFinalVolume(of(-5L))
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_CloseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, of("500.00"))
			.withToken(PositionField.OPEN_PRICE, of("450.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, ofRUB5("226.91"))
			.withToken(PositionField.USED_MARGIN, ofRUB5("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN, ofRUB5("100"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME, of(10L))
			// It differs of defined in security's (which is 0.02) and
			// must be used instead of security's attribute!
			.withToken(QFPositionField.QF_TICK_VALUE, ofRUB5("0.025"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, of(-10L), of("44.08"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY5)
			.setInitialCurrentPrice(of("500.00"))
			.setInitialOpenPrice(of("450.00"))
			.setInitialProfitAndLoss(ofRUB5("226.91"))
			.setInitialUsedMargin(ofRUB5("120"))
			.setInitialVarMargin(ofRUB5("100"))
			.setInitialVarMarginClose(ofRUB5("26.86193"))
			.setInitialVarMarginInter(ofRUB5("100.05015"))
			.setInitialVolume(of(10L))
			.setInitialTickValue(ofRUB5("0.025"))
			.setFinalCurrentPrice(ZERO_PRICE)
			.setFinalOpenPrice(ZERO_PRICE)
			.setFinalProfitAndLoss(ofRUB5("103.91208"))
			.setFinalUsedMargin(ZERO_MONEY5)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ofRUB5("3.86193")) // 26.86193 + (9.2pt / 0.01 * 0.025) = 26.86193 - 23 = 3.86193
			.setFinalVarMarginInter(ofRUB5("100.05015"))
			.setFinalVolume(of(0L))
			.setFinalTickValue(null);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_CloseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.015"))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, of(5L), of("47.50"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(of("-245.10"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB5("0.015"))
			.setFinalCurrentPrice(ZERO_PRICE)
			.setFinalOpenPrice(ZERO_PRICE)
			.setFinalProfitAndLoss(ofRUB5("66.6"))
			.setFinalUsedMargin(ZERO_MONEY2)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ofRUB5("13.78")) // 10.03 + (2.5pt / 0.01 * 0.015) = 10.03 + 3.75 = 13.78
			.setFinalVarMarginInter(ofRUB5("52.82"))
			.setFinalVolume(of(0L))
			.setFinalTickValue(null);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_IncreaseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("500.00"))
			.withToken(PositionField.OPEN_PRICE,			of("450.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("226.91"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("100"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME,		of(10L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.45"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, of(5L), of("45.02"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2) // This works because CDValueTriplet checked and mutated argument
			.setInitialCurrentPrice(of("500.00"))
			.setInitialOpenPrice(of("450.00"))
			.setInitialProfitAndLoss(ofRUB5("226.91"))
			.setInitialUsedMargin(ofRUB5("120"))
			.setInitialVarMargin(ofRUB5("100"))
			.setInitialVarMarginClose(ofRUB5("26.86193"))
			.setInitialVarMarginInter(ofRUB5("100.05015"))
			.setInitialVolume(of(10L))
			.setInitialTickValue(ofRUB5("0.45"))
			.setFinalCurrentPrice(of("750.00"))
			.setFinalOpenPrice(of("675.10"))
			.setFinalProfitAndLoss(ofRUB5("3497.41208"))
			.setFinalUsedMargin(ofRUB5("180.00"))
			.setFinalVarMargin(ofRUB5("3370.5")) // cpr:750.0pt - opr:675.1pt = 74.9pt / 0.01 * 0.45 = 3370.5 
			.setFinalVarMarginClose(ofRUB5("26.86193"))
			.setFinalVarMarginInter(ofRUB5("100.05015"))
			.setFinalVolume(of(15L))
			.setFinalTickValue(ofRUB2("0.02")); // such dangerous situation should be checked at level above
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_IncreaseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("52.65"))
			.withToken(PositionField.USED_MARGIN, 			ofRUB5("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, 		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, 		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE, 		ofRUB5("0.12"))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, of(-5L), of("49.01"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY5)
			.setInitialCurrentPrice(of("-245.10"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB5("0.12"))
			.setFinalCurrentPrice(of("-500.00"))
			.setFinalOpenPrice(of("-485.05"))
			.setFinalProfitAndLoss(ofRUB5("-116.55"))
			.setFinalUsedMargin(ofRUB5("120"))
			.setFinalVarMargin(ofRUB5("-179.4")) // cpr:-500.0pt - opr:-485.05pt = 14.95pt / 0.01 * 0.12 = 179.4 
			.setFinalVarMarginClose(ofRUB5("10.03"))
			.setFinalVarMarginInter(ofRUB5("52.82"))
			.setFinalVolume(of(-10L))
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_DecreaseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("500.00"))
			.withToken(PositionField.OPEN_PRICE,			of("431.05"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("264.81"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("137.9"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME,		of(10L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.3"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, of(-2L), of("45.02"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(of("500.00"))
			.setInitialOpenPrice(of("431.05"))
			.setInitialProfitAndLoss(ofRUB5("264.81"))
			.setInitialUsedMargin(ofRUB5("120"))
			.setInitialVarMargin(ofRUB5("137.9"))
			.setInitialVarMarginClose(ofRUB5("26.86193"))
			.setInitialVarMarginInter(ofRUB5("100.05015"))
			.setInitialVolume(of(10L))
			.setInitialTickValue(ofRUB5("0.3"))
			.setFinalCurrentPrice(of("400.00"))
			.setFinalOpenPrice(of("344.83"))
			.setFinalProfitAndLoss(ofRUB5("1896.61208"))
			.setFinalUsedMargin(ofRUB5("96"))
														// AVG pr = 431.05 / 10 = 43.105 -> 43.11 (2 decimals)
														// decrease 2 x 43.11 = 86.22
														// new open pr = 431.05 - 86.22 = 344.83
														// vm pt = 45.02 * 2 - 100 = -9.96
														// vm cl = price - avg pr * 2 = 3.82pt = 114.6 = 141.46193
			.setFinalVarMargin(ofRUB5("1655.10"))
			.setFinalVarMarginClose(ofRUB5("141.46193")) 
			.setFinalVarMarginInter(ofRUB5("100.05015"))
			.setFinalVolume(CDecimalBD.of(8L))
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_DecreaseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.15"))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, of(1L), of("49.01"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(of("-245.10"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB5("0.15"))
			.setFinalCurrentPrice(of("-200.00"))
			.setFinalOpenPrice(of("-192.00"))
			.setFinalProfitAndLoss(ofRUB5("-72.3"))
			.setFinalUsedMargin(ofRUB5("48"))
														// AVG pr = -240 / 5 = -48.0
														// decrease 1 x -48.0
														// new open pr = -240 - -48.0 = -192.0
														// vm cl pt = 49.01 - 48.0 = -1.01pt (loss)
														// vm cl = -1.01 / 0.01 * 0.15 = -15.15 + 10.03 = -5.12
			.setFinalVarMargin(ofRUB5("-120"))			// vm = 50 * 4 - (240 - 48) = 200 - 192 = 8pt = -120.0 (loss)
			.setFinalVarMarginClose(ofRUB5("-5.12"))
			.setFinalVarMarginInter(ofRUB5("52.82"))
			.setFinalVolume(of(-4L))
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_SwapLongToShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("500.00"))
			.withToken(PositionField.OPEN_PRICE,			of("431.05"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("264.81"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("137.9"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME,		of(10L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.25"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, of(-15L), of("45.02"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(of("500.00"))
			.setInitialOpenPrice(of("431.05"))
			.setInitialProfitAndLoss(ofRUB5("264.81"))
			.setInitialUsedMargin(ofRUB5("120"))
			.setInitialVarMargin(ofRUB5("137.9"))
			.setInitialVarMarginClose(ofRUB5("26.86193"))
			.setInitialVarMarginInter(ofRUB5("100.05015"))
			.setInitialVolume(of(10L))
			.setInitialTickValue(ofRUB5("0.25"))
			.setFinalCurrentPrice(of("-250.00"))
			.setFinalOpenPrice(of("-225.10"))
			.setFinalProfitAndLoss(ofRUB5("-16.83792"))
			.setFinalUsedMargin(ofRUB5("60"))
			.setFinalVarMargin(ofRUB5("-622.5"))
			.setFinalVarMarginClose(ofRUB5("505.61193")) // pt's 450.2 - 431.05 = 19.15 pt = 478.75 -> 505.61193
			.setFinalVarMarginInter(ofRUB5("100.05015"))
			.setFinalVolume(of(-5L))
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_SwapShortToLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.12"))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, of(10L), of("49.01"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(of("-245.10"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB5("0.12"))
			.setFinalCurrentPrice(of("250.00"))
			.setFinalOpenPrice(of("245.05"))
			.setFinalProfitAndLoss(ofRUB5("61.65"))
			.setFinalUsedMargin(ofRUB5("60"))
			.setFinalVarMargin(ofRUB5("59.4"))		  // 4.95pt = 59.4
			.setFinalVarMarginClose(ofRUB5("-50.57")) // pt's 245.05 - 240.0 = -5.05 pt (loss) = -60.6 + 10.03 = -50.57
			.setFinalVarMarginInter(ofRUB5("52.82"))
			.setFinalVolume(of(5L))
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testChangePosition_ThrowsIfZeroVolume() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		
		service.changePosition(p, ZERO, of("48.24"));
	}
	
	@Test
	public void testRefreshByCurrentState1_ThrowsIfPosTickValNotSpecified() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("51.16")
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("14"))
			.buildUpdate());
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Var.margin calculation failed. Info: account=TEST symbol=BEST "
				+ "cur.pr=-255.80 opn.pr=-240.00 tick.sz=0.01 tick.val=null");
		
		service.refreshByCurrentState(p);
	}

	@Test
	public void testRefreshByCurrentState1_OK() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.20"))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("51.16")
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN,		ofRUB2("14"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.refreshByCurrentState(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY5)
			.setInitialCurrentPrice(of("-245.10"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB5("0.20"))
			.setFinalCurrentPrice(of("-255.80"))
			.setFinalOpenPrice(of("-240.00"))
			.setFinalProfitAndLoss(ofRUB5("-253.15"))
			.setFinalUsedMargin(ofRUB5("70"))
			.setFinalVarMargin(ofRUB5("-316.0"))
			.setFinalVarMarginClose(ofRUB5("10.03"))
			.setFinalVarMarginInter(ofRUB5("52.82"))
			.setFinalVolume(of(-5L))
			.setFinalTickValue(ofRUB5("0.20"));
		assertEquals(expected, actual);
	}

	@Test
	public void testRefreshByCurrentState2_ThrowsIfPosTickValNotSpecified() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, 		of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("51.16")
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("14"))
			.buildUpdate());
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Var.margin calculation failed. Info: account=TEST symbol=BEST "
				+ "cur.pr=-255.40 opn.pr=-240.00 tick.sz=0.01 tick.val=null");
		
		service.refreshByCurrentState(p, CDecimalBD.of("51.08"));

	}

	@Test
	public void testRefreshByCurrentState2_OK() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, 		of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE,	ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB2("0.4"))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("51.16")
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("14"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.refreshByCurrentState(p, CDecimalBD.of("51.08"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(of("-245.10"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB2("0.4"))
			.setFinalCurrentPrice(of("-255.40"))
			.setFinalOpenPrice(of("-240.00"))
			.setFinalProfitAndLoss(ofRUB5("-553.15"))
			.setFinalUsedMargin(ofRUB5("70"))
			.setFinalVarMargin(ofRUB5("-616.0"))
			.setFinalVarMarginClose(ofRUB5("10.03"))
			.setFinalVarMarginInter(ofRUB5("52.82"))
			.setFinalVolume(of(-5L))
			.setFinalTickValue(ofRUB2("0.4"));
		assertEquals(expected, actual);
	}

	@Test
	public void testMidClearing_ForOpenPosition() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			// Current price can be any value. Settlement price will be used to
			// refresh position current price and all linked values like margin
			.withToken(PositionField.CURRENT_PRICE, 		of("-555.55")) 
			.withToken(PositionField.OPEN_PRICE, 			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB5("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2")) // must be reset
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("10.03")) // must be kept
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("52.82")) // must be kept
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB2("0.70"))
			.buildUpdate());
		// Last price will not used
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("49.07")
			.withSize(1000L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN,	ofRUB2("14.5"))
			.withToken(SecurityField.SETTLEMENT_PRICE,	of("49.08"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.midClearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setChangeVolume(ZERO)
			.setInitialCurrentPrice(of("-555.55"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB2("0.70"))
			.setFinalCurrentPrice(of("-245.40"))
			.setFinalOpenPrice(of("-245.40"))
			.setFinalProfitAndLoss(ofRUB5("-315.15"))	// sum up all types of margin
			.setFinalUsedMargin(ofRUB5("72.5"))
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(ofRUB5("-315.15"))	// cl pr - op pr = (-5 * 49.08) - -240.0 = -5.4pt
													  	// 5.4 / 0.01 * 0.7 = -378.0
														// -378.0 + 52.82 + 10.03 = -315.15
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMidClearing_ForClosedPosition() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, 		ZERO_PRICE) // zero if position closed
			.withToken(PositionField.OPEN_PRICE,			ZERO_PRICE) // zero if position closed
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB5("35.97")) // just sum of margin of all types
			.withToken(PositionField.USED_MARGIN, 			ZERO_MONEY5) // zero if position closed
			.withToken(QFPositionField.QF_VAR_MARGIN, 		ZERO_MONEY5) // zero if position closed
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("25.94")) // must be not lost
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER,	ofRUB5("10.03")) // must be not lost
			.withToken(PositionField.CURRENT_VOLUME,		of(0L))
			.withToken(QFPositionField.QF_TICK_VALUE,		null)
			.buildUpdate());

		QFPositionChangeUpdate actual = service.midClearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
				.setChangeBalance(ZERO_MONEY2)
				.setChangeVolume(ZERO)
				.setInitialCurrentPrice(ZERO_PRICE)
				.setInitialOpenPrice(ZERO_PRICE)
				.setInitialProfitAndLoss(ofRUB5("35.97"))
				.setInitialUsedMargin(ZERO_MONEY5)
				.setInitialVarMargin(ZERO_MONEY5)
				.setInitialVarMarginClose(ofRUB5("25.94"))
				.setInitialVarMarginInter(ofRUB5("10.03"))
				.setInitialVolume(ZERO)
				.setInitialTickValue(null)
				.setFinalCurrentPrice(ZERO_PRICE)
				.setFinalOpenPrice(ZERO_PRICE)
				.setFinalProfitAndLoss(ofRUB5("35.97"))
				.setFinalUsedMargin(ZERO_MONEY5)
				.setFinalVarMargin(ZERO_MONEY5)
				.setFinalVarMarginClose(ZERO_MONEY5)
				.setFinalVarMarginInter(ofRUB5("35.97"))
				.setFinalTickValue(null); // because position is closed
			assertEquals(expected, actual);
	}

	@Test
	public void testClearing_ForOpenPosition() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE,			of("-245.10"))
			.withToken(PositionField.OPEN_PRICE,			of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS,		ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN,			ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN,		ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME,		of(-5L))
			.withToken(QFPositionField.QF_TICK_VALUE,		ofRUB5("0.25"))
			.buildUpdate());
		// The last price of security must not be used
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("49.07")
			.withSize(1000L)
			.buildL1Update());
		// The tick value of security must not be used for margin calculation
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN,	ofRUB2("14.5"))
			.withToken(SecurityField.SETTLEMENT_PRICE,	of("49.08"))
			.withToken(SecurityField.TICK_SIZE,			of("0.01"))
			.withToken(SecurityField.TICK_VALUE,		ofRUB2("0.02"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.clearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ofRUB5("-72.15"))	// was short, settl price * -5 - opn pri -> money
												// 49.08 * -5 = -245.4 - -240 = 5.4pt -> -135.0 money
												// +vm cls + vm inter = -72.15
			.setChangeVolume(ZERO)
			.setInitialCurrentPrice(of("-245.10"))
			.setInitialOpenPrice(of("-240.00"))
			.setInitialProfitAndLoss(ofRUB5("52.65"))
			.setInitialUsedMargin(ofRUB5("60"))
			.setInitialVarMargin(ofRUB5("-10.2"))
			.setInitialVarMarginClose(ofRUB5("10.03"))
			.setInitialVarMarginInter(ofRUB5("52.82"))
			.setInitialVolume(of(-5L))
			.setInitialTickValue(ofRUB5("0.25"))
			.setFinalCurrentPrice(of("-245.40"))
			.setFinalOpenPrice(of("-245.40"))
			.setFinalProfitAndLoss(ZERO_MONEY2)
			.setFinalUsedMargin(ofRUB5("72.5"))
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(ZERO_MONEY5)
			.setFinalTickValue(ofRUB2("0.02"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClearing_ForClosedPosition() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, ZERO_PRICE)
			.withToken(PositionField.OPEN_PRICE, ZERO_PRICE)
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("62.85"))
			.withToken(PositionField.USED_MARGIN, ZERO_MONEY2)
			.withToken(QFPositionField.QF_VAR_MARGIN, ZERO_MONEY5)
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, ZERO)
			.withToken(QFPositionField.QF_TICK_VALUE, null)
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.clearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(CDecimalBD.ofRUB2("62.85"))
			.setChangeVolume(ZERO)
			.setInitialCurrentPrice(ZERO_PRICE)
			.setInitialOpenPrice(ZERO_PRICE)
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("62.85"))
			.setInitialUsedMargin(ZERO_MONEY2)
			.setInitialVarMargin(ZERO_MONEY5)
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(ZERO)
			.setInitialTickValue(null)
			.setFinalCurrentPrice(ZERO_PRICE)
			.setFinalOpenPrice(ZERO_PRICE)
			.setFinalProfitAndLoss(ZERO_MONEY2)
			.setFinalUsedMargin(ZERO_MONEY2)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(ZERO_MONEY5)
			.setFinalTickValue(null);
		assertEquals(expected, actual);
	}

}
