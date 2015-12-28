package ru.prolib.aquila.core.data;

import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Тиковые данные.
 * <p>
 * Представляет собой пару значений (основное и опциональное) связанные с
 * определенным моментом на временной шкале. Можно использовать для
 * представления значения цены, цены/объема или значения произвольного индекса
 * в определенный момент времени.  
 */
public class Tick {
	private final LocalDateTime time;
	private final Double value;
	private final Double optValue;

	/**
	 * Конструктор.
	 * <p>
	 * @param time время
	 * @param value основное значение
	 * @param optValue опциональное значение
	 */
	public Tick(LocalDateTime time, Double value, Double optValue) {
		super();
		this.time = time;
		this.value = value;
		this.optValue = optValue;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Опциональное значение не используется.
	 * <p>
	 * @param time время
	 * @param value значение
	 */
	public Tick(LocalDateTime time, Double value) {
		this(time, value, null);
	}
	
	public Tick(LocalDateTime time, Double value, int optValue) {
		this(time, value, new Double(optValue));
	}
	
	/**
	 * Получить время.
	 * <p>
	 * @return время
	 */
	public LocalDateTime getTime() {
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
	public Double getOptionalValue() {
		return optValue;
	}
	
	/**
	 * Получить опциональное значение.
	 * <p>
	 * @return опциональное значение или 0, если значение не определено
	 */
	public long getOptionalValueAsLong() {
		return optValue == null ? 0L : optValue.longValue();
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
				.append(optValue, o.optValue)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[val=" + value
			+ (optValue == null ? "" : ", opt=" + optValue)
			+ " at " + time + "]";
	}
	

}
