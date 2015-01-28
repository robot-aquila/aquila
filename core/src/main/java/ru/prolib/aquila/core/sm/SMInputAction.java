package ru.prolib.aquila.core.sm;

/**
 * Интерфейс входного действия.
 * <p>
 * Входное действие обрабатывает входные данные и определяет момент и
 * направление выхода из состояния.
 */
public interface SMInputAction {
	
	/**
	 * Обработчик ввода данных.
	 * <p>
	 * @param data данные
	 * @return дескриптор выхода или null, если следует оставаться в текущем
	 * состоянии
	 */
	public SMExit input(Object data);

}
