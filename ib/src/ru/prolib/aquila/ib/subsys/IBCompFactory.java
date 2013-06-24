package ru.prolib.aquila.ib.subsys;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactory;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;

/**
 * Интерфейс фабрики компонентов терминала Interactive Brokers.
 * <p>
 * 2012-11-23<br>
 * $Id: IBCompFactory.java 527 2013-02-14 15:14:09Z whirlwind $
 */
@Deprecated
public interface IBCompFactory extends BMFactory {
	
	/**
	 * Создать объект подключения к IB API.
	 * <p>
	 * @return объект подключения
	 */
	public IBClient createClient();

	/**
	 * Создать модификатор инструмента.
	 * <p>
	 * @return модификатор инструмента
	 */
	public S<EditableSecurity> mSecurity();
	
	/**
	 * Создать фабрику запросов IB API.
	 * <p>
	 * @return фабрика запросов к IB API
	 */
	public IBRequestFactory createRequestFactory();

	/**
	 * Создать фасад подсистемы контрактов.
	 * <p>
	 * @return фасад подсистемы контрактов
	 */
	public IBContracts createContracts();
	
	/**
	 * Создать модификатор портфеля.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePortfolio> mPortfolio();
	
	/**
	 * Создать модификатор позиции.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePosition> mPosition();
	
	/**
	 * Создать модификатор заявки.
	 * <p>
	 * Создает модификатор заявки на основании событий типа
	 * {@link IBEventOpenOrder} или {@link IBEventOrderStatus}.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableOrder> mOrder();

}
