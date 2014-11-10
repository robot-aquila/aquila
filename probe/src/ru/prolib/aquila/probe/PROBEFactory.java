package ru.prolib.aquila.probe;

import java.util.Properties;
import org.joda.time.*;
import org.joda.time.format.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.probe.timeline.*;

/**
 * Фабрика эмулятора терминала.
 */
public class PROBEFactory implements TerminalFactory {
	public static final String RUN_INTERVAL_START = "run-interval-start";
	public static final String RUN_INTERVAL_END = "run-interval-end";
	
	private static final String ID_PREFIX = "Probe";
	private static final Counter id = new SimpleCounter();
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	/**
	 * Конструктор.
	 */
	public PROBEFactory() {
		super();
	}
	
	/**
	 * Получить следующий идентификатор.
	 * <p>
	 * Формирует идентификатор очередного экземпляра терминала.
	 * <p>
	 * @return идентификатор
	 */
	private synchronized static final String getNextId() {
		return ID_PREFIX + id.incrementAndGet();
	}

	/**
	 * Создать терминал.
	 * <p>
	 * Примечание: используется формат времени <i>yyyy-MM-dd HH:mm:ss.SSS</i><br>
	 * Параметры конфигурации:<br>
	 * <i>run-interval-start</i> - время начала рабочего интервала<br> 
	 * <i>run-interval-end</i> - время окончания рабочего интервала (не вкл.)<br>
	 */
	@Override
	public Terminal createTerminal(Properties config) throws Exception {
		PROBETerminal terminal = new PROBETerminal(getNextId());
		PROBEServiceLocator locator = terminal.getServiceLocator();
		EventSystem es = terminal.getEventSystem();
		locator.setTimeline(createTimeline(es, config));
		StarterQueue starter = terminal.getStarter();
		starter.add(new EventQueueStarter(es.getEventQueue(), 3000));
		return terminal;
	}
	
	private TLSTimeline
		createTimeline(EventSystem es, Properties config)
	{
		return new TLSTimelineFactory(es).produce(new Interval(
				df.parseDateTime(config.getProperty(RUN_INTERVAL_START)),
				df.parseDateTime(config.getProperty(RUN_INTERVAL_END))));
	}

}
