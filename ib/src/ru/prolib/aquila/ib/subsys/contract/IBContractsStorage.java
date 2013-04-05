package ru.prolib.aquila.ib.subsys.contract;

import com.ib.client.ContractDetails;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.IBException;

/**
 * Интерфейс хранилища контрактов.
 * <p>
 * Хранилище контрактов прозрачно отслеживает все получаемые через IB API
 * контракты и обеспечивает доступ к контракту в том виде, в котором он был
 * получен от IB. Так же позволяет инициировать запрос деталей контракта
 * по идентификатору контракта.
 * <p>
 * 2013-01-04<br>
 * $Id: IBContractsStorage.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public interface IBContractsStorage {
	
	/**
	 * Получить детали контракта по идентификатору.
	 * <p>
	 * @param conId идентификатор контракта
	 * @return детали контракта
	 * @throws IBContractUnavailableException контракт не загружен
	 */
	public ContractDetails getContract(int conId) throws IBException;
	
	/**
	 * Проверить доступность информации о контракте.
	 * <p>
	 * Позволяет проверить был ли загружен контракт с указанным идентификатором. 
	 * <p>
	 * @param conId идентификатор контракта
	 * @return доступность контракта
	 */
	public boolean isContractAvailable(int conId);
	
	/**
	 * Запросить детали контракта.
	 * <p>
	 * Инициирует отправку запроса деталей контракта через IB API. Запрос
	 * отправляется независимо от того, была ли информация о контракте получена
	 * ранее или нет.
	 * <p>
	 * @param conId идентификатор контракта
	 */
	public void loadContract(int conId);
	
	/**
	 * Получить тип события: при следующей загрузке деталей контракта.
	 * <p>
	 * Возвращает тип события, позволяющий отследить событие, связанное с
	 * поступлением информации о контракте с указанным идентификатором. Список
	 * получателей очищается каждый раз, после получения информации по
	 * контракту.
	 * <p>
	 * @param conId идентификатор контракта
	 * @return тип события
	 */
	public EventType OnContractLoadedOnce(int conId);
	
	public void start();

}
