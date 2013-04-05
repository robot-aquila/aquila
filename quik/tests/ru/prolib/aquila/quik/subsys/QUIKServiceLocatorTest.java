package ru.prolib.aquila.quik.subsys;

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
import ru.prolib.aquila.quik.QUIKConfig;
import ru.prolib.aquila.quik.QUIKConfigImpl;
import ru.prolib.aquila.quik.subsys.order.QUIKOrderProcessor;
import ru.prolib.aquila.quik.subsys.portfolio.QUIKAccounts;
import ru.prolib.aquila.quik.subsys.security.*;
import ru.prolib.aquila.t2q.T2QService;


/**
 * 2013-01-19<br>
 * $Id: QUIKServiceLocatorTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class QUIKServiceLocatorTest {
	private static IMocksControl control;
	private static EditableTerminal terminal;
	private static QUIKServiceLocator locator;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		locator = new QUIKServiceLocator(terminal);
	}
	
	@Test
	public void testGetTerminal() throws Exception {
		assertSame(terminal, locator.getTerminal());
		assertSame(terminal, locator.getTerminal());
	}
	
	@Test
	public void testGetEventSystem() throws Exception {
		EventSystemImpl es = (EventSystemImpl) locator.getEventSystem();
		assertSame(es, locator.getEventSystem());
	}
	
	@Test
	public void testGetDescriptors() throws Exception {
		QUIKSecurityDescriptorsImpl descrs =
			(QUIKSecurityDescriptorsImpl) locator.getDescriptors();
		assertSame(descrs, locator.getDescriptors());
		assertSame(locator, descrs.getServiceLocator());
	}
	
	@Test
	public void testGetCompFactory() throws Exception {
		QUIKCompFactory expected = new QUIKCompFactory(locator,
				new BMFactoryImpl(locator.getEventSystem(), terminal));
		
		QUIKCompFactory actual = locator.getCompFactory();
		assertEquals(expected, actual);
		assertSame(actual, locator.getCompFactory());
	}
	
	@Test
	public void testGetFailedOrderId() throws Exception {
		Counter expected = new SimpleCounter();
		Counter actual = locator.getFailedOrderNumerator();
		assertEquals(expected, actual);
		assertSame(actual, locator.getFailedOrderNumerator());
	}

	@Test
	public void testGetTransactionService() throws Exception {
		T2QService ts = control.createMock(T2QService.class);
		locator.setTransactionService(ts);
		assertSame(ts, locator.getTransactionService());
	}
	
	@Test (expected=QUIKServiceNotAvailableException.class)
	public void testGetTransactionService_ThrowsIfNoService() throws Exception {
		locator.getTransactionService();
	}
	
	@Test
	public void testGetTransactionNumerator() throws Exception {
		Counter transId = locator.getTransactionNumerator();
		assertNotNull(transId);
		assertEquals(new SimpleCounter(), transId);
		assertSame(transId, locator.getTransactionNumerator());
	}
	
	@Test
	public void testGetOrderProcessor() throws Exception {
		QUIKOrderProcessor expected = new QUIKOrderProcessor(locator);
		QUIKOrderProcessor actual = locator.getOrderProcessor();
		assertEquals(expected, actual);
		assertSame(actual, locator.getOrderProcessor());
	}
	
	@Test
	public void testGetTimer() throws Exception {
		Timer timer = locator.getTimer();
		assertNotNull(timer);
		assertSame(timer, locator.getTimer());
	}
	
	@Test
	public void testSetTimer() throws Exception {
		Timer timer = new Timer();
		locator.setTimer(timer);
		assertSame(timer, locator.getTimer());
		assertSame(timer, locator.getTimer());
	}
	
	@Test
	public void SetConfig() throws Exception {
		assertNull(locator.getConfig());
		QUIKConfig config = new QUIKConfigImpl();
		locator.setConfig(config);
		assertSame(config, locator.getConfig());
		assertSame(config, locator.getConfig());
	}
	
	@Test
	public void getAccounts() throws Exception {
		QUIKAccounts accounts = locator.getAccounts();
		assertNotNull(accounts);
		assertSame(accounts, locator.getAccounts());
		assertSame(terminal, accounts.getFirePanicEvent());
	}

}
