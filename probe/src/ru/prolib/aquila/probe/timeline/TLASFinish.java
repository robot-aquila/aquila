package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.sm.*;

/**
 * Автомат хронологии: состояние завершения работы.
 * <p>
 */
public class TLASFinish extends SMState implements SMEnterAction {
	public static final String EOK = "OK";
	
	private final TLSTimeline heap;

	/**
	 * Конструктор.
	 * <p>
	 * @param heap фасад хронологии
	 */
	public TLASFinish(TLSTimeline heap) {
		super();
		this.heap = heap;
		registerExit(EOK);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		heap.setState(TLCmdType.FINISH);
		heap.fireFinish();
		heap.close();
		return getExit(EOK);
	}
	
}
