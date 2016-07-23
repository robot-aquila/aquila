package ru.prolib.aquila.core.utils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class IdUtils {
	private static final String SEPARATOR = "-";
	private static final DateTimeFormatter dateFormat;
	
	static {
		dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
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
	 * Get safe ID.
	 * <p>
	 * This method is deprecated. Use {@link #getSafeFile(Symbol, LocalDate, String)}.
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
	public File getSafeFile(Symbol symbol, LocalDate date, String suffix) {
		return new File(getSafeSymbolId(symbol) + SEPARATOR + dateFormat.format(date) + suffix);
	}

}
