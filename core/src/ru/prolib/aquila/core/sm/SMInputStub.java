package ru.prolib.aquila.core.sm;

/**
 * Вход-заглушка.
 * <p>
 * Используется для жесткого связывания входа с дескриптором выхода.
 */
public class SMInputStub implements SMInputAction {
	private final SMExit exit;

	/**
	 * Конструктор.
	 * <p>
	 * @param exit дескриптор выхода
	 */
	public SMInputStub(SMExit exit) {
		super();
		this.exit = exit;
	}

	@Override
	public SMExit input(Object data) {
		return exit;
	}

}
