package ru.prolib.aquila.probe.timeline;

import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Стратегия опроса источников событий.
 * <p>
 * Этот класс содержит набор низкоуровневых функций эмулятора. 
 */
public class TLSIntrgStrategy {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TLSIntrgStrategy.class);
	}
	
	private final TLEventSources sources;
	private final TLEventQueue queue;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param sources набор источников событий
	 * @param queue последовательность событий
	 */
	public TLSIntrgStrategy(TLEventSources sources, TLEventQueue queue) {
		super();
		this.sources = sources;
		this.queue = queue;
	}
		
	/**
	 * Опросить источник событий.
	 * <p>
	 * @param src источник событий
	 */
	public void interrogate(TLEventSource src) {
		TLEvent event;
		DateTime poa = queue.getPOA();
		try {
			if ( src.closed() ) {
				sources.removeSource(src);
			} else if ( (event = src.pullEvent()) != null ) {
				queue.pushEvent(event);
				if ( poa.compareTo(event.getTime()) < 0 ) {
					sources.disableUntil(src, event.getTime());
				}
			} else {
				sources.disableUntil(src, poa.plus(1));
			}
		} catch ( TLException e ) {
			// Это может быть либо ошибка работы источника (например IO),
			// либо запоздавшее событие, что означает некорректную реализацию
			// источника. Независимо от причин, удаляем "сломанный" источник
			// из набора доступных источников.
			sources.removeSource(src);
			logger.error("Event request failed: ", e);
		}
	}
	
	/**
	 * Получить источники для опроса.
	 * <p>
	 * @return список источников. Пустой список свидетельствует о том, что
	 * в моменте не осталось источников для опроса.
	 */
	public List<TLEventSource> getForInterrogating() {
		return sources.getSources(queue.getPOA());
	}

}
