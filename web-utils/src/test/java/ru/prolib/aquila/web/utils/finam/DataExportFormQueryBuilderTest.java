package ru.prolib.aquila.web.utils.finam;

import static org.junit.Assert.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Before;
import org.junit.Test;

public class DataExportFormQueryBuilderTest {
	private DataExportFormQueryBuilder builder;
	private DataExportParams params;
	private URI formAction;

	@Before
	public void setUp() throws Exception {
		builder = new DataExportFormQueryBuilder();
		params = new DataExportParams();
		formAction = new URI("https://localhost/somedir/d2/output9.txt");
	}
	
	private Map<String, String> parseQuery(URI uri) {
		Map<String, String> result = new HashMap<>();
		for ( NameValuePair pair : URLEncodedUtils.parse(uri, "UTF-8") ) {
			result.put(pair.getName(), pair.getValue());
		}
		return result;
	}
	
	private void assertQueryParam(String param, String value) throws Exception {
		Map<String, String> query = parseQuery(builder.buildQuery(formAction, params));
		assertEquals("Query param [" + param + "] mismatch: ", value, query.get(param));
	}
	
	@Test
	public void testBuildQuery_Defaults() throws Exception {
		params.setFileName("rts-combined");
		params.setDateFrom(LocalDate.of(1996, 8, 20));
		params.setDateTo(LocalDate.of(1996, 9, 1));
		URI expected = new URI("https://localhost/somedir/d2/rts-combined.csv?"
			+ "market=14&em=17455&code=RTS&apply=0&df=20&mf=7&yf=1996&"
			+ "from=20.08.1996&dt=1&mt=8&yt=1996&to=01.09.1996&p=1&"
			+ "f=rts-combined&e=.csv&cn=RTS&dtf=1&tmf=1&MSOR=0&"
			+ "mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1");

		assertEquals(expected, builder.buildQuery(formAction, params));
	}
	
	@Test
	public void testBuildQuery_FormActionUsage() throws Exception {
		formAction = new URI("http://192.168.1.1/testbest.txt");
		assertEquals("/RTS.csv", builder.buildQuery(formAction, params).getPath());
		
		formAction = new URI("ftp://zulu24.com/d2/");
		assertEquals("/d2/RTS.csv", builder.buildQuery(formAction, params).getPath());
		
		formAction = new URI("http://localhost");
		assertEquals("/RTS.csv", builder.buildQuery(formAction, params).getPath());
	}
	
