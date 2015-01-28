package ru.prolib.aquila.core.data;

import org.joda.time.DateTime;

/**
 * Устаревшие данные.
 * <p>
 * Может возбуждаться например в случае попытки добавления в последовательность
 * свечей свечи, датированной более ранним периодом, чем текущая точка
 * актуальности последовательности.
 *
 */
public class OutOfDateException extends ValueException {
	private static final long serialVersionUID = -6368714328899539492L;
	private final DateTime time;
	private final Object operand;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param time время, относительно которого данные признаны устаревшими
	 * @param operand в зависимости от контекста дата, интервал, свеча и т.п.
	 */
	public OutOfDateException(DateTime time, Object operand) {
		super("Out of date " + time + ": " + operand);
		this.time = time;
		this.operand = operand;
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
	 * Получить операнд.
	 * <p>
	 * @return операнд
	 */
	public Object getOperand() {
		return operand;
	}

}
