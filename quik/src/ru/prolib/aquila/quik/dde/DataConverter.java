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
public class DataConverter {
	private final SimpleDateFormat fullTimeFormat;
	private final String dateFormat, timeFormat;
	private Row row;
	
	public DataConverter(String dateFormat, String timeFormat) {
		super();
		this.dateFormat = dateFormat;
		this.timeFormat = timeFormat;
		fullTimeFormat = new SimpleDateFormat(dateFormat + " " + timeFormat);
	}
	
	public void setCurrentRow(Row row) {
		this.row = row;
	}
	
	public Row getCurrentRow() {
		return row;
	}
	
	/**
	 * Конвертировать в дату на основании строковых значений даты и времени.
	 * <p>
	 * @param dateId идентификатор колонки со строковой датой
	 * @param timeId идентификатор колонки со строковым временем
	 * @return дата
	 * @throws ValueException
	 */
	public Date getTime(String dateId, String timeId) throws ValueException {
		String fullTime = (String) row.get(dateId) + " " + (String) row.get(timeId);
		try {
			
			return fullTimeFormat.parse(fullTime);
		} catch ( ParseException e ) {
			throw new ValueException();
		}
	}
	
	/**
	 * Получить ненулевое строковое значение ряда.
	 * <p>
	 * @param elementId идентификатор элемента
	 * @return строковое значение
	 * @throws ValueException получено нулевое или не строковое значение
	 */
	public String getNotNullString(String elementId) throws ValueException {
		try {
			String value = (String) row.get(elementId);
			if ( value == null ) {
				//throw new ValueException();
			}
		} catch ( ClassCastException e ) {
			
		}
		return null;
	}

}
