package ru.prolib.aquila.core.timetable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.*;

/**
 * Дескриптор внутридневного времени.
 * <p>
 * Данный класс позволяет определять временную точку внутри дня с использованием
 * минуты часа. Дескриптор всегда указывает на первую миллисекунду указанной
 * минуты. Например, дескриптор 12:00 указывает на внутридневное время,
 * соответствующее 12:00:00.000.
 */
@XStreamAlias("HourMinute")
public class HMSpan implements Span {
	@XStreamAsAttribute
	private final int hour;
	@XStreamAsAttribute
	private final int minute;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param hour час 0-23
	 * @param minute минута 0-59
	 */
	public HMSpan(int hour, int minute) {
		super();
		if ( hour < 0 || hour > 23 ) {
			throw new IllegalArgumentException("Wrong hour: " + hour);
		}
		if ( minute < 0 || minute > 59 ) {
			throw new IllegalArgumentException("Wrong minute: " + minute);
		}
		this.hour = hour;
		this.minute = minute;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param minutes внутридневное время в минутах, относительно начала суток
	 */
	public HMSpan(int minutes) {
		this(minutes / 60, minutes % 60);
	}
	
	/**
	 * Получить номер часа суток.
	 * <p>
	 * @return номер часа, соответствующий времени дескриптора
	 */
	public int getHour() {
		return hour;
	}
	
	/**
	 * Получить номер минуты часа суток.
	 * <p>
	 * @return номер минуты часа, соответствующей времени дескриптора.
	 */
	public int getMinute() {
		return minute;
	}
	
	/**
	 * Дескриптор меньше времени?
	 * <p>
	 * Выполняется сравнение в рамках внутридневного времени. Дескриптор 
	 * указывает на первую миллисекунды заданной минуты часа (ноль). Это значит,
	 * что он будет меньше любого времени, которое указывает на следующую
	 * миллисекунду минуты часа, соответствующей времени дескриптора.
	 */
	@Override
	public boolean less(DateTime time) {
		return align(time).compareTo(time) < 0;
	}

	/**
	 * Дескриптор меньше или равен времени?
	 * <p>
	 * Выполняется сравнение в рамках внутридневного времени. Дескриптор
	 * указывает на первую миллисекунду заданной минуты часа (ноль). Это значит,
	 * что он будет меньше любого времени, которое указывает на следующую
	 * миллисекунду и будет равен времени, которое указывает на точно такое же
	 * внутридневное время, соответствующее времени дескриптора.
	 */
	@Override
	public boolean lessOrEquals(DateTime time) {
		return align(time).compareTo(time) <= 0;
	}

	/**
	 * Дескриптор больше указанного времени?
	 * <p>
	 * Выполняется сравнение в рамках внутридневного времени. Дескриптор
	 * указывает на первую миллисекунду заданной минуты часа (ноль). Это значит,
	 * что он будет больше любого предшествующего времени, начиная с последней
	 * миллисекунды предыдущей минуты. 
	 */
	@Override
	public boolean greater(DateTime time) {
		return align(time).compareTo(time) > 0;
	}
	
	/**
	 * Дескриптор больше или равен времени?
	 * <p>
	 * Выполняется сравнение в рамках внутридневного времени. Дескриптор
	 * указывает на первую миллисекунду заданной минуты часа (ноль). Это значит,
	 * что он будет больше любого предшествующего времени, начиная с последней
	 * миллисекунды предыдущей минуты и будет равен времени, которое указывает
	 * на точно такое же внутридневное время, соответствующее времени
	 * дескриптора.
	 */
	@Override
	public boolean greaterOrEquals(DateTime time) {
		return align(time).compareTo(time) >= 0;
	}

	/**
	 * Выровнять по времени дескриптора.
	 * <p>
	 * Выравнивает часы и минуты указанной временной метки по установленному
	 * значению минуты часа. Секунды и миллисекунды устанавливаются в нулевое
	 * значение.
	 */
	@Override
	public DateTime align(DateTime time) {
		return time.withTime(hour, minute, 0, 0); 
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != HMSpan.class ) {
			return false;
		}
		HMSpan o = (HMSpan) other;
		return new EqualsBuilder()
			.append(o.hour, hour)
			.append(o.minute, minute)
			.isEquals();
	}
	
	/**
	 * Конвертировать в минуты.
	 * <p>
	 * @return внутридневная временная точка, выраженная в минутах
	 */
	public int toMinutes() {
		return hour * 60 + minute;
	}
	
	@Override
	public String toString() {
		return String.format("%02d:%02d", hour, minute);
	}

}
