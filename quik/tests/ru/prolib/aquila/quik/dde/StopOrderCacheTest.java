package ru.prolib.aquila.quik.dde;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class StopOrderCacheTest {
	private static SimpleDateFormat timeFormat;
	private StopOrderCache row;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	@Before
	public void setUp() throws Exception {
		row = new StopOrderCache(100L, 20L, OrderStatus.CANCELLED,
				"GAZP", "EQBR", "LX01", "3466", OrderDirection.SELL,
				5L, 130.00d, 129.00d, 135.00d,
				new Price(PriceUnit.MONEY, 5.0d),
				new Price(PriceUnit.PERCENT, 0.1d), 800L,
				timeFormat.parse("2013-05-06 15:05:00.100"),
				timeFormat.parse("2013-05-06 16:30:00.500"),
				OrderType.TAKE_PROFIT_AND_STOP_LIMIT);
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		assertEquals(new Date(), row.getEntryTime());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Long> vId = new Variant<Long>()
			.add(100L)
			.add(123L);
		Variant<Long> vTrnId = new Variant<Long>(vId)
			.add(20L)
			.add(15L);
		Variant<OrderStatus> vStatus = new Variant<OrderStatus>(vTrnId)
			.add(OrderStatus.CANCELLED)
			.add(OrderStatus.ACTIVE);
		Variant<String> vSecCode = new Variant<String>(vStatus)
			.add("GAZP")
			.add("SBER");
		Variant<String> vClassCode = new Variant<String>(vSecCode)
			.add("EQBR")
			.add("SPBFUT");
		Variant<String> vAcc = new Variant<String>(vClassCode)
			.add("LX01")
			.add("ZX00");
		Variant<String> vClnId = new Variant<String>(vAcc)
			.add("3466")
			.add("FFFFF");
		Variant<OrderDirection> vDir = new Variant<OrderDirection>(vClnId)
			.add(OrderDirection.BUY)
			.add(OrderDirection.SELL);
		Variant<Long> vQty = new Variant<Long>(vDir)
			.add(5L)
			.add(1L);
		Variant<Double> vPrice = new Variant<Double>(vQty)
			.add(130.0d)
			.add(180.0d);
		Variant<Double> vSlp = new Variant<Double>(vPrice)
			.add(129.0d)
			.add(128.0d);
		Variant<Double> vTpp = new Variant<Double>(vSlp)
			.add(135.0d)
			.add(137.0d);
		Variant<Price> vOff = new Variant<Price>(vTpp)
			.add(new Price(PriceUnit.MONEY, 5.0d))
			.add(new Price(PriceUnit.PERCENT, 10.0d));
		Variant<Price> vSprd = new Variant<Price>(vOff)
			.add(new Price(PriceUnit.PERCENT, 0.1d))
			.add(new Price(PriceUnit.MONEY, 0.5d));
		Variant<Long> vLnkId = new Variant<Long>(vSprd)
			.add(800L)
			.add(null);
		Variant<Date> vTime = new Variant<Date>(vLnkId)
			.add(timeFormat.parse("2013-05-06 15:05:00.100"))
			.add(timeFormat.parse("2013-05-06 15:05:00.255"));
		Variant<Date> vWdTime = new Variant<Date>(vTime)
			.add(timeFormat.parse("2013-05-06 16:30:00.500"))
			.add(timeFormat.parse("2013-05-07 18:45:00.000"));
		Variant<OrderType> vType = new Variant<OrderType>(vWdTime)
			.add(OrderType.TAKE_PROFIT_AND_STOP_LIMIT)
			.add(OrderType.MARKET);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		StopOrderCache x = null, found = null;
		do {
			x = new StopOrderCache(vId.get(), vTrnId.get(), vStatus.get(),
					vSecCode.get(), vClassCode.get(), vAcc.get(), vClnId.get(),
					vDir.get(), vQty.get(), vPrice.get(), vSlp.get(),
					vTpp.get(), vOff.get(), vSprd.get(), vLnkId.get(),
					vTime.get(), vWdTime.get(), vType.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Long(100L), found.getId());
		assertEquals(new Long(20L), found.getTransId());
		assertEquals(OrderStatus.CANCELLED, found.getStatus());
		assertEquals("GAZP", found.getSecurityCode());
		assertEquals("EQBR", found.getSecurityClassCode());
		assertEquals("LX01", found.getAccountCode());
		assertEquals("3466", found.getClientCode());
		assertEquals(OrderDirection.SELL, found.getDirection());
		assertEquals(new Long(5), found.getQty());
		assertEquals(130.0d, found.getPrice(), 0.01d);
		assertEquals(129.0d, found.getStopLimitPrice(), 0.01d);
		assertEquals(135.0d, found.getTakeProfitPrice(), 0.01d);
		assertEquals(new Price(PriceUnit.MONEY, 5.0d), found.getOffset());
		assertEquals(new Price(PriceUnit.PERCENT, 0.1d), found.getSpread());
		assertEquals(new Long(800L), found.getLinkedOrderId());
		assertEquals(timeFormat.parse("2013-05-06 15:05:00.100"),
				found.getTime());
		assertEquals(timeFormat.parse("2013-05-06 16:30:00.500"),
				found.getWithdrawTime());
		assertEquals(OrderType.TAKE_PROFIT_AND_STOP_LIMIT, found.getType());
	}

}
 