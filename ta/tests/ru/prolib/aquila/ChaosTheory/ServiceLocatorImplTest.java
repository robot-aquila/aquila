package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.ipc.ltam.Session;
import ru.prolib.aquila.rxltdde.Receiver.ReceiverService;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.stat.TrackingTradesImpl;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.jdbc.DbAccessor;
import ru.prolib.aquila.ta.ds.jdbc.DbAccessorImpl;
import ru.prolib.aquila.ta.ds.quik.ExportQuik;
import ru.prolib.aquila.ta.ds.quik.RXltDdeDispatcher;

public class ServiceLocatorImplTest {
	ServiceLocatorImpl locator;

	@Before
	public void setUp() throws Exception {
		locator = new ServiceLocatorImpl();
	}
	
	@Test
	public void testGetDatabase_Ok() throws Exception {
		DbAccessor dba = new DbAccessorImpl();
		locator.setDatabase(dba);
		assertSame(dba, locator.getDatabase());
	}
	
	@Test (expected=ServiceLocatorNoServiceException.class)
	public void testGetDatabase_ThrowsNoInstance() throws Exception {
		locator.getDatabase();
	}
	
	@Test
	public void testGetMarketData_Ok() throws Exception {
		MarketData ds = createMock(MarketData.class);
		locator.setMarketData(ds);
		assertSame(ds, locator.getMarketData());
	}
	
	@Test (expected=ServiceLocatorNoServiceException.class)
	public void testGetMarketData_ThrowsNoInstance() throws Exception {
		locator.getMarketData();
	}
	
	@Test
	public void testGetIpcSession_Ok() throws Exception {
		Session sess = new Session();
		locator.setIpcSession(sess);
		assertSame(sess, locator.getIpcSession());
	}
	
	@Test
	public void testGetIpcSession_CreateDefault() throws Exception {
		Session sess = (Session) locator.getIpcSession();
		assertNotNull(sess);
	}
	
	@Test
	public void testGetRXltDdeDispatcher_Ok() throws Exception {
		RXltDdeDispatcher disp = new RXltDdeDispatcher();
		locator.setRXltDdeDispatcher(disp);
		assertSame(disp, locator.getRXltDdeDispatcher());
	}
	
	@Test
	public void testGetRXltDdeDispatcher_CreateDefault() throws Exception {
		RXltDdeDispatcher disp = locator.getRXltDdeDispatcher();
		assertNotNull(disp);
	}
	
	@Test
	public void testGetRXltDdeReceiver_Ok() throws Exception {
		ReceiverService recv = new ReceiverService("localhost",12561,null);
		locator.setRXltDdeReceiver(recv);
		assertSame(recv, locator.getRXltDdeReceiver());
	}
	
	@Test
	public void testGetRXltDdeReceiver_CreateDefault() throws Exception {
		//ReceiverService recv = locator.getRXltDdeReceiver();
		// TODO: тест состояния по добавлению акцессоров в сервис
		//assertNotNull(recv);
	}
	
	@Test
	public void testGetExportService_Ok() throws Exception {
		ExportQuik service = new ExportQuik("allDeals");
		locator.setExportService(service);
		assertSame(service, locator.getExportService());
	}
	
	@Test (expected=ServiceLocatorNoServiceException.class)
	public void testGetExportService_ThrowsNoInstance() throws Exception {
		locator.getExportService();
	}
	
	@Test
	public void testGetProperties_Ok() throws Exception {
		Props props = new PropsImpl();
		locator.setProperties(props);
		assertSame(props, locator.getProperties());
	}
	
	@Test
	public void testGetProperties_CreateDefault() throws Exception {
		Props props = locator.getProperties();
		assertEquals(0, props.size());
	}
	
	@Test
	public void testGetPortfolio_Ok() throws Exception {
		Portfolio port = createMock(Portfolio.class);
		locator.setPortfolio(port);
		assertSame(port, locator.getPortfolio());
	}

	@Test (expected=ServiceLocatorNoServiceException.class)
	public void testGetPortfolio_ThrowsNoInstance() throws Exception {
		locator.getPortfolio();
	}

	@Test
	public void testGetAssets_Ok() throws Exception {
		Assets assets = createMock(Assets.class);
		locator.setAssets(assets);
		assertSame(assets, locator.getAssets());
	}

	@Test (expected=ServiceLocatorNoServiceException.class)
	public void testGetAssets_ThrowsNoInstance() throws Exception {
		locator.getAssets();
	}
	
	@Test
	public void testGetPortfolioState_Ok() throws Exception {
		PortfolioState state = createMock(PortfolioState.class);
		locator.setPortfolioState(state);
		assertSame(state, locator.getPortfolioState());
	}
	
	@Test (expected=ServiceLocatorNoServiceException.class)
	public void testGetPortfolioState_ThrowsNoInstance() throws Exception {
		locator.getPortfolioState();
	}
	
	@Test
	public void testGetPortfolioOrders_Ok() throws Exception {
		PortfolioOrders orders = createMock(PortfolioOrders.class);
		locator.setPortfolioOrders(orders);
		assertSame(orders, locator.getPortfolioOrders());
	}
	
	@Test (expected=ServiceLocatorNoServiceException.class)
	public void testGetPortfolioOrders_ThrowsNoInstance() throws Exception {
		locator.getPortfolioOrders();
	}
	
	@Test
	public void testGetTrackingTrades_Ok() throws Exception {
		TrackingTrades tracking = createMock(TrackingTrades.class);
		locator.setTrackingTrades(tracking);
		assertSame(tracking, locator.getTrackingTrades());
	}
	
	@Test
	public void testGetTrackingTrades_DefaultInstance() throws Exception {
		TrackingTradesImpl tracking =
				(TrackingTradesImpl) locator.getTrackingTrades();
		assertNotNull(tracking);
	}

}
