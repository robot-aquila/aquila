package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

/**
 * Базовый интерфейс индикатора.
 * <p>
 * @param <T> - тип значения индикатора
 */
public interface Indicator<T> extends Series<T>, Starter {
	
	/**
	 * Проверить состояние индикатора.
	 * <p>
	 * @return true - индикатор работает, false - индикатор выключен
	 */
	public boolean started();

	/**
	 * Получить тип события: при запуске индикатора.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStarted();
	
	/**
	 * Получить тип события: при останове индикатора.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopped();

}
