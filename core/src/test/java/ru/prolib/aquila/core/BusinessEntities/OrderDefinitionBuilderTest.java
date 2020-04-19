package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OrderDefinitionBuilderTest {
	static Account ACCOUNT = new Account("TEST_ACCOUNT");
	static Symbol SYMBOL = new Symbol("MSFT");
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	@Rule public ExpectedException eex = ExpectedException.none();
	OrderDefinitionBuilder service;

	@Before
	public void setUp() throws Exception {
		service = new OrderDefinitionBuilder();
	}
	
	@Test
	public void testBuildDefinition() {
		assertSame(service, service.withAccount(ACCOUNT));
		assertSame(service, service.withSymbol(SYMBOL));
		assertSame(service, service.withType(OrderType.LMT));
		assertSame(service, service.withAction(OrderAction.SELL));
		assertSame(service, service.withQty(of(100L)));
		assertSame(service, service.withPrice(of("120.57")));
		assertSame(service, service.withComment("test order"));
		assertSame(service, service.withMaxExecutionTime(3000L));
		assertSame(service, service.withPlacementTime(T("2020-01-13T20:35:14Z")));
		
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.SELL,
				of(100L),
				of("120.57"),
				"test order",
				3000L,
				T("2020-01-13T20:35:14Z")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithPlacementTime_Str() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.COVER)
			.withQty(of(10L))
			.withPrice(of("200.15"))
			.withMaxExecutionTime(2500L)
			.withPlacementTime("2014-12-31T23:59:00Z");
		
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.COVER,
				of(10L),
				of("200.15"),
				null,
				2500L,
				T("2014-12-31T23:59:00Z")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfAccountNotSpecified() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Account was not defined");
		service.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.COVER)
			.withQty(of(1L))
			.withPrice(of("504.96"))
			.withComment("foobar")
			.withMaxExecutionTime(5000L);
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfSymbolNotSpecified() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol was not defined");
		service.withAccount(ACCOUNT)
			.withType(OrderType.LMT)
			.withAction(OrderAction.COVER)
			.withQty(of(1L))
			.withPrice(of("504.96"))
			.withComment("foobar")
			.withMaxExecutionTime(5000L);

		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_LimitIsDefaultType() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withAction(OrderAction.BUY)
			.withQty(of(1L))
			.withPrice(of("203.26"))
			.withComment("new order")
			.withMaxExecutionTime(5000L);
		
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.BUY,
				of(1L),
				of("203.26"),
				"new order",
				5000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfTypeNotSpecified() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Type was not defined");
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(null)
			.withAction(OrderAction.SELL)
			.withQty(of(2000L))
			.withPrice(of("901.3405"))
			.withComment("aqua base")
			.withMaxExecutionTime(80000L);
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfActionNotDpecified() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Action was not specified");
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withQty(of(20L))
			.withPrice(of("785.01"))
			.withComment("zulu24")
			.withMaxExecutionTime(10000L);
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfQtyNotSpecified() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Quantity was not specified");
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.COVER)
			.withPrice(of("665.123"))
			.withComment("kabuki")
			.withMaxExecutionTime(7000L);
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfPriceNotSpecifiedForLimitType() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Price expected to be not null for " + OrderType.LMT + " order type");
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.SELL_SHORT)
			.withQty(of(10L))
			.withComment("zimbabwe")
			.withMaxExecutionTime(5000L);
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfPriceSpecifiedForMarketType() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Price expected to be null for " + OrderType.MKT + " order type");
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.MKT)
			.withAction(OrderAction.SELL)
			.withQty(of(10L))
			.withPrice(of("250.00"))
			.withComment("balanga")
			.withMaxExecutionTime(1000L);
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_UndefinedCommentIsAllowed() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.COVER)
			.withQty(of(10L))
			.withPrice(of("200.15"))
			.withMaxExecutionTime(2500L);
		
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.COVER,
				of(10L),
				of("200.15"),
				null,
				2500L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_ThrowsIfMaxExecutionTimeNotSpecified() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Max execution time was not specified");
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.BUY)
			.withQty(of(100L))
			.withPrice(of("100.02"))
			.withComment("zambia");
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ResetsActionToEnsureItIsSpecifiedEachTime() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.SELL)
			.withQty(of(100L))
			.withPrice(of("245.24"))
			.withComment("foobar")
			.withMaxExecutionTime(2500L)
			.buildDefinition();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Action was not specified");
		service.withQty(of(200L))
			.withPrice(of("123.12"));
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ResetsQtyToUnsureItIsSpecifiedEachTime() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.SELL)
			.withQty(of(100L))
			.withPrice(of("245.24"))
			.withComment("foobar")
			.withMaxExecutionTime(2500L)
			.buildDefinition();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Quantity was not specified");
		service.withAction(OrderAction.SELL)
			.withPrice(of("123.12"));
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_ResetsPriceToEnsureItIsSpecifiedEachTime() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withType(OrderType.LMT)
			.withAction(OrderAction.SELL)
			.withQty(of(100L))
			.withPrice(of("245.24"))
			.withComment("foobar")
			.withMaxExecutionTime(2500L)
			.buildDefinition();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Price expected to be not null for " + OrderType.LMT + " order type");
		service.withAction(OrderAction.BUY)
			.withQty(of(100L));
		
		service.buildDefinition();
	}
	
	@Test
	public void testBuildDefinition_WithLimitBuy() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zulu 15")
			.withMaxExecutionTime(25000L);

		assertSame(service, service.withLimitBuy(of(100L), of("24.2701")));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.BUY,
				of(100L),
				of("24.2701"),
				"zulu 15",
				25000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithLimitSell() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zulu 15")
			.withMaxExecutionTime(25000L);
	
		assertSame(service, service.withLimitSell(of(100L), of("24.2701")));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.SELL,
				of(100L),
				of("24.2701"),
				"zulu 15",
				25000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithLimitCover() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zulu 15")
			.withMaxExecutionTime(25000L);
	
		assertSame(service, service.withLimitCover(of(100L), of("24.2701")));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.COVER,
				of(100L),
				of("24.2701"),
				"zulu 15",
				25000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithLimitSellShort() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zulu 15")
			.withMaxExecutionTime(25000L);
	
		assertSame(service, service.withLimitSellShort(of(100L), of("24.2701")));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.LMT,
				OrderAction.SELL_SHORT,
				of(100L),
				of("24.2701"),
				"zulu 15",
				25000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithMarketBuy() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zyama")
			.withMaxExecutionTime(1000L);
	
		assertSame(service, service.withMarketBuy(of(200L)));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.MKT,
				OrderAction.BUY,
				of(200L),
				null,
				"zyama",
				1000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithMarketSell() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zyama")
			.withMaxExecutionTime(1000L);
	
		assertSame(service, service.withMarketSell(of(200L)));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.MKT,
				OrderAction.SELL,
				of(200L),
				null,
				"zyama",
				1000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithMarketCover() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zyama")
			.withMaxExecutionTime(1000L);
	
		assertSame(service, service.withMarketCover(of(200L)));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.MKT,
				OrderAction.COVER,
				of(200L),
				null,
				"zyama",
				1000L,
				null
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildDefinition_WithMarketSellShort() {
		service.withAccount(ACCOUNT)
			.withSymbol(SYMBOL)
			.withComment("zyama")
			.withMaxExecutionTime(1000L);
	
		assertSame(service, service.withMarketSellShort(of(200L)));
		OrderDefinition actual = service.buildDefinition();
		
		OrderDefinition expected = new OrderDefinition(
				ACCOUNT,
				SYMBOL,
				OrderType.MKT,
				OrderAction.SELL_SHORT,
				of(200L),
				null,
				"zyama",
				1000L,
				null
			);
		assertEquals(expected, actual);
	}

}
