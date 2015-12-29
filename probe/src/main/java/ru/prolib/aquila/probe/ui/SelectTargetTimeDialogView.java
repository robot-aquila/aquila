package ru.prolib.aquila.probe.ui;

import java.time.LocalDateTime;

public interface SelectTargetTimeDialogView {
	
	/**
	 * Отобразить диалог выбора времени.
	 * <p>
	 * @param initialTime начальное (текущее) время. Это значение определяет
	 * начальное и минимальные значения, допустимые для выбора.
	 * @return возвращает выборанное время или null, если пользователь закрыл
	 * диалог, отменив выбор значения или выбранное время равно начальному
	 */
	public LocalDateTime showDialog(LocalDateTime initialTime);

}
