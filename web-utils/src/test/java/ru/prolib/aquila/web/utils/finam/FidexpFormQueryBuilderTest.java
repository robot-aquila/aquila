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

public class FidexpFormQueryBuilderTest {
	private FidexpFormQueryBuilder builder;
	private FidexpFormParams params;
	private URI formAction;

	@Before
	public void setUp() throws Exception {
		builder = new FidexpFormQueryBuilder();
		params = new FidexpFormParams();
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
		Map<FidexpPeriod, String> expected = new HashMap<>();
		expected.put(FidexpPeriod.TICKS, "1");
		expected.put(FidexpPeriod.M1, "2");
		expected.put(FidexpPeriod.M5, "3");		
		expected.put(FidexpPeriod.M10, "4");
		expected.put(FidexpPeriod.M15, "5");
		expected.put(FidexpPeriod.M30, "6");
		expected.put(FidexpPeriod.H1, "7");
		expected.put(FidexpPeriod.D1, "8");
		expected.put(FidexpPeriod.W1, "9");
		expected.put(FidexpPeriod.MONTH, "10");
		
		assertEquals(expected.size(), FidexpPeriod.values().length);
		Iterator<Map.Entry<FidexpPeriod, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<FidexpPeriod, String> dummy = it.next();
			params.setPeriod(dummy.getKey());
			assertQueryParam("p", dummy.getValue());
		}
	}

	@Test
	public void testBuildQuery_FileExt() throws Exception {
		params.setFileExt(FidexpFileExt.CSV);
		assertQueryParam("e", ".csv");
		
		params.setFileExt(FidexpFileExt.TXT);
		assertQueryParam("e", ".txt");
	}
	
	@Test
	public void testBuildQuery_DateFormat() throws Exception {
		Map<FidexpDateFormat, String> expected = new HashMap<>();
		expected.put(FidexpDateFormat.YYYYMMDD, "1");
		expected.put(FidexpDateFormat.YYMMDD, "2");
		expected.put(FidexpDateFormat.DDMMYY, "3");
		expected.put(FidexpDateFormat.DDslashMMslashYY, "4");
		expected.put(FidexpDateFormat.MMslashDDslashYY, "5");
		
		assertEquals(expected.size(), FidexpDateFormat.values().length);
		Iterator<Map.Entry<FidexpDateFormat, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<FidexpDateFormat, String> dummy = it.next();
			params.setDateFormat(dummy.getKey());
			assertQueryParam("dtf", dummy.getValue());
		}
	}
	
	@Test
	public void testBuildQuery_TimeFormat() throws Exception {
		Map<FidexpTimeFormat, String> expected = new HashMap<>();
		expected.put(FidexpTimeFormat.HHMMSS, "1");
		expected.put(FidexpTimeFormat.HHMM, "2");
		expected.put(FidexpTimeFormat.HHcolonMMcolonSS, "3");
		expected.put(FidexpTimeFormat.HHcolonMM, "4");
		
		assertEquals(expected.size(), FidexpTimeFormat.values().length);
		Iterator<Map.Entry<FidexpTimeFormat, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<FidexpTimeFormat, String> dummy = it.next();
			params.setTimeFormat(dummy.getKey());
			assertQueryParam("tmf", dummy.getValue());
		}
	}
	
	@Test
	public void testBuildQuery_CandleTime() throws Exception {
		params.setCandleTime(FidexpCandleTime.START_OF_CANDLE);
		assertQueryParam("MSOR", "0");
		
		params.setCandleTime(FidexpCandleTime.END_OF_CANDLE);
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
		Map<FidexpFieldSeparator, String> expected = new HashMap<>();
		expected.put(FidexpFieldSeparator.COMMA, "1");
		expected.put(FidexpFieldSeparator.FULL_STOP, "2");
		expected.put(FidexpFieldSeparator.SEMICOLON, "3");
		expected.put(FidexpFieldSeparator.TAB, "4");
		expected.put(FidexpFieldSeparator.SPACE, "5");
		
		assertEquals(expected.size(), FidexpFieldSeparator.values().length);
		Iterator<Map.Entry<FidexpFieldSeparator, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<FidexpFieldSeparator, String> dummy = it.next();
			params.setFieldSeparator(dummy.getKey());
			assertQueryParam("sep", dummy.getValue());
		}
	}
	
	@Test
	public void testBuildQuery_DigitSeparator() throws Exception {
		Map<FidexpDigitSeparator, String> expected = new HashMap<>();
		expected.put(FidexpDigitSeparator.NONE, "1");
		expected.put(FidexpDigitSeparator.FULL_STOP, "2");
		expected.put(FidexpDigitSeparator.COMMA, "3");
		expected.put(FidexpDigitSeparator.SPACE, "4");
		expected.put(FidexpDigitSeparator.APOSTROPHE, "5");
		
		assertEquals(expected.size(), FidexpDigitSeparator.values().length);
		Iterator<Map.Entry<FidexpDigitSeparator, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<FidexpDigitSeparator, String> dummy = it.next();
			params.setDigitSeparator(dummy.getKey());
			assertQueryParam("sep2", dummy.getValue());
		}
	}

	@Test
	public void testBuildQuery_DataFormat() throws Exception {
		Map<FidexpDataFormat, String> expected = new HashMap<>();
		expected.put(FidexpDataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, "1");
		expected.put(FidexpDataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE, "2");
		expected.put(FidexpDataFormat.TICKER_PER_DATE_TIME_CLOSE_VOL, "3");
		expected.put(FidexpDataFormat.TICKER_PER_DATE_TIME_CLOSE, "4");
		expected.put(FidexpDataFormat.DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, "5");
		expected.put(FidexpDataFormat.TICKER_PER_DATE_TIME_LAST_VOL, "6");
		expected.put(FidexpDataFormat.TICKER_DATE_TIME_LAST_VOL, "7");
		expected.put(FidexpDataFormat.TICKER_DATE_TIME_LAST, "8");
		expected.put(FidexpDataFormat.DATE_TIME_LAST_VOL, "9");
		expected.put(FidexpDataFormat.DATE_TIME_LAST, "10");
		expected.put(FidexpDataFormat.DATE_TIME_LAST_VOL_ID, "11");
		
		assertEquals(expected.size(), FidexpDataFormat.values().length);
		Iterator<Map.Entry<FidexpDataFormat, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<FidexpDataFormat, String> dummy = it.next();
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
