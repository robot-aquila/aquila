package ru.prolib.aquila.core.data;

import org.joda.time.Interval;

/**
 * Нарушении границ интервала.
 * <p>
 * Может возбуждаться например в процессе агрегирования свечи с меньшим
 * интервалом в свечу с большим интервалом или агрегировании в свечу сделки,
 * время которой не принадлежит интервалу свечи.
 */
public class OutOfIntervalException extends ValueException {
	private static final long serialVersionUID = -8810862977023100698L;
	private final Interval interval;
	private final Object operand;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param interval интервал
	 * @param operand в зависимости от контекста дата, интервал, свеча и т.п.
	 */
	public OutOfIntervalException(Interval interval, Object operand) {
		super("Out of interval " + interval + ": " + operand);
		this.interval = interval;
		this.operand = operand;
	}
	
	/**
	 * Получить интервал.
	 * <p>
	 * @return интервал
	 */
	public Interval getInterval() {
		return interval;
	}
	
	/**
	 * Получить операнд.
	 * <p>
	 * @return операнд
	 */
	public Object getOperand() {
		return operand;
	}

}
