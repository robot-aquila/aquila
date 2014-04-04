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
	public static final SMExit STUB = new SMExit(SMState.FINAL, "stub");
	
	private final SMState owner;
	private final String id;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param owner состояние-владелец
	 * @param id символьный идентификатор
	 */
	public SMExit(SMState owner, String id) {
		super();
		this.owner = owner;
		this.id = id;
	}
	
	/**
	 * Получить состояние.
	 * <p>
	 * @return состояние, которому относится данный выход
	 */
	public SMState getState() {
		return owner;
	}
	
	/**
	 * Получить символьный идентификатор выхода.
	 * <p>
	 * @return идентификатор
	 */
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return id;
	}

}
