package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.sm.*;

/**
 * Автомат хронологии: состояние завершения работы.
 * <p>
 */
public class TLStateFinish extends TLState {
	public final SMExit onFinished;

	/**
	 * Конструктор.
	 * <p>
	 * @param facade фасад подсистемы эмуляции
	 */
	public TLStateFinish(TLSimulationFacade facade) {
		super(facade);
		onFinished = registerExit();
	}

	@Override
	public void prepare() {
		
	}

	@Override
	public SMExit pass() {
		facade.clearCommands();
		facade.fireFinished();
		return onFinished;
	}

	@Override
	public void cleanup() {
		
	}
	
}
