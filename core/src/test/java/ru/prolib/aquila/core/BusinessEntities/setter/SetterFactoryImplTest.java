package ru.prolib.aquila.core.BusinessEntities.setter;


import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-10-28<br>
 * $Id: SetterFactoryImplTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class SetterFactoryImplTest {
	private static SetterFactoryImpl factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factory = new SetterFactoryImpl();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(factory.equals(factory));
		assertTrue(factory.equals(new SetterFactoryImpl()));
		assertFalse(factory.equals(this));
		assertFalse(factory.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, 233205).toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}
	
	@Test
	public void testOrderSetAccount() throws Exception {
		assertEquals(new OrderSetAccount(), factory.orderSetAccount());
	}
	
	@Test
	public void testOrderSetDirection() throws Exception {
		assertEquals(new OrderSetDirection(), factory.orderSetDirection());
	}
	
	@Test
	public void testOrderSetId() throws Exception {
		assertEquals(new OrderSetId(), factory.orderSetId());
	}
	
	@Test
	public void testOrderSetPrice() throws Exception {
		assertEquals(new OrderSetPrice(), factory.orderSetPrice());
	}
	
	@Test
	public void testOrderSetQty() throws Exception {
		assertEquals(new OrderSetQty(), factory.orderSetQty());
	}
	
	@Test
	public void testOrderSetQtyRest() throws Exception {
		assertEquals(new OrderSetQtyRest(), factory.orderSetQtyRest());
	}

	@Test
	public void testOrderSetSecurity() throws Exception {
		assertEquals(new OrderSetSymbol(),
				factory.orderSetSymbol());
	}

	@Test
	public void testOrderSetStatus() throws Exception {
		assertEquals(new OrderSetStatus(), factory.orderSetStatus());
	}

	@Test
	public void testOrderSetType() throws Exception {
		assertEquals(new OrderSetType(), factory.orderSetType());
	}
	
	@Test
	public void testOrderSetExecutedVolume() throws Exception {
		assertEquals(new OrderSetExecutedVolume(),
				factory.orderSetExecutedVolume());
	}
	
	@Test
	public void testOrderSetAvgExecutedPrice() throws Exception {
		assertEquals(new OrderSetAvgExecutedPrice(),
				factory.orderSetAvgExecutedPrice());
	}

	@Test
	public void testPositionSetCurrQty() throws Exception {
		assertEquals(new PositionSetCurrQty(),factory.positionSetCurrQty());
	}

	@Test
	public void testPositionSetLockQty() throws Exception {
		assertEquals(new PositionSetLockQty(),factory.positionSetLockQty());
	}

	@Test
	public void testPositionSetOpenQty() throws Exception {
		assertEquals(new PositionSetOpenQty(),factory.positionSetOpenQty());
	}

	@Test
	public void testPositionSetVarMargin() throws Exception {
		assertEquals(new PositionSetVarMargin(),
				factory.positionSetVarMargin());
	}

	@Test
	public void testPortfolioSetCash() throws Exception {
		assertEquals(new PortfolioSetCash(), factory.portfolioSetCash());
	}
	
	@Test
	public void testPortfolioSetBalance() throws Exception {
		assertEquals(new PortfolioSetBalance(), factory.portfolioSetBalance());
	}

	@Test
	public void testPortfolioSetVarMarging() throws Exception {
		assertEquals(new PortfolioSetVariationMargin(),
				factory.portfolioSetVarMargin());
	}

	@Test
	public void testSecuritySetLotSize() throws Exception {
		assertEquals(new SecuritySetLotSize(), factory.securitySetLotSize());
	}

	@Test
	public void testSecuritySetMaxPrice() throws Exception {
		assertEquals(new SecuritySetMaxPrice(), factory.securitySetMaxPrice());
	}

	@Test
	public void testSecuritySetMinPrice() throws Exception {
		assertEquals(new SecuritySetMinPrice(), factory.securitySetMinPrice());
	}

	@Test
	public void testSecuritySetMinStepPrice() throws Exception {
		assertEquals(new SecuritySetMinStepPrice(),
				factory.securitySetMinStepPrice());
	}
	
	@Test
	public void testSecuritySetMinStepSize() throws Exception {
		assertEquals(new SecuritySetMinStepSize(),
				factory.securitySetMinStepSize());
	}
	
	@Test
	public void testSecuritySetPrecision() throws Exception {
		assertEquals(new SecuritySetPrecision(),factory.securitySetPrecision());
	}
	
	@Test
	public void testSecuritySetLastPrice() throws Exception {
		assertEquals(new SecuritySetLastPrice(),factory.securitySetLastPrice());
	}
	
	@Test
	public void testTradeSetDirection() throws Exception {
		assertEquals(new TradeSetDirection(), factory.tradeSetDirection());
	}
	
	@Test
	public void testTradeSetId() throws Exception {
		assertEquals(new TradeSetId(), factory.tradeSetId());
	}
	
	@Test
	public void testTradeSetPrice() throws Exception {
		assertEquals(new TradeSetPrice(), factory.tradeSetPrice());
	}
	
	@Test
	public void testTradeSetQty() throws Exception {
		assertEquals(new TradeSetQty(), factory.tradeSetQty());
	}
	
	@Test
	public void testTradeSetSecurity() throws Exception {
		assertEquals(new TradeSetSymbol(), factory.tradeSetSymbol());
	}

	@Test
	public void testTradeSetTime() throws Exception {
		assertEquals(new TradeSetTime(), factory.tradeSetTime());
	}

	@Test
	public void testTradeSetVolume() throws Exception {
		assertEquals(new TradeSetVolume(), factory.tradeSetVolume());
	}
	
	@Test
	public void testSecuritySetDisplayName() throws Exception {
		assertEquals(new SecuritySetDisplayName(),
				factory.securitySetDisplayName());
	}
	
	@Test
	public void testSecuritySetAskPrice() throws Exception {
		assertEquals(new SecuritySetAskPrice(), factory.securitySetAskPrice());
	}

	@Test
	public void testSecuritySetAskSize() throws Exception {
		assertEquals(new SecuritySetAskSize(), factory.securitySetAskSize());
	}

	@Test
	public void testSecuritySetBidPrice() throws Exception {
		assertEquals(new SecuritySetBidPrice(), factory.securitySetBidPrice());
	}

	@Test
	public void testSecuritySetBidSize() throws Exception {
		assertEquals(new SecuritySetBidSize(), factory.securitySetBidSize());
	}
	
	@Test
	public void testSecuritySetOpenPrice() throws Exception {
		assertEquals(new SecuritySetOpenPrice(),
				factory.securitySetOpenPrice());
	}

	@Test
	public void testSecuritySetClosePrice() throws Exception {
		assertEquals(new SecuritySetClosePrice(),
				factory.securitySetClosePrice());
	}
	
	@Test
	public void testSecuritySetHighPrice() throws Exception {
		assertEquals(new SecuritySetHighPrice(),
				factory.securitySetHighPrice());
	}

	@Test
	public void testSecuritySetLowPrice() throws Exception {
		assertEquals(new SecuritySetLowPrice(), factory.securitySetLowPrice());
	}
	
	@Test
	public void testSecuritySetStatus() throws Exception {
		assertEquals(new SecuritySetStatus(), factory.securitySetStatus());
	}
	
	@Test
	public void testPositionSetMarketValue() throws Exception {
		assertEquals(new PositionSetMarketValue(),
				factory.positionSetMarketValue());
	}
	
	@Test
	public void testPositionSetBookValue() throws Exception {
		assertEquals(new PositionSetBookValue(),
				factory.positionSetBookValue());
	}
	
	@Test
	public void testOrderSetTime() throws Exception {
		assertEquals(new OrderSetTime(), factory.orderSetTime());
	}

	@Test
	public void testOrderSetLastChangeTime() throws Exception {
		assertEquals(new OrderSetLastChangeTime(),
				factory.orderSetLastChangeTime());
	}

}
