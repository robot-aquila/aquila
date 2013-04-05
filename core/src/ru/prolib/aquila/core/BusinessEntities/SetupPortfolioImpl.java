package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;
import ru.prolib.aquila.core.*;

/**
 * Спецификация портфеля.
 * <p>
 * 2013-01-11<br>
 * $Id: SetupPortfolioImpl.java 562 2013-03-06 15:22:54Z whirlwind $
 */
public class SetupPortfolioImpl implements SetupPortfolio {
	private final EventDispatcher dispatcher;
	private final EventType onCommit;
	private final EventType onRollback;
	private SetupPositionsImpl initial,current;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dispatcher
	 * @param onCommit
	 * @param onRollback
	 */
	public SetupPortfolioImpl(EventDispatcher dispatcher,
			EventType onCommit, EventType onRollback)
	{
		super();
		this.dispatcher = dispatcher;
		this.onCommit = onCommit;
		this.onRollback = onRollback;
		initial = new SetupPositionsImpl();
		current = new SetupPositionsImpl();
	}
	
	/**
	 * Получить начальное состояние.
	 * <p>
	 * Начальное состояние соответствует состоянию на момент создания или после
	 * последней фиксации изменений.
	 * <p>
	 * @return набор позиций
	 */
	public synchronized SetupPositions getInitialSetup() {
		return initial;
	}
	
	/**
	 * Получить текущее состояние.
	 * <p>
	 * @return набор позиций
	 */
	public synchronized SetupPositions getCurrentSetup() {
		return current;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public synchronized SetupPosition getPosition(SecurityDescriptor security) {
		return current.getPosition(security);
	}

	@Override
	public synchronized void removePosition(SecurityDescriptor security) {
		current.removePosition(security);
	}

	@Override
	public synchronized List<SetupPosition> getPositions() {
		return current.getPositions();
	}

	@Override
	public synchronized boolean hasChanged() {
		return ! initial.equals(current);
	}

	@Override
	public synchronized void rollback() {
		if ( hasChanged() ) {
			resetChanges();
			SetupPositions copy = current.clone();
			dispatcher.dispatch(new SetupPositionsEvent(onRollback, copy));
		}
	}

	@Override
	public synchronized void commit() {
		if ( hasChanged() ) {
			initial = current.clone();
			forceCommit();
		}
	}

	@Override
	public EventType OnCommit() {
		return onCommit;
	}

	@Override
	public EventType OnRollback() {
		return onRollback;
	}
	
	@Override
	public synchronized SetupPositions clone() {
		return current.clone();
	}

	@Override
	public synchronized void forceCommit() {
		SetupPositions copy = current.clone();
		dispatcher.dispatch(new SetupPositionsEvent(onCommit, copy));
	}

	@Override
	public synchronized void resetChanges() {
		current = initial.clone();
	}

}
