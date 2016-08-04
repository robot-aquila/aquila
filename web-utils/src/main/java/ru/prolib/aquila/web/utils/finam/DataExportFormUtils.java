package ru.prolib.aquila.web.utils.finam;

import java.util.HashMap;
import java.util.Map;

public class DataExportFormUtils {
	private static final Map<FileExt, String> fileExtToString;
	private static final Map<String, FileExt> stringToFileExt;
	private static final Map<Period, Integer> periodToId;
	private static final Map<Integer, Period> idToPeriod;
	private static final Map<DateFormat, Integer> dateFormatToId;
	private static final Map<Integer, DateFormat> idToDateFormat;
	private static final Map<TimeFormat, Integer> timeFormatToId;
	private static final Map<Integer, TimeFormat> idToTimeFormat;
	private static final Map<CandleTime, Integer> candleTimeToId;
	private static final Map<Integer, CandleTime> idToCandleTime;
	private static final Map<DataFormat, Integer> dataFormatToId;
	private static final Map<Integer, DataFormat> idToDataFormat;
	private static final Map<FieldSeparator, Integer> fieldSeparatorToId;
	private static final Map<Integer, FieldSeparator> idToFieldSeparator;
	private static final Map<DigitSeparator, Integer> digitSeparatorToId;
	private static final Map<Integer, DigitSeparator> idToDigitSeparator;
	
	static {
		fileExtToString = new HashMap<>();
		stringToFileExt = new HashMap<>();
		map(FileExt.CSV, ".csv");
		map(FileExt.TXT, ".txt");
		
		periodToId = new HashMap<>();
		idToPeriod = new HashMap<>();
		map(Period.TICKS, 1);
		map(Period.M1, 2);
		map(Period.M5, 3);
		map(Period.M10, 4);
		map(Period.M15, 5);
		map(Period.M30, 6);
		map(Period.H1, 7);
		map(Period.D1, 8);
		map(Period.W1, 9);
		map(Period.MONTH, 10);
		
		dateFormatToId = new HashMap<>();
		idToDateFormat = new HashMap<>();
		map(DateFormat.YYYYMMDD, 1);
		map(DateFormat.YYMMDD, 2);
		map(DateFormat.DDMMYY, 3);
		map(DateFormat.DDslashMMslashYY, 4);
		map(DateFormat.MMslashDDslashYY, 5);
		
		timeFormatToId = new HashMap<>();
		idToTimeFormat = new HashMap<>();
		map(TimeFormat.HHMMSS, 1);
		map(TimeFormat.HHMM, 2);
		map(TimeFormat.HHcolonMMcolonSS, 3);
		map(TimeFormat.HHcolonMM, 4);
		
		candleTimeToId = new HashMap<>();
		idToCandleTime = new HashMap<>();
		map(CandleTime.START_OF_CANDLE, 0);
		map(CandleTime.END_OF_CANDLE, 1);
		
		dataFormatToId = new HashMap<>();
		idToDataFormat = new HashMap<>();
		map(DataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, 1);
		map(DataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE, 2);
		map(DataFormat.TICKER_PER_DATE_TIME_CLOSE_VOL, 3);
		map(DataFormat.TICKER_PER_DATE_TIME_CLOSE, 4);
		map(DataFormat.DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL, 5);
		map(DataFormat.TICKER_PER_DATE_TIME_LAST_VOL, 6);
		map(DataFormat.TICKER_DATE_TIME_LAST_VOL, 7);
		map(DataFormat.TICKER_DATE_TIME_LAST, 8);
		map(DataFormat.DATE_TIME_LAST_VOL, 9);
		map(DataFormat.DATE_TIME_LAST, 10);
		map(DataFormat.DATE_TIME_LAST_VOL_ID, 11);
		
		fieldSeparatorToId = new HashMap<>();
		idToFieldSeparator = new HashMap<>();
		map(FieldSeparator.COMMA, 1);
		map(FieldSeparator.FULL_STOP, 2);
		map(FieldSeparator.SEMICOLON, 3);
		map(FieldSeparator.TAB, 4);
		map(FieldSeparator.SPACE, 5);
		
		digitSeparatorToId = new HashMap<>();
		idToDigitSeparator = new HashMap<>();
		map(DigitSeparator.NONE, 1);
		map(DigitSeparator.FULL_STOP, 2);
		map(DigitSeparator.COMMA, 3);
		map(DigitSeparator.SPACE, 4);
		map(DigitSeparator.APOSTROPHE, 5);
	}
	
	static void map(DigitSeparator format, int id) {
		digitSeparatorToId.put(format, id);
		idToDigitSeparator.put(id,  format);
	}
	
	static void map(FieldSeparator format, int id) {
		fieldSeparatorToId.put(format, id);
		idToFieldSeparator.put(id,  format);
	}
	
	static void map(DataFormat format, int id) {
		dataFormatToId.put(format, id);
		idToDataFormat.put(id,  format);
	}
	
	static void map(CandleTime format, int id) {
		candleTimeToId.put(format, id);
		idToCandleTime.put(id,  format);
	}
	
	static void map(TimeFormat format, int id) {
		timeFormatToId.put(format, id);
		idToTimeFormat.put(id, format);
	}
	
	static void map(DateFormat format, int id) {
		dateFormatToId.put(format, id);
		idToDateFormat.put(id,  format);
	}
	
	static void map(FileExt ext, String value) {
		fileExtToString.put(ext, value);
		stringToFileExt.put(value, ext);
	}
	
	static void map(Period period, int id) {
		periodToId.put(period, id);
		idToPeriod.put(id,  period);
	}
	
	public String toString(int value) {
		return Integer.toString(value);
	}
	
	public String toString(FileExt ext) {
		return fileExtToString.get(ext);
	}
	
	public FileExt toFileExt(String ext) {
		return stringToFileExt.get(ext);
	}
	
	public int toId(Period period) {
		return periodToId.get(period);
	}
	
	public String toString(Period period) {
		return toString(toId(period));
	}
	
	public int toId(DateFormat format) {
		return dateFormatToId.get(format);
	}
	
	public String toString(DateFormat format) {
		return toString(toId(format));
	}
	
	public int toId(TimeFormat format) {
		return timeFormatToId.get(format);
	}
	
	public String toString(TimeFormat format) {
		return toString(toId(format));
	}
	
	public int toId(CandleTime format) {
		return candleTimeToId.get(format);
	}
	
	public String toString(CandleTime format) {
		return toString(toId(format));
	}
	
	public int toId(DataFormat format) {
		return dataFormatToId.get(format);
	}
	
	public String toString(DataFormat format) {
		return toString(toId(format));
	}
	
	public int toId(FieldSeparator format) {
		return fieldSeparatorToId.get(format);
	}
	
	public String toString(FieldSeparator format) {
		return toString(toId(format));
	}
	
	public int toId(DigitSeparator format) {
		return digitSeparatorToId.get(format);
	}
	
	public String toString(DigitSeparator format) {
		return toString(toId(format));
	}
	
}
