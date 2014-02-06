package ru.prolib.aquila.quik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;

/**
 * Терминал QUIK.
 */
public class QUIKTerminal extends TerminalImpl<QUIKServiceLocator> {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QUIKTerminal.class);
	}

	/**
	 * Конструктор (служебный).
	 * <p>
	 * @param es фасад событийной системы
	 * @param locator сервис-локатор
	 */
	public QUIKTerminal(EventSystem es, QUIKServiceLocator locator) {
		super(es);
		setServiceLocator(locator);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает экземпляр боевой версии сервис-локатора (только для Win).
	 * <p>
	 * @param es фасад событийной системы
	 */
	public QUIKTerminal(EventSystem es) {
		this(es, new QUIKServiceLocator(es));
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param queueId идентификатор очереди событий
	 */
	public QUIKTerminal(String queueId) {
		this(new EventSystemImpl(new EventQueueImpl(queueId)));
	}

	/**
	 * Получить кэш данных.
	 * <p>
	 * @return кэш данных
	 */
	public Cache getDataCache() {
		return getServiceLocator().getDataCache();
	}

	/**
	 * Получить API-подключение.
	 * <p> 
	 * @return объект подключения
	 */
	public QUIKClient getClient() {
		return getServiceLocator().getClient();
	}
	
	@Override
	public void requestSecurity(SecurityDescriptor descr) {
		logger.warn("TODO: not implemented");
	}

}
