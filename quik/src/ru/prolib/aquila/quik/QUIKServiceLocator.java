package ru.prolib.aquila.quik;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.quik.assembler.cache.CacheBuilder;

/**
 * Сервис-локатор.
 */
public class QUIKServiceLocator {
	private final Cache cache;
	private final QUIKClient client;
	
	/**
	 * Конструктор (служебный).
	 * <p>
	 * Реализация подключения к QUIK работает только в ОС Windows. Единственный
	 * способ протестировать работу локатора это замена подключения моком с
	 * последующим впрыском через этот конструктор.  
	 * <p>
	 * @param client API-клиент
	 * @param cache кэш данных
	 */
	public QUIKServiceLocator(QUIKClient client, Cache cache) {
		super();
		this.cache = cache;
		this.client = client;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад событийной системы.
	 */
	public QUIKServiceLocator(EventSystem es) {
		this(new QUIKClient(), new CacheBuilder().createCache(es));
	}

	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	public Cache getDataCache() {
		return cache;
	}
	
	/**
	 * Получить клиенское подключение к QUIK.
	 * <p>
	 * @return клиент
	 */
	public QUIKClient getClient() {
		return client;
	}
	
}
