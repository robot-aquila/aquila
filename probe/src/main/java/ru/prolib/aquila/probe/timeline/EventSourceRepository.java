package ru.prolib.aquila.probe.timeline;

import java.util.List;

import org.joda.time.DateTime;

public interface EventSourceRepository {

	/**
	 * Получить источники событий.
	 * <p>
	 * Возвращает источники событий, которые действительны начиная с указанного
	 * времени.
	 * <p>
	 * @param time временная метка
	 * @return список источников
	 */
	public abstract List<TLEventSource> getSources(DateTime time);

	/**
	 * Получить источники событий.
	 * <p>
	 * Возвращает список всех зарегистрированных источников событий.
	 * <p>
	 * @return список источников
	 */
	public abstract List<TLEventSource> getSources();

	/**
	 * Добавить источник событий.
	 * <p>
	 * @param source источник событий
	 */
	public abstract void registerSource(TLEventSource source);

	/**
	 * Удалить источник событий.
	 * <p>
	 * Удаляет источник из реестра. Если указанный источник не был добавлен,
	 * то ничего не выполняет.
	 * <p>
	 * @param source источник событий
	 */
	public abstract void removeSource(TLEventSource source);

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
	public abstract void disableUntil(TLEventSource source, DateTime time);

	/**
	 * Завершить работу со всеми источниками событий.
	 */
	public abstract void close();

	/**
	 * Проверить факт регистрации источника событий.
	 * <p>
	 * @param source источник для проверки
	 * @return true - если источник зарегистрирован, false - источник не
	 * зарегистрирован
	 */
	public abstract boolean isRegistered(TLEventSource source);

	/**
	 * Получить время следующего включения источника в выборку.
	 * <p>
	 * @param source источник событий
	 * @return null, если источник не зарегистрирован или
	 * не находится в состоянии временного исключения. Если источник временно
	 * исключен из процесса, то возвращается временная метка, указывающая на
	 * момент включения источника в выборку. 
	 */
	public abstract DateTime getDisabledUntil(TLEventSource source);

}