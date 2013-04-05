package ru.prolib.aquila.ib.subsys.security;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.getter.*;
import ru.prolib.aquila.ib.subsys.*;

/**
 * Фабрика поставщиков инструментов.
 * <p>
 * 2012-11-23<br>
 * $Id: IBSecurityHandlerFactoryImpl.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBSecurityHandlerFactoryImpl implements IBSecurityHandlerFactory {
	private final IBServiceLocator locator;
	private final long timeout;
	private final G<Contract> gSecDescr2Contr;

	/**
	 * Создать фабрику.
	 * <p>
	 * @param locator сервис-локатор
	 * @param timeout таймаут ожидания ответов ms
	 */
	public IBSecurityHandlerFactoryImpl(IBServiceLocator locator,long timeout) {
		super();
		this.locator = locator;
		this.timeout = timeout;
		this.gSecDescr2Contr = new IBGetSecurityDescriptorContract();
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис локатор
	 */
	public IBServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить таймаут ожидания ответов.
	 * <p>
	 * @return таймаут ms
	 */
	public long getTimeout() {
		return timeout;
	}

	@Override
	public IBSecurityHandler createHandler(SecurityDescriptor descr) {
		Contract contract = gSecDescr2Contr.get(descr);
		return new IBSecurityHandler(locator, descr, 
			locator.getRequestFactory().requestContract(contract),
			locator.getRequestFactory().requestMarketData(contract),
			locator.getCompFactory().mSecurity(), timeout);
	}

}
