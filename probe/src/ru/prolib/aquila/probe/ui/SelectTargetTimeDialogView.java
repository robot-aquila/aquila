package ru.prolib.aquila.probe.ui;

import org.joda.time.DateTime;

public interface SelectTargetTimeDialogView {
	
	/**
	 * Отобразить диалог выбора времени.
	 * <p>
	 * @param initialTime начальное (текущее) время. Используется для расчета
	 * минимально-возможного времени для выбора. Это время на 1 миллисекунду
	 * больше начального.
	 * @return возвращает выборанное время или null, если пользователь закрыл
	 * диалог, отменив выбор значения 
	 */
	public DateTime showDialog(DateTime initialTime);

}
