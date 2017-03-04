package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;

public class MoexContractSymbolUpdateConverterTest {
	private MoexContractSymbolUpdateConverter converter;

	@Before
	public void setUp() throws Exception {
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
			.withToken(MoexContractField.LOT_SIZE, 1000L)
			.withToken(MoexContractField.QUOTATION, MoexQuotationType.RUR)
			.withToken(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2015, 6, 17))
			.withToken(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2016, 9, 15))
			.withToken(MoexContractField.DELIVERY, LocalDate.of(2016, 9, 15))
			.withToken(MoexContractField.TICK_SIZE, new FDecimal("0.05"))
			.withToken(MoexContractField.TICK_VALUE, new FMoney("0.5", "RUB"))
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, new FDecimal("70469"))
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, new FDecimal("76343"))
			.withToken(MoexContractField.SETTLEMENT_PRICE, new FDecimal("73406"))
			.withToken(MoexContractField.FEE, new FMoney("1", "RUB"))
			.withToken(MoexContractField.INTRADAY_FEE, new FMoney("0.5", "RUB"))
			.withToken(MoexContractField.NEGOTIATION_FEE, new FMoney("1", "RUB"))
			.withToken(MoexContractField.EXERCISE_FEE, new FMoney("1", "RUB"))
			.withToken(MoexContractField.INITIAL_MARGIN, new FMoney("5874", "RUB"))
			.withToken(MoexContractField.INITIAL_MARGIN_DATE, LocalDate.of(2016, 8, 23))
			.withToken(MoexContractField.FX_INTRADAY_CLEARING, LocalTime.of(13, 45))
			.withToken(MoexContractField.FX_EVENING_CLEARING, LocalTime.of(18, 30))
			.withToken(MoexContractField.SETTLEMENT_PROC_DESCR, "Test")
			.buildUpdate();
		
		DeltaUpdate actual = converter.toSymbolUpdate(source);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime(Instant.parse("2016-08-23T15:45:00Z"))
			.withToken(SecurityField.DISPLAY_NAME, "Eu-9.16")
			.withToken(SecurityField.LOT_SIZE, 1000L)
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.05", 2))
			.withToken(SecurityField.TICK_VALUE, new FMoney("0.5", 2, "RUB"))
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5874", 2, "RUB"))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("73406", 2))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("70469", 2))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("76343", 2))
			.buildUpdate();
		assertEquals(3, SecurityField.getVersion());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToSymbolUpdate_PriceScaleFix() {
		DeltaUpdate actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.TICK_SIZE, new FDecimal("12.0"))
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, new FDecimal("15.23"))
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, new FDecimal("26.7"))
			.withToken(MoexContractField.SETTLEMENT_PRICE, new FDecimal("13.5"))
			.buildUpdate());
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, new FDecimal("12.00", 2))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("15.23", 2))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("26.70", 2))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("13.50", 2))
			.buildUpdate();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToSymbolUpdate_TickValScaleFix() {
		converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.TICK_VALUE, new FMoney("10.2301", "RUB"))
			.buildUpdate());
		
		DeltaUpdate actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.TICK_VALUE, new FMoney("10.20", "RUB"))
			.buildUpdate());
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_VALUE, new FMoney("10.2", 4, "RUB"))
			.buildUpdate();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToSymbolUpdate_InitMarginScaleFix() {
		converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.INITIAL_MARGIN, new FMoney("5621", "RUB"))
			.buildUpdate());
		
		DeltaUpdate actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.INITIAL_MARGIN, new FMoney("5407.5", "RUB"))
			.buildUpdate());
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5407.50", 2, "RUB"))
			.buildUpdate();
		assertEquals(expected, actual);
		
		actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.INITIAL_MARGIN, new FMoney("5412.753", "RUB"))
			.buildUpdate());
		
		expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5412.753", "RUB"))
			.buildUpdate();
		assertEquals(expected, actual);
	}

}
