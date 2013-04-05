package ru.prolib.aquila.quik.subsys.row;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.row.Spec;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.subsys.*;

/**
 * 2013-02-15<br>
 * $Id$
 */
public class RowAdaptersTest {
	private QUIKConfigImpl config;
	private QUIKServiceLocator locator;
	private RowAdapters adapters;

	@Before
	public void setUp() throws Exception {
		config = new QUIKConfigImpl();
		config.dateFormat = "yyyy-MM/dd";
		config.timeFormat = "HH:mm:ss.S";
		locator = new QUIKServiceLocator(new TerminalDecorator());
		locator.setConfig(config);
		adapters = new RowAdapters(locator);
	}
	
	@Test
	public void testEquals() throws Exception {
		QUIKServiceLocator locator2 =
			new QUIKServiceLocator(new TerminalDecorator());
		assertTrue(adapters.equals(adapters));
		assertTrue(adapters.equals(new RowAdapters(locator)));
		assertFalse(adapters.equals(new RowAdapters(locator2)));
		assertFalse(adapters.equals(null));
		assertFalse(adapters.equals(this));
	}
	
	@Test
	public void testGetAllDealsRequiredFields() throws Exception {
		String[] expected = {
				"TRADENUM",
				"TRADEDATE",
				"TRADETIME",
				"SECCODE",
				"CLASSCODE",
				"PRICE",
				"QTY",
				"BUYSELL",
				"VALUE",
		};
		assertArrayEquals(expected, adapters.getAllDealsRequiredFields());
	}
	
	@Test
	public void testGetPortfolioStkRequiredFields() throws Exception {
		String[] expected = {
				"FIRMID",
				"CLIENTCODE",
				"TOTALMONEYBAL",	// кэш
				"ALLASSETS",		// баланс (стоимость активов+кэш)
				"PROFITLOSS",		// доход/убыток за сессию
								// стоимость активов = ALLASSETS - TOTALMONEYBAL 
		};
		assertArrayEquals(expected, adapters.getPortfolioStkRequiredFields());
	}
	
	@Test
	public void testGetPortfolioFutRequiredFields() throws Exception {
		String[] expected = {
				"FIRMID",
				"TRDACCID",
				"CBPLPLANNED",
				"CBPLIMIT",
				"VARMARGIN",
				"LIMIT_TYPE",
		};
		assertArrayEquals(expected, adapters.getPortfolioFutRequiredFields());
	}
	
	@Test
	public void testGetPositionStkRequiredFields() throws Exception {
		String[] expected = {
				"FIRMID",
				"CLIENT_CODE",
				"TRDACCID",
				"SEC_SHORT_NAME",
				"OPENBAL",
				"CURRENTBAL",
				"LOCKED",
		};
		assertArrayEquals(expected, adapters.getPositionStkRequiredFields());
	}
	
	@Test
	public void testGetPositionFutRequiredFields() throws Exception {
		String[] expected = {
				"FIRMID",
				"TRDACCID",
				"SEC_SHORT_NAME",
				"START_NET",
				"TOTAL_NET",
				"VARMARGIN",
		};
		assertArrayEquals(expected, adapters.getPositionFutRequiredFields());
	}
	
	@Test
	public void testGetOrderRequiredFields() throws Exception {
		String[] expected = {
				"ORDERNUM",
				"TRANSID",
				"STATUS",
				"SECCODE",
				"CLASSCODE",
				"ACCOUNT",
				"CLIENTCODE",
				"BUYSELL",
				"QTY",
				"BALANCE",
				"PRICE",
				"ORDERDATE",
				"ORDERTIME",
				"WITHDRAW_DATE",
				"WITHDRAW_TIME",
				"MODE",
		};
		assertArrayEquals(expected, adapters.getOrderRequiredFields());
	}
	
