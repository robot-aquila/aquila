package ru.prolib.aquila.ib.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

import com.ib.client.*;

public class IBWrapperTest {
	private IMocksControl control;
	private MainHandler hMain;
	private ContractHandler hContr1, hContr2, hContr3;
	private OrderHandler hOrder1, hOrder2, hOrder3;
	private IBWrapper wrapper;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		hMain = control.createMock(MainHandler.class);
		hContr1 = control.createMock(ContractHandler.class);
		hContr2 = control.createMock(ContractHandler.class);
		hContr3 = control.createMock(ContractHandler.class);
		hOrder1 = control.createMock(OrderHandler.class);
		hOrder2 = control.createMock(OrderHandler.class);
		hOrder3 = control.createMock(OrderHandler.class);
		wrapper = new IBWrapper();
		wrapper.setMainHandler(hMain);
	}
	
	/**
	 * Фикстурный дескриптор обработчика контракта.
	 */
	static class ContFR {
		private final int reqId;
		private final ContractHandler handler;
		
		ContFR(int reqId, ContractHandler handler) {
			super();
			this.reqId = reqId;
			this.handler = handler;
		}
		
	}
	
	/**
	 * Фикстурный дескриптор обработчика заявки.
	 */
	static class OrderFR {
		private final int reqId;
		private final OrderHandler handler;
		
		OrderFR(int reqId, OrderHandler handler) {
			super();
			this.reqId = reqId;
			this.handler = handler;
		}
	}
	
	/**
	 * Создать дескриптор деталей контракта.
	 * <p>
	 * @param id идентификатор контракта
	 * @return дескриптор контракта
	 */
	private ContractDetails createContractDetails(int id) {
		ContractDetails details1 = new ContractDetails();
		details1.m_summary = new Contract();
		details1.m_summary.m_conId = id;
		return details1;
	}
	
	/**
	 * Создать экземпляр состояния заявки.
	 * <p>
	 * Конструктор класса статуса защищенный. Данный метод создает экземпляр
	 * используя рефлекшн API.
	 * <p>
	 * @return новый экземпляр состояния
	 * @throws Exception
	 */
	private OrderState createOrderState() throws Exception {
		Constructor<OrderState> con = OrderState.class.getDeclaredConstructor();
		con.setAccessible(true);
		con.newInstance();
		return con.newInstance();		
	}
	
	@Test
	public void testGetMainHandler() throws Exception {
		assertSame(hMain, wrapper.getMainHandler());
	}
	
	@Test
	public void testGetContractHandler() throws Exception {
		assertNull(wrapper.getContractHandler(412));
		wrapper.setContractHandler(412, hContr1);
		assertSame(hContr1, wrapper.getContractHandler(412));
	}
	
	@Test
	public void testGetOrderHandler() throws Exception {
		assertNull(wrapper.getOrderHandler(891));
		wrapper.setOrderHandler(891, hOrder1);
		assertSame(hOrder1, wrapper.getOrderHandler(891));
	}
	
	@Test
	public void testManagedAccounts() throws Exception {
		hMain.managedAccounts(eq("one,two"));
		control.replay();
		
		wrapper.managedAccounts("one,two");
		
		control.verify();
	}
	
	@Test
	public void testNextValidId() throws Exception {
		hMain.nextValidId(eq(815));
		control.replay();
		
		wrapper.nextValidId(815);
		
		control.verify();
	}
	
	@Test
	public void testConnectionOpened() throws Exception {
		wrapper.setContractHandler(1, hContr1);
		wrapper.setContractHandler(2, hContr2);
		wrapper.setContractHandler(3, hContr3);
		wrapper.setOrderHandler(7, hOrder1);
		wrapper.setOrderHandler(8, hOrder2);
		wrapper.setOrderHandler(9, hOrder3);
		
		hMain.connectionOpened();
		hContr1.connectionOpened();
		hContr2.connectionOpened();
		hContr3.connectionOpened();
		hOrder1.connectionOpened();
		hOrder2.connectionOpened();
		hOrder3.connectionOpened();
		control.replay();
		
		wrapper.connectionOpened();
		
		control.verify();
	}
	
	@Test
	public void testConnectionClosed() throws Exception {
		wrapper.setContractHandler(1, hContr1);
		wrapper.setContractHandler(2, hContr2);
		wrapper.setContractHandler(3, hContr3);
		wrapper.setOrderHandler(7, hOrder1);
		wrapper.setOrderHandler(8, hOrder2);
		wrapper.setOrderHandler(9, hOrder3);

		hContr1.connectionClosed();
		hContr2.connectionClosed();
		hContr3.connectionClosed();
		hOrder1.connectionClosed();
		hOrder2.connectionClosed();
		hOrder3.connectionClosed();
		hMain.connectionClosed();
		control.replay();
		
		wrapper.connectionClosed();
		
		control.verify();
	}
	
	@Test
	public void testContractDetails_WithHandler() throws Exception {
		wrapper.setContractHandler(84, hContr1);
		
		ContractDetails details = createContractDetails(924);
		hContr1.contractDetails(eq(84), eq(details));
		control.replay();
		
		wrapper.contractDetails(84, details);
	
		control.verify();
	}
	
	@Test
	public void testContractDetails_MainHandler() throws Exception {
		ContractDetails details = createContractDetails(114);
		hMain.contractDetails(eq(672), eq(details));
		control.replay();
		
		wrapper.contractDetails(672, details);
		
		control.verify();
	}
	
	@Test
	public void testExecDetails() throws Exception {
		control.replay();
		
		wrapper.execDetails(415, new Contract(), new Execution());
		
		control.verify();
	}

	@Test
	public void testOpenOrder_WithHandler() throws Exception {
		wrapper.setOrderHandler(721, hOrder1);
		
		Contract contract = new Contract();
		Order order = new Order();
		OrderState state = createOrderState();
		hOrder1.openOrder(eq(721), eq(contract), eq(order), eq(state));
		control.replay();
		
		wrapper.openOrder(721, contract, order, state);
		
		control.verify();
	}
	
	@Test
	public void testOpenOrder_MainHandler() throws Exception {
		Contract contract = new Contract();
		Order order = new Order();
		OrderState state = createOrderState();
		hMain.openOrder(eq(2292), eq(contract), eq(order), eq(state));
		control.replay();
		
		wrapper.openOrder(2292, contract, order, state);
		
		control.verify();
	}
	
	@Test
	public void testOrderStatus_WithHandler() throws Exception {
		wrapper.setOrderHandler(972, hOrder1);
		hOrder1.orderStatus(eq(972), eq("foo"), eq(14), eq(6), eq(215d),
				eq(100), eq(0), eq(214d), eq(5), eq("X"));
		control.replay();
		
		wrapper.orderStatus(972, "foo", 14, 6, 215d, 100, 0, 214d, 5, "X");
		
		control.verify();
	}
	
	@Test
	public void testOrderStatus_MainHandler() throws Exception {
		hMain.orderStatus(eq(416), eq("foo"), eq(14), eq(6), eq(215d),
				eq(100), eq(0), eq(214d), eq(5), eq("X"));
		control.replay();
		
		wrapper.orderStatus(416, "foo", 14, 6, 215d, 100, 0, 214d, 5, "X");
		
		control.verify();
	}
	
	@Test
	public void testTickGeneric_WithHandler() throws Exception {
		wrapper.setContractHandler(514, hContr1);
		hContr1.tickPrice(eq(514), eq(2), eq(14.72d));
		control.replay();
		
		wrapper.tickGeneric(514, 2, 14.72d);
		
		control.verify();
	}
	
	@Test
	public void testTickGeneric_MainHandler() throws Exception {
		hMain.tickPrice(eq(218), eq(4), eq(24.72d));
		control.replay();
		
		wrapper.tickGeneric(218, 4, 24.72d);
		
		control.verify();
	}

	@Test
	public void testTickPrice_WithHandler() throws Exception {
		wrapper.setContractHandler(882, hContr1);
		hContr1.tickPrice(eq(882), eq(1), eq(15.72d));
		control.replay();
		
		wrapper.tickPrice(882, 1, 15.72d, 0);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice_MainHandler() throws Exception {
		hMain.tickPrice(eq(191), eq(0), eq(18d));
		control.replay();
		
		wrapper.tickPrice(191, 0, 18d, 54192);
		
		control.verify();
	}
	
	@Test
	public void testTickSize_WithHandler() throws Exception {
		wrapper.setContractHandler(117, hContr1);
		hContr1.tickSize(eq(117), eq(1), eq(800));
		control.replay();
		
		wrapper.tickSize(117, 1, 800);
		
		control.verify();
	}
	
	@Test
	public void testTickSize_MainHandler() throws Exception {
		hMain.tickSize(eq(642), eq(0), eq(1800));
		control.replay();
		
		wrapper.tickSize(642, 0, 1800);
		
		control.verify();
	}
	
	@Test
	public void testUpdateAccountValue() throws Exception {
		hMain.updateAccount(eq("foo"), eq("24"), eq("USD"), eq("TEST"));
		control.replay();
		
		wrapper.updateAccountValue("foo", "24", "USD", "TEST");
		
		control.verify();
	}
	
	@Test
	public void testUpdatePortfolio() throws Exception {
		Contract contract = new Contract();
		hMain.updatePortfolio(eq(contract), eq(10), eq(13.48d), eq(130.15d),
				eq(13.22d), eq(0d), eq(1d), eq("TEST"));
		control.replay();
		
		wrapper.updatePortfolio(contract, 10, 13.48d, 130.15d, 13.22d, 0d, 1d,
				"TEST");
		
		control.verify();
	}
	
	@Test
	public void testError_Exception() throws Exception {
		control.replay();
		
		wrapper.error(new Exception("this is test exception"));
		
		control.verify();
	}
	
	@Test
	public void testError_Message() throws Exception {
		control.replay();
		
		wrapper.error("this is test error message");
		
		control.verify();
	}
	
	@Test
	public void testError_WithContractHandler() throws Exception {
		wrapper.setContractHandler(873, hContr1);
		hContr1.error(eq(873), eq(15), eq("test error message"));
		control.replay();
		
		wrapper.error(873, 15, "test error message");
		
		control.verify();
	}
	
	@Test
	public void testError_WithOrderHandler() throws Exception {
		wrapper.setOrderHandler(824, hOrder1);
		hOrder1.error(eq(824), eq(110), eq("another test error message"));
		control.replay();
		
		wrapper.error(824, 110, "another test error message");
		
		control.verify();
	}
	
	@Test
	public void testError_MainHandler() throws Exception {
		hMain.error(eq(824), eq(400), eq("some error message"));
		control.replay();
		
		wrapper.error(824, 400, "some error message");
		
		control.verify();
	}
	
	@Test
	public void testError_IfBothHandlersThenSelectContract() throws Exception {
		wrapper.setContractHandler(873, hContr1);
		wrapper.setOrderHandler(873, hOrder1);
		hContr1.error(eq(873), eq(15), eq("test error message"));
		control.replay();
		
		wrapper.error(873, 15, "test error message");
		
		control.verify();
	}
	
	@Test
	public void testAccountDownloadEnd() throws Exception {
		hMain.accountDownloadEnd(eq("TEST"));
		control.replay();
		
		wrapper.accountDownloadEnd("TEST");
		
		control.verify();
	}
	
	@Test
	public void testBondContractDetails_WithHandler() throws Exception {
		wrapper.setContractHandler(184, hContr1);
		
		ContractDetails details = createContractDetails(1924);
		hContr1.bondContractDetails(eq(184), eq(details));
		control.replay();
		
		wrapper.bondContractDetails(184, details);
	
		control.verify();
	}
	
	@Test
	public void testBondContractDetails_MainHandler() throws Exception {
		ContractDetails details = createContractDetails(726);
		hMain.bondContractDetails(eq(992), eq(details));
		control.replay();
		
		wrapper.bondContractDetails(992, details);
	
		control.verify();
	}
	
	@Test
	public void testComissionReport() throws Exception {
		CommissionReport report = new CommissionReport();
		hMain.commissionReport(same(report));
		control.replay();
		
		wrapper.commissionReport(report);
		
		control.verify();
	}
	
	@Test
	public void testContractDetailsEnd_WithHandler() throws Exception {
		wrapper.setContractHandler(882, hContr1);
		hContr1.contractDetailsEnd(eq(882));
		control.replay();
		
		wrapper.contractDetailsEnd(882);
		
		control.verify();
	}

	@Test
	public void testContractDetailsEnd_MainHandler() throws Exception {
		hMain.contractDetailsEnd(eq(1172));
		control.replay();
		
		wrapper.contractDetailsEnd(1172);
		
		control.verify();
	}
	
	@Test
	public void testCurrentTime() throws Exception {
		hMain.currentTime(eq(827133454L));
		control.replay();
		
		wrapper.currentTime(827133454L);
		
		control.verify();
	}
	
	@Test
	public void testDeltaNeutralValidation() throws Exception {
		control.replay();
		
		wrapper.deltaNeutralValidation(192, new UnderComp());
		
		control.verify();
	}
	
	@Test
	public void testExecDetailsEnd() throws Exception {
		control.replay();
		
		wrapper.execDetailsEnd(115);
		
		control.verify();
	}
	
	@Test
	public void testFundamentalData() throws Exception {
		control.replay();
		
		wrapper.fundamentalData(112, "foobar");
		
		control.verify();
	}
	
	@Test
	public void testHistoricalData() throws Exception {
		control.replay();
		
		wrapper.historicalData(221, "foobar", 5d, 10d, 4d, 8d, 10, 1, 1d, true);
		
		control.verify();
	}
	
	@Test
	public void testMarketDataType() throws Exception {
		control.replay();
		
		wrapper.marketDataType(8, 15);
		
		control.verify();
	}
	
	@Test
	public void testOpenOrderEnd() throws Exception {
		control.replay();
		
		wrapper.openOrderEnd();
		
		control.verify();
	}
	
	@Test
	public void testRealtimeBar() throws Exception {
		control.replay();
		
		wrapper.realtimeBar(889, 117L, 1d, 2d, 3d, 4d, 5, 6d, 7);
		
		control.verify();
	}
	
	@Test
	public void testReceiveFA() throws Exception {
		control.replay();
		
		wrapper.receiveFA(9, "xml");
		
		control.verify();
	}
	
	@Test
	public void testScannerData() throws Exception {
		control.replay();
		
		wrapper.scannerData(8, 1, createContractDetails(2), "x", "b", "p", "h");
		
		control.verify();
	}
	
	@Test
	public void testScannerDataEnd() throws Exception {
		control.replay();
		
		wrapper.scannerDataEnd(882);
		
		control.verify();
	}
	
	@Test
	public void testScannerParameters() throws Exception {
		control.replay();
		
		wrapper.scannerParameters("xml");
		
		control.verify();
	}
	
	@Test
	public void testTickEFP() throws Exception {
		control.replay();
		
		wrapper.tickEFP(112, 0, 2d, "2.00", 3d, 4, "date", 5d, 6d);
		
		control.verify();
	}
	
	@Test
	public void testTickOptionComputation() throws Exception {
		control.replay();
		
		wrapper.tickOptionComputation(84, 1, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d);
		
		control.verify();
	}
	
	@Test
	public void testTickSnapshotEnd() throws Exception {
		control.replay();
		
		wrapper.tickSnapshotEnd(8812);
		
		control.verify();
	}
	
	@Test
	public void testTickString() throws Exception {
		control.replay();
		
		wrapper.tickString(431, 2, "foobar");
		
		control.verify();
	}
	
	@Test
	public void testUpdateAccountTime() throws Exception {
		control.replay();
		
		wrapper.updateAccountTime("zulu");
		
		control.verify();
	}
	
	@Test
	public void testUpdateMktDepth() throws Exception {
		control.replay();
		
		wrapper.updateMktDepth(1, 2, 0, 5, 18d, 1000);
		
		control.verify();
	}
	
	@Test
	public void testUpdateMktDepthL2() throws Exception {
		control.replay();
		
		wrapper.updateMktDepthL2(801, 10, "X", 1, 2, 92d, 120);
		
		control.verify();
	}
	
	@Test
	public void testUpdateNewsBulletin() throws Exception {
		control.replay();
		
		wrapper.updateNewsBulletin(1, 2, "message", "IDEALPRO");
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(wrapper.equals(wrapper));
		assertFalse(wrapper.equals(null));
		assertFalse(wrapper.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<ContFR> conts1 = new Vector<ContFR>();
		conts1.add(new ContFR(1, hContr1));
		conts1.add(new ContFR(2, hContr2));
		List<ContFR> conts2 = new Vector<ContFR>();
		conts2.add(new ContFR(1, hContr2));
		conts2.add(new ContFR(2, hContr3));
		for ( ContFR fr : conts1 ) {
			wrapper.setContractHandler(fr.reqId, fr.handler);
		}
		
		List<OrderFR> ords1 = new Vector<OrderFR>();
		ords1.add(new OrderFR(10, hOrder2));
		List<OrderFR> ords2 = new Vector<OrderFR>();
		ords2.add(new OrderFR(10, hOrder2));
		ords2.add(new OrderFR(11, hOrder3));
		for ( OrderFR fr : ords1 ) {
			wrapper.setOrderHandler(fr.reqId, fr.handler);
		}
		
		Variant<MainHandler> vMain = new Variant<MainHandler>()
			.add(hMain)
			.add(control.createMock(MainHandler.class));
		Variant<List<ContFR>> vConts = new Variant<List<ContFR>>(vMain)
			.add(conts1)
			.add(conts2);
		Variant<List<OrderFR>> vOrds = new Variant<List<OrderFR>>(vConts)
			.add(ords1)
			.add(ords2);
		Variant<?> iterator = vOrds;
		int foundCnt = 0;
		IBWrapper x = null, found = null;
		do {
			x = new IBWrapper();
			x.setMainHandler(vMain.get());
			for ( ContFR fr : vConts.get() ) {
				x.setContractHandler(fr.reqId, fr.handler);
			}
			for ( OrderFR fr : vOrds.get() ) {
				x.setOrderHandler(fr.reqId, fr.handler);
			}
			if ( wrapper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		for ( ContFR fr : conts1 ) {
			assertTrue(found.hasContractHandler(fr.reqId));
		}
		for ( OrderFR fr : ords1 ) {
			assertTrue(found.hasOrderHandler(fr.reqId));
		}
	}

}
