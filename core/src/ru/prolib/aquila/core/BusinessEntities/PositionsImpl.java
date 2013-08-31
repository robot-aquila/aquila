package ru.prolib.aquila.core.BusinessEntities;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionEventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionsEventDispatcher;

/**
 * Набор торговых позиций.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionsImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PositionsImpl implements EditablePositions {
	private final Map<SecurityDescriptor, EditablePosition> map;
	private final Portfolio portfolio;
	private final PositionsEventDispatcher dispatcher;
	
	/**
	 * Создать набор позиций.
	 * <p>
	 * @param portfolio портфель, к которому относится набор позиций
	 * @param dispatcher диспетчер событий
	 */
	public PositionsImpl(Portfolio portfolio,
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

	@Override
	public synchronized List<Position> getPositions() {
		return new LinkedList<Position>(map.values());
	}

	@Override
	public EventType OnPositionAvailable() {
		return dispatcher.OnAvailable();
	}

	@Override
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

	@Override
	public synchronized
		EditablePosition getEditablePosition(Security security)
	{
		SecurityDescriptor descr = security.getDescriptor();
		EditablePosition pos = map.get(descr);
		if ( pos == null ) {
			pos = createPosition(security);
			map.put(descr, pos);
			pos.OnChanged().addListener(dispatcher);
		}
		return pos;
	}

	@Override
	public EventType OnPositionChanged() {
		return dispatcher.OnChanged();
	}

	@Override
	public synchronized int getPositionsCount() {
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

	@Override
	public synchronized Position getPosition(Security security) {
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
	protected synchronized
			void setPosition(SecurityDescriptor descr, EditablePosition p)
	{
		map.put(descr, p);
	}
	
	/**
	 * Сравнить два набора.
	 * <p>
	 * При сравнении двух наборов, портфели не сравниваются. Так как портфели
	 * в свою очередь сравнивают собственные наборы позиций, возникает
	 * бесконечная рекурсия.
	 */
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionsImpl.class ) {
			return false;
		}
		PositionsImpl o = (PositionsImpl) other;
		return new EqualsBuilder()
			// Портфель не сравниваем - рекурсия
			.append(o.map, map)
			.isEquals();
	}

}