	@Test
	public void testGetStopOrderRequiredFields() throws Exception {
		String[] expected = {
				"STOP_ORDERNUM",
				"TRANSID",
				"STATUS",
				"SECCODE",
				"CLASSCODE",
				"ACCOUNT",
				"CLIENTCODE",
				"BUYSELL",
				"QTY",
				"PRICE",
				"STOP_ORDERKIND",
				"LINKED_ORDER",
				"CONDITION_PRICE",
				"CONDITION_PRICE2",
				"OFFSET",
				"OFFSET_UNITS",
				"SPREAD",
				"SPREAD_UNITS",
				"STOP_ORDERDATE",
				"STOP_ORDERTIME",
		};
		assertArrayEquals(expected, adapters.getStopOrderRequiredFields());
	}
	
	@Test
	public void testGetSecurityRequiredFields() throws Exception {
		String[] expected = {
				"lotsize",
				"pricemax",
				"pricemin",
				"steppricet",
				"SEC_PRICE_STEP",
				"SEC_SCALE",
				"CODE",
				"CLASS_CODE",
				"last",
				"open",
				"prevlegalclosepr",
				"LONGNAME",
				"SHORTNAME",
				"offer",
				"bid",
				"high",
				"low",
				"curstepprice",
				"CLASSNAME",
		};
		
		assertArrayEquals(expected, adapters.getSecurityRequiredFields());
	}
	
	@Test
	public void testCreateAllDealsAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Trades: ");
		expected.put("TRD_DIR", ea.createOrderDir("BUYSELL", "BUY", "SELL"));
		expected.put("TRD_ID", ea.createLong("TRADENUM"));
		expected.put("TRD_PRICE", ea.createDouble("PRICE"));
		expected.put("TRD_QTY", ea.createLong("QTY"));
		expected.put("TRD_SECDESCR", ea.createSecDescr("SECCODE", "CLASSCODE"));
		expected.put("TRD_TIME", ea.createDate("TRADEDATE", "TRADETIME",
				"yyyy-MM/dd", "HH:mm:ss.S", true));
		expected.put("TRD_VOL", ea.createDouble("VALUE"));
		
