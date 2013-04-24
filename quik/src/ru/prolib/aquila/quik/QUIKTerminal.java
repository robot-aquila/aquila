package ru.prolib.aquila.quik;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.quik.dde.Cache;

/**
 * Публичный интерфейс терминала QUIK.
 * <p>
 * Определяет дополнительные методы доступа к специфическим механизмам
 * реализации.
 */
interface QUIKTerminal extends Terminal {
	
	/**
	 * Получить кэш DDE.
	 * <p>
	 * @return кэш DDE
	 */
	public Cache getDdeCache();

}
