package ru.prolib.aquila.ib;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Интерфейс терминала IB.
 * <p>
 * Дополнительные публичные методы терминала.
 */
public interface IBTerminal extends Terminal {
	
	/**
	 * Запрос деталей контракта.
	 * <p>
	 * @param contractId идентификатор контракта
	 */
	public void requestContract(int contractId);

}
