package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Набор торговых позиций.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionsImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class Positions {
	private final Map<SecurityDescriptor, EditablePosition> map;
	private final Portfolio portfolio;
	private final PositionsEventDispatcher dispatcher;
	
	/**
	 * Создать набор позиций.
	 * <p>
	 * @param portfolio портфель, к которому относится набор позиций
	 * @param dispatcher диспетчер событий
	 */
	public Positions(Portfolio portfolio,
			PositionsEventDispatcher dispatcher)
	{
		super();
		this.portfolio = portfolio;
		this.dispatcher = dispatcher;
		map = new LinkedHashMap<SecurityDescriptor, EditablePosition>();
	}
	
	/**
	 * Получить портфель.
	 * <p>
	 * @return портфель
	 */
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public PositionsEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	public List<Position> getPositions() {
		return new LinkedList<Position>(map.values());
	}

	public EventType OnPositionAvailable() {
		return dispatcher.OnAvailable();
	}

	public void fireEvents(EditablePosition position) {
		synchronized ( position ) {
			if ( position.isAvailable() ) {
				position.fireChangedEvent();
			} else {
				position.setAvailable(true);
				dispatcher.fireAvailable(position);
			}
			position.resetChanges();
		}
	}

	public EditablePosition getEditablePosition(Security security) {
		SecurityDescriptor descr = security.getDescriptor();
		EditablePosition pos = map.get(descr);
		if ( pos == null ) {
			pos = createPosition(security);
			map.put(descr, pos);
			dispatcher.startRelayFor(pos);
		}
		return pos;
	}

	public EventType OnPositionChanged() {
		return dispatcher.OnChanged();
	}

	public int getPositionsCount() {
		return map.size();
	}
	
	/**
	 * Создать экземпляр позиции.
	 * <p>
	 * @param security инструмент
	 * @return экземпляр позиции
	 */
	private EditablePosition createPosition(Security security) {
		return new PositionImpl(portfolio, security,
			new PositionEventDispatcher(((EditableTerminal)
				portfolio.getTerminal()).getEventSystem(),
					portfolio.getAccount(),
					security.getDescriptor()));
	}

	public Position getPosition(Security security) {
		return getEditablePosition(security);
	}
	
	/**
	 * Установить экземпляр позиции.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param p экземпляр позиции
	 */
	protected void setPosition(SecurityDescriptor descr, EditablePosition p) {
		map.put(descr, p);
	}

}
