package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

/**
 * Тиковые данные.
 * <p>
 * Представляет собой два значения (основное-value и опциональное-volume)
 * связанные с определенным моментом на временной оси. Предназначен для
 * представления значения цены, цены/объема или значения произвольного индекса
 * в определенный момент времени.  
 */
public class Tick {
	private final DateTime time;
	private final Double value;
	private final Double volume;

	/**
	 * Конструктор.
	 * <p>
	 * @param time время
	 * @param value основное значение
	 * @param volume опциональное значение
	 */
	public Tick(DateTime time, Double value, Double volume) {
		super();
		this.time = time;
		this.value = value;
		this.volume = volume;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Значение объема не используется.
	 * <p>
	 * @param time время
	 * @param value значение
	 */
	public Tick(DateTime time, Double value) {
		this(time, value, null);
	}
	
	/**
	 * Получить время.
	 * <p>
	 * @return время
	 */
	public DateTime getTime() {
		return time;
	}
	
	/**
	 * Получить основное значение.
	 * <p>
	 * @return основное значение
	 */
	public Double getValue() {
		return value;
	}
	
	/**
	 * Получить опциональное значение.
	 * <p>
	 * @return опциональное значение
	 */
	public Double getVolume() {
		return volume;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == Tick.class ) {
			Tick o = (Tick) other;
			return new EqualsBuilder()
				.append(time, o.time)
				.append(value, o.value)
				.append(volume, o.volume)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[val=" + value
			+ (volume == null ? "" : ", vol=" + volume)
			+ " at " + time + "]";
	}
	

}
