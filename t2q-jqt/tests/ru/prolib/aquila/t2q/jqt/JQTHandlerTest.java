package ru.prolib.aquila.t2q.jqt;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.JQTrans.JQTransServer;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QConnStatus;
import ru.prolib.aquila.t2q.T2QHandler;
import ru.prolib.aquila.t2q.T2QOrder;
import ru.prolib.aquila.t2q.T2QTrade;
import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * 2013-01-31<br>
 * $Id: JQTHandlerTest.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class JQTHandlerTest {
	private IMocksControl control;
	private T2QHandler commonHandler;
	private JQTHandler handler;
	private JQTransServer server;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		//Logger.getRootLogger().setLevel(Level.OFF);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		commonHandler = control.createMock(T2QHandler.class);
		server = control.createMock(JQTransServer.class);
		handler = new JQTHandler(commonHandler);
		handler.setServer(server);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(commonHandler, handler.getCommonHandler());
		assertSame(server, handler.getServer());
	}
	
	@Test
	public void testOnConnectionStatus_UnknownStatus() throws Exception {
		commonHandler.OnConnStatus(null);
		control.replay();
		handler.OnConnectionStatus(128, 0, "Test message");
		control.verify();
	}
	
	@Test
	public void testOnConnectionStatus_Ok() throws Exception {
		Map<Integer, T2QConnStatus> map = new HashMap<Integer, T2QConnStatus>();
		map.put( 8, T2QConnStatus.QUIK_CONN);
		map.put( 9, T2QConnStatus.QUIK_DISC);
		map.put(10, T2QConnStatus.DLL_CONN);
		map.put(11, T2QConnStatus.DLL_DISC);
		Iterator<Map.Entry<Integer, T2QConnStatus>> it =
				map.entrySet().iterator();
	    while ( it.hasNext() ) {
	        Map.Entry<Integer, T2QConnStatus> pair = it.next();
	        control.resetToStrict();
	        commonHandler.OnConnStatus(pair.getValue());
	        control.replay();
	        handler.OnConnectionStatus(pair.getKey(), 123, "Test");
	        control.verify();
	    }
	}
	
	@Test
	public void testOnTransactionReply_ResultNok() throws Exception {
		commonHandler.OnTransReply(T2QTransStatus.ERR_NOK, 12345L, null, "Err");
		control.replay();
		handler.OnTransactionReply(1, 15, 0, 12345L, 0L, "Err");
		control.verify();
	}
	
	@Test
	public void testOnTransactionReply_Ok() throws Exception {
		Map<Integer,T2QTransStatus> map = new HashMap<Integer,T2QTransStatus>();
		map.put( 0, T2QTransStatus.SENT);
		map.put( 1, T2QTransStatus.RECV);
		map.put( 2, T2QTransStatus.ERR_CON);
		map.put( 3, T2QTransStatus.DONE);
		map.put( 4, T2QTransStatus.ERR_TSYS);
		map.put( 5, T2QTransStatus.ERR_REJ);
		map.put( 6, T2QTransStatus.ERR_LIMIT);
		map.put(10, T2QTransStatus.ERR_UNSUPPORTED);
		map.put(11, T2QTransStatus.ERR_AUTH);
		map.put(12, T2QTransStatus.ERR_TIMEOUT);
		map.put(13, T2QTransStatus.ERR_CROSS);
		Iterator<Map.Entry<Integer, T2QTransStatus>> it =
			map.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, T2QTransStatus> pair = it.next();
			control.resetToStrict();
			commonHandler.OnTransReply(pair.getValue(), 123L, 876L, "Test");
			control.replay();
			handler.OnTransactionReply(0, 0, pair.getKey(), 123L, 876L, "Test");
			control.verify();
		}
	}
	
	@Test
	public void testOnOrderStatus_Ok() throws Exception {
		T2QOrder expected = new T2QOrder(1, 2L, 34L, "foo", "bar", 5.6d, 7,
			8.9d, true, 2, "FIRM", "CLIENT", "ACCOUNT",
			200L, 20130314L, 24051L, 101010L,// qty, date, time, activationTime 
			202020L, 20130315, 2.0d, // withdrawTime, expiry, accruedInt
			15.05d, 100L, "UID", "Test"); // yield, uid, userId, brokerRef
		expect(server.getOrderFirmId(891L)).andReturn("FIRM");
		expect(server.getOrderClientCode(891L)).andReturn("CLIENT");
		expect(server.getOrderAccount(891L)).andReturn("ACCOUNT");
		expect(server.getOrderQty(891L)).andReturn(200L);
		expect(server.getOrderDate(891L)).andReturn(20130314L);
		expect(server.getOrderTime(891L)).andReturn(24051L);
		expect(server.getOrderActivationTime(891L)).andReturn(101010L);
		expect(server.getOrderWithdrawTime(891L)).andReturn(202020L);
		expect(server.getOrderExpiry(891L)).andReturn(20130315L);
		expect(server.getOrderAccruedInt(891L)).andReturn(2.0d);
		expect(server.getOrderYield(891L)).andReturn(15.05d);
		expect(server.getOrderUid(891L)).andReturn(100L);
		expect(server.getOrderUserId(891L)).andReturn("UID");
		expect(server.getOrderBrokerRef(891L)).andReturn("Test");
		commonHandler.OnOrderStatus(eq(expected));
		control.replay();
		
		fail("TODO: incomplete");
		//handler.OnOrderStatus(1,2L,34L,"foo","bar",5.6d,7,8.9d,true,2,891);
		
		control.verify();
	}
	
	@Test
	public void testOnOrderStatus_NoServerInstance() throws Exception {
		handler.setServer(null);
		control.replay();
		
		fail("TODO: incomplete");
		//handler.OnOrderStatus(1,2L,34L,"foo","bar",5.6d,7,8.9d,true,2,891);
		
		control.verify();
	}
	
	@Test
	public void testOnTradeStatus_NoServerInstance() throws Exception {
		handler.setServer(null);
		control.replay();
		
		fail("TODO: incomplete");
		//handler.OnTradeStatus(0,80L,18L,"SRT","APL",420.0d,100L,42.0d,false,3L);
		
		control.verify();
	}
	
	@Test
	public void testOnTradeStatus_Ok() throws Exception {
		T2QTrade expected = new T2QTrade(0, 80L, 8L, "SRT", "APL", 420.0d,
			100L, 42.0d, false,
			20130314L, 20130415L, 203000L, // date, settleDate, time
			false, 1.15d, 18.25d, // isMarginal, accruedInt, yield
			0.1d, 0.2d, // tsCommission, clearingCenterCommission
			0.3d, 0.4d, // exchangeCommission, tradingSystemCommission
			30.94d, 0.14d, 22.15d, 14.86d,//price2,repoRate,repoValue,repo2Value
			1.8d, 5L, 12.44d, // accruedInt2, repoTerm, startDiscount,
			18.5d, 11.23d, true, //lowerDiscount,upperDiscount,blockSecurities
			"USD", "RUR", "ABC", // currency, settleCurrency, settleCode
			"LX01", "test", "865", "1514", //account,brokerRef,clientCode,userId
			"FIRM","PFIRM","LSE", // firmId, partnerFirmId, exchangeCode,
			"STID"); // stationId
		expect(server.getTradeDate(34L)).andReturn(20130314L);
		expect(server.getTradeSettleDate(34L)).andReturn(20130415L);
		expect(server.getTradeTime(34L)).andReturn(203000L);
		expect(server.getTradeIsMarginal(34L)).andReturn(false);
		expect(server.getTradeAccruedInt(34L)).andReturn(1.15d);
		expect(server.getTradeYield(34L)).andReturn(18.25d);
		expect(server.getTradeTsCommission(34L)).andReturn(0.1d);
		expect(server.getTradeClearingCenterCommission(34L)).andReturn(0.2d);
		expect(server.getTradeExchangeCommission(34L)).andReturn(0.3d);
		expect(server.getTradeTradingSystemCommission(34L)).andReturn(0.4d);
		expect(server.getTradePrice2(34L)).andReturn(30.94d);
		expect(server.getTradeRepoRate(34L)).andReturn(0.14d);
		expect(server.getTradeRepoValue(34L)).andReturn(22.15d);
		expect(server.getTradeRepo2Value(34L)).andReturn(14.86d);
		expect(server.getTradeAccruedInt2(34L)).andReturn(1.8d);
		expect(server.getTradeRepoTerm(34L)).andReturn(5);
		expect(server.getTradeStartDiscount(34L)).andReturn(12.44d);
		expect(server.getTradeLowerDiscount(34L)).andReturn(18.5d);
		expect(server.getTradeUpperDiscount(34L)).andReturn(11.23d);
		expect(server.getTradeBlockSecurities(34L)).andReturn(true);
		expect(server.getTradeCurrency(34L)).andReturn("USD");
		expect(server.getTradeSettleCurrency(34L)).andReturn("RUR");
		expect(server.getTradeSettleCode(34L)).andReturn("ABC");
		expect(server.getTradeAccount(34L)).andReturn("LX01");
		expect(server.getTradeBrokerRef(34L)).andReturn("test");
		expect(server.getTradeClientCode(34L)).andReturn("865");
		expect(server.getTradeUserId(34L)).andReturn("1514");
		expect(server.getTradeFirmId(34L)).andReturn("FIRM");
		expect(server.getTradePartnerFirmId(34L)).andReturn("PFIRM");
		expect(server.getTradeExchangeCode(34L)).andReturn("LSE");
		expect(server.getTradeStationId(34L)).andReturn("STID");
		commonHandler.OnTradeStatus(eq(expected));
		control.replay();
		
		fail("TODO: incomplete");
		//handler.OnTradeStatus(0,80L,8L,"SRT","APL",420.0d,100L,42.0d,false,34L);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<T2QHandler> vHandler = new Variant<T2QHandler>()
			.add(commonHandler)
			.add(control.createMock(T2QHandler.class));
		Variant<JQTransServer> vServer = new Variant<JQTransServer>(vHandler)
			.add(server)
			.add(control.createMock(JQTransServer.class));
		Variant<?> iterator = vServer;
		int foundCnt = 0;
		JQTHandler x = null, found = null;
		do {
			x = new JQTHandler(vHandler.get());
			x.setServer(vServer.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(commonHandler, found.getCommonHandler());
		assertSame(server, found.getServer());
	}

}
