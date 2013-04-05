package ru.prolib.aquila.core.data.getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;

/**
 * Конвертер даты из двух строк в объект класса {@link java.util.Date Date}.
 * <p>
 * Формирует дату на основе содержимого двух строковых значений, полученных
 * с помощью геттеров даты и времени, а так же заданного формата даты и времени.
 * Позволяет задавать характер поведения геттера: строгое (strict) и нестрогое
 * (nice).
 * <p>
 * Строгое поведение требует наличия корректных данных, результат обработки
 * которых обязательно приведет к формированию корректного объекта типа
 * {@link java.util.Date Date}. При строгом подходе, в случае если корректную
 * дату получить не удается, данный геттер выполняется запрос на генерацию
 * события о паническом состоянии терминала.
 * <p>
 * Нестрогое поведение рассматривает недостаточность данных как нормальную
 * ситуацию. В данном случае, под недостаточностью подразумевается вариант,
 * при котором геттеры даты и времени возвращают null или пустую строки. При
 * чем оба геттера должны вернуть недостаточные данные, иначе ситуация
 * рассматривается как ошибочная (например, забыли добавить необходимую колонку
 * в исходную таблицу). Данный подход позволяет использовать данный класс
 * для тех полей, в которых время является опциональным значением (например
 * время активации стоп-заявки или время снятия заявки). В этом случае
 * результатом работы данного геттера будет null, который (предположительно)
 * будет игнорироваться соответствующим сеттером.
 * <p>
 * Независимо от характера поведения, любые ошибки парсинга (преобразования
 * строки в объект) рассматриваются как критические и приводят к генерации
 * соответствующего события о паническом состоянии.
 * <p>
 * 2013-02-14<br>
 * $Id$
 */
public class GDate2E implements G<Date> {
	private final FirePanicEvent firePanic;
	private final G<String> gDate, gTime;
	private final String formatString;
	private final SimpleDateFormat format;
	private final boolean strict;
	private final String msgPrefix; 
	
	/**
	 * Конструктор.
	 * <p>
	 * Для определения форматов даты и времени используется формат, принятый
	 * для {@link java.text.SimpleDateFormat SimpleDateFormat}. Префикс
	 * сообщения используется в целях облегчения идентификации источника
	 * ошибки. Этот префикс будет добавлен перед текстом с расшифровкой ошибки
	 * и передан в метод генерации события о паническом состоянии.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @param gDate геттер строкового представления даты
	 * @param gTime геттер строкового представления времени
	 * @param dateFormat формат даты 
	 * @param timeFormat формат времени
	 * @param msgPrefix префикс сообщения о паническом состоянии
	 */
	public GDate2E(FirePanicEvent firePanic, boolean strict,
			G<String> gDate, G<String> gTime,
			String dateFormat, String timeFormat,
			String msgPrefix)
	{
		super();
		this.firePanic = firePanic;
		this.strict = strict;
		this.gDate = gDate;
		this.gTime = gTime;
		formatString = dateFormat + " " + timeFormat;
		format = new SimpleDateFormat(formatString);
		this.msgPrefix = msgPrefix;
	}
	
	/**
	 * Получить генератор событий.
	 * <p>
	 * @return генератор событий
	 */
	public FirePanicEvent getFirePanicEvent() {
		return firePanic;
	}
	
	/**
	 * Проверить строгий характер поведения.
	 * <p>
	 * @return true - строгий характер, false - нестрогий
	 */
	public boolean isStrict() {
		return strict;
	}
	
	/**
	 * Получить геттер строки с датой.
	 * <p>
	 * @return геттер
	 */
	public G<String> getDateGetter() {
		return gDate;
	}
	
	/**
	 * Получить геттер строки со временем.
	 * <p>
	 * @return геттер
	 */
	public G<String> getTimeGetter() {
		return gTime;
	}
	
	/**
	 * Получить строку формата времени.
	 * <p>
	 * Строка формата образуется конкатенацией строки формата даты со строкой
	 * формата времени через пробел.
	 * <p>
	 * @return строка формата
	 */
	public String getFormatString() {
		return formatString;
	}
	
	/**
	 * Получить префикс сообщения об ошибке.
	 * <p>
	 * @return префикс сообщения
	 */
	public String getMessagePrefix() {
		return msgPrefix;
	}

	@Override
	public Date get(Object source) {
		String dateString = gDate.get(source);
		String timeString = gTime.get(source);
		if ( dateString == null || dateString.length() == 0
		  || timeString == null || timeString.length() == 0 )
		{
			if ( ! strict && (dateString == null ? dateString == timeString
					: dateString.equals(timeString)) )
			{
				// Нестрогое поведение и обе части совпадают:
				// либо пустые, либо null. Это штатная ситуация, просто
				// опциональное значение которое в данном случае недоступно.
			} else {
				String msg = msgPrefix +
					"Incorrect date & time combination: {}='{}', {}='{}'";
				Object[] args = { gDate, dateString, gTime, timeString };
				firePanic.firePanicEvent(1, msg, args);
			}
			return null;
		}
		String combined = dateString + " " + timeString;
		try {
			return format.parse(combined);
		} catch ( ParseException e ) {
			String msg = msgPrefix + "Date format '{}' mismatch for '{}'";
			Object args[] = { formatString, combined };
			firePanic.firePanicEvent(1, msg, args);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GDate2E.class ) {
			GDate2E o = (GDate2E) other;
			return new EqualsBuilder()
				.append(firePanic, o.firePanic)
				.append(formatString, o.formatString)
				.append(gDate, o.gDate)
				.append(gTime, o.gTime)
				.append(msgPrefix, o.msgPrefix)
				.append(strict, o.strict)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[date=" + gDate + ", time="
			+ gTime + ", strict=" + strict
			+ ", fmt='" + formatString + "', msgPfx='" + msgPrefix + "']";
	}

}
