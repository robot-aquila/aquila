package ru.prolib.aquila.ib.subsys.security;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.subsys.IBCompFactory;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.IBRequestContract;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactory;
import ru.prolib.aquila.ib.subsys.api.IBRequestMarketData;
import ru.prolib.aquila.ib.subsys.security.IBSecurityHandler;
import ru.prolib.aquila.ib.subsys.security.IBSecurityHandlerFactory;
import ru.prolib.aquila.ib.subsys.security.IBSecurityHandlerFactoryImpl;

/**
 * 2012-11-23<br>
 * $Id: IBSecurityHandlerFactoryImplTest.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBSecurityHandlerFactoryImplTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private IBServiceLocator locator;
	private static IBRequestFactory reqfactory; 
	private static IBCompFactory compfact; 
	private static IBRequestContract reqContr;
	private static IBRequestMarketData reqMktData;
	private static IBSecurityHandlerFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("AAPL","SMART","EUR",SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(IBServiceLocator.class);
		reqfactory = control.createMock(IBRequestFactory.class);
		compfact = control.createMock(IBCompFactory.class);
		reqContr = control.createMock(IBRequestContract.class);
		reqMktData = control.createMock(IBRequestMarketData.class);
		factory = new IBSecurityHandlerFactoryImpl(locator, 5000L);
		
		expect(locator.getRequestFactory()).andStubReturn(reqfactory);
		expect(locator.getCompFactory()).andStubReturn(compfact);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateHandler() throws Exception {
		Contract contract = new Contract();
		contract.m_secType = "FUT";
		contract.m_exchange = "SMART";
		contract.m_currency = "EUR";
		contract.m_symbol = "AAPL";
		S<EditableSecurity> modifier = control.createMock(S.class);
		expect(reqfactory.requestContract(contract)).andReturn(reqContr);
		expect(reqfactory.requestMarketData(contract)).andReturn(reqMktData);
		expect(compfact.mSecurity()).andReturn(modifier);
		control.replay();
		IBSecurityHandler handler = factory.createHandler(descr);
		control.verify();
		assertSame(descr, handler.getSecurityDescriptor());
		assertSame(locator, handler.getServiceLocator());
		assertSame(reqContr, handler.getRequestContract());
		assertSame(modifier, handler.getSecurityModifier());
		assertEquals(5000L, handler.getRequestTimeout());
	}

}
