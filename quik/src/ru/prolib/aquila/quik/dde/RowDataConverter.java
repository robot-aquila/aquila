package ru.prolib.aquila.quik.dde;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;

/**
 * Набор конвертеров данных на основе ряда.
 * <p>
 * Обеспечивает конвертацию элементов ряда таблицы формата XLT в форму,
 * пригодную для использования в локальной среде исполнения.
 */
public class RowDataConverter {
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
	 * но обязательно оба одновременно. В этом случае и в случае, если
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
		if ( date.length() == 0 && time.length() == 0 && permitNullResult ) {
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
	 * Получить ненулевое строковое значение ряда.
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
			throw new RowDataTypeMismatchException(elementId, "string");
		}
	}

}
