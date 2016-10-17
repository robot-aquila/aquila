package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*; 

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.DoubleUtils;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;

public class MoexContractSymbolUpdateConverterTest {
	private IMocksControl control;
	private DoubleUtils doubleUtilsMock;
	private MoexContractSymbolUpdateConverter converter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		doubleUtilsMock = control.createMock(DoubleUtils.class);
		converter = new MoexContractSymbolUpdateConverter(doubleUtilsMock);
	}

	@Test
	public void testToSymbolUpdate() {
		DeltaUpdate source = new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime(Instant.parse("2016-08-23T15:45:00Z"))
			.withToken(MoexContractField.SYMBOL, "Eu-9.16")
			.withToken(MoexContractField.SYMBOL_CODE, "EuU6")
			.withToken(MoexContractField.CONTRACT_DESCR, "Futures on EUR-RUB Exchange Rate")
			.withToken(MoexContractField.TYPE, MoexContractType.FUTURES)
			.withToken(MoexContractField.SETTLEMENT, MoexSettlementType.CASH_SETTLED)
			.withToken(MoexContractField.LOT_SIZE, 1000)
			.withToken(MoexContractField.QUOTATION, MoexQuotationType.RUR)
			.withToken(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2015, 6, 17))
			.withToken(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2016, 9, 15))
			.withToken(MoexContractField.DELIVERY, LocalDate.of(2016, 9, 15))
			.withToken(MoexContractField.TICK_SIZE, 0.05d)
			.withToken(MoexContractField.TICK_VALUE, 0.5d)
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, 70469.0d)
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, 76343.0d)
			.withToken(MoexContractField.SETTLEMENT_PRICE, 73406.0d)
			.withToken(MoexContractField.FEE, 1.0d)
			.withToken(MoexContractField.INTRADAY_FEE, 0.5d)
			.withToken(MoexContractField.NEGOTIATION_FEE, 1.0d)
			.withToken(MoexContractField.EXERCISE_FEE, 1.0d)
			.withToken(MoexContractField.INITIAL_MARGIN, 5874.0d)
			.withToken(MoexContractField.INITIAL_MARGIN_DATE, LocalDate.of(2016, 8, 23))
			.withToken(MoexContractField.FX_INTRADAY_CLEARING, LocalTime.of(13, 45))
			.withToken(MoexContractField.FX_EVENING_CLEARING, LocalTime.of(18, 30))
			.withToken(MoexContractField.SETTLEMENT_PROC_DESCR, "Test")
			.buildUpdate();
		expect(doubleUtilsMock.scaleOf(0.05d)).andReturn(2);
		control.replay();
		
		DeltaUpdate actual = converter.toSymbolUpdate(source);
		
		control.verify();
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime(Instant.parse("2016-08-23T15:45:00Z"))
			.withToken(SecurityField.DISPLAY_NAME, "Eu-9.16")
			.withToken(SecurityField.SCALE, 2)
			.withToken(SecurityField.LOT_SIZE, 1000)
			.withToken(SecurityField.TICK_SIZE, 0.05d)
			.withToken(SecurityField.TICK_VALUE, 0.5d)
			.withToken(SecurityField.INITIAL_MARGIN, 5874.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 73406.0d)
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 70469.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 76343.0d)
			.buildUpdate();
		assertEquals(1, SecurityField.getVersion());
		assertEquals(expected, actual);
	}

}
