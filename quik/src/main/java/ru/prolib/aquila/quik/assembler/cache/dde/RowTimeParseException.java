package ru.prolib.aquila.quik.assembler.cache.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Исключения разбора времени на основании строковых элементов ряда.
 */
public class RowTimeParseException extends RowDataException {
	private static final long serialVersionUID = -6049815075343452795L;
	private final String dateElementId, timeElementId, dateValue, timeValue,
		dateFormat, timeFormat;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dateElementId идентификатор элемента строки даты
	 * @param timeElementId идентификатор элемента строки времени
	 * @param dateValue значение даты
	 * @param timeValue значение времени
	 * @param dateFormat ожидаемый формат даты
	 * @param timeFormat ожидаемый формат времени
	 */
	public RowTimeParseException(String dateElementId, String timeElementId,
			String dateValue, String timeValue,
			String dateFormat, String timeFormat)
	{
		super("Cannot parse ["+ dateElementId + "=" + dateValue + ","
				+ timeElementId + "=" + timeValue + "] according to the ["
				+ dateFormat + " " + timeFormat + "] format");
		this.dateElementId = dateElementId;
		this.timeElementId = timeElementId;
		this.dateValue = dateValue;
		this.timeValue = timeValue;
		this.dateFormat = dateFormat;
		this.timeFormat = timeFormat;
	}
	
	public String getDateElementId() {
		return dateElementId;
	}
	
	public String getTimeElementId() {
		return timeElementId;
	}
	
	public String getDateValue() {
		return dateValue;
	}
	
	public String getTimeValue() {
		return timeValue;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public String getTimeFormat() {
		return timeFormat;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != RowTimeParseException.class ) {
			return false;
		}
		RowTimeParseException o = (RowTimeParseException) other;
		return new EqualsBuilder()
			.append(dateElementId, o.dateElementId)
			.append(timeElementId, o.timeElementId)
			.append(dateValue, o.dateValue)
			.append(timeValue, o.timeValue)
			.append(dateFormat, o.dateFormat)
			.append(timeFormat, o.timeFormat)
			.isEquals();
	}

}
