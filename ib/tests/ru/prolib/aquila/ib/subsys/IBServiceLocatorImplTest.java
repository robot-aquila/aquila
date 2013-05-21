package ru.prolib.aquila.ib.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Timer;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;
import ru.prolib.aquila.ib.subsys.IBServiceLocatorImpl;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactory;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactoryImpl;
import ru.prolib.aquila.ib.subsys.contract.IBContractUtilsImpl;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;
import ru.prolib.aquila.ib.subsys.contract.IBContractsImpl;
import ru.prolib.aquila.ib.subsys.contract.IBContractsStorageImpl;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactory;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactoryImpl;

/**
 * 2013-01-08<br>
 * $Id: IBServiceLocatorImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBServiceLocatorImplTest {
	private static IMocksControl control;
	private static EditableTerminal terminal;
	private static EditableOrders orders;
	private static EditablePortfolios portfolios;
	private IBServiceLocatorImpl locator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		orders = control.createMock(EditableOrders.class);
		portfolios = control.createMock(EditablePortfolios.class);
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getOrdersInstance()).andStubReturn(orders);
		expect(terminal.getPortfoliosInstance()).andStubReturn(portfolios);
		locator = new IBServiceLocatorImpl(terminal);
	}
	
	@Test
	public void testGetEventSystem() throws Exception {
		EventSystem eSys = locator.getEventSystem();
		assertNotNull(eSys);
		assertSame(eSys, locator.getEventSystem());
	}
	
	@Test
	public void testGetApiEventSystem() throws Exception {
		EventSystem eApiSys = locator.getApiEventSystem();
		assertNotNull(eApiSys);
		assertNotSame(eApiSys, locator.getEventSystem());
	}
	
	@Test
	public void testGetApiClient() throws Exception {
		IBClient client = locator.getApiClient();
		assertNotNull(client);
		assertSame(client, locator.getApiClient());
	}
	
	@Test
	public void testGetTerminal() throws Exception {
		assertSame(terminal, locator.getTerminal());
	}
	
	@Test
	public void testGetContracts() {
		IBContractsImpl contracts = (IBContractsImpl) locator.getContracts();
		assertSame(contracts, locator.getContracts());
		IBContracts expected = new IBContractsImpl(
			new IBContractsStorageImpl(locator,
				((IBContractsStorageImpl) contracts.getContractsStorage())
					.getEventDispatcher()), new IBContractUtilsImpl());
		assertEquals(expected, contracts);
		assertSame(contracts, locator.getContracts());
	}
	
	@Test
	public void testGetRequestFactory() throws Exception {
		IBRequestFactory expected = new IBRequestFactoryImpl(
				locator.getEventSystem(), locator,
				locator.getRequestNumerator());
		IBRequestFactory actual = locator.getRequestFactory(); 
		assertEquals(expected, actual);
		assertSame(actual, locator.getRequestFactory());
	}
	
	@Test
	public void testGetRunnableFactory() throws Exception {
		control.replay();
		IBRunnableFactory expected = new IBRunnableFactoryImpl(terminal,
				locator.getContracts(),
				new OrderResolverStd(terminal,
						locator.getCompFactory().createOrderFactory()),
				locator.getCompFactory().mPortfolio(),
				locator.getCompFactory().mOrder(),
				locator.getCompFactory().mPosition());
		IBRunnableFactory actual = locator.getRunnableFactory();
		control.verify();
		assertEquals(expected, actual);
		assertSame(actual, locator.getRunnableFactory());
	}
	
	@Test
	public void testGetCompFactory() throws Exception {
		IBCompFactory expected = new IBCompFactoryImpl(locator);
		IBCompFactory actual = locator.getCompFactory();
		assertEquals(expected, actual);
		assertSame(actual, locator.getCompFactory());
	}

	@Test
	public void testGetTransactionNumerator() throws Exception {
		Counter expected = new SimpleCounter();
		Counter actual = locator.getTransactionNumerator();
		assertEquals(expected, actual);
		assertSame(actual, locator.getTransactionNumerator());
	}
	
	@Test
	public void testGetRequestNumerator() throws Exception {
		Counter expected = locator.getTransactionNumerator();
		assertSame(expected, locator.getRequestNumerator());
	}
	
	@Test
	public void testGetTimer() throws Exception {
		Timer actual = locator.getTimer();
		assertNotNull(actual);
		assertSame(actual, locator.getTimer());
	}
	
	@Test
	public void testSetEventSystem() throws Exception {
		EventSystem es = control.createMock(EventSystem.class);
		locator.setEventSystem(es);
		assertSame(es, locator.getEventSystem());
	}
	
	@Test
	public void testSetRequestFactory() throws Exception {
		IBRequestFactory factory = control.createMock(IBRequestFactory.class);
		locator.setRequestFactory(factory);
		assertSame(factory, locator.getRequestFactory());
	}
	
}
