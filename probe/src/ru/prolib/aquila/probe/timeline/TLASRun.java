package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.sm.*;

/**
 * Автомат хронологии: состояние выполнения эмуляции.
 * <p>
 * В этом состоянии процедура симуляции шага вызывается циклически, до тех пор,
 * пока не будет выполнено одни из условий выхода:
 * <ul>
 * <li>Поступила команда {@link TLCmd#FINISH} или {@link TLCmd#PAUSE};</li>
 * <li>ТА достигло времени отсечки;</li>
 * <li>ТА вышло за границу РП.</li>
 * </ul>
 */
public class TLASRun  extends SMState
	implements SMEnterAction,SMInputAction
{
	public static final String EEND = "END";
	public static final String EPAUSE = "PAUSE";
	private final TLSTimeline heap;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param heap фасад хронологии
	 */
	public TLASRun(TLSTimeline heap) {
		super();
		this.heap = heap;
		setEnterAction(this);
		registerInput(this);
		registerExit(EEND);
		registerExit(EPAUSE);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		heap.setState(TLCmdType.RUN);
		heap.setBlockingMode(false);
		heap.fireRun();
		return null;
	}

	@Override
	public SMExit input(Object data) {
		TLCmd cmd = (TLCmd) data;
		if ( cmd != null ) {
			switch ( cmd.getType() ) {
			case PAUSE:
				return getExit(EPAUSE);
			case FINISH:
				return getExit(EEND);
			case RUN:
				heap.setCutoff(cmd.getTime());
				break;
			}			
		}
		
		if ( heap.getPOA().compareTo(heap.getCutoff()) >= 0 ) {
			return getExit(EPAUSE);
		}
		
		boolean res = heap.execute();
		heap.fireStep();
		return res && ! heap.finished() ? null : getExit(EEND);
	}

}
