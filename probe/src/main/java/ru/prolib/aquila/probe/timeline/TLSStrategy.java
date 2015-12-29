package ru.prolib.aquila.probe.timeline;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

/**
 * Конвейр симуляции хронологии.
 * <p>
 */
public class TLSStrategy {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TLSStrategy.class);
	}
	
	private final EventSourceRepository sources;
	private final TLEventQueue queue;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param sources реестр источников событий
	 * @param queue очередь событий хронологии
	 */
	public TLSStrategy(EventSourceRepository sources,
			TLEventQueue queue)
	{
		super();
		this.sources = sources;
		this.queue = queue;
	}
	
	/**
	 * Выполнить симуляцию шага.
	 * <p>
	 * @return true - продолжать симуляцию, false - симуляция завершена
	 */
	public boolean execute() {
		List<TLEventSource> list;
		while ( (list = sources.getSources(queue.getPOA())).size() > 0 ) {
			for ( TLEventSource src : list ) {
				interrogate(src);
			}
		}
		TLEventStack stack = queue.pullStack();
		if ( stack != null ) {
			stack.execute();
		}
		return queue.shiftToNextStack();
	}
	
	/**
	 * Опросить источник событий.
	 * <p>
	 * @param src источник событий
	 */
	private void interrogate(TLEventSource src) {
		TLEvent event;
		LocalDateTime poa = queue.getPOA();
		Interval wp = queue.getInterval();
		try {
			if ( src.closed() ) {
				sources.removeSource(src);
				logger.debug("Remove event source (closed): {}", src);
			} else if ( (event = src.pullEvent()) == null ) {
				sources.removeSource(src);
				logger.debug("Remove event source (gave null event): {}", src);
			} else if ( wp.contains(event.getTime().toInstant(ZoneOffset.UTC)) ) {
				queue.pushEvent(event);
				if ( poa.compareTo(event.getTime()) < 0 ) {
					sources.disableUntil(src, event.getTime());
				}
			} else if ( poa.compareTo(event.getTime()) > 0 ) {
				// Источник выдал событие более раннее, чем текущее значение ТА.
				// Такого быть не должно - этот источник баганутый.
				sources.removeSource(src);
				logger.error("Remove corrupted event source (gave past event): {}", src);
			} else {
				// Источник выдал событие более позднее, чем конец РП.
				// Это нормальная ситуация, но источник нужно исключить из
				// реестра, что бы не опрашивать его в дальнейшем.
				sources.removeSource(src);
				logger.debug("Remove event source (end of period reached): {}", src);
			}
		} catch ( TLException e ) {
			// Это может быть либо ошибка работы источника (например IO),
			// либо запоздавшее событие, что означает некорректную реализацию
			// источника. Независимо от причин, удаляем "сломанный" источник
			// из набора доступных источников.
			sources.removeSource(src);
			logger.error("Remove event source " + src + " due exception", e);
		}
	}

}
