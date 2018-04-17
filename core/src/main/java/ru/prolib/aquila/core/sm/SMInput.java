package ru.prolib.aquila.core.sm;

/**
 * Вход (приемник данных).
 * <p>
 * Дескриптор приема данных.
 */
final public class SMInput {
	private final SMStateHandler owner;
	private final SMInputAction action;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param owner состояние-владелец
	 * @param action функция
	 */
	public SMInput(SMStateHandler owner, SMInputAction action) {
		super();
		this.owner = owner;
		this.action = action;
	}

	/**
	 * Обработчик ввода данных.
	 * <p>
	 * @param data данные
	 * @return дескриптор выхода или null, если следует оставаться в текущем
	 * состоянии
	 */
	public SMExit input(Object data) {
		return action.input(data);
	}
	
	/**
	 * Получить состояние.
	 * <p>
	 * @return состояние, которому принадлежит данный вход
	 */
	public SMStateHandler getState() {
		return owner;
	}

}
