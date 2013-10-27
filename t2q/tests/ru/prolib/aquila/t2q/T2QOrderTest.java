package ru.prolib.aquila.t2q;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-19<br>
 * $Id$
 */
public class T2QOrderTest {
	private T2QOrder order;

	@Before
	public void setUp() throws Exception {
		order = new T2QOrder(1, 1000L, 256L, "SPBFUT", "RIH3", 156190.d, 10,
				1561900.d, true, 0, "FIRM", "CLIENT", "LX01", //
				100, 20130313, 82153, 12345,
				6789, 4567, 12.34d, 56.78d,
				777, "USERID", "Comment");
	}
	
	@Test
	public void testConstruct2() throws Exception {
		T2QOrder o2 = new T2QOrder(order, 284L);
		assertEquals(1, o2.getMode());
		assertEquals(284L, o2.getTransId());
		assertEquals(256L, o2.getOrderId());
		assertEquals("SPBFUT", o2.getClassCode());
		assertEquals("RIH3", o2.getSecCode());
		assertEquals(156190.d, o2.getPrice(), 0.01d);
		assertEquals(10L, o2.getBalance());
		assertEquals(1561900.d, o2.getValue(), 0.01d);
		assertTrue(o2.isSell());
		assertEquals(0, o2.getStatus());
		assertEquals("FIRM", o2.getFirmId());
		assertEquals("CLIENT", o2.getClientCode());
		assertEquals("LX01", o2.getAccount());
		assertEquals(100, o2.getQty());
		assertEquals(20130313l, o2.getDate());
		assertEquals(82153l, o2.getTime());
		assertEquals(12345, o2.getActivationTime());
		assertEquals(6789, o2.getWithdrawTime());
		assertEquals(4567, o2.getExpiry());
		assertEquals(12.34d, o2.getAccruedInt(), 0.01d);
		assertEquals(56.78d, o2.getYield(), 0.01d);
		assertEquals(777L, o2.getUid());
		assertEquals("USERID", o2.getUserId());
		assertEquals("Comment", o2.getBrokerRef());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> vMode = new Variant<Integer>()
			.add(1)
			.add(2);
		Variant<Long> vTransId = new Variant<Long>(vMode)
			.add(1000L)
			.add(234L);
		Variant<Long> vOrdId = new Variant<Long>(vTransId)
			.add(256L)
			.add(123L);
		Variant<String> vClsCode = new Variant<String>(vOrdId)
			.add("TEST")
			.add("SPBFUT");
		Variant<String> vSecCode = new Variant<String>(vClsCode)
			.add("RIH3")
			.add("ZXY");
		Variant<Double> vPrice = new Variant<Double>(vSecCode)
			.add(156190.d)
			.add(235.491d);
		Variant<Long> vBal = new Variant<Long>(vPrice)
			.add(10L)
			.add(55L);
		Variant<Double> vVal = new Variant<Double>(vBal)
			.add(1561900.d)
			.add(123.d);
		Variant<Boolean> vSell = new Variant<Boolean>(vVal)
			.add(true)
			.add(false);
		Variant<Integer> vStat = new Variant<Integer>(vSell)
			.add(0)
			.add(3);
		Variant<String> vFirm = new Variant<String>(vStat)
			.add("foo")
			.add("FIRM");
		Variant<String> vClnCode = new Variant<String>(vFirm)
			.add("CLIENT")
			.add("bar");
		Variant<String> vAcc = new Variant<String>(vClnCode)
			.add("LX01")
			.add("zulu");
		Variant<Long> vQty = new Variant<Long>(vAcc)
			.add(100l)
			.add(200l);
		Variant<Long> vDate = new Variant<Long>(vQty)
			.add(20130313l)
			.add(20200220l);
		Variant<Long> vTime = new Variant<Long>(vDate)
			.add(82153l)
			.add(99999l);
		Variant<Long> vAtime = new Variant<Long>(vTime)
			.add(12345l)
			.add(65432l);
		Variant<Long> vWtime = new Variant<Long>(vAtime)
			.add(6789l)
			.add(9876l);
		Variant<Long> vExp = new Variant<Long>(vWtime)
			.add(4567L)
			.add(1112L);
		Variant<Double> vAcrInt = new Variant<Double>(vExp)
			.add(12.34d)
			.add(34.21d);
		Variant<Double> vYld = new Variant<Double>(vAcrInt)
			.add(56.78d)
			.add(98.76d);
		Variant<Long> vUid = new Variant<Long>(vYld)
			.add(777L)
			.add(888L);
		Variant<String> vUserId = new Variant<String>(vUid)
			.add("USERID")
			.add("XxXx");
		Variant<String> vBkrRef = new Variant<String>(vUserId)
			.add("Comment")
			.add("-=O=-");
		Variant<?> iterator = vBkrRef;
		int foundCnt = 0;
		T2QOrder x = null, found = null;
		do {
			x = new T2QOrder(vMode.get(), vTransId.get(), vOrdId.get(),
					vClsCode.get(), vSecCode.get(), vPrice.get(), vBal.get(),
					vVal.get(), vSell.get(), vStat.get(),
					vFirm.get(), vClnCode.get(), vAcc.get(),
					vQty.get(), vDate.get(), vTime.get(), vAtime.get(),
					vWtime.get(),vExp.get(), vAcrInt.get(), vYld.get(),
					vUid.get(), vUserId.get(), vBkrRef.get());
			if ( order.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(1, found.getMode());
		assertEquals(1000L, found.getTransId());
		assertEquals(256L, found.getOrderId());
		assertEquals("SPBFUT", found.getClassCode());
		assertEquals("RIH3", found.getSecCode());
		assertEquals(156190.d, found.getPrice(), 0.01d);
		assertEquals(10L, found.getBalance());
		assertEquals(1561900.d, found.getValue(), 0.01d);
		assertTrue(found.isSell());
		assertEquals(0, found.getStatus());
		assertEquals("FIRM", found.getFirmId());
		assertEquals("CLIENT", found.getClientCode());
		assertEquals("LX01", found.getAccount());
		
		assertEquals(100, found.getQty());
		assertEquals(20130313l, found.getDate());
		assertEquals(82153l, found.getTime());
		assertEquals(12345, found.getActivationTime());
		assertEquals(6789, found.getWithdrawTime());
		assertEquals(4567, found.getExpiry());
		assertEquals(12.34d, found.getAccruedInt(), 0.01d);
		assertEquals(56.78d, found.getYield(), 0.01d);
		assertEquals(777L, found.getUid());
		assertEquals("USERID", found.getUserId());
		assertEquals("Comment", found.getBrokerRef());
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(order.equals(order));
		assertFalse(order.equals(this));
		assertFalse(order.equals(null));
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "T2QOrder[trn=1000, id=256, Sell, sec=RIH3@SPBFUT, "
			+ "price=156190.0, "
			+ "balance=10/100, value=1561900.0, status=FILLED, "
			+ "date/time=20130313/82153, "
			+ "atime=12345, wtime=6789, expiry=4567, ...]";
		assertEquals(expected, order.toString());
	}

}
