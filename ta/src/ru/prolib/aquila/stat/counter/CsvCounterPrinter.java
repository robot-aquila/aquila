package ru.prolib.aquila.stat.counter;

import java.io.PrintStream;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.text.StrBuilder;

public class CsvCounterPrinter implements CounterPrinter {
	public static final String NOT_EXISTS = "N/A";
	@SuppressWarnings("rawtypes")
	public static final CounterFormat FORMAT_DEFAULT = new CommonFormat();
	public static final DoubleFormat FORMAT_DECIMAL0 = new DoubleFormat(0);
	public static final DoubleFormat FORMAT_DECIMAL2 = new DoubleFormat(2);
	public static final DoubleFormat FORMAT_DECIMAL4 = new DoubleFormat(4);
	
	@SuppressWarnings("rawtypes")
	private final LinkedHashMap<String, CounterFormat> columns;
	private String separator = ",";
	
	@SuppressWarnings("rawtypes")
	public CsvCounterPrinter() {
		super();
		columns = new LinkedHashMap<String, CounterFormat>();
	}
	
	@Override
	public void printHeaders(PrintStream stream) {
		stream.println(new StrBuilder()
			.appendWithSeparators(columns.keySet(), separator).toString());
	}
	
	/**
	 * Добавить колонку отчета.
	 * 
	 * Добавляет колонку с назначением дефолтного формата вывода.
	 * 
	 * @param id заголовок колонки
	 */
	public void addHeader(String id) {
		columns.put(id, FORMAT_DEFAULT);
	}
	
	/**
	 * Добавить колонку отчета.
	 * 
	 * Добавляет колонку с назначением указанного формата вывода.
	 * 
	 * @param id заголовок колонки
	 * @param format формат вывода
	 */
	public void addHeader(String id, CounterFormat<?> format) {
		columns.put(id, format);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void print(CounterSet counters, PrintStream stream) {
		String list[] = new String[columns.size()];
		int col = 0;
		for ( String id : columns.keySet() ) {
			try {
				list[col] = columns.get(id).format(counters.get(id));
			} catch ( CounterNotExistsException e ) {
				list[col] = NOT_EXISTS;
			}
			col ++;
		}
		stream.println(new StrBuilder().appendWithSeparators(list, separator));
	}

}
