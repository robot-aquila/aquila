package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.sm.*;

/**
 * Болванка состояния автомата симуляции хронологии.
 * <p>
 * Задача данного класса предоставить отдельно базу под реализацию состояния, с
 * целью облегчения последующего тестирования этапов работы состояния
 * посредством вызова стандартизированных методов.
 */
abstract public class TLState extends SMState implements SMEnterAction {
	protected final TLSimulationFacade facade;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param facade фасад подсистемы симуляции
	 */
	public TLState(TLSimulationFacade facade) {
		super();
		this.facade = facade;
		setEnterAction(this);
	}
	
	@Override
	final public SMExit enter() {
		SMExit e;
		prepare();
		do {
			e = pass();
		} while ( e == null );
		cleanup();
		return e;
	}
	
	/**
	 * Процедура подготовки.
	 * <p>
	 * Эта процедура вызывается один раз перед входом в цикл вызовов метода
	 * {@link #pass()}. Конкретные реализации могут использовать данный метод
	 * для подготовки состояния к работе в момент входа в него.
	 */
	abstract public void prepare();
	
	/**
	 * Процедура состояния.
	 * <p>
	 * Эта процедура вызывается до тех пор, пока в результате вызова не будет
	 * получен дескриптор выхода. Данный метод обязателен для реализации
	 * состояния. 
	 * <p>
	 * @return дескриптор выхода
	 */
	abstract public SMExit pass();
	
	/**
	 * Проуедура завершения.
	 * <p>
	 * Эта процедура вызывается один раз после того, как был получен дескриптор
	 * выхода. Конкретные реализации могут использовать данный метод для
	 * освобождения ресурсов и управления соответствующими признаками на выходе
	 * из состояния.  
	 */
	abstract public void cleanup();

}
