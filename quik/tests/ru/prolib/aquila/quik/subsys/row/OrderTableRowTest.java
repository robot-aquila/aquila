package ru.prolib.aquila.quik.subsys.row;


import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrderTableRowTest {
	private static SecurityDescriptor descr1, descr2;
	private static Account acc1, acc2;
	private static SimpleDateFormat timeFormat;
	private OrderTableRow row;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		descr1 = new SecurityDescriptor("SBER","EQBR","RUB",SecurityType.STK);
		descr1 = new SecurityDescriptor("RIM3","SPBF","USD",SecurityType.FUT);
		acc1 = new Account("foo");
		acc2 = new Account("bar");
	}

	@Before
	public void setUp() throws Exception {
		row = new OrderTableRow();
		row.setAccount(acc1);
		row.setDirection(OrderDirection.BUY);
		row.setId(125L);
		row.setPrice(18.24D);
		row.setQty(10L);
		row.setQtyRest(0L);
		row.setSecurityDescriptor(descr1);
		row.setStatus(OrderStatus.FILLED);
		row.setTime(timeFormat.parse("1999-01-01 20:15:30"));
		row.setTransId(1024L);
		row.setType(OrderType.LIMIT);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Account> vAcc = new Variant<Account>()
			.add(acc1)
			.add(acc2);
		Variant<OrderDirection> vDir = new Variant<OrderDirection>(vAcc)
			.add(OrderDirection.BUY)
			.add(OrderDirection.SELL);
		Variant<Long> vId = new Variant<Long>(vDir)
			.add(125L)
			.add(800L);
		Variant<Double> vPrice = new Variant<Double>(vId)
			.add(18.24D)
			.add(158.12D);
		Variant<Long> vQty = new Variant<Long>(vPrice)
			.add(10L)
			.add(52L);
		Variant<Long> vRst = new Variant<Long>(vQty)
			.add(0L)
			.add(5L);
		Variant<SecurityDescriptor> vDsc = new Variant<SecurityDescriptor>(vRst)
			.add(descr1)
			.add(descr2);
		Variant<OrderStatus> vStat = new Variant<OrderStatus>(vDsc)
			.add(OrderStatus.FILLED)
			.add(OrderStatus.ACTIVE);
		Variant<Date> vTime = new Variant<Date>(vStat)
			.add(timeFormat.parse("1999-01-01 20:15:30"))
			.add(timeFormat.parse("2015-06-15 18:45:00"));
		Variant<Long> vTrnId = new Variant<Long>(vTime)
			.add(1024L)
			.add(812L);
		Variant<OrderType> vType = new Variant<OrderType>(vTrnId)
			.add(OrderType.LIMIT)
			.add(OrderType.MARKET);
		Variant<?> iterator = vType;
		OrderTableRow x = null, found = null;
		int foundCnt = 0;
		do {
			x = new OrderTableRow();
			x.setAccount(vAcc.get());
			x.setDirection(vDir.get());
			x.setId(vId.get());
			x.setPrice(vPrice.get());
			x.setQty(vQty.get());
			x.setQtyRest(vRst.get());
			x.setSecurityDescriptor(vDsc.get());
			x.setStatus(vStat.get());
			x.setTime(vTime.get());
			x.setTransId(vTrnId.get());
			x.setType(vType.get());
			if ( row.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(acc1, found.getAccount());
		assertEquals(OrderDirection.BUY, found.getDirection());
		assertEquals(new Long(125L), found.getId());
		assertEquals(18.24D, found.getPrice(), 0.01d);
		assertEquals(new Long(10L), found.getQty());
		assertEquals(new Long(0L), found.getQtyRest());
		assertEquals(descr1, found.getSecurityDescriptor());
		assertEquals(OrderStatus.FILLED, found.getStatus());
		assertEquals(timeFormat.parse("1999-01-01 20:15:30"), found.getTime());
		assertEquals(new Long(1024L), found.getTransId());
		assertEquals(OrderType.LIMIT, found.getType());
	}

}
