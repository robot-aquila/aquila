package ru.prolib.aquila.ib.subsys;

import java.util.Timer;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactory;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactoryImpl;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactory;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactoryImpl;

/**
 * Локатор сервисов.
 * <p>
 * 2013-01-08<br>
 * $Id: IBServiceLocatorImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBServiceLocatorImpl implements IBServiceLocator {
	private final EditableTerminal terminal;
	private final Counter transId = new SimpleCounter();
	private EventSystem eSys,eSysApi;
	private IBClient client;
	private IBCompFactory fcomp;
	private IBContracts contracts;
	private IBRequestFactory freq;
	private IBRunnableFactory frun;
	private Timer timer;
	
	public IBServiceLocatorImpl(EditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}

	@Override
	public synchronized EventSystem getEventSystem() {
		if ( eSys == null ) {
			eSys = new EventSystemImpl(new EventQueueImpl("IB"));
		}
		return eSys;
	}

	@Override
	public synchronized EventSystem getApiEventSystem() {
		if ( eSysApi == null ) {
			eSysApi = new EventSystemImpl(new EventQueueImpl("IB-API"));
		}
		return eSysApi;
	}

	@Override
	public synchronized IBClient getApiClient() {
		if ( client == null ) {
			client = getCompFactory().createClient();
		}
		return client;
	}

	@Override
	public synchronized EditableTerminal getTerminal() {
		return terminal;
	}

	@Override
	public synchronized IBContracts getContracts() {
		if ( contracts == null ) {
			contracts = getCompFactory().createContracts();
		}
		return contracts;
	}

	@Override
	public synchronized IBRequestFactory getRequestFactory() {
		if ( freq == null ) {
			freq = new IBRequestFactoryImpl(getEventSystem(),
					this, getRequestNumerator());
		}
		return freq;
	}

	@Override
	public synchronized IBRunnableFactory getRunnableFactory() {
		if ( frun == null ) {
			IBCompFactory fc = getCompFactory();
			frun = new IBRunnableFactoryImpl(getTerminal(),
				fc.createPortfolioFactory(), getContracts(),
				new OrderResolverStd(getTerminal(),fc.createOrderFactory()),
				fc.mPortfolio(), fc.mOrder(), fc.mPosition());
		}
		return frun;
	}

	@Override
	public synchronized IBCompFactory getCompFactory() {
		if ( fcomp == null ) {
			fcomp = new IBCompFactoryImpl(this);
		}
		return fcomp;
	}

	@Override
	public synchronized Counter getTransactionNumerator() {
		return transId;
	}

	@Override
	public synchronized Counter getRequestNumerator() {
		return getTransactionNumerator();
	}

	@Override
	public synchronized Timer getTimer() {
		if ( timer == null ) {
			timer = new Timer(true);
		}
		return timer;
	}

	@Override
	public synchronized void setEventSystem(EventSystem es) {
		this.eSys = es;
	}

	@Override
	public synchronized void setRequestFactory(IBRequestFactory factory) {
		this.freq = factory;
	}

}
