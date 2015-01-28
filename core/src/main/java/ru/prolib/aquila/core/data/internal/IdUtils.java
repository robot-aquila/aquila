package ru.prolib.aquila.core.data.internal;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.utils.FileNameEncoder;

public class IdUtils {
	private static final String SEPARATOR = "-";
	private final FileNameEncoder nameEncoder;

	public IdUtils(FileNameEncoder nameEncoder) {
		super();
		this.nameEncoder = nameEncoder;
	}
	
	public IdUtils() {
		this(new FileNameEncoder());
	}

	/**
	 * Получить безопасное имя файла.
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
	 * @param descr дескриптор инструмента
	 * @return безопасный строковый идентификатор
	 */
	public String getSafeFilename(SecurityDescriptor descr) {
		return nameEncoder.encode(descr.getCode()) + SEPARATOR
				+ nameEncoder.encode(descr.getClassCode()) + SEPARATOR
				+ nameEncoder.encode(descr.getCurrencyCode()) + SEPARATOR
				+ nameEncoder.encode(descr.getType().toString());
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
