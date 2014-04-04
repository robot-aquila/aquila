package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.sm.*;

/**
 * Автомат хронологии: состояние паузы.
 * <p>
 * В этом состоянии программа в блокирующем режиме ожидает поступление следующей
 * команды. 
 */
public class TLStatePause extends TLState {
	public final SMExit onFinish, onRun;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param facade фасад подсистемы эмуляции
	 */
	public TLStatePause(TLSimulationFacade facade) {
		super(facade);
		onFinish = registerExit();
		onRun = registerExit();
	}

	@Override
	public void prepare() {
		facade.firePaused();
	}

	@Override
	public SMExit pass() {
		TLCommand c;
		try {
			c = facade.tellb();
		} catch ( InterruptedException e ) {
			//Thread.currentThread().interrupt();
			return onFinish;
		}
		if ( c.isRun() ) {
			return onRun;
		} else {
			facade.pull(); // Удалить команду из очереди.
			return c == TLCommand.FINISH ? onFinish : null;
		}
	}

	@Override
	public void cleanup() {
		
	}

}
