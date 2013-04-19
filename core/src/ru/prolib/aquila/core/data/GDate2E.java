package ru.prolib.aquila.core.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Геттер даты на основе 2х строковых значений.
 * <p>
 * Время заявки, сделки, свечи может передаваться в двух полях (QUIK, FINAM).
 * Данный геттер использует два строковых геттера для извлечения строковых
 * представлений даты и времени и формирует на их основе объект даты.
 * <p>
 * 2012-08-28<br>
 * $Id: GDate2E.java 527 2013-02-14 15:14:09Z whirlwind $
 */
@Deprecated
public class GDate2E implements G<Date> {
	private final G<String> gDate;
	private final G<String> gTime;
	private final String dateFormat;
	private final String timeFormat;
	private final SimpleDateFormat df;
	
	/**
	 * Создать геттер.
	 * <p>
	 * Форматы даты и времени должны соответствовать шаблонам, используемым
	 * классов {@link java.text.SimpleDateFormat SimpleDateFormat}. 
	 * <p>
	 * @param gDate геттер строки даты
	 * @param gTime геттер строки времени
	 * @param dateFormat формат даты
	 * @param timeFormat формат времени
	 */
	public GDate2E(G<String> gDate, G<String> gTime,
				   String dateFormat, String timeFormat)
	{
		super();
		this.gDate = gDate;
		this.gTime = gTime;
		this.dateFormat = dateFormat;
		this.timeFormat = timeFormat;
		df = new SimpleDateFormat(dateFormat + " " + timeFormat);
	}
	
	/**
	 * Получить геттер строки со значением даты.
	 * <p>
	 * @return геттер
	 */
	public G<String> getDateGetter() {
		return gDate;
	}
	
	/**
	 * Получить геттер строки со значением времени.
	 * <p>
	 * @return геттер
	 */
	public G<String> getTimeGetter() {
		return gTime;
	}
	
	/**
	 * Получить строку формата времени.
	 * <p>
	 * @return формат времени
	 */
	public String getTimeFormat() {
		return timeFormat;
	}
	
	/**
	 * Получить строку формата даты.
	 * <p>
	 * @return формат даты
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	@Override
	public Date get(Object object) throws ValueException {
		String datePart = gDate.get(object);
		String timePart = gTime.get(object);
		if ( datePart == null || timePart == null ) {
			return null;
		}
		try {
			return df.parse(datePart + " " + timePart);
		} catch (ParseException e) {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GDate2E ) {
			GDate2E o = (GDate2E) other;
			return new EqualsBuilder()
				.append(gDate, o.gDate)
				.append(gTime, o.gTime)
				.append(dateFormat, o.dateFormat)
				.append(timeFormat, o.timeFormat)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, /*0*/73343)
			.append(gDate)
			.append(gTime)
			.append(dateFormat)
			.append(timeFormat)
			.toHashCode();
	}

}
