package ru.prolib.aquila.web.utils.finam;

import java.util.HashMap;
import java.util.Map;

public class FidexpFormUtils {
	private static final Map<FidexpFileExt, String> fileExtToString;
	private static final Map<String, FidexpFileExt> stringToFileExt;
	private static final Map<FidexpPeriod, Integer> periodToId;
	private static final Map<Integer, FidexpPeriod> idToPeriod;
	private static final Map<FidexpDateFormat, Integer> dateFormatToId;
	private static final Map<Integer, FidexpDateFormat> idToDateFormat;
	private static final Map<FidexpTimeFormat, Integer> timeFormatToId;
	private static final Map<Integer, FidexpTimeFormat> idToTimeFormat;
	private static final Map<FidexpCandleTime, Integer> candleTimeToId;
	private static final Map<Integer, FidexpCandleTime> idToCandleTime;
	private static final Map<FidexpDataFormat, Integer> dataFormatToId;
	private static final Map<Integer, FidexpDataFormat> idToDataFormat;
	private static final Map<FidexpFieldSeparator, Integer> fieldSeparatorToId;
	private static final Map<Integer, FidexpFieldSeparator> idToFieldSeparator;
	private static final Map<FidexpDigitSeparator, Integer> digitSeparatorToId;
	private static final Map<Integer, FidexpDigitSeparator> idToDigitSeparator;
	
	static {
		fileExtToString = new HashMap<>();
		stringToFileExt = new HashMap<>();
		map(FidexpFileExt.CSV, ".csv");
		map(FidexpFileExt.TXT, ".txt");
		
		periodToId = new HashMap<>();
		idToPeriod = new HashMap<>();
		map(FidexpPeriod.TICKS, 1);
		map(FidexpPeriod.M1, 2);
		map(FidexpPeriod.M5, 3);
		map(FidexpPeriod.M10, 4);
		map(FidexpPeriod.M15, 5);
		map(FidexpPeriod.M30, 6);
		map(FidexpPeriod.H1, 7);
		map(FidexpPeriod.D1, 8);
		map(FidexpPeriod.W1, 9);
		map(FidexpPeriod.MONTH, 10);
		
		dateFormatToId = new HashMap<>();
		idToDateFormat = new HashMap<>();
		map(FidexpDateFormat.YYYYMMDD, 1);
		map(FidexpDateFormat.YYMMDD, 2);
		map(FidexpDateFormat.DDMMYY, 3);
		map(FidexpDateFormat.DDslashMMslashYY, 4);
		map(FidexpDateFormat.MMslashDDslashYY, 5);
		
		timeFormatToId = new HashMap<>();
		idToTimeFormat = new HashMap<>();
		map(FidexpTimeFormat.HHMMSS, 1);
		map(FidexpTimeFormat.HHMM, 2);
		map(FidexpTimeFormat.HHcolonMMcolonSS, 3);
		map(FidexpTimeFormat.HHcolonMM, 4);
		
		candleTimeToId = new HashMap<>();
		idToCandleTime = new HashMap<>();
		map(FidexpCandleTime.START_OF_CANDLE, 0);
		map(FidexpCandleTime.END_OF_CANDLE, 1);
		
		dataFormatToId = new HashMap<>();
		idToDataFormat = new HashMap<>();
		map(FidexpDataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, 1);
		map(FidexpDataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE, 2);
		map(FidexpDataFormat.TICKER_PER_DATE_TIME_CLOSE_VOL, 3);
		map(FidexpDataFormat.TICKER_PER_DATE_TIME_CLOSE, 4);
		map(FidexpDataFormat.DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, 5);
		map(FidexpDataFormat.TICKER_PER_DATE_TIME_LAST_VOL, 6);
		map(FidexpDataFormat.TICKER_DATE_TIME_LAST_VOL, 7);
		map(FidexpDataFormat.TICKER_DATE_TIME_LAST, 8);
		map(FidexpDataFormat.DATE_TIME_LAST_VOL, 9);
		map(FidexpDataFormat.DATE_TIME_LAST, 10);
		map(FidexpDataFormat.DATE_TIME_LAST_VOL_ID, 11);
		
		fieldSeparatorToId = new HashMap<>();
		idToFieldSeparator = new HashMap<>();
		map(FidexpFieldSeparator.COMMA, 1);
		map(FidexpFieldSeparator.FULL_STOP, 2);
		map(FidexpFieldSeparator.SEMICOLON, 3);
		map(FidexpFieldSeparator.TAB, 4);
		map(FidexpFieldSeparator.SPACE, 5);
		
		digitSeparatorToId = new HashMap<>();
		idToDigitSeparator = new HashMap<>();
		map(FidexpDigitSeparator.NONE, 1);
		map(FidexpDigitSeparator.FULL_STOP, 2);
		map(FidexpDigitSeparator.COMMA, 3);
		map(FidexpDigitSeparator.SPACE, 4);
		map(FidexpDigitSeparator.APOSTROPHE, 5);
	}
	
