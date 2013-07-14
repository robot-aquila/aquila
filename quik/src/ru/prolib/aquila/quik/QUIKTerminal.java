package ru.prolib.aquila.quik;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.quik.assembler.cache.Cache;

/**
 * Публичный интерфейс терминала QUIK.
 * <p>
 * Определяет дополнительные методы доступа к специфическим механизмам
 * реализации.
 */
public interface QUIKTerminal extends Terminal {
	
	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	public Cache getDataCache();

}
