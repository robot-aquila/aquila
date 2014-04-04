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
public class TLEventSources {
	private final Map<KW<TLEventSource>, DateTime> sources; 
	
	/**
	 * Конструктор.
	 */
	public TLEventSources() {
		super();
		sources = new LinkedHashMap<KW<TLEventSource>, DateTime>();
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
		for ( Map.Entry<KW<TLEventSource>, DateTime> e : sources.entrySet() ) {
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
		for ( KW<TLEventSource> key : sources.keySet() ) {
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
		if ( ! sources.containsKey(key)) {
			sources.put(key, null);
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
		sources.remove(new KW<TLEventSource>(source));
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
		if ( sources.containsKey(key) ) {
			sources.put(key, time);
		}
	}
	
	/**
	 * Завершить работу со всеми источниками событий.
	 */
	public synchronized void close() {
		for ( KW<TLEventSource> key : sources.keySet() ) {
			key.instance().close();
		}
		sources.clear();
	}

}
