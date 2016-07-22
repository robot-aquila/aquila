package ru.prolib.aquila.finam.tools.web;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class DataExportParamsTest {
	private DataExportParams params;

	@Before
	public void setUp() throws Exception {
		params = new DataExportParams();
	}
	
	@Test
	public void testDefaults() {
		assertEquals(14, params.getMarketID());
		assertEquals(17455, params.getQuoteID());
		assertEquals(LocalDate.now(), params.getDateFrom());
		assertEquals(LocalDate.now(), params.getDateTo());
		assertEquals(Period.M5, params.getPeriod());
		assertEquals("RTS", params.getContractName());
		assertEquals("RTS", params.getFileName());
		assertEquals(FileExt.CSV, params.getFileExt());
		assertEquals(DateFormat.YYYYMMDD, params.getDateFormat());
		assertEquals(TimeFormat.HHMMSS, params.getTimeFormat());
		assertEquals(CandleTime.START_OF_CANDLE, params.getCandleTime());
		assertTrue(params.getUseMoscowTime());
		assertEquals(FieldSeparator.COMMA, params.getFieldSeparator());
		assertEquals(DigitSeparator.NONE, params.getDigitSeparator());
		assertEquals(DataFormat.DATE_TIME_LAST_VOL, params.getDataFormat());
		assertTrue(params.getAddHeader());
		assertFalse(params.getFillEmptyPeriods());
	}
	
	@Test
	public void testSetMarketID() {
		assertSame(params, params.setMarketId(836));
		assertEquals(836, params.getMarketID());
	}
	
	@Test
	public void testSetQuoteID() {
		assertSame(params, params.setQuoteID(77162));
		assertEquals(77162, params.getQuoteID());
	}
	
	@Test
	public void testSetDateFrom() {
		assertSame(params, params.setDateFrom(LocalDate.of(1998, 12, 5)));
		assertEquals(LocalDate.of(1998,  12, 5), params.getDateFrom());
	}
	
	@Test
	public void testSetDateTo() {
		assertSame(params, params.setDateTo(LocalDate.of(2002, 5, 18)));
		assertEquals(LocalDate.of(2002,  5, 18), params.getDateTo());
	}

	@Test
	public void testSetPeriod() {
		assertSame(params, params.setPeriod(Period.M10));
		assertEquals(Period.M10, params.getPeriod());
	}

	@Test
	public void testSetContractName() {
		assertSame(params, params.setContractName("zulu24"));
		assertEquals("zulu24", params.getContractName());
	}

	@Test
	public void testSetFileName() {
		assertSame(params, params.setFileName("foobar"));
		assertEquals("foobar", params.getFileName());
	}

	@Test
	public void testSetFileExt() {
		assertSame(params, params.setFileExt(FileExt.TXT));
		assertEquals(FileExt.TXT, params.getFileExt());
	}

	@Test
	public void testSetDateFormat() {
		assertSame(params, params.setDateFormat(DateFormat.DDslashMMslashYY));
		assertEquals(DateFormat.DDslashMMslashYY, params.getDateFormat());
	}

	@Test
	public void testSetTimeFormat() {
		assertSame(params, params.setTimeFormat(TimeFormat.HHcolonMMcolonSS));
		assertEquals(TimeFormat.HHcolonMMcolonSS, params.getTimeFormat());
	}

	@Test
	public void testSetCandleTime() {
		assertSame(params, params.setCandleTime(CandleTime.END_OF_CANDLE));
		assertEquals(CandleTime.END_OF_CANDLE, params.getCandleTime());
	}

	@Test
	public void testSetUseMoscowTime() {
		assertSame(params, params.setUseMoscowTime(false));
		assertFalse(params.getUseMoscowTime());
	}

	@Test
	public void testSetFieldSeparator() {
		assertSame(params, params.setFieldSeparator(FieldSeparator.SPACE));
		assertEquals(FieldSeparator.SPACE, params.getFieldSeparator());
	}

	@Test
	public void testSetDigitSeparator() {
		assertSame(params, params.setDigitSeparator(DigitSeparator.FULL_STOP));
		assertEquals(DigitSeparator.FULL_STOP, params.getDigitSeparator());
	}

	@Test
	public void testSetDataFormat() {
		assertSame(params, params.setDataFormat(DataFormat.DATE_TIME_LAST));
		assertEquals(DataFormat.DATE_TIME_LAST, params.getDataFormat());
	}

	@Test
	public void testSetAddHeader() {
		assertSame(params, params.setAddHeader(false));
		assertFalse(params.getAddHeader());
	}

	@Test
	public void testSetFillEmptyPeriods() {
		assertSame(params, params.setFillEmptyPeriods(true));
		assertTrue(params.getFillEmptyPeriods());
	}

}
