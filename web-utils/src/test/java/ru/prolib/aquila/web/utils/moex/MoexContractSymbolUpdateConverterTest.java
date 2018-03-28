package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;

public class MoexContractSymbolUpdateConverterTest {
	
	static Instant TMSK(String timeString) {
		return ZonedDateTime.of(LocalDateTime.parse(timeString), ZoneId.of("Europe/Moscow")).toInstant();
	}
	
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
			.withToken(MoexContractField.TYPE, "FUTURES")
			.withToken(MoexContractField.SETTLEMENT, "CASH_SETTLED")
			.withToken(MoexContractField.LOT_SIZE, CDecimalBD.of(1000L))
			.withToken(MoexContractField.QUOTATION, "RUR")
			.withToken(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2015, 6, 17))
			.withToken(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2016, 9, 15))
			.withToken(MoexContractField.DELIVERY, LocalDate.of(2016, 9, 15))
			.withToken(MoexContractField.TICK_SIZE, CDecimalBD.of("0.05"))
			.withToken(MoexContractField.TICK_VALUE, CDecimalBD.of("0.5", "RUB"))
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, CDecimalBD.of(70469L))
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, CDecimalBD.of(76343L))
			.withToken(MoexContractField.SETTLEMENT_PRICE, CDecimalBD.of(73406L))
			.withToken(MoexContractField.FEE, CDecimalBD.of("1", "RUB"))
			.withToken(MoexContractField.INTRADAY_FEE, CDecimalBD.of("0.5", "RUB"))
			.withToken(MoexContractField.NEGOTIATION_FEE, CDecimalBD.of("1", "RUB"))
			.withToken(MoexContractField.EXERCISE_FEE, CDecimalBD.of("1", "RUB"))
			.withToken(MoexContractField.INITIAL_MARGIN, CDecimalBD.of("5874", "RUB"))
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
			.withToken(SecurityField.LOT_SIZE, CDecimalBD.of(1000L))
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.05"))
			.withToken(SecurityField.TICK_VALUE, CDecimalBD.of("0.50", "RUB"))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.of("5874.00", "RUB"))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("73406.00"))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of("70469.00"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of("76343.00"))
			.withToken(SecurityField.EXPIRATION_TIME, TMSK("2016-09-15T00:00:00"))
			.buildUpdate();
		assertEquals(5, SecurityField.getVersion());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToSymbolUpdate_PriceScaleFix() {
		DeltaUpdate actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.TICK_SIZE, CDecimalBD.of("12.0"))
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, CDecimalBD.of("15.23"))
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, CDecimalBD.of("26.7"))
			.withToken(MoexContractField.SETTLEMENT_PRICE, CDecimalBD.of("13.5"))
			.buildUpdate());
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("12.00"))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of("15.23"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of("26.70"))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("13.50"))
			.buildUpdate();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToSymbolUpdate_TickValScaleFix() {
		converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.TICK_VALUE, CDecimalBD.of("10.2301", "RUB"))
			.buildUpdate());
		
		DeltaUpdate actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.TICK_VALUE, CDecimalBD.of("10.20", "RUB"))
			.buildUpdate());
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_VALUE, CDecimalBD.of("10.2000", "RUB"))
			.buildUpdate();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToSymbolUpdate_InitMarginScaleFix() {
		converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.INITIAL_MARGIN, CDecimalBD.of("5621", "RUB"))
			.buildUpdate());
		
		DeltaUpdate actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.INITIAL_MARGIN, CDecimalBD.of("5407.5", "RUB"))
			.buildUpdate());
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.of("5407.50", "RUB"))
			.buildUpdate();
		assertEquals(expected, actual);
		
		actual = converter.toSymbolUpdate(new DeltaUpdateBuilder()
			.withToken(MoexContractField.INITIAL_MARGIN, CDecimalBD.of("5412.753", "RUB"))
			.buildUpdate());
		
		expected = new DeltaUpdateBuilder()
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.of("5412.753", "RUB"))
			.buildUpdate();
		assertEquals(expected, actual);
	}

}
