package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("100000"))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"))
			.withToken(SecurityField.TICK_VALUE, CDecimalBD.ofRUB2("0.02"))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("50.00"))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("12.00"))
			.buildUpdate());

		service = new QFCalcUtils();
	}
	
	@Test
	public void testChangePosition_OpenLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("100.05015"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(10L),
				CDecimalBD.of("45.00"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(ZERO_PRICE)
			.setInitialOpenPrice(ZERO_PRICE)
			.setInitialProfitAndLoss(ZERO_MONEY2)
			.setInitialUsedMargin(ZERO_MONEY2)
			.setInitialVarMargin(ZERO_MONEY5)
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("26.86193"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setInitialVolume(ZERO)
			.setFinalCurrentPrice(CDecimalBD.of("500.00")) // by settlement price 50.0
			.setFinalOpenPrice(CDecimalBD.of("450.00"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("226.91"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("120"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("100")) // x2 because of tick value 0.02
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("26.86193"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setFinalVolume(CDecimalBD.of(10L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_OpenShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB2("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB2("52.82"))
			.buildUpdate());
		// should use the last price instead of settlement price
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("49.02")
			.withSize(1L)
			.buildL1Update());
		
		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(-5L),
				CDecimalBD.of("48.00"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(ZERO_PRICE)
			.setInitialOpenPrice(ZERO_PRICE)
			.setInitialProfitAndLoss(ZERO_MONEY2)
			.setInitialUsedMargin(ZERO_MONEY2)
			.setInitialVarMargin(ZERO_MONEY5)
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(ZERO)
			.setFinalCurrentPrice(CDecimalBD.of("-245.10")) // by last price 49.02
			.setFinalOpenPrice(CDecimalBD.of("-240.00"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("60"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-10.2")) // x2 because of tick value 0.02
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setFinalVolume(CDecimalBD.of(-5L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_CloseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("500.00"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("450.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("226.91"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("100"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(-10L),
				CDecimalBD.of("44.08"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("500.00"))
			.setInitialOpenPrice(CDecimalBD.of("450.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("226.91"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("120"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("100"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("26.86193"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setInitialVolume(CDecimalBD.of(10L))
			.setFinalCurrentPrice(ZERO_PRICE)
			.setFinalOpenPrice(ZERO_PRICE)
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("108.51"))
			.setFinalUsedMargin(ZERO_MONEY2)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("8.46193")) // + loss -18.4
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setFinalVolume(CDecimalBD.of(0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_CloseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(5L),
				CDecimalBD.of("47.50"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(ZERO_PRICE)
			.setFinalOpenPrice(ZERO_PRICE)
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("67.85"))
			.setFinalUsedMargin(ZERO_MONEY2)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("15.03")) // + profit 2.5 x 2
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setFinalVolume(CDecimalBD.of(0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_IncreaseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("500.00"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("450.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("226.91"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("100"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(5L),
				CDecimalBD.of("45.02"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("500.00"))
			.setInitialOpenPrice(CDecimalBD.of("450.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("226.91"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("120"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("100"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("26.86193"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setInitialVolume(CDecimalBD.of(10L))
			.setFinalCurrentPrice(CDecimalBD.of("750.00"))
			.setFinalOpenPrice(CDecimalBD.of("675.10"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("276.71"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("180.00"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("149.80")) // 74.9 x 2 = 149.8
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("26.86193"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setFinalVolume(CDecimalBD.of(15L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_IncreaseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(-5L),
				CDecimalBD.of("49.01"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(CDecimalBD.of("-500.00"))
			.setFinalOpenPrice(CDecimalBD.of("-485.05"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("32.95"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("120"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-29.9"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setFinalVolume(CDecimalBD.of(-10L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_DecreaseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("500.00"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("431.05"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("264.81"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("137.9"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(-2L),
				CDecimalBD.of("45.02"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("500.00"))
			.setInitialOpenPrice(CDecimalBD.of("431.05"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("264.81"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("120"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("137.9"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("26.86193"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setInitialVolume(CDecimalBD.of(10L))
			.setFinalCurrentPrice(CDecimalBD.of("400.00"))
			.setFinalOpenPrice(CDecimalBD.of("344.83"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("244.89"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("96"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("110.34"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("34.50193"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setFinalVolume(CDecimalBD.of(8L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_DecreaseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(1L),
				CDecimalBD.of("49.01"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(CDecimalBD.of("-200.00"))
			.setFinalOpenPrice(CDecimalBD.of("-192.00"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("44.83"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("48"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-16"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("8.01")) // loss -2,02
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setFinalVolume(CDecimalBD.of(-4L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_SwapLongToShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("500.00"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("431.05"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("264.81"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("120"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("137.9"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("26.86193"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("100.05015"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(10L))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(-15L),
				CDecimalBD.of("45.02"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("500.00"))
			.setInitialOpenPrice(CDecimalBD.of("431.05"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("264.81"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("120"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("137.9"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("26.86193"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setInitialVolume(CDecimalBD.of(10L))
			.setFinalCurrentPrice(CDecimalBD.of("-250.00"))
			.setFinalOpenPrice(CDecimalBD.of("-225.10"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("115.41"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("60"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-49.8"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("65.16193")) // +38.3
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("100.05015"))
			.setFinalVolume(CDecimalBD.of(-5L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_SwapShortToLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p,
				CDecimalBD.of(10L),
				CDecimalBD.of("49.01"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(CDecimalBD.of("250.00"))
			.setFinalOpenPrice(CDecimalBD.of("245.05"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("62.65"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("60"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("9.9"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("-0.07")) // +10.1
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setFinalVolume(CDecimalBD.of(5L));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testChangePosition_ThrowsIfZeroVolume() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		
		service.changePosition(p, ZERO, CDecimalBD.of("48.24"));
	}
	
	@Test
	public void testRefreshByCurrentState1() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("51.16")
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("14"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.refreshByCurrentState(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(CDecimalBD.of("-255.80"))
			.setFinalOpenPrice(CDecimalBD.of("-240.00"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("31.25"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("70"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-31.6"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setFinalVolume(CDecimalBD.of(-5L));
		assertEquals(expected, actual);
	}

	@Test
	public void testRefreshByCurrentState2() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("51.16")
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("14"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.refreshByCurrentState(p, CDecimalBD.of("51.08"));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(CDecimalBD.of("-255.40"))
			.setFinalOpenPrice(CDecimalBD.of("-240.00"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("32.05"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("70"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("-30.8"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setFinalVolume(CDecimalBD.of(-5L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMidClearing() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());
		// Last price will not used
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("49.07")
			.withSize(1000L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("14.5"))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("49.08"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.midClearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(ZERO_MONEY2)
			.setChangeVolume(ZERO)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(CDecimalBD.of("-245.40"))
			.setFinalOpenPrice(CDecimalBD.of("-245.40"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB2("-0.77"))
			.setFinalUsedMargin(CDecimalBD.ofRUB2("72.5"))
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(CDecimalBD.ofRUB5("-0.77"));
		assertEquals(expected, actual);
	}

	@Test
	public void testClearing() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, CDecimalBD.of("-245.10"))
			.withToken(PositionField.OPEN_PRICE, CDecimalBD.of("-240.00"))
			.withToken(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofRUB2("52.65"))
			.withToken(PositionField.USED_MARGIN, CDecimalBD.ofRUB2("60"))
			.withToken(QFPositionField.QF_VAR_MARGIN, CDecimalBD.ofRUB5("-10.2"))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, CDecimalBD.ofRUB5("10.03"))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, CDecimalBD.ofRUB5("52.82"))
			.withToken(PositionField.CURRENT_VOLUME, CDecimalBD.of(-5L))
			.buildUpdate());
		// Last price will not used
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice("49.07")
			.withSize(1000L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("14.5"))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("49.08"))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.clearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(CDecimalBD.ofRUB2("52.05"))
			.setChangeVolume(ZERO)
			.setInitialCurrentPrice(CDecimalBD.of("-245.10"))
			.setInitialOpenPrice(CDecimalBD.of("-240.00"))
			.setInitialProfitAndLoss(CDecimalBD.ofRUB2("52.65"))
			.setInitialUsedMargin(CDecimalBD.ofRUB2("60"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("-10.2"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("10.03"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("52.82"))
			.setInitialVolume(CDecimalBD.of(-5L))
			.setFinalCurrentPrice(CDecimalBD.of("-245.40"))
			.setFinalOpenPrice(CDecimalBD.of("-245.40"))
			.setFinalProfitAndLoss(ZERO_MONEY2)
			.setFinalUsedMargin(CDecimalBD.ofRUB2("72.5"))
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(ZERO_MONEY5);
		assertEquals(expected, actual);
	}

}
