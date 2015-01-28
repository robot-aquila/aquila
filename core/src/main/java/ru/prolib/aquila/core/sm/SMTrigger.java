package ru.prolib.aquila.core.sm;

/**
 * Интерфейс триггера.
 */
public interface SMTrigger {
	
	public void activate(SMTriggerRegistry registry);
	
	public void deactivate();

}
