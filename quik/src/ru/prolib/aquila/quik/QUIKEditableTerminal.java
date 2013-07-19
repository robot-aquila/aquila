package ru.prolib.aquila.quik;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.*;

/**
 * Служебный интерфейс терминала QUIK.
 */
public interface QUIKEditableTerminal extends EditableTerminal {

	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	public Cache getDataCache();
	
	/**
	 * Получить клиенское подключение к QUIK.
	 * <p>
	 * @return клиент
	 */
	public QUIKClient getClient();
	
}
