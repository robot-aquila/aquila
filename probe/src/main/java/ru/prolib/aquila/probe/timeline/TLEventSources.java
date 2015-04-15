package ru.prolib.aquila.probe.timeline;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.utils.KW;

/**
 * Реестр источников событий.
 * <p>
 * Задача данного класса заключается в управлении набором источников событий в
 * контексте работы с хронологией. Помимо непосредственно хранения набора,
 * объект класса позволяет временно исключать источники до наступления заданного
 * времени.
 * <p>
 * Так как различные источники не связаны между собой, последовательности
 * событий, выдаваемые двумя разными источниками, могут значительно различаться
 * как по датировке, так и по объему данных. Поскольку система рассчитана на
 * прокачку большого объема данных (например, все сделки по инструменту за
 * торговую сессию), отсутствие какой либо синхронизации источников практически
 * наверняка приведет к перегрузке объекта хронологии будущими событиями. Что бы
 * избежать "забегания" далеко вперед, данный класс позволяет исключать
 * источники по времени.
 */
public class TLEventSources implements EventSourceRepository {
	private final Map<KW<TLEventSource>, DateTime> sources; 
	
	/**
	 * Конструктор.
	 */
	public TLEventSources() {
		super();
		sources = new LinkedHashMap<KW<TLEventSource>, DateTime>();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#getSources(org.joda.time.DateTime)
	 */
	@Override
	public synchronized List<TLEventSource> getSources(DateTime time) {
		List<TLEventSource> list = new Vector<TLEventSource>();
		for ( Map.Entry<KW<TLEventSource>, DateTime> e : sources.entrySet() ) {
			DateTime entryTime = e.getValue();
			if ( entryTime == null || entryTime.compareTo(time) <= 0 ) {
				list.add(e.getKey().instance());
			}
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#getSources()
	 */
	@Override
	public synchronized List<TLEventSource> getSources() {
		List<TLEventSource> list = new Vector<TLEventSource>();
		for ( KW<TLEventSource> key : sources.keySet() ) {
			list.add(key.instance());
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#registerSource(ru.prolib.aquila.probe.timeline.TLEventSource)
	 */
	@Override
	public synchronized void registerSource(TLEventSource source) {
		KW<TLEventSource> key = new KW<TLEventSource>(source);
		if ( ! sources.containsKey(key)) {
			sources.put(key, null);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#removeSource(ru.prolib.aquila.probe.timeline.TLEventSource)
	 */
	@Override
	public synchronized void removeSource(TLEventSource source) {
		sources.remove(new KW<TLEventSource>(source));
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#disableUntil(ru.prolib.aquila.probe.timeline.TLEventSource, org.joda.time.DateTime)
	 */
	@Override
	public synchronized void disableUntil(TLEventSource source, DateTime time) {
		KW<TLEventSource> key = new KW<TLEventSource>(source);
		if ( sources.containsKey(key) ) {
			sources.put(key, time);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#close()
	 */
	@Override
	public synchronized void close() {
		for ( KW<TLEventSource> key : sources.keySet() ) {
			key.instance().close();
		}
		sources.clear();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#isRegistered(ru.prolib.aquila.probe.timeline.TLEventSource)
	 */
	@Override
	public synchronized boolean isRegistered(TLEventSource source) {
		return sources.containsKey(new KW<TLEventSource>(source));
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.EventSourceRepository#getDisabledUntil(ru.prolib.aquila.probe.timeline.TLEventSource)
	 */
	@Override
	public synchronized DateTime getDisabledUntil(TLEventSource source) {
		return sources.get(new KW<TLEventSource>(source));
	}

}