	static void map(FidexpDigitSeparator format, int id) {
		digitSeparatorToId.put(format, id);
		idToDigitSeparator.put(id,  format);
	}
	
	static void map(FidexpFieldSeparator format, int id) {
		fieldSeparatorToId.put(format, id);
		idToFieldSeparator.put(id,  format);
	}
	
	static void map(FidexpDataFormat format, int id) {
		dataFormatToId.put(format, id);
		idToDataFormat.put(id,  format);
	}
	
	static void map(FidexpCandleTime format, int id) {
		candleTimeToId.put(format, id);
		idToCandleTime.put(id,  format);
	}
	
	static void map(FidexpTimeFormat format, int id) {
		timeFormatToId.put(format, id);
		idToTimeFormat.put(id, format);
	}
	
	static void map(FidexpDateFormat format, int id) {
		dateFormatToId.put(format, id);
		idToDateFormat.put(id,  format);
	}
	
	static void map(FidexpFileExt ext, String value) {
		fileExtToString.put(ext, value);
		stringToFileExt.put(value, ext);
	}
	
	static void map(FidexpPeriod period, int id) {
		periodToId.put(period, id);
		idToPeriod.put(id,  period);
	}
	
	public String toString(int value) {
		return Integer.toString(value);
	}
	
	public String toString(FidexpFileExt ext) {
		return fileExtToString.get(ext);
	}
	
	public FidexpFileExt toFileExt(String ext) {
		return stringToFileExt.get(ext);
	}
	
	public int toId(FidexpPeriod period) {
		return periodToId.get(period);
	}
	
	public String toString(FidexpPeriod period) {
		return toString(toId(period));
	}
	
	public int toId(FidexpDateFormat format) {
		return dateFormatToId.get(format);
	}
	
	public String toString(FidexpDateFormat format) {
		return toString(toId(format));
	}
	
	public int toId(FidexpTimeFormat format) {
		return timeFormatToId.get(format);
	}
	
	public String toString(FidexpTimeFormat format) {
		return toString(toId(format));
	}
	
	public int toId(FidexpCandleTime format) {
		return candleTimeToId.get(format);
	}
	
	public String toString(FidexpCandleTime format) {
		return toString(toId(format));
	}
	
	public int toId(FidexpDataFormat format) {
		return dataFormatToId.get(format);
	}
	
	public String toString(FidexpDataFormat format) {
		return toString(toId(format));
	}
	
	public int toId(FidexpFieldSeparator format) {
		return fieldSeparatorToId.get(format);
	}
	
	public String toString(FidexpFieldSeparator format) {
		return toString(toId(format));
	}
	
	public int toId(FidexpDigitSeparator format) {
		return digitSeparatorToId.get(format);
	}
	
	public String toString(FidexpDigitSeparator format) {
		return toString(toId(format));
	}
	
}
