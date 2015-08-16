package ru.prolib.aquila.probe;

import java.util.Properties;
import org.joda.time.*;
import org.joda.time.format.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;

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
	
	private final PROBETerminalBuilder builder;

	public PROBEFactory() {
		this(new PROBETerminalBuilder());
	}
	
	public PROBEFactory(PROBETerminalBuilder builder) {
		super();
		this.builder = builder;
	}
	
	private PROBETerminalBuilder getTerminalBuilder() {
		return builder;
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
	
	public PROBETerminal createTerminal() throws Exception {
		Properties props = new Properties();
		props.setProperty(PROBEFactory.RUN_INTERVAL_START, "2015-01-01 00:00:00.000");
		props.setProperty(PROBEFactory.RUN_INTERVAL_END, "2015-01-01 23:59:59.000");
		props.setProperty(PROBEFactory.DATA_STORAGE_PATH, "");
		return createTerminal(props);
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
	public PROBETerminal createTerminal(Properties config) throws Exception {
		throw new RuntimeException("Not implemented");
		/*
		return getTerminalBuilder()
			.withCommonEventSystemAndQueueId(getNextId())
			.withCommonTimelineAndTimeInterval(new Interval(
					df.parseDateTime(config.getProperty(RUN_INTERVAL_START)),
					df.parseDateTime(config.getProperty(RUN_INTERVAL_END))))
			.withCommonDataStorageAndPath(config.getProperty(DATA_STORAGE_PATH))
			.buildTerminal();
		*/
	}

}
