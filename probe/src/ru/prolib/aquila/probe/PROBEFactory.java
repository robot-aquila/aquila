package ru.prolib.aquila.probe;

import java.io.File;
import java.util.Properties;
import org.joda.time.*;
import org.joda.time.format.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.probe.internal.PROBEServiceLocator;
import ru.prolib.aquila.probe.internal.XFactory;

/**
 * Фабрика эмулятора терминала.
 */
public class PROBEFactory implements TerminalFactory {
	public static final String RUN_INTERVAL_START = "run-interval-start";
	public static final String RUN_INTERVAL_END = "run-interval-end";
	public static final String DATA_STORAGE_PATH = "data-storage-path";
	
	private static final String ID_PREFIX = "Probe";
	private static final Counter id = new SimpleCounter();
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	private final XFactory x;

	/**
	 * Конструктор (служебный).
	 * <p>
	 * @param x
	 */
	public PROBEFactory(XFactory x) {
		super();
		this.x = x;
	}
	
	/**
	 * Конструктор.
	 */
	public PROBEFactory() {
		this(new XFactory());
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
	 * Получить номер последнего созданного экземпяра.
	 * <p>
	 * @return номер экземпляра
	 */
	public synchronized int getInstancesCount() {
		return id.get();
	}

	/**
	 * Создать терминал.
	 * <p>
	 * Примечание: используется формат времени <i>yyyy-MM-dd HH:mm:ss.SSS</i><br>
	 * Параметры конфигурации:<br>
	 * <i>run-interval-start</i> - время начала рабочего интервала<br> 
	 * <i>run-interval-end</i> - время окончания рабочего интервала (не вкл.)<br>
	 * <i>data-storage-path</i> - путь к каталогу с данными<br>
	 */
	@Override
	public Terminal createTerminal(Properties config) throws Exception {
		PROBETerminal terminal = x.newTerminal(getNextId());
		PROBEServiceLocator locator = terminal.getServiceLocator();
		EventSystem es = terminal.getEventSystem();
		locator.setTimeline(x.newTimeline(es, new Interval(
				df.parseDateTime(config.getProperty(RUN_INTERVAL_START)),
				df.parseDateTime(config.getProperty(RUN_INTERVAL_END)))));
		terminal.setScheduler(x.newScheduler(locator.getTimeline()));
		locator.setDataProvider(x.newDataProvider(terminal));
		File root = new File(config.getProperty(DATA_STORAGE_PATH));
		locator.setDataStorage(x.newDataStorage(root));		
		StarterQueue starter = terminal.getStarter();
		starter.add(x.newQueueStarter(es.getEventQueue(), 3000));
		return terminal;
	}

}
