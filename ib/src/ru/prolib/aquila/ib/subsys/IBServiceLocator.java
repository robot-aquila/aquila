package ru.prolib.aquila.ib.subsys;

import java.util.Timer;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactory;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactory;

/**
 * Сервис-локатор.
 * <p>
 * 2013-01-08<br>
 * $Id: IBServiceLocator.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public interface IBServiceLocator {
	
	public EventSystem getEventSystem();
	
	public EventSystem getApiEventSystem();
	
	public IBClient getApiClient();
	
	public EditableTerminal getTerminal();
	
	public IBContracts getContracts();

	public IBRequestFactory getRequestFactory();
	
	public IBRunnableFactory getRunnableFactory();
	
	public IBCompFactory getCompFactory();
	
	public Counter getTransactionNumerator();
	
	public Counter getRequestNumerator();
	
	public Timer getTimer();
	
	public void setEventSystem(EventSystem es);
	
	public void setRequestFactory(IBRequestFactory factory);

}
