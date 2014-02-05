package ru.prolib.aquila.probe.timeline;

import java.util.List;
import java.util.Vector;

import org.joda.time.DateTime;


/**
 * Набор вспомогательных функций хронологии событий.
 */
public class TLTimelineHelper {
	
	/**
	 * Конструктор.
	 */
	public TLTimelineHelper() {
		super();
	}
	
	/**
	 * Извлечь порцию событий.
	 * <p>
	 * Данный метод используется для опроса источников событий, активных на
	 * указанную ТА. Каждый источник опрашиваются до тех пор, пока не будет
	 * получено первое событие в будущем относительно ТА. При получении такого
	 * события источник отключается до наступления времени события. При этом,
	 * само событие не теряется, а возвращается в рамках списка. Возвращение
	 * источником null-события приводит к отключению источника на одну
	 * милллисекунду (минимальный временной тик). Работа прекращается, когда для
	 * ТА заканчиваются активные источники событий.
	 * <p>
	 * @param poa точка актуальности
	 * @param sources набор источников событий
	 * @return список событий (может быть пустым)
	 * @throws TLException 
	 */
	public List<TLEvent> pullEvents(DateTime poa, TLEventSources sources)
		throws TLException
	{
		List<TLEvent> result = new Vector<TLEvent>();
		while ( true) {
			List<TLEventSource> list = sources.getSources(poa);
			if ( list.size() == 0 ) {
				break;
			}
			for ( TLEventSource src : list ) {
				if ( src.closed() ) {
					sources.disableUntil(src, poa.plus(1));
					continue;
				}
				TLEvent event = src.pullEvent();
				if ( event == null ) {
					sources.disableUntil(src, poa.plus(1));
				} else {
					result.add(event);
					if ( poa.compareTo(event.getTime()) < 0 ) {
						sources.disableUntil(src, event.getTime());
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Добавить события хронологии.
	 * <p> 
	 * @param events список событий
	 * @param timeline хронология
	 * @throws TLOutOfDateException
	 */
	public void pushEvents(List<TLEvent> events, TLEventCache timeline)
		throws TLOutOfDateException
	{
		for ( TLEvent event : events ) {
			timeline.pushEvent(event);
		}
	}

}
