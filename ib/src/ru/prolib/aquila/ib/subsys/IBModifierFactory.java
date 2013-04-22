package ru.prolib.aquila.ib.subsys;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Интерфейс фабрики модификаторов атрибутов объектов бизнес-модели.
 * <p>
 * 2012-12-20<br>
 * $Id: IBModifierFactory.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public interface IBModifierFactory {

	/**
	 * Модификатор торгового счета заявки на основе события
	 * {@link IBEventOpenOrder}.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableOrder> orderOoAccount();

	/**
	 * Модификатор направления заявки на основе события
	 * {@link IBEventOpenOrder}.
	 * <p> 
	 * @return модификатор
	 */
	public S<EditableOrder> orderOoDir();
	
	/**
	 * Модификатор кол-ва заявки на основе события
	 * {@link IBEventOpenOrder}.
	 * <p> 
	 * @return модификатор
	 */
	public S<EditableOrder> orderOoQty();

	/**
	 * Модификатор дескриптора торгуемого инструмента заявки на основе события
	 * {@link IBEventOpenOrder}.
	 * <p> 
	 * @return модификатор
	 */
	public S<EditableOrder> orderOoSecurityDescr();

	/**
	 * Модификатор статуса заявки на основе события
	 * {@link IBEventOpenOrder}.
	 * <p> 
	 * @return модификатор
	 */
	public S<EditableOrder> orderOoStatus();

	/**
	 * Модификатор типа заявки на основе события
	 * {@link IBEventOpenOrder}.
	 * <p> 
	 * @return модификатор
	 */
	public S<EditableOrder> orderOoType();

	/**
	 * Модификатор статуса заявки на основе события
	 * {@link IBEventOrderStatus}.
	 * <p> 
	 * @return модификатор
	 */
	public S<EditableOrder> orderOsStatus();
	
	/**
	 * Модификатор неисполненного остатка заявки на основе события
	 * {@link IBEventOrderStatus}.
	 * <p> 
	 * @return модификатор
	 */
	public S<EditableOrder> orderOsQtyRest();
	
	/**
	 * Модификатор исполненного объема заявки на основе события
	 * {@link IBEventOrderStatus}.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableOrder> orderOsExecutedVolume();
	
	/**
	 * Модификатор средней цены исполненной части заявки на основе события
	 * {@link IBEventOrderStatus}.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableOrder> orderOsAvgExecutedPrice();

	/**
	 * Модификатор инструмента на основании контракта.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableSecurity> securityContract();
	
	/**
	 * Модификатор инструмента на основании события о тиковых данных.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableSecurity> securityTick();
	
	/**
	 * Модификатор кэша портфеля на основе события {@link IBEventUpdateAccount}.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePortfolio> portCash();
	
	/**
	 * Можификатор баланса портфеля на основе события
	 * {@link IBEventUpdateAccount}.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePortfolio> portBalance();
	
	/**
	 * Модификатор текущего размера позиции {@link IBEventUpdatePortfolio}.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePosition> posCurrQty();
	
	/**
	 * Модификатор рыночной стоимости позиции.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePosition> posMarketValue();
	
	/**
	 * Модификатор балансовой стоимости позиции.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePosition> posBookValue();
	
	/**
	 * Модификатор вариационной маржи позиции.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePosition> posVarMargin();

}
