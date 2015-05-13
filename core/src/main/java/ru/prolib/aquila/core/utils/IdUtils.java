package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

public class IdUtils {
	private static final String SEPARATOR = "-";
	private static final DateTimeFormatter dateFormat;
	
	static {
		dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
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
	 * @param descr - security descriptor
	 * @return безопасный строковый идентификатор
	 */
	public String getSafeId(SecurityDescriptor descr) {
		String[] chunks = {
				coder.encode(descr.getCode()),
				coder.encode(descr.getClassCode()),
				coder.encode(descr.getCurrencyCode()),
				coder.encode(descr.getType().toString())
		};
		return StringUtils.join(chunks, SEPARATOR);
	}
	
	/**
	 * Get safe ID.
	 * <p>
	 * @param descr - security descriptor
	 * @param date - date
	 * @return the safe identifier based on arguments
	 */
	public String getSafeId(SecurityDescriptor descr, LocalDate date) {
		String[] chunks = {
				getSafeId(descr),
				dateFormat.print(date)
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

}