	@Test
	public void testBuildQuery_Period() throws Exception {
		Map<Period, String> expected = new HashMap<>();
		expected.put(Period.TICKS, "1");
		expected.put(Period.M1, "2");
		expected.put(Period.M5, "3");		
		expected.put(Period.M10, "4");
		expected.put(Period.M15, "5");
		expected.put(Period.M30, "6");
		expected.put(Period.H1, "7");
		expected.put(Period.D1, "8");
		expected.put(Period.W1, "9");
		expected.put(Period.MONTH, "10");
		
		assertEquals(expected.size(), Period.values().length);
		Iterator<Map.Entry<Period, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Period, String> dummy = it.next();
			params.setPeriod(dummy.getKey());
			assertQueryParam("p", dummy.getValue());
		}
	}

	@Test
	public void testBuildQuery_FileExt() throws Exception {
		params.setFileExt(FileExt.CSV);
		assertQueryParam("e", ".csv");
		
		params.setFileExt(FileExt.TXT);
		assertQueryParam("e", ".txt");
	}
	
	@Test
	public void testBuildQuery_DateFormat() throws Exception {
		Map<DateFormat, String> expected = new HashMap<>();
		expected.put(DateFormat.YYYYMMDD, "1");
		expected.put(DateFormat.YYMMDD, "2");
		expected.put(DateFormat.DDMMYY, "3");
		expected.put(DateFormat.DDslashMMslashYY, "4");
		expected.put(DateFormat.MMslashDDslashYY, "5");
		
		assertEquals(expected.size(), DateFormat.values().length);
		Iterator<Map.Entry<DateFormat, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<DateFormat, String> dummy = it.next();
			params.setDateFormat(dummy.getKey());
			assertQueryParam("dtf", dummy.getValue());
		}
	}
	
	@Test
	public void testBuildQuery_TimeFormat() throws Exception {
		Map<TimeFormat, String> expected = new HashMap<>();
		expected.put(TimeFormat.HHMMSS, "1");
		expected.put(TimeFormat.HHMM, "2");
		expected.put(TimeFormat.HHcolonMMcolonSS, "3");
		expected.put(TimeFormat.HHcolonMM, "4");
		
		assertEquals(expected.size(), TimeFormat.values().length);
		Iterator<Map.Entry<TimeFormat, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<TimeFormat, String> dummy = it.next();
			params.setTimeFormat(dummy.getKey());
			assertQueryParam("tmf", dummy.getValue());
		}
	}
	
	@Test
	public void testBuildQuery_CandleTime() throws Exception {
		params.setCandleTime(CandleTime.START_OF_CANDLE);
		assertQueryParam("MSOR", "0");
		
		params.setCandleTime(CandleTime.END_OF_CANDLE);
		assertQueryParam("MSOR", "1");
	}
	
	@Test
	public void testBuildQuery_UseMoscowTime() throws Exception {
		params.setUseMoscowTime(true);
		assertQueryParam("mstime", "on");
		assertQueryParam("mstimever", "1");
		
		params.setUseMoscowTime(false);
		assertQueryParam("mstime", null);
		assertQueryParam("mstimever", "0");
	}
	
	@Test
	public void testBuildQuery_FieldSeparator() throws Exception {
		Map<FieldSeparator, String> expected = new HashMap<>();
		expected.put(FieldSeparator.COMMA, "1");
		expected.put(FieldSeparator.FULL_STOP, "2");
		expected.put(FieldSeparator.SEMICOLON, "3");
		expected.put(FieldSeparator.TAB, "4");
		expected.put(FieldSeparator.SPACE, "5");
		
		assertEquals(expected.size(), FieldSeparator.values().length);
		Iterator<Map.Entry<FieldSeparator, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<FieldSeparator, String> dummy = it.next();
			params.setFieldSeparator(dummy.getKey());
			assertQueryParam("sep", dummy.getValue());
		}
	}
	
	@Test
	public void testBuildQuery_DigitSeparator() throws Exception {
		Map<DigitSeparator, String> expected = new HashMap<>();
		expected.put(DigitSeparator.NONE, "1");
		expected.put(DigitSeparator.FULL_STOP, "2");
		expected.put(DigitSeparator.COMMA, "3");
		expected.put(DigitSeparator.SPACE, "4");
		expected.put(DigitSeparator.APOSTROPHE, "5");
		
		assertEquals(expected.size(), DigitSeparator.values().length);
		Iterator<Map.Entry<DigitSeparator, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<DigitSeparator, String> dummy = it.next();
			params.setDigitSeparator(dummy.getKey());
			assertQueryParam("sep2", dummy.getValue());
		}
	}

	@Test
	public void testBuildQuery_DataFormat() throws Exception {
		Map<DataFormat, String> expected = new HashMap<>();
		expected.put(DataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, "1");
		expected.put(DataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE, "2");
		expected.put(DataFormat.TICKER_PER_DATE_TIME_CLOSE_VOL, "3");
		expected.put(DataFormat.TICKER_PER_DATE_TIME_CLOSE, "4");
		expected.put(DataFormat.DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, "5");
		expected.put(DataFormat.TICKER_PER_DATE_TIME_LAST_VOL, "6");
		expected.put(DataFormat.TICKER_DATE_TIME_LAST_VOL, "7");
		expected.put(DataFormat.TICKER_DATE_TIME_LAST, "8");
		expected.put(DataFormat.DATE_TIME_LAST_VOL, "9");
		expected.put(DataFormat.DATE_TIME_LAST, "10");
		expected.put(DataFormat.DATE_TIME_LAST_VOL_ID, "11");
		
		assertEquals(expected.size(), DataFormat.values().length);
		Iterator<Map.Entry<DataFormat, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<DataFormat, String> dummy = it.next();
			params.setDataFormat(dummy.getKey());
			assertQueryParam("datf", dummy.getValue());
		}
	}

	@Test
	public void testBuildQuery_AddHeader() throws Exception {
		params.setAddHeader(true);
		assertQueryParam("at", "1");
		
		params.setAddHeader(false);
		assertQueryParam("at", null);
	}
	
	@Test
	public void testBuildQuery_FillEmptyPeriods() throws Exception {
		params.setFillEmptyPeriods(true);
		assertQueryParam("fsp", "1");
		
		params.setFillEmptyPeriods(false);
		assertQueryParam("fsp", null);
	}

}
