package ru.prolib.aquila.core.BusinessEntities;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Набор торговых позиций.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionsImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PositionsImpl implements EditablePositions, EventListener {
	private final Map<SecurityDescriptor, EditablePosition> map;
	private final Portfolio portfolio;
	private final EventDispatcher dispatcher;
	private final EventType onAvailable,onChanged;
	
	/**
	 * Создать набор позиций.
	 * <p>
	 * @param portfolio портфель, к которому относится набор позиций
	 * @param dispatcher диспетчер событий
	 * @param onAvailable тип события при инициализации позиции
	 * @param onChanged тип события при инициализации позиции
	 */
	public PositionsImpl(Portfolio portfolio,
						 EventDispatcher dispatcher,
						 EventType onAvailable,
						 EventType onChanged)
	{
		super();
		this.portfolio = portfolio;
		this.dispatcher = dispatcher;
		this.onAvailable = onAvailable;
		this.onChanged = onChanged;
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
	 * Получить используемый диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public synchronized List<Position> getPositions() {
		return new LinkedList<Position>(map.values());
	}

	@Override
	public EventType OnPositionAvailable() {
		return onAvailable;
	}

	@Override
	public void fireEvents(EditablePosition position) {
		synchronized ( position ) {
			if ( position.isAvailable() ) {
				position.fireChangedEvent();
			} else {
				position.setAvailable(true);
				dispatcher.dispatch(new PositionEvent(onAvailable, position));
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
			pos.OnChanged().addListener(this);
		}
		return pos;
	}

	@Override
	public EventType OnPositionChanged() {
		return onChanged;
	}

	@Override
	public synchronized int getPositionsCount() {
		return map.size();
	}
	
	@Override
	public void onEvent(Event event) {
		if ( event instanceof PositionEvent ) {
			Position pos = ((PositionEvent) event).getPosition();
			EventType map[][] = {
					{ pos.OnChanged(), onChanged },
			};
			for ( int i = 0; i < map.length; i ++ ) {
				if ( event.isType(map[i][0]) ) {
					dispatcher.dispatch(new PositionEvent(map[i][1], pos));
					break;
				}
			}
		}
	}
	
	/**
	 * Создать экземпляр позиции.
	 * <p>
	 * @param security инструмент
	 * @return экземпляр позиции
	 */
	private EditablePosition createPosition(Security security) {
		EventSystem es = ((EditableTerminal) portfolio.getTerminal())
			.getEventSystem(); 
		EventDispatcher dispatcher = es.createEventDispatcher("Position["
				+ portfolio.getAccount() + ":"
				+ security.getDescriptor() + "]"); 
		return new PositionImpl(portfolio, security, dispatcher,
				es.createGenericType(dispatcher, "OnChanged"));
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
			.append(o.dispatcher, dispatcher)
			.append(o.map, map)
			.append(o.onAvailable, onAvailable)
			.append(o.onChanged, onChanged)
			//.append(o.portfolio, portfolio)
			.isEquals();
	}

}
