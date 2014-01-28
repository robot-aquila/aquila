package ru.prolib.aquila.probe.timeline;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.utils.KW;

/**
 * Реестр источников событий.
 */
public class TLEventSourceRegistry {
	private Map<KW<TLEventSource>, DateTime> registry; 
	
	/**
	 * Конструктор.
	 */
	public TLEventSourceRegistry() {
		super();
		registry = new LinkedHashMap<KW<TLEventSource>, DateTime>();
	}
	
	/**
	 * Получить источники событий.
	 * <p>
	 * Возвращает источники событий, которые действительны начиная с указанного
	 * времени.
	 * <p>
	 * @param time временная метка
	 * @return список источников
	 */
	public synchronized List<TLEventSource> getSources(DateTime time) {
		List<TLEventSource> list = new Vector<TLEventSource>();
		for ( Map.Entry<KW<TLEventSource>, DateTime> e : registry.entrySet() ) {
			DateTime entryTime = e.getValue();
			if ( entryTime == null || entryTime.compareTo(time) <= 0 ) {
				list.add(e.getKey().instance());
			}
		}
		return list;
	}
	
	/**
	 * Получить источники событий.
	 * <p>
	 * Возвращает список всех зарегистрированных источников событий.
	 * <p>
	 * @return список источников
	 */
	public synchronized List<TLEventSource> getSources() {
		List<TLEventSource> list = new Vector<TLEventSource>();
		for ( KW<TLEventSource> key : registry.keySet() ) {
			list.add(key.instance());
		}
		return list;
	}
	
	/**
	 * Добавить источник событий.
	 * <p>
	 * @param source источник событий
	 */
	public synchronized void registerSource(TLEventSource source) {
		KW<TLEventSource> key = new KW<TLEventSource>(source);
		if ( ! registry.containsKey(key)) {
			registry.put(key, null);
		}
	}
	
	/**
	 * Удалить источник событий.
	 * <p>
	 * Удаляет источник из реестра. Если указанный источник не был добавлен,
	 * то ничего не выполняет.
	 * <p>
	 * @param source источник событий
	 */
	public synchronized void removeSource(TLEventSource source) {
		registry.remove(new KW<TLEventSource>(source));
	}
	
	/**
	 * Отметить источник неактивным.
	 * <p>
	 * Делает источник событий неактивным до наступления указанного времени.
	 * Неактивные источники не возвращаются методом
	 * {@link #getSources(DateTime)}, если в качестве его аргумента указано
	 * время меньше времени активации источника. Изначально все источники
	 * считаются активными. Если указан незарегистрированный источник, то ничего
	 * не выполняет.
	 * <p>
	 * @param source источник событий
	 * @param time время активации
	 */
	public synchronized void disableUntil(TLEventSource source, DateTime time) {
		KW<TLEventSource> key = new KW<TLEventSource>(source);
		if ( registry.containsKey(key) ) {
			registry.put(key, time);
		}
	}
	
	/**
	 * Завершить работу со всеми источниками событий.
	 */
	public synchronized void close() {
		for ( KW<TLEventSource> key : registry.keySet() ) {
			key.instance().close();
		}
		registry.clear();
	}

}
