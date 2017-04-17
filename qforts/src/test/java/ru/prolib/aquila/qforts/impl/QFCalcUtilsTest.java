package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFCalcUtilsTest {
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
			.withToken(PortfolioField.BALANCE, FMoney.ofRUB2(100000.0))
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, FDecimal.of2(0.01))
			.withToken(SecurityField.TICK_VALUE, FMoney.ofRUB2(0.02))
			.withToken(SecurityField.SETTLEMENT_PRICE, FDecimal.of2(50.0))
			.withToken(SecurityField.INITIAL_MARGIN, FMoney.ofRUB2(12.0))
			.buildUpdate());

		service = new QFCalcUtils();
	}
	
	@Test
	public void testChangePosition_OpenLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(26.86193))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(100.05015))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, 10L, FDecimal.of2(45.0));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.ZERO2)
			.setInitialOpenPrice(FDecimal.ZERO2)
			.setInitialProfitAndLoss(FMoney.ZERO_RUB2)
			.setInitialUsedMargin(FMoney.ZERO_RUB2)
			.setInitialVarMargin(FMoney.ZERO_RUB5)
			.setInitialVarMarginClose(FMoney.ofRUB5(26.86193))
			.setInitialVarMarginInter(FMoney.ofRUB5(100.05015))
			.setInitialVolume(0L)
			.setFinalCurrentPrice(FDecimal.of2(500.0)) // by settlement price 50.0
			.setFinalOpenPrice(FDecimal.of2(450.0))
			.setFinalProfitAndLoss(FMoney.ofRUB2(226.91208))
			.setFinalUsedMargin(FMoney.ofRUB2(120.0))
			.setFinalVarMargin(FMoney.ofRUB5(100.0)) // x2 because of tick value 0.02
			.setFinalVarMarginClose(FMoney.ofRUB5(26.86193))
			.setFinalVarMarginInter(FMoney.ofRUB5(100.05015))
			.setFinalVolume(10L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_OpenShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB2(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB2(52.82))
			.buildUpdate());
		// should use the last price instead of settlement price
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice(49.02)
			.withSize(1L)
			.buildL1Update());
		
		QFPositionChangeUpdate actual = service.changePosition(p, -5L, FDecimal.of2(48.0));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.ZERO2)
			.setInitialOpenPrice(FDecimal.ZERO2)
			.setInitialProfitAndLoss(FMoney.ZERO_RUB2)
			.setInitialUsedMargin(FMoney.ZERO_RUB2)
			.setInitialVarMargin(FMoney.ZERO_RUB5)
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(0L)
			.setFinalCurrentPrice(FDecimal.of2(-245.1)) // by last price 49.02
			.setFinalOpenPrice(FDecimal.of2(-240.0))
			.setFinalProfitAndLoss(FMoney.ofRUB2(52.65))
			.setFinalUsedMargin(FMoney.ofRUB2(60.0))
			.setFinalVarMargin(FMoney.ofRUB5(-10.2)) // x2 because of tick value 0.02
			.setFinalVarMarginClose(FMoney.ofRUB5(10.03))
			.setFinalVarMarginInter(FMoney.ofRUB5(52.82))
			.setFinalVolume(-5L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_CloseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(500.0))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(450.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(226.91208))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(120.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(100.0))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(26.86193))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(100.05015))
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, -10L, FDecimal.of2(44.08));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(500.0))
			.setInitialOpenPrice(FDecimal.of2(450.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(226.91208))
			.setInitialUsedMargin(FMoney.ofRUB2(120.0))
			.setInitialVarMargin(FMoney.ofRUB5(100.0))
			.setInitialVarMarginClose(FMoney.ofRUB5(26.86193))
			.setInitialVarMarginInter(FMoney.ofRUB5(100.05015))
			.setInitialVolume(10L)
			.setFinalCurrentPrice(FDecimal.ZERO2)
			.setFinalOpenPrice(FDecimal.ZERO2)
			.setFinalProfitAndLoss(FMoney.ofRUB2(108.51208))
			.setFinalUsedMargin(FMoney.ZERO_RUB2)
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ofRUB5(8.46193)) // + loss -18.4
			.setFinalVarMarginInter(FMoney.ofRUB5(100.05015))
			.setFinalVolume(0L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_CloseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, 5L, FDecimal.of2(47.5));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.ZERO2)
			.setFinalOpenPrice(FDecimal.ZERO2)
			.setFinalProfitAndLoss(FMoney.ofRUB2(67.85))
			.setFinalUsedMargin(FMoney.ZERO_RUB2)
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ofRUB5(15.03)) // + profit 2.5 x 2
			.setFinalVarMarginInter(FMoney.ofRUB5(52.82))
			.setFinalVolume(0L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_IncreaseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(500.0))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(450.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(226.91208))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(120.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(100.0))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(26.86193))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(100.05015))
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, 5L, FDecimal.of2(45.02));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(500.0))
			.setInitialOpenPrice(FDecimal.of2(450.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(226.91208))
			.setInitialUsedMargin(FMoney.ofRUB2(120.0))
			.setInitialVarMargin(FMoney.ofRUB5(100.0))
			.setInitialVarMarginClose(FMoney.ofRUB5(26.86193))
			.setInitialVarMarginInter(FMoney.ofRUB5(100.05015))
			.setInitialVolume(10L)
			.setFinalCurrentPrice(FDecimal.of2(750.0))
			.setFinalOpenPrice(FDecimal.of2(675.1))
			.setFinalProfitAndLoss(FMoney.ofRUB2(276.71))
			.setFinalUsedMargin(FMoney.ofRUB2(180.0))
			.setFinalVarMargin(FMoney.ofRUB5(149.8)) // 74.9 x 2 = 149.8
			.setFinalVarMarginClose(FMoney.ofRUB5(26.86193))
			.setFinalVarMarginInter(FMoney.ofRUB5(100.05015))
			.setFinalVolume(15L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_IncreaseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, -5L, FDecimal.of2(49.01));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.of2(-500.0))
			.setFinalOpenPrice(FDecimal.of2(-485.05))
			.setFinalProfitAndLoss(FMoney.ofRUB2(32.95))
			.setFinalUsedMargin(FMoney.ofRUB2(120.0))
			.setFinalVarMargin(FMoney.ofRUB5(-29.9))
			.setFinalVarMarginClose(FMoney.ofRUB5(10.03))
			.setFinalVarMarginInter(FMoney.ofRUB5(52.82))
			.setFinalVolume(-10L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_DecreaseLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(500.0))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(431.05))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(264.81208))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(120.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(137.9))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(26.86193))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(100.05015))
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, -2L, FDecimal.of2(45.02));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(500.0))
			.setInitialOpenPrice(FDecimal.of2(431.05))
			.setInitialProfitAndLoss(FMoney.ofRUB2(264.81208))
			.setInitialUsedMargin(FMoney.ofRUB2(120.0))
			.setInitialVarMargin(FMoney.ofRUB5(137.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(26.86193))
			.setInitialVarMarginInter(FMoney.ofRUB5(100.05015))
			.setInitialVolume(10L)
			.setFinalCurrentPrice(FDecimal.of2(400.0))
			.setFinalOpenPrice(FDecimal.of2(344.83))
			.setFinalProfitAndLoss(FMoney.ofRUB2(244.89208))
			.setFinalUsedMargin(FMoney.ofRUB2(96.0))
			.setFinalVarMargin(FMoney.ofRUB5(110.34))
			.setFinalVarMarginClose(FMoney.ofRUB5(34.50193))
			.setFinalVarMarginInter(FMoney.ofRUB5(100.05015))
			.setFinalVolume(8L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_DecreaseShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, 1L, FDecimal.of2(49.01));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.of2(-200.0))
			.setFinalOpenPrice(FDecimal.of2(-192.0))
			.setFinalProfitAndLoss(FMoney.ofRUB2(44.83))
			.setFinalUsedMargin(FMoney.ofRUB2(48.0))
			.setFinalVarMargin(FMoney.ofRUB5(-16.0))
			.setFinalVarMarginClose(FMoney.ofRUB5(8.01)) // loss -2,02
			.setFinalVarMarginInter(FMoney.ofRUB5(52.82))
			.setFinalVolume(-4L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_SwapLongToShort() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(500.0))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(431.05))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(264.81208))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(120.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(137.9))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(26.86193))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(100.05015))
			.withToken(PositionField.CURRENT_VOLUME, 10L)
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.changePosition(p, -15L, FDecimal.of2(45.02));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(500.0))
			.setInitialOpenPrice(FDecimal.of2(431.05))
			.setInitialProfitAndLoss(FMoney.ofRUB2(264.81208))
			.setInitialUsedMargin(FMoney.ofRUB2(120.0))
			.setInitialVarMargin(FMoney.ofRUB5(137.9))
			.setInitialVarMarginClose(FMoney.ofRUB5(26.86193))
			.setInitialVarMarginInter(FMoney.ofRUB5(100.05015))
			.setInitialVolume(10L)
			.setFinalCurrentPrice(FDecimal.of2(-250.0))
			.setFinalOpenPrice(FDecimal.of2(-225.1))
			.setFinalProfitAndLoss(FMoney.ofRUB2(115.41208))
			.setFinalUsedMargin(FMoney.ofRUB2(60.0))
			.setFinalVarMargin(FMoney.ofRUB5(-49.8))
			.setFinalVarMarginClose(FMoney.ofRUB5(65.16193)) // +38.3
			.setFinalVarMarginInter(FMoney.ofRUB5(100.05015))
			.setFinalVolume(-5L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testChangePosition_SwapShortToLong() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());

		QFPositionChangeUpdate actual = service.changePosition(p, 10L, FDecimal.of2(49.01));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.of2(250.0))
			.setFinalOpenPrice(FDecimal.of2(245.05))
			.setFinalProfitAndLoss(FMoney.ofRUB2(62.65))
			.setFinalUsedMargin(FMoney.ofRUB2(60.0))
			.setFinalVarMargin(FMoney.ofRUB5(9.9))
			.setFinalVarMarginClose(FMoney.ofRUB5(-0.07)) // +10.1
			.setFinalVarMarginInter(FMoney.ofRUB5(52.82))
			.setFinalVolume(5L);
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testChangePosition_ThrowsIfZeroVolume() throws Exception {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		
		service.changePosition(p, 0L, FDecimal.of2(48.24));
	}
	
	@Test
	public void testRefreshByCurrentState1() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice(51.16)
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, FMoney.ofRUB2(14.0))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.refreshByCurrentState(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.of2(-255.8))
			.setFinalOpenPrice(FDecimal.of2(-240.0))
			.setFinalProfitAndLoss(FMoney.ofRUB2(31.25))
			.setFinalUsedMargin(FMoney.ofRUB2(70.0))
			.setFinalVarMargin(FMoney.ofRUB5(-31.6))
			.setFinalVarMarginClose(FMoney.ofRUB5(10.03))
			.setFinalVarMarginInter(FMoney.ofRUB5(52.82))
			.setFinalVolume(-5L);
		assertEquals(expected, actual);
	}

	@Test
	public void testRefreshByCurrentState2() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice(51.16)
			.withSize(100L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, FMoney.ofRUB2(14.0))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.refreshByCurrentState(p, FDecimal.of2(51.08));
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.of2(-255.4))
			.setFinalOpenPrice(FDecimal.of2(-240.0))
			.setFinalProfitAndLoss(FMoney.ofRUB2(32.05))
			.setFinalUsedMargin(FMoney.ofRUB2(70.0))
			.setFinalVarMargin(FMoney.ofRUB5(-30.8))
			.setFinalVarMarginClose(FMoney.ofRUB5(10.03))
			.setFinalVarMarginInter(FMoney.ofRUB5(52.82))
			.setFinalVolume(-5L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMidClearing() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		// Last price will not used
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice(49.07)
			.withSize(1000L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, FMoney.ofRUB2(14.5))
			.withToken(SecurityField.SETTLEMENT_PRICE, FDecimal.of2(49.08))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.midClearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeVolume(0L)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.of2(-245.4))
			.setFinalOpenPrice(FDecimal.of2(-245.4))
			.setFinalProfitAndLoss(FMoney.ofRUB2(-0.77))
			.setFinalUsedMargin(FMoney.ofRUB2(72.5))
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5)
			.setFinalVarMarginInter(FMoney.ofRUB5(-0.77));
		assertEquals(expected, actual);
	}

	@Test
	public void testClearing() {
		EditablePosition p = terminal.getEditablePortfolio(account).getEditablePosition(symbol);
		p.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, FDecimal.of2(-245.1))
			.withToken(PositionField.OPEN_PRICE, FDecimal.of2(-240.0))
			.withToken(PositionField.PROFIT_AND_LOSS, FMoney.ofRUB2(52.65))
			.withToken(PositionField.USED_MARGIN, FMoney.ofRUB2(60.0))
			.withToken(QFPositionField.QF_VAR_MARGIN, FMoney.ofRUB5(-10.2))
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, FMoney.ofRUB5(10.03))
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, FMoney.ofRUB5(52.82))
			.withToken(PositionField.CURRENT_VOLUME, -5L)
			.buildUpdate());
		// Last price will not used
		terminal.getEditableSecurity(symbol).consume(new L1UpdateBuilder(symbol)
			.withTrade()
			.withPrice(49.07)
			.withSize(1000L)
			.buildL1Update());
		terminal.getEditableSecurity(symbol).consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, FMoney.ofRUB2(14.5))
			.withToken(SecurityField.SETTLEMENT_PRICE, FDecimal.of2(49.08))
			.buildUpdate());
		
		QFPositionChangeUpdate actual = service.clearing(p);
		
		QFPositionChangeUpdate expected = new QFPositionChangeUpdate(account, symbol)
			.setChangeBalance(FMoney.ofRUB2(52.05))
			.setChangeVolume(0L)
			.setInitialCurrentPrice(FDecimal.of2(-245.1))
			.setInitialOpenPrice(FDecimal.of2(-240.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(52.65))
			.setInitialUsedMargin(FMoney.ofRUB2(60.0))
			.setInitialVarMargin(FMoney.ofRUB5(-10.2))
			.setInitialVarMarginClose(FMoney.ofRUB5(10.03))
			.setInitialVarMarginInter(FMoney.ofRUB5(52.82))
			.setInitialVolume(-5L)
			.setFinalCurrentPrice(FDecimal.of2(-245.4))
			.setFinalOpenPrice(FDecimal.of2(-245.4))
			.setFinalProfitAndLoss(FMoney.ZERO_RUB2)
			.setFinalUsedMargin(FMoney.ofRUB2(72.5))
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5)
			.setFinalVarMarginInter(FMoney.ZERO_RUB5);
		assertEquals(expected, actual);
	}

}
