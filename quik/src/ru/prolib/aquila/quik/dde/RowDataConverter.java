package ru.prolib.aquila.quik.dde;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;

/**
 * Набор конвертеров данных на основе ряда.
 * <p>
 * Обеспечивает конвертацию элементов ряда таблицы формата XLT в форму,
 * пригодную для использования в локальной среде исполнения.
 */
public class RowDataConverter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(RowDataConverter.class);
	}
	
	private final SimpleDateFormat fullTimeFormat;
	private final String dateFormat, timeFormat;
	
	public RowDataConverter(String dateFormat, String timeFormat) {
		super();
		this.dateFormat = dateFormat;
		this.timeFormat = timeFormat;
		fullTimeFormat = new SimpleDateFormat(dateFormat + " " + timeFormat);
	}
	
	/**
	 * Конвертировать в дату на основании строковых значений даты и времени.
	 * <p>
	 * Конвертирует значение двух строковых полей (не могут быть null) в дату
	 * на основании формата даты и времени, заданных при создании конвертера.
	 * Исходные значения строк даты и времени могут быть пустыми строками,
	 * (не обязательно оба одновременно). В этом случае и в случае, если
	 * соответствующим параметром вызова разрешен возврат null-значения,
	 * результирующая дата будет равна null. Если null-результат не разрешен, то
	 * будет выполнена попытка распарсить некорректную строку времени и, как
	 * следствие, выброс исключения.
	 * <p>
	 * @param row ряд
	 * @param dateId идентификатор колонки со строковой датой
	 * @param timeId идентификатор колонки со строковым временем
	 * @param permitNullResult разрешить пустые строки и null результат
	 * @return дата или null, если оба компонента пустые строки
	 * @throws ValueException ошибка обращения к элементу ряда
	 * @throws RowNullValueException нулевое значение элемента ряда
	 * @throws RowDataTypeMismatchException неожиданный тип данных
	 */
	public Date getTime(Row row, String dateId, String timeId,
			boolean permitNullResult) throws ValueException
	{
		String date = getString(row, dateId);
		String time = getString(row, timeId);
		if ( (date.length() == 0 || time.length() == 0) && permitNullResult ) {
			return null;
		}
		try {
			return fullTimeFormat.parse(date + " " + time);
		} catch ( ParseException e ) {
			throw new RowTimeParseException(dateId, timeId,
					date, time, dateFormat, timeFormat);
		}
	}
	
	/**
	 * Получить определенное строковое значение ряда.
	 * <p>
	 * @param row ряд
	 * @param elementId идентификатор элемента
	 * @return строковое значение
	 * @throws ValueException ошибка обращения к элементу ряда
	 * @throws RowNullValueException нулевое значение элемента ряда
	 * @throws RowDataTypeMismatchException неожиданный тип данных
	 */
	public String getString(Row row, String elementId) throws ValueException {
		try {
			String value = (String) row.get(elementId);
			if ( value == null ) {
				throw new RowNullValueException(elementId);
			}
			return value;
		} catch ( ClassCastException e ) {
			Object args[] = { elementId, e.getMessage() };
			logger.error("For element {}: {}", args);
			throw new RowDataTypeMismatchException(elementId, "string");
		}
	}

	/**
	 * Получить определенное вещественное значение ряда.
	 * <p>
	 * @param row ряд
	 * @param elementId идентификатор элемента
	 * @return вещественное значение
	 * @throws ValueException ошибка обращения к элементу ряда
	 * @throws RowNullValueException нулевое значение элемента ряда
	 * @throws RowDataTypeMismatchException неожиданный тип данных
	 */
	public Double getDouble(Row row, String elementId) throws ValueException {
		Object value = row.get(elementId);
		if ( value == null ) {
			throw new RowNullValueException(elementId);
		}
		if ( value instanceof Double ) {
			return (Double) row.get(elementId);
		} else if ( value instanceof String ) {
			String str = (String) value;
			if ( str.length() == 0 ) {
				throw new RowNullValueException(elementId);
			}
			try {
				return Double.parseDouble(str);
			} catch ( NumberFormatException e ) {
				Object args[] = { elementId, str };
				logger.error("Cannot parse to double: {}={}", args);
				
			}
		}
		throw new RowDataTypeMismatchException(elementId, "double");
	}
	
	/**
	 * Получить вещественное значение ряда или null.
	 * <p>
	 * Метод для извлечения вещественных значений из "сомнительных" полей,
	 * которые могут содержать несоответствующий тип данных (например строку).
	 * Данный метод так же рассматривает нулевые значения как неопределенные,
	 * то есть null. Если соответствующее поле вообще не существует, то так
	 * же возвращается null. 
	 * <p>
	 * @param row ряд
	 * @param elementId идентификатор элемента
	 * @return вещественное или null, если значение ряда не соответствует
	 * @throws ValueException ошибка обращения к элементу ряда
	 */
	public Double getDoubleOrNull(Row row, String elementId)
			throws ValueException
	{
		try {
			Double value = getDouble(row, elementId);
			if ( value == 0.0d ) {
				return null;
			}
			return value;
		} catch ( RowNullValueException e ) {
			return null;
		} catch ( RowDataTypeMismatchException e ) {
			return null;
		}
	}
	
	/**
	 * Получить определенное длиное целое значение ряда.
	 * <p> 
	 * @param row ряд
	 * @param elementId идентификатор элемента
	 * @return длинное целое
	 * @throws ValueException ошибка обращения к элементу ряда
	 * @throws RowNullValueException нулевое значение элемента ряда
	 * @throws RowDataTypeMismatchException неожиданный тип данных
	 */
	public Long getLong(Row row, String elementId) throws ValueException {
		return getDouble(row, elementId).longValue();
	}
	
	/**
	 * Получить определенное целое значение ряда.
	 * <p>
	 * @param row ряд
	 * @param elementId идентификатор элемента
	 * @return целое
	 * @throws ValueException ошибка обращения к элементу ряда
	 * @throws RowNullValueException нулевое значение элемента ряда
	 * @throws RowDataTypeMismatchException неожиданный тип данных
	 */
	public Integer getInteger(Row row, String elementId) throws ValueException {
		return getDouble(row, elementId).intValue();
	}
	
	/**
	 * Получить соответствующее строковому элементу ряда значение по карте.
	 * <p>
	 * @param row ряд
	 * @param elementId идентификатор элемента
	 * @param map карта соответствия значений исходному строковому значению
	 * @return соотвествующее значение из карты
	 * @throws ValueException ошибка обращения к элементу ряда
	 * @throws RowNullValueException нулевое значение элемента ряда
	 * @throws RowDataTypeMismatchException неожиданный тип данных
	 * @throws RowUnmappedValueException нет соответствия в карте
	 */
	public Object getStringMappedTo(Row row, String elementId,
			Map<String, ? extends Object> map) throws ValueException
	{
		String key = getString(row, elementId);
		if ( map.containsKey(key) ) {
			return map.get(key);
		}
		throw new RowUnmappedValueException(elementId, key, map.keySet());
	}

}
