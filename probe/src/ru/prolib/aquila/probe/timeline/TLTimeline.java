package ru.prolib.aquila.probe.timeline;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Хронология событий.
 */
public class TLTimeline {
	private final TLEventSources sources;
	private final TLEventCache cache;
	private final TLTimelineHelper helper;
	private final Interval interval;
	
	/**
	 * Конструктор (для тестов).
	 * <p>
	 * @param sources источник событий
	 * @param timeline хронология
	 * @param helper набор вспомогательных функций 
	 * @param interval рабочий период
	 */
	public TLTimeline(TLEventSources sources, TLEventCache timeline,
			TLTimelineHelper helper, Interval interval)
	{
		super();
		this.sources = sources;
		this.cache = timeline;
		this.helper = helper;
		this.interval = interval;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param sources источник событий
	 * @param timeline хронология
	 * @param interval рабочий период
	 */
	public TLTimeline(TLEventSources sources, TLEventCache timeline,
			Interval interval)
	{
		this(sources, timeline, new TLTimelineHelper(), interval);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param interval рабочий период
	 */
	public TLTimeline(Interval interval) {
		this(new TLEventSources(), new TLEventCache(interval.getStart()),
				interval);
	}
	
	TLEventSources getEventSources() {
		return sources;
	}
	
	TLEventCache getEventCache() {
		return cache;
	}
	
	TLTimelineHelper getHelper() {
		return helper;
	}
	
	/**
	 * Получить ТА.
	 * <p>
	 * @return точка актуальности
	 */
	public DateTime getPOA() {
		return cache.getPOA();
	}
	
	/**
	 * Получить рабочий период.
	 * <p>
	 * @return РП
	 */
	public Interval getInterval() {
		return interval;
	}
	
	/**
	 * Добавить событие на шкалу времени.
	 * <p>
	 * @param event событие
	 * @throws TLOutOfDateException запаздывающее событие 
	 */
	public void pushEvent(TLEvent event) throws TLOutOfDateException {
		cache.pushEvent(event);
	}
	
	/**
	 * Зарегистрировать источник событий.
	 * <p>
	 * @param source источник событий
	 */
	public void registerSource(TLEventSource source) {
		sources.registerSource(source);
	}
	
	/**
	 * Прекратить работу с источником событий.
	 * <p>
	 * @param source источник событий
	 */
	public void removeSource(TLEventSource source) {
		sources.removeSource(source);
	}
	
	/**
	 * Выполнить шаг вперед по шкале времени.
	 * <p>
	 * Данный метод является основным методом работы с временной шкалой - он
	 * формирует стек событий очередного шага и выполняет события шага. 
	 * <p>
	 * @return true - можно продолжать, false - конец данных
	 * @throws TLException
	 */
	public boolean nextTimeStep() throws TLException {
		List<TLEvent> events = helper.pullEvents(getPOA(), sources);
		if ( events.size() == 0 ) {
			return false;
		}
		helper.pushEvents(events, cache);
		TLEventStack stack = cache.pullStack();
		if ( stack == null ) {
			return false;
		}
		stack.execute();
		return interval.contains(cache.getPOA());
	}
	
	/**
	 * Закрыть источники событий.
	 */
	public void close() {
		sources.close();
	}
	
}
