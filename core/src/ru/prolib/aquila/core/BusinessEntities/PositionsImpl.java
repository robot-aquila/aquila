package ru.prolib.aquila.core.BusinessEntities;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionFactory;

/**
 * Набор торговых позиций.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionsImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PositionsImpl implements EditablePositions,EventListener {
	private final Map<SecurityDescriptor, EditablePosition> map;
	private final PositionFactory factory;
	private final EventDispatcher dispatcher;
	private final EventType onAvailable,onChanged;
	
	/**
	 * Создать набор позиций.
	 * <p>
	 * @param factory фабрика позиций
	 * @param dispatcher диспетчер событий
	 * @param onAvailable тип события при инициализации позиции
	 * @param onChanged тип события при инициализации позиции
	 */
	public PositionsImpl(PositionFactory factory,
						 EventDispatcher dispatcher,
						 EventType onAvailable,
						 EventType onChanged)
	{
		super();
		if ( factory == null ) {
			throw new NullPointerException("Factory cannot be null");
		}
		this.factory = factory;
		if ( dispatcher == null ) {
			throw new NullPointerException("Event dispatcher cannot be null");
		}
		this.dispatcher = dispatcher;
		if ( onAvailable == null || onChanged == null ) {
			throw new NullPointerException("Event type cannot be null");
		}
		this.onAvailable = onAvailable;
		this.onChanged = onChanged;
		map = new LinkedHashMap<SecurityDescriptor, EditablePosition>();
	}
	
	/**
	 * Получить используемую фабрику позиций.
	 * <p>
	 * @return фабрика позиций
	 */
	public PositionFactory getPositionFactory() {
		return factory;
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
	public synchronized Position getPosition(SecurityDescriptor descr) {
		return getEditablePosition(descr);
	}

	@Override
	public EventType OnPositionAvailable() {
		return onAvailable;
	}

	@Override
	public void firePositionAvailableEvent(Position position) {
		dispatcher.dispatch(new PositionEvent(onAvailable, position));
	}

	@Override
	public synchronized
		EditablePosition getEditablePosition(SecurityDescriptor descr)
	{
		EditablePosition pos = map.get(descr);
		if ( pos == null ) {
			// TODO: по аналогии с инструментами, может возникнуть необходимость
			// инстанцировать различные классы для различных источников. Это
			// не важно для потребителей сервиса, но может быть важно для
			// поставщиков. Так что, следует подумать, что бы перенести это в
			// обработчик ряда. А может и не возникнет... Плюс такого подхода в
			// том, что работу с позициями можно начинать до того, как поступят
			// первые данные по позиции.
			pos = factory.createPosition(descr);
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

	@Override
	public synchronized Position getPosition(Security security) {
		return getPosition(security.getDescriptor());
	}

}
