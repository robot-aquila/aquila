package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.sm.*;

/**
 * Автомат хронологии: состояние паузы.
 * <p>
 * В этом состоянии программа в блокирующем режиме ожидает поступление следующей
 * команды. Выход из состояния осуществляется по команде
 * {@link TLCmdType#FINISH} или {@link TLCmdType#RUN}.
 */
public class TLASPause extends SMState
	implements SMEnterAction,SMInputAction
{
	public static final String EEND = "END";
	public static final String ERUN = "RUN";
	
	private final TLSTimeline heap;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param heap фасад хронологии
	 */
	public TLASPause(TLSTimeline heap) {
		super();
		this.heap = heap;
		setEnterAction(this);
		registerInput(this);
		registerExit(EEND);
		registerExit(ERUN);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		heap.setState(TLCmdType.PAUSE);
		heap.setBlockingMode(true);
		heap.firePause();
		return null;
	}

	@Override
	public SMExit input(Object data) {
		TLCmd cmd = (TLCmd) data;
		switch ( cmd.getType() ) {
		case FINISH:
			return getExit(EEND);
		case RUN:
			heap.setCutoff(cmd.getTime());
			return getExit(ERUN);
		default:
			return null;
		}
	}

}
