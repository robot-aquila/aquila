package ru.prolib.aquila.core.sm;

/**
 * Дескриптор выхода.
 * <p>
 * Дескриптор выхода из состояния позволяет связывать состояния переходами.
 */
final public class SMExit {
	/**
	 * Выход-заглушка.
	 */
	public static final SMExit STUB = new SMExit(SMState.FINAL);
	
	private final SMState owner;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param owner состояние-владелец
	 */
	public SMExit(SMState owner) {
		super();
		this.owner = owner;
	}
	
	/**
	 * Получить состояние.
	 * <p>
	 * @return состояние, которому относится данный выход
	 */
	public SMState getState() {
		return owner;
	}

}
