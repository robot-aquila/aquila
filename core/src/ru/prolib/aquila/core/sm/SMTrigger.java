package ru.prolib.aquila.core.sm;

/**
 * Триггер.
 * <p>
 * Любое взаимодействие с автоматом осуществляется через триггеры. 
 * Триггер - это объект, инициирующий активность в конкретном состоянии.
 * Это значит, что  
 * 
 */
public interface SMTrigger {
	
	public void enable();
	
	public void disable();
	
	public void input(Object object);

}
