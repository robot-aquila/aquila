package ru.prolib.aquila.ib;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Интерфейс терминала IB.
 * <p>
 * Дополнительные публичные методы терминала.
 */
public interface IBTerminal extends Terminal {
	
	/**
	 * Асинхронный запрос инструмента.
	 * <p>
	 * @param descr дескриптор инструмента
	 */
	public void requestSecurity(SecurityDescriptor descr);
	
	/**
	 * Запрос деталей контракта.
	 * <p>
	 * @param contractId идентификатор контракта
	 */
	public void requestContract(int contractId);
	
	/**
	 * Получить тип события: при провале запроса инструмента.
	 * <p>
	 * Позволяет отлавливать ошибки на запросы инструментов. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityRequestError();

}
