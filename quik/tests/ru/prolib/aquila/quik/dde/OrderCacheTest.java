package ru.prolib.aquila.quik.dde;


import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.OrderCache;

public class OrderCacheTest {
	private static SimpleDateFormat timeFormat;
	private OrderCache row;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	@Before
	public void setUp() throws Exception {
		row = new OrderCache(125L, 1024L, OrderStatus.FILLED,
				"SBER", "EQBR", "LX001", "9645",
				OrderDirection.BUY, 10L, 5L, 18.24D,
				timeFormat.parse("1999-01-01 20:15:30.000"),
				timeFormat.parse("1999-01-01 21:30:00.000"),
				OrderType.LIMIT);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		assertEquals(new Date(), row.getEntryTime());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Long> vId = new Variant<Long>()
			.add(125L)
			.add(800L);
		Variant<Long> vTrnId = new Variant<Long>(vId)
			.add(1024L)
			.add(812L);
		Variant<OrderStatus> vStat = new Variant<OrderStatus>(vTrnId)
			.add(OrderStatus.FILLED)
			.add(OrderStatus.ACTIVE);
		Variant<String> vSecCode = new Variant<String>(vStat)
			.add("SBER")
			.add("GAZP");
		Variant<String> vClsCode = new Variant<String>(vSecCode)
			.add("EQBR")
			.add("SPBFUT");
		Variant<String> vAccCode = new Variant<String>(vClsCode)
			.add("LX001")
			.add("BC551");
		Variant<String> vClnCode = new Variant<String>(vAccCode)
			.add("9645")
			.add("8879");
		Variant<OrderDirection> vDir = new Variant<OrderDirection>(vClnCode)
			.add(OrderDirection.BUY)
			.add(OrderDirection.SELL);
		Variant<Long> vQty = new Variant<Long>(vDir)
			.add(10L)
			.add(52L);
		Variant<Long> vQtyRst = new Variant<Long>(vQty)
			.add(0L)
			.add(5L);
		Variant<Double> vPrice = new Variant<Double>(vQtyRst)
			.add(18.24D)
			.add(158.12D);
		Variant<Date> vTime = new Variant<Date>(vPrice)
			.add(timeFormat.parse("1999-01-01 20:15:30.000"))
			.add(timeFormat.parse("2015-06-15 18:45:00.000"));
		Variant<Date> vWdTime = new Variant<Date>(vTime)
			.add(timeFormat.parse("1999-01-01 21:30:00.000"))
			.add(null);
		Variant<OrderType> vType = new Variant<OrderType>(vWdTime)
			.add(OrderType.LIMIT)
			.add(OrderType.MARKET);
		Variant<?> iterator = vType;
		OrderCache x = null, found = null;
		int foundCnt = 0;
		do {
			x = new OrderCache(vId.get(), vTrnId.get(), vStat.get(),
					vSecCode.get(), vClsCode.get(),
					vAccCode.get(), vClnCode.get(),
					vDir.get(), vQty.get(), vQtyRst.get(), vPrice.get(), 
					vTime.get(), vWdTime.get(),  vType.get());
			if ( row.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Long(125L), found.getId());
		assertEquals(new Long(1024L), found.getTransId());
		assertEquals(OrderStatus.FILLED, found.getStatus());
		assertEquals("SBER", found.getSecurityCode());
		assertEquals("EQBR", found.getSecurityClassCode());
		assertEquals("LX001", found.getAccountCode());
		assertEquals("9645", found.getClientCode());
		assertEquals(OrderDirection.BUY, found.getDirection());
		assertEquals(new Long(10L), found.getQty());
		assertEquals(new Long(5L), found.getQtyRest());
		assertEquals(18.24D, found.getPrice(), 0.01d);
		assertEquals(timeFormat.parse("1999-01-01 20:15:30.000"),
				found.getTime());
		assertEquals(timeFormat.parse("1999-01-01 21:30:00.000"),
				found.getWithdrawTime());
		assertEquals(OrderType.LIMIT, found.getType());
	}

}
