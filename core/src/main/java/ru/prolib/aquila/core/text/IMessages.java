package ru.prolib.aquila.core.text;

/**
 * Facade to access text messages.
 */
public interface IMessages {
	
	public String get(MsgID msgId);
	
	public String format(MsgID msgId, Object... args);

}
