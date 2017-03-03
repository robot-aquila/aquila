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
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;

public class MoexContractSymbolUpdateConverterTest {
	private IMocksControl control;
	private MoexContractSymbolUpdateConverter converter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		converter = new MoexContractSymbolUpdateConverter();
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
			.withToken(MoexContractField.TICK_SIZE, new FDecimal("0.05"))
			.withToken(MoexContractField.TICK_VALUE, new FMoney("0.5", "RUB"))
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
		control.replay();
		
		DeltaUpdate actual = converter.toSymbolUpdate(source);
		
		control.verify();
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime(Instant.parse("2016-08-23T15:45:00Z"))
			.withToken(SecurityField.DISPLAY_NAME, "Eu-9.16")
			.withToken(SecurityField.LOT_SIZE, 1000)
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.05"))
			.withToken(SecurityField.TICK_VALUE, new FMoney("0.5", "RUB"))
			.withToken(SecurityField.INITIAL_MARGIN, 5874.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 73406.0d)
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 70469.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 76343.0d)
			.buildUpdate();
		assertEquals(2, SecurityField.getVersion());
		assertEquals(expected, actual);
	}

}
