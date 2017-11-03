package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.data.DataFormatException;

public class MoexContractPtmlConverterTest {
	private static final String RUB = CDecimalBD.RUB;
	private MoexContractPtmlConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new MoexContractPtmlConverter();
	}

	@Test
	public void testToString() throws Exception {
		Map<Integer, Object> fix = new HashMap<>();
		fix.put(MoexContractField.CONTRACT_DESCR, "foobar");
		fix.put(MoexContractField.SYMBOL, "MSFT");
		fix.put(MoexContractField.SYMBOL_CODE, "MS01");
		fix.put(MoexContractField.QUOTATION, "POINTS");
		fix.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		fix.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		fix.put(MoexContractField.TYPE, "FUTURES");
		fix.put(MoexContractField.DELIVERY, LocalDate.of(1997, 8, 16));
		fix.put(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2008, 10, 5));
		fix.put(MoexContractField.INITIAL_MARGIN_DATE, LocalDate.of(2016, 8, 8));
		fix.put(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2045,  12, 10));
		fix.put(MoexContractField.FX_EVENING_CLEARING, LocalTime.of(18, 45));
		fix.put(MoexContractField.FX_INTRADAY_CLEARING, LocalTime.of(13, 50));
		fix.put(MoexContractField.LOT_SIZE, CDecimalBD.of(10L));
		fix.put(MoexContractField.EXERCISE_FEE, CDecimalBD.of("5.15", RUB));
		fix.put(MoexContractField.FEE, CDecimalBD.of("3.19", RUB));
		fix.put(MoexContractField.INTRADAY_FEE, CDecimalBD.of("15.32", RUB));
		fix.put(MoexContractField.NEGOTIATION_FEE, CDecimalBD.of("1.02", RUB));
		fix.put(MoexContractField.INITIAL_MARGIN, CDecimalBD.of("12450.0", RUB));
		fix.put(MoexContractField.TICK_VALUE, CDecimalBD.of("2.13", RUB));
		fix.put(MoexContractField.TICK_SIZE, CDecimalBD.of("0.01"));
		fix.put(MoexContractField.SETTLEMENT_PRICE, CDecimalBD.of("1248.05"));
		fix.put(MoexContractField.LOWER_PRICE_LIMIT, CDecimalBD.of("95.14"));
		fix.put(MoexContractField.UPPER_PRICE_LIMIT, CDecimalBD.of("120.83"));
		
		Map<Integer, String> actual = new HashMap<>();
		Iterator<Map.Entry<Integer, Object>> it = fix.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, Object> dummy = it.next();
			int token = dummy.getKey();
			actual.put(token, converter.toString(token, dummy.getValue()));
		}
		
		Map<Integer, String> expected = new HashMap<>();
		expected.put(MoexContractField.CONTRACT_DESCR, "foobar");
		expected.put(MoexContractField.SYMBOL, "MSFT");
		expected.put(MoexContractField.SYMBOL_CODE, "MS01");
		expected.put(MoexContractField.QUOTATION, "POINTS");
		expected.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		expected.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		expected.put(MoexContractField.TYPE, "FUTURES");
		expected.put(MoexContractField.DELIVERY, "1997-08-16");
		expected.put(MoexContractField.FIRST_TRADING_DAY, "2008-10-05");
		expected.put(MoexContractField.INITIAL_MARGIN_DATE, "2016-08-08");
		expected.put(MoexContractField.LAST_TRADING_DAY, "2045-12-10");
		expected.put(MoexContractField.FX_EVENING_CLEARING, "18:45");
		expected.put(MoexContractField.FX_INTRADAY_CLEARING, "13:50");
		expected.put(MoexContractField.LOT_SIZE, "10");
		expected.put(MoexContractField.EXERCISE_FEE, "5.15");
		expected.put(MoexContractField.FEE, "3.19");
		expected.put(MoexContractField.INTRADAY_FEE, "15.32");
		expected.put(MoexContractField.NEGOTIATION_FEE, "1.02");
		expected.put(MoexContractField.INITIAL_MARGIN, "12450.0");
		expected.put(MoexContractField.TICK_VALUE, "2.13");
		expected.put(MoexContractField.TICK_SIZE, "0.01");
		expected.put(MoexContractField.SETTLEMENT_PRICE, "1248.05");
		expected.put(MoexContractField.LOWER_PRICE_LIMIT, "95.14");
		expected.put(MoexContractField.UPPER_PRICE_LIMIT, "120.83");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToObject() throws Exception {
		Map<Integer, String> fix = new HashMap<>();
		fix.put(MoexContractField.CONTRACT_DESCR, "foobar");
		fix.put(MoexContractField.SYMBOL, "MSFT");
		fix.put(MoexContractField.SYMBOL_CODE, "MS01");
		fix.put(MoexContractField.QUOTATION, "POINTS");
		fix.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		fix.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		fix.put(MoexContractField.TYPE, "FUTURES");
		fix.put(MoexContractField.DELIVERY, "1997-08-16");
		fix.put(MoexContractField.FIRST_TRADING_DAY, "2008-10-05");
		fix.put(MoexContractField.INITIAL_MARGIN_DATE, "2016-08-08");
		fix.put(MoexContractField.LAST_TRADING_DAY, "2045-12-10");
		fix.put(MoexContractField.FX_EVENING_CLEARING, "18:45");
		fix.put(MoexContractField.FX_INTRADAY_CLEARING, "13:50");
		fix.put(MoexContractField.LOT_SIZE, "10");
		fix.put(MoexContractField.EXERCISE_FEE, "5.15");
		fix.put(MoexContractField.FEE, "3.19");
		fix.put(MoexContractField.INTRADAY_FEE, "15.32");
		fix.put(MoexContractField.NEGOTIATION_FEE, "1.02");
		fix.put(MoexContractField.INITIAL_MARGIN, "12450.0");
		fix.put(MoexContractField.TICK_VALUE, "2.13");
		fix.put(MoexContractField.TICK_SIZE, "0.01");
		fix.put(MoexContractField.SETTLEMENT_PRICE, "1248.05");
		fix.put(MoexContractField.LOWER_PRICE_LIMIT, "95.14");
		fix.put(MoexContractField.UPPER_PRICE_LIMIT, "120.83");

		Map<Integer, Object> actual = new HashMap<>();
		Iterator<Map.Entry<Integer, String>> it = fix.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, String> dummy = it.next();
			int token = dummy.getKey();
			actual.put(token, converter.toObject(token, dummy.getValue()));
		}

		Map<Integer, Object> expected = new HashMap<>();
		expected.put(MoexContractField.CONTRACT_DESCR, "foobar");
		expected.put(MoexContractField.SYMBOL, "MSFT");
		expected.put(MoexContractField.SYMBOL_CODE, "MS01");
		expected.put(MoexContractField.QUOTATION, "POINTS");
		expected.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		expected.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		expected.put(MoexContractField.TYPE, "FUTURES");
		expected.put(MoexContractField.DELIVERY, LocalDate.of(1997, 8, 16));
		expected.put(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2008, 10, 5));
		expected.put(MoexContractField.INITIAL_MARGIN_DATE, LocalDate.of(2016, 8, 8));
		expected.put(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2045,  12, 10));
		expected.put(MoexContractField.FX_EVENING_CLEARING, LocalTime.of(18, 45));
		expected.put(MoexContractField.FX_INTRADAY_CLEARING, LocalTime.of(13, 50));
		expected.put(MoexContractField.LOT_SIZE, CDecimalBD.of(10L));
		expected.put(MoexContractField.EXERCISE_FEE, CDecimalBD.of("5.15", RUB));
		expected.put(MoexContractField.FEE, CDecimalBD.of("3.19", RUB));
		expected.put(MoexContractField.INTRADAY_FEE, CDecimalBD.of("15.32", RUB));
		expected.put(MoexContractField.NEGOTIATION_FEE, CDecimalBD.of("1.02", RUB));
		expected.put(MoexContractField.INITIAL_MARGIN, CDecimalBD.of("12450", RUB)); // must be trimmed
		expected.put(MoexContractField.TICK_VALUE, CDecimalBD.of("2.13", RUB));
		expected.put(MoexContractField.TICK_SIZE, CDecimalBD.of("0.01"));
		expected.put(MoexContractField.SETTLEMENT_PRICE, CDecimalBD.of("1248.05"));
		expected.put(MoexContractField.LOWER_PRICE_LIMIT, CDecimalBD.of("95.14"));
		expected.put(MoexContractField.UPPER_PRICE_LIMIT, CDecimalBD.of("120.83"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToObject_TickSizeAndValue_ScientificNotation() throws Exception {
		assertEquals(CDecimalBD.of("0.0001"), converter.toObject(MoexContractField.TICK_SIZE, "1.0E-4"));
		assertEquals(CDecimalBD.of("0.015", RUB), converter.toObject(MoexContractField.TICK_VALUE, "15.0E-3"));
	}
	
	@Test
	public void testToObject_TickSizeAndValue_TrailingZeroes() throws Exception {
		assertEquals(CDecimalBD.of("0.01"), converter.toObject(MoexContractField.TICK_SIZE, "0.01000"));
		assertEquals(CDecimalBD.ofRUB2("0.15"), converter.toObject(MoexContractField.TICK_VALUE, "0.1500"));
	}

	@Test
	public void testToObject_TickSizeAndValue_NoDecimals() throws Exception {
		assertEquals(CDecimalBD.of(10L), converter.toObject(MoexContractField.TICK_SIZE, "10.0"));
		assertEquals(CDecimalBD.of("1", RUB), converter.toObject(MoexContractField.TICK_VALUE, "1.0000"));
	}

	@Test (expected=DataFormatException.class)
	public void testToObject_TickSize_FormatError() throws Exception {
		converter.toObject(MoexContractField.TICK_SIZE, "foo");
	}

	@Test (expected=DataFormatException.class)
	public void testToObject_TickValue_FormatError() throws Exception {
		converter.toObject(MoexContractField.TICK_VALUE, "bar");
	}
	
	@Test
	public void testToObject_MoexDateFormat_FirstTradingDay() throws Exception {
		assertEquals(LocalDate.of(2015, 12, 25),
				converter.toObject(MoexContractField.FIRST_TRADING_DAY, "25.12.2015"));
	}
	
	@Test
	public void testToObject_MoexDateFormat_LastTradingDay() throws Exception {
		assertEquals(LocalDate.of(2017, 12, 21),
				converter.toObject(MoexContractField.LAST_TRADING_DAY, "21.12.2017"));
	}
	
	@Test
	public void testToObject_MoexDateFormat_Delivery() throws Exception {
		assertEquals(LocalDate.of(2017, 12, 21),
				converter.toObject(MoexContractField.DELIVERY, "21.12.2017"));
	}
	
	@Test
	public void testToObject_MoexDateFormat_InitialMarginDate() throws Exception {
		assertEquals(LocalDate.of(2017, 11, 2),
				converter.toObject(MoexContractField.INITIAL_MARGIN_DATE, "02.11.2017"));
	}
	
	@Test
	public void testToObject_MoexTimeFormat_FX_IntradayClearing() throws Exception {
		assertEquals(LocalTime.of(13, 45),
				converter.toObject(MoexContractField.FX_INTRADAY_CLEARING, "13:45 Moscow time"));
	}
	
	@Test
	public void testToObject_MoexTimeFormat_FX_EveningClearing() throws Exception {
		assertEquals(LocalTime.of(18, 30),
				converter.toObject(MoexContractField.FX_EVENING_CLEARING, "18:30 Moscow time"));
	}

}
