package ru.prolib.aquila.ib.subsys.run;

import ru.prolib.aquila.ib.event.IBEventOrder;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Интерфейс фабрики исполняемых задач.
 * <p>
 * 2013-01-07<br>
 * $Id: IBRunnableFactory.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBRunnableFactory {
	
	/**
	 * Создать обновление заявки.
	 * <p>
	 * @param event событие-основание
	 * @return задача
	 */
	public Runnable createUpdateOrder(IBEventOrder event);
	
	/**
	 * Создать обновление позиции.
	 * <p>
	 * @param event событие основание
	 * @return задача
	 */
	public Runnable createUpdatePosition(IBEventUpdatePortfolio event);
	
	/**
	 * Создать обновление торгового счета (портфеля).
	 * <p>
	 * @param event событие-основание
	 * @return задача
	 */
	public Runnable createUpdateAccount(IBEventUpdateAccount event);

}