		assertEquals(expected, adapters.createAllDealsAdapters());
	}
	
	@Test
	public void testCreatePortfolioStkAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Portfolio STK: ");
		expected.put("PORT_ACC", ea.createAccount("FIRMID", "CLIENTCODE"));
		expected.put("PORT_CASH", ea.createDouble("TOTALMONEYBAL"));
		expected.put("PORT_BAL", ea.createDouble("ALLASSETS"));
		expected.put("PORT_VMARG", ea.createDouble("PROFITLOSS"));
		
		assertEquals(expected, adapters.createPortfolioStkAdapters());
	}
	
	@Test
	public void testCreatePortfolioFutAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Portfolio FUT: ");
		expected.put("PORT_ACC", ea.createAccount("FIRMID", "TRDACCID"));
		expected.put("PORT_CASH", ea.createDouble("CBPLPLANNED"));
		expected.put("PORT_BAL", ea.createDouble("CBPLIMIT"));
		expected.put("PORT_VMARG", ea.createDouble("VARMARGIN"));
		expected.put("LIMIT_TYPE", ea.createString("LIMIT_TYPE"));
		
		assertEquals(expected, adapters.createPortfolioFutAdapters());
	}
	
	@Test
	public void testCreatePositionStkAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Position STK: ");
		expected.put("POS_ACC",
				ea.createAccount("FIRMID", "CLIENT_CODE", "TRDACCID"));
		expected.put("POS_SECDESCR", ea.createSecDescr("SEC_SHORT_NAME"));
		expected.put("POS_CURR", ea.createLong("CURRENTBAL"));
		expected.put("POS_LOCK", ea.createLong("LOCKED"));
		expected.put("POS_OPEN", ea.createLong("OPENBAL"));
		
		assertEquals(expected, adapters.createPositionStkAdapters());
	}
	
	@Test
	public void testCreatePositionFutAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Position FUT: ");
		expected.put("POS_ACC", ea.createAccount("FIRMID", "TRDACCID"));
		expected.put("POS_SECDESCR", ea.createSecDescr("SEC_SHORT_NAME"));
		expected.put("POS_OPEN", ea.createLong("START_NET"));
		expected.put("POS_CURR", ea.createLong("TOTAL_NET"));
		expected.put("POS_VMARG", ea.createDouble("VARMARGIN"));
		
		assertEquals(expected, adapters.createPositionFutAdapters());
	}

	@Test
	public void testCreateSecurityAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Security: ");
		expected.put(Spec.SEC_LOTSZ, ea.createInteger("lotsize", false));
		expected.put(Spec.SEC_MAXPR, ea.createElement("pricemax",Double.class));
		expected.put(Spec.SEC_MINPR, ea.createElement("pricemin",Double.class));
		expected.put(Spec.SEC_MINSTEPPR,
				ea.createElement("steppricet", Double.class));
		expected.put(Spec.SEC_MINSTEPSZ, ea.createDouble("SEC_PRICE_STEP"));
		expected.put(Spec.SEC_PREC, ea.createInteger("SEC_SCALE"));
		Map<String, SecurityType> types = new HashMap<String, SecurityType>();
		types.put("ФОРТС фьючерсы", SecurityType.FUT);
		expected.put(Spec.SEC_DESCR, ea.createSecDescr("CODE", "CLASS_CODE",
				"curstepprice", "SUR", "CLASSNAME", types));
		expected.put(Spec.SEC_LAST, ea.createElement("last", Double.class));
		expected.put(Spec.SEC_OPEN, ea.createElement("open", Double.class));
		expected.put(Spec.SEC_CLOSE,
				ea.createElement("prevlegalclosepr", Double.class));
		expected.put(Spec.SEC_DISPNAME, ea.createString("LONGNAME"));
		expected.put(Spec.SEC_SHORTNAME, ea.createString("SHORTNAME"));
		expected.put(Spec.SEC_ASKPR, ea.createElement("offer", Double.class));
		expected.put(Spec.SEC_BIDPR, ea.createElement("bid", Double.class));
		expected.put(Spec.SEC_HIGH, ea.createElement("high", Double.class));
		expected.put(Spec.SEC_LOW, ea.createElement("low", Double.class));
		
		assertEquals(expected, adapters.createSecurityAdapters());
	}
	
	@Test
	public void testCreateOrderAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Order: ");
		expected.put("ORD_ID", ea.createLong("ORDERNUM"));
		expected.put("ORD_TRANSID", ea.createLong("TRANSID"));
		Map<String, OrderStatus> status = new HashMap<String, OrderStatus>();
		status.put("KILLED", OrderStatus.CANCELLED);
		status.put("ACTIVE", OrderStatus.ACTIVE);
		status.put("FILLED", OrderStatus.FILLED);
		expected.put("ORD_STATUS",
				ea.createStringMap("STATUS", status, true, null));
		expected.put("ORD_SECDESCR", ea.createSecDescr("SECCODE", "CLASSCODE"));
		expected.put("ORD_ACC", ea.createOrderAccount("CLIENTCODE", "ACCOUNT"));
		Map<String, OrderDirection> dir = new HashMap<String, OrderDirection>();
		dir.put("B", OrderDirection.BUY);
		dir.put("S", OrderDirection.SELL);
		expected.put("ORD_DIR", ea.createStringMap("BUYSELL", dir, true, null));
		expected.put("ORD_QTY", ea.createLong("QTY"));
		expected.put("ORD_QTYREST", ea.createLong("BALANCE"));
		expected.put("ORD_PRICE", ea.createDouble("PRICE"));
		expected.put("ORD_TIME", ea.createDate("ORDERDATE", "ORDERTIME",
				"yyyy-MM/dd", "HH:mm:ss.S", true));
		expected.put("ORD_CHNGTIME", ea.createDate("WITHDRAW_DATE",
				"WITHDRAW_TIME", "yyyy-MM/dd", "HH:mm:ss.S", false));
		Map<String, OrderType> type = new HashMap<String, OrderType>();
		type.put("L", OrderType.LIMIT);
		type.put("M", OrderType.MARKET);
		expected.put("ORD_TYPE", ea.createOrderType("MODE", type));
		
		assertEquals(expected, adapters.createOrderAdapters());
	}
	
	@Test
	public void testCreateStopOrderAdapters() throws Exception {
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		ElementAdapters ea = new ElementAdapters(locator, "Stop-order: ");
		expected.put("ORD_ID", ea.createLong("STOP_ORDERNUM"));
		expected.put("ORD_TRANSID", ea.createLong("TRANSID"));
		Map<String, OrderStatus> status = new HashMap<String, OrderStatus>();
		status.put("KILLED", OrderStatus.CANCELLED);
		status.put("ACTIVE", OrderStatus.ACTIVE);
		status.put("FILLED", OrderStatus.FILLED);
		expected.put("ORD_STATUS",
				ea.createStringMap("STATUS", status, true, null));
		expected.put("ORD_SECDESCR", ea.createSecDescr("SECCODE", "CLASSCODE"));
		expected.put("ORD_ACC", ea.createOrderAccount("CLIENTCODE", "ACCOUNT"));
		Map<String, OrderDirection> dir = new HashMap<String, OrderDirection>();
		dir.put("B", OrderDirection.BUY);
		dir.put("S", OrderDirection.SELL);
		expected.put("ORD_DIR", ea.createStringMap("BUYSELL", dir, true, null));
		expected.put("ORD_PRICE", ea.createDouble("PRICE"));
		expected.put("ORD_TIME", ea.createDate("STOP_ORDERDATE",
				"STOP_ORDERTIME", "yyyy-MM/dd", "HH:mm:ss.S", true));
		Map<String, OrderType> type = new HashMap<String, OrderType>();
		type.put("Стоп-лимит", OrderType.STOP_LIMIT);
		type.put("Тэйк-профит", OrderType.TAKE_PROFIT);
		type.put("Тэйк-профит и стоп-лимит", OrderType.TPSL);
		expected.put("ORD_TYPE",
			ea.createStringMap("STOP_ORDERKIND", type, true, OrderType.OTHER));
		expected.put("ORD_LINKID", ea.createLong("LINKED_ORDER"));
		Map<String, G<Double>> stopLimit = new HashMap<String, G<Double>>();
		stopLimit.put("Стоп-лимит",	ea.createDouble("CONDITION_PRICE"));
		stopLimit.put("Тэйк-профит и стоп-лимит",
				ea.createDouble("CONDITION_PRICE2"));
		expected.put("ORD_STOPLMT",
				ea.createStringMap2G("STOP_ORDERKIND", stopLimit, null));
		Map<String, G<Double>> takeProfit = new HashMap<String, G<Double>>();
		takeProfit.put("Тэйк-профит", ea.createDouble("CONDITION_PRICE"));
		takeProfit.put("Тэйк-профит и стоп-лимит",
				ea.createDouble("CONDITION_PRICE"));
		expected.put("ORD_TAKEPFT",
				ea.createStringMap2G("STOP_ORDERKIND", takeProfit, null));
		Map<String, PriceUnit> unit = new HashMap<String, PriceUnit>();
		unit.put("Д", PriceUnit.MONEY);
		unit.put("%", PriceUnit.PERCENT);
		expected.put("ORD_OFFSET",
				ea.createPrice("OFFSET", "OFFSET_UNITS", unit));
		expected.put("ORD_SPREAD",
				ea.createPrice("SPREAD", "SPREAD_UNITS", unit));
		expected.put("ORD_QTY", ea.createLong("QTY"));

		assertEquals(expected, adapters.createStopOrderAdapters());
	}

}
