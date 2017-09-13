package ru.prolib.aquila.core.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class IdUtils {
	public static final String SEPARATOR = "-";
	private static final DateTimeFormatter dateFormat;
	private static final IdUtils instance;
	
	static {
		dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		instance = new IdUtils();
	}
	
	public static IdUtils getInstance() {
		return instance;
	}

	private final StrCoder coder;
	
	public IdUtils(StrCoder coder) {
		super();
		this.coder = coder; 
	}
	
	public IdUtils() {
		this(new StrCoder());
	}
	
	public StrCoder getStrCoder() {
		return coder;
	}

	/**
	 * Получить безопасный идентификатор.
	 * <p>
	 * Атрибуты дескриптора инструмента могут содержать любые символы, включая
	 * спецсимволы файловой системы. Что бы избежать потенциальных проблем и
	 * при этом сохранить возможность визуально распозновать хранимые данные,
	 * для имен файлов используется следующий подход. Сначала каждый атрибут
	 * дескриптора кодируется по алгоритму URL-encode, который расширен и
	 * на спецсимволы. В последствии полученные элементы конкатенируются
	 * в единую строку по шаблону CO-CL-CU-TY, где CO - код, CL - код класса,
	 * CU - код валюты, TY - код типа инструмента. Полученный идентификатор
	 * гарантирует безопасное использование в рамках файловой системы,
	 * способствует узнаваемости данных а, также позволяет выполнять обратное
	 * преобразование из строкового значения в дескриптор инструмента.  
	 * <p>
	 * This method is deprecated. Use {@link #getSafeSymbolId(Symbol)}.
	 * <p>
	 * @param symbol - symbol info
	 * @return безопасный строковый идентификатор
	 */
	@Deprecated
	public String getSafeId(Symbol symbol) {
		String[] chunks = {
				coder.encode(symbol.getCode()),
				coder.encode(symbol.getExchangeID()),
				coder.encode(symbol.getCurrencyCode()),
				coder.encode(symbol.getTypeCode())
		};
		return StringUtils.join(chunks, SEPARATOR);
	}
	
	/**
	 * Get safe symbol identifier.
	 * <p>
	 * @param symbol - symbol
	 * @return A filesystem-safe symbol equivalent.
	 */
	public String getSafeSymbolId(Symbol symbol) {
		return coder.encode(symbol.toString());
	}
	
	/**
	 * Convert safe symbol ID to symbol.
	 * <p>
	 * @param safeSymbolId - safe string representation of the symbol
	 * @return the symbol
	 * @throws IllegalArgumentException - the argument is not a safe symbol ID
	 */
	public Symbol toSymbol(String safeSymbolId) throws IllegalArgumentException {
		return new Symbol(coder.decode(safeSymbolId));
	}
	
	/**
	 * Get safe ID.
	 * <p>
	 * This method is deprecated. Use {@link #getSafeFilename(Symbol, LocalDate, String)}.
	 * <p>
	 * @param symbol - symbol info
	 * @param date - date
	 * @return the safe identifier based on arguments
	 */
	@Deprecated
	public String getSafeId(Symbol symbol, LocalDate date) {
		String[] chunks = {
				getSafeId(symbol),
				dateFormat.format(date)
		};
		return StringUtils.join(chunks, SEPARATOR);
	}
	
	/**
	 * Добавить разделитель в конец строки.
	 * <p>
	 * @param str исходная строка
	 * @return строка с добавленным разделителем
	 */
	public String appendSeparator(String str) {
		return str + SEPARATOR;
	}
	
	/**
	 * Create a safe filename based on symbol, date and file extension.
	 * <p>
	 * @param symbol - the symbol
	 * @param date - the date
	 * @param suffix - filename suffix (Note: this component not encoded).
	 * @return the safe file based on arguments
	 */
	public String getSafeFilename(Symbol symbol, LocalDate date, String suffix) {
		return getSafeSymbolId(symbol) + SEPARATOR + dateFormat.format(date) + suffix;
	}
	
	/**
	 * Create a safe filename based on symbol and file extension.
	 * <p>
	 * @param symbol - the symbol
	 * @param suffix - filename suffix (Note: this component not encoded).
	 * @return the safe file based on arguments
	 */
	public String getSafeFilename(Symbol symbol, String suffix) {
		return getSafeSymbolId(symbol) + suffix;
	}
	
	/**
	 * Test that the filename is a data file of specified symbol.
	 * <p>
	 * This method is useful to determine filenames which were produced by
	 * calling the {@link #getSafeFilename(Symbol, LocalDate, String)} method.
	 * <p>
	 * @param filename - filename (without path)
	 * @param symbol - expected symbol
	 * @param suffix - expected filename suffix
	 * @return true if the filename is a data file
	 */
	public boolean isSafeFilename3(String filename, Symbol symbol, String suffix) {
		try {
			parseSafeFilename3(filename, symbol, suffix);
		} catch ( DateTimeParseException e ) {
			return false;
		}
		return true;
	}
	
	/**
	 * Parse date of the data segment.
	 * <p>
	 * This method is useful to determine date of the data segment which
	 * filename was produced by calling the
	 * {@link #getSafeFilename(Symbol, LocalDate, String)}
	 * method. Expected that the name has already verified by
	 * {@link #isSafeFilename3(String, Symbol, String)} method. 
	 * <p>
	 * @param filename - filename (without path components)
	 * @param symbol - expected symbol
	 * @param suffix - expected filename suffix
	 * @return the date obtained by parsing the filename
	 * @throws DateTimeParseException - error parsing the date part
	 */
	public LocalDate parseSafeFilename3(String filename, Symbol symbol, String suffix) {
		String symbolPrefix = getSafeSymbolId(symbol);
		String dummy = filename.substring(symbolPrefix.length() + SEPARATOR.length(),
				filename.length() - suffix.length());
		return LocalDate.parse(dummy, dateFormat);
	}

}
