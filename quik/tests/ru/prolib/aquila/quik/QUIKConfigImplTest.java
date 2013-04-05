package ru.prolib.aquila.quik;


import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-11-09<br>
 * $Id: QUIKConfigImplTest.java 547 2013-02-26 04:45:07Z whirlwind $
 */
public class QUIKConfigImplTest {
	private QUIKConfigImpl config;

	@Before
	public void setUp() throws Exception {
		config = new QUIKConfigImpl();
		config.allDeals = "all-deals";
		config.orders = "orders";
		config.portfoliosFUT = "portfolio.fut";
		config.portfoliosSTK = "portfolio.stk";
		config.positionsFUT = "position.fut";
		config.positionsSTK = "position.stk";
		config.securities = "securities";
		config.stopOrders = "stop-orders";
		config.serviceName = "DDE";
		config.quikPath = "C:\\quik5";
		config.dateFormat = "y-M-d";
		config.timeFormat = "H:mm";
		config.skipTRANS2QUIK = true;
	}
	
	@Test
	public void testDefaults() throws Exception {
		config = new QUIKConfigImpl();
		assertNull(config.allDeals);
		assertNull(config.orders);
		assertNull(config.portfoliosFUT);
		assertNull(config.portfoliosSTK);
		assertNull(config.positionsFUT);
		assertNull(config.positionsSTK);
		assertNull(config.securities);
		assertNull(config.stopOrders);
		assertNull(config.serviceName);
		assertNull(config.quikPath);
		assertNull(config.dateFormat);
		assertNull(config.timeFormat);
		assertFalse(config.skipTRANS2QUIK);
	}
	
	@Test
	public void testGetters() throws Exception {
		assertEquals("all-deals", config.getAllDeals());
		assertEquals("orders", config.getOrders());
		assertEquals("portfolio.fut", config.getPortfoliosFUT());
		assertEquals("portfolio.stk", config.getPortfoliosSTK());
		assertEquals("position.fut", config.getPositionsFUT());
		assertEquals("position.stk", config.getPositionsSTK());
		assertEquals("securities", config.getSecurities());
		assertEquals("stop-orders", config.getStopOrders());
		assertEquals("DDE", config.getServiceName());
		assertEquals("C:\\quik5", config.getQUIKPath());
		assertEquals("y-M-d", config.getDateFormat());
		assertEquals("H:mm", config.getTimeFormat());
		assertTrue(config.skipTRANS2QUIK());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vDeals = new Variant<String>()
			.add("all-deals")
			.add("zulu24")
			.add(null);
		Variant<String> vOrders = new Variant<String>(vDeals)
			.add("orders")
			.add("zulu15")
			.add(null);
		Variant<String> vPortF = new Variant<String>(vOrders)
			.add("portfolio.fut")
			.add("zulu18")
			.add(null);
		Variant<String> vPortS = new Variant<String>(vPortF)
			.add("portfolio.stk")
			.add("zulu92")
			.add(null);
		Variant<String> vPosF = new Variant<String>(vPortS)
			.add("position.fut")
			.add("zulu233")
			.add(null);
		Variant<String> vPosS = new Variant<String>(vPosF)
			.add("zulu420")
			.add("position.stk")
			.add(null);
		Variant<String> vSec = new Variant<String>(vPosS)
			.add("anoter table")
			.add("securities")
			.add(null);
		Variant<String> vStopOrd = new Variant<String>(vSec)
			.add("moon 34")
			.add("stop-orders")
			.add(null);
		Variant<String> vSvcName = new Variant<String>(vStopOrd)
			.add("zulu")
			.add("DDE");
		Variant<String> vQuik = new Variant<String>(vSvcName)
			.add("C:\\zulu15")
			.add("C:\\quik5");
		Variant<String> vDateFmt = new Variant<String>(vQuik)
			.add("y-M-d")
			.add("foo");
		Variant<String> vTimeFmt = new Variant<String>(vDateFmt)
			.add("H:mm")
			.add("bar");
		Variant<Boolean> vSkpT2Q = new Variant<Boolean>(vTimeFmt)
			.add(true)
			.add(false);
		Variant<?> iterator = vSkpT2Q;
		int foundCnt = 0;
		QUIKConfigImpl found = null, x = null;
		do {
			x = new QUIKConfigImpl();
			x.allDeals = vDeals.get();
			x.orders = vOrders.get();
			x.portfoliosFUT = vPortF.get();
			x.portfoliosSTK = vPortS.get();
			x.positionsFUT = vPosF.get();
			x.positionsSTK = vPosS.get();
			x.securities = vSec.get();
			x.stopOrders = vStopOrd.get();
			x.serviceName = vSvcName.get();
			x.quikPath = vQuik.get();
			x.dateFormat = vDateFmt.get();
			x.timeFormat = vTimeFmt.get();
			x.skipTRANS2QUIK = vSkpT2Q.get();
			if ( config.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("all-deals", found.getAllDeals());
		assertEquals("orders", found.getOrders());
		assertEquals("portfolio.fut", found.getPortfoliosFUT());
		assertEquals("portfolio.stk", found.getPortfoliosSTK());
		assertEquals("position.fut", found.getPositionsFUT());
		assertEquals("position.stk", found.getPositionsSTK());
		assertEquals("securities", found.getSecurities());
		assertEquals("stop-orders", found.getStopOrders());
		assertEquals("DDE", found.getServiceName());
		assertEquals("C:\\quik5", found.getQUIKPath());
		assertEquals("y-M-d", found.getDateFormat());
		assertEquals("H:mm", found.getTimeFormat());
		assertTrue(found.skipTRANS2QUIK());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(config.equals(config));
		assertFalse(config.equals(null));
		assertFalse(config.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 125809)
			.append("all-deals")
			.append("orders")
			.append("portfolio.fut")
			.append("portfolio.stk")
			.append("position.fut")
			.append("position.stk")
			.append("securities")
			.append("stop-orders")
			.append("DDE")
			.append("C:\\quik5")
			.append("y-M-d")
			.append("H:mm")
			.append(true)
			.toHashCode();
		assertEquals(hashCode, config.hashCode());
	}

}
