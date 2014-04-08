package ru.prolib.aquila.core.sm;

/**
 * Входное действие.
 */
public interface SMEnterAction {
	
	/**
	 * Выполнить действие входа в состояние.
	 * <p>
	 * @param triggers реестр триггеров
	 * @return дескриптор выхода или null, если остаться в текущем состоянии
	 */
	public SMExit enter(SMTriggerRegistry triggers);

}
