package ru.prolib.aquila.t2q;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-13<br>
 * $Id: T2QTradeTest.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class T2QTradeTest {
	private T2QTrade trade;

	@Before
	public void setUp() throws Exception {
		trade = new T2QTrade(
				1, // mode
				234, // id
				567, // orderId
				"EQBR", // classCode
				"SBER", // secCode
				8.19d, // price
				100L, // qty
				819.00d, // value,
				true, // is sell
				20130313, // date
				20200313, // settleDate
				101010, // time
				false, // isMarginal
				12.34d, // accruedInt
				56.78d, // yield
				90.10d, // tsCommission
				222.33d, // clearingCenterCommission
				111.22d, // exchangeCommission
				555.22d, // tradingSystemCommission
				18.29d, // price2
				11.98d, // repoRate
				78.29d, //repoValue
				5.39d, // repo2Value
				67.15d, // accruedInt2
				24L, // repoTerm
				0.5d, // startDiscount
				0.8d, // lowerDiscount
				0.9d, // upperDiscount
				true, // blockSecurities
				"USD", // currency
				"RUR", // settleCurrency
				"ABC", // settleCode
				"LX01", // account
				"Comment", // brokerRef
				"8972", // clientCode
				"XXXX", // userId
				"FIRM", // firmId
				"PFIRM", // partnerFirmId
				"EXCH", // exchangeCode
				"SID" // stationId
		);

	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(trade.equals(trade));
		assertFalse(trade.equals(null));
		assertFalse(trade.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		double aprob = 0.6; // Probability of additional variant
		Random rnd = new Random();
		Variant<Integer> vMode = new Variant<Integer>().add(1);
		if ( rnd.nextDouble() > aprob ) vMode.add(2);
		Variant<Long> vId = new Variant<Long>(vMode).add(234L);
		if ( rnd.nextDouble() > aprob ) vId.add(876L);
		Variant<Long> vOrdId = new Variant<Long>(vId).add(567L);
		if ( rnd.nextDouble() > aprob ) vOrdId.add(111L);
		Variant<String> vClass = new Variant<String>(vOrdId).add("EQBR");
		if ( rnd.nextDouble() > aprob ) vClass.add("XXXX");
		Variant<String> vSec = new Variant<String>(vClass).add("SBER");
		if ( rnd.nextDouble() > aprob ) vSec.add("GAZP");
		Variant<Double> vPrice = new Variant<Double>(vSec).add(8.19d);
		if ( rnd.nextDouble() > aprob ) vPrice.add(1.11d);
		Variant<Long> vQty = new Variant<Long>(vPrice).add(100L);
		if ( rnd.nextDouble() > aprob ) vQty.add(200L);
		Variant<Double> vVal = new Variant<Double>(vQty).add(819.00d);
		if ( rnd.nextDouble() > aprob ) vVal.add(900.00d);
		Variant<Boolean> vSell = new Variant<Boolean>(vVal).add(true);
		if ( rnd.nextDouble() > aprob ) vSell.add(false);
		Variant<Long> vDate = new Variant<Long>(vSell).add(20130313L);
		if ( rnd.nextDouble() > aprob ) vDate.add(512L);
		Variant<Long> vStlDate = new Variant<Long>(vDate).add(20200313L);
		if ( rnd.nextDouble() > aprob ) vStlDate.add(10000000L);
		Variant<Long> vTime = new Variant<Long>(vStlDate).add(101010L);
		if ( rnd.nextDouble() > aprob ) vTime.add(202020L);
		Variant<Boolean> vMarg = new Variant<Boolean>(vTime).add(false);
		if ( rnd.nextDouble() > aprob ) vMarg.add(true);
		Variant<Double> vAcrInt = new Variant<Double>(vMarg).add(12.34d);
		if ( rnd.nextDouble() > aprob ) vAcrInt.add(11.22d);
		Variant<Double> vYld = new Variant<Double>(vAcrInt).add(56.78d);
		if ( rnd.nextDouble() > aprob ) vYld.add(11.22d);
		Variant<Double> vTsCom = new Variant<Double>(vYld).add(90.10d);
		if ( rnd.nextDouble() > aprob ) vTsCom.add(81.23d);
		Variant<Double> vClrCom = new Variant<Double>(vTsCom).add(222.33d);
		if ( rnd.nextDouble() > aprob ) vClrCom.add(876.12d);
		Variant<Double> vExCom = new Variant<Double>(vClrCom).add(111.22d);
		if ( rnd.nextDouble() > aprob ) vExCom.add(876.54d);
		Variant<Double> vTrSysCom = new Variant<Double>(vExCom).add(555.22d);
		if ( rnd.nextDouble() > aprob ) vTrSysCom.add(111.22d);
		Variant<Double> vPrice2 = new Variant<Double>(vTrSysCom).add(18.29d);
		if ( rnd.nextDouble() > aprob ) vPrice2.add(20.20d);
		Variant<Double> vRepoRate = new Variant<Double>(vPrice2).add(11.98d);
		if ( rnd.nextDouble() > aprob ) vRepoRate.add(10.98d);
		Variant<Double> vRepoVal = new Variant<Double>(vRepoRate).add(78.29d);
		if ( rnd.nextDouble() > aprob ) vRepoVal.add(11.22d);
		Variant<Double> vRepo2Val = new Variant<Double>(vRepoVal).add(5.39d);
		if ( rnd.nextDouble() > aprob ) vRepo2Val.add(1.12d);
		Variant<Double> vAcrInt2 = new Variant<Double>(vRepo2Val).add(67.15d);
		if ( rnd.nextDouble() > aprob ) vAcrInt2.add(67.20d);
		Variant<Long> vRepoTerm = new Variant<Long>(vAcrInt2).add(24L);
		if ( rnd.nextDouble() > aprob ) vRepoTerm.add(12L);
		Variant<Double> vStaDsc = new Variant<Double>(vRepoTerm).add(0.5d);
		if ( rnd.nextDouble() > aprob ) vStaDsc.add(0.0d);
		Variant<Double> vLowDsc = new Variant<Double>(vStaDsc).add(0.8d);
		if ( rnd.nextDouble() > aprob ) vLowDsc.add(0.1d);
		Variant<Double> vUppDsc = new Variant<Double>(vLowDsc).add(0.9d);
		if ( rnd.nextDouble() > aprob ) vUppDsc.add(0.2d);
		Variant<Boolean> vBlkSec = new Variant<Boolean>(vUppDsc).add(true);
		if ( rnd.nextDouble() > aprob ) vBlkSec.add(false);
		Variant<String> vCur = new Variant<String>(vBlkSec).add("USD");
		if ( rnd.nextDouble() > aprob ) vCur.add("AAA");
		Variant<String> vStlCur = new Variant<String>(vCur).add("RUR");
		if ( rnd.nextDouble() > aprob ) vStlCur.add("BBB");
		Variant<String> vStlCod = new Variant<String>(vStlCur).add("ABC");
		if ( rnd.nextDouble() > aprob ) vStlCod.add("CBA");
		Variant<String> vAcc = new Variant<String>(vStlCod).add("LX01");
		if ( rnd.nextDouble() > aprob ) vAcc.add("XX00");
		Variant<String> vCom = new Variant<String>(vAcc).add("Comment");
		if ( rnd.nextDouble() > aprob ) vCom.add("Tnemmoc");
		Variant<String> vClnCod = new Variant<String>(vCom).add("8972");
		if ( rnd.nextDouble() > aprob ) vClnCod.add("8___");
		Variant<String> vUsrId = new Variant<String>(vClnCod).add("XXXX");
		if ( rnd.nextDouble() > aprob ) vUsrId.add("YYYY");
		Variant<String> vFirm = new Variant<String>(vUsrId).add("FIRM");
		if ( rnd.nextDouble() > aprob ) vFirm.add("MRIF");
		Variant<String> vPfirm = new Variant<String>(vFirm).add("PFIRM");
		if ( rnd.nextDouble() > aprob ) vPfirm.add("XxXx");
		Variant<String> vExch = new Variant<String>(vPfirm).add("EXCH");
		if ( rnd.nextDouble() > aprob ) vExch.add("12345");
		Variant<String> vStatId = new Variant<String>(vExch).add("SID");
		if ( rnd.nextDouble() > aprob ) vStatId.add("DIS");
		Variant<?> iterator = vStatId;
		int foundCnt = 0;
		T2QTrade x = null, found = null;
		//System.out.println("Total variants count: " + iterator.count());
		do {
			x = new T2QTrade(vMode.get(), vId.get(), vOrdId.get(),
					vClass.get(), vSec.get(), vPrice.get(), vQty.get(),
					vVal.get(), vSell.get(), vDate.get(), vStlDate.get(),
					vTime.get(), vMarg.get(), vAcrInt.get(), vYld.get(),
					vTsCom.get(), vClrCom.get(), vExCom.get(), vTrSysCom.get(),
					vPrice2.get(), vRepoRate.get(), vRepoVal.get(),
					vRepo2Val.get(), vAcrInt2.get(), vRepoTerm.get(),
					vStaDsc.get(), vLowDsc.get(), vUppDsc.get(), vBlkSec.get(),
					vCur.get(), vStlCur.get(), vStlCod.get(), vAcc.get(),
					vCom.get(), vClnCod.get(), vUsrId.get(), vFirm.get(),
					vPfirm.get(), vExch.get(), vStatId.get());
			if ( trade.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(1, found.getMode());
		assertEquals(234, found.getId());
		assertEquals(567, found.getOrderId());
		assertEquals("EQBR", found.getClassCode());
		assertEquals("SBER", found.getSecCode());
		assertEquals(8.19d, found.getPrice(), 0.01d);
		assertEquals(100L, found.getQty());
		assertEquals(819.00d, found.getValue(), 0.01d);
		assertTrue(found.isSell());
		assertEquals(20130313L, found.getDate());
		assertEquals(20200313L, found.getSettleDate());
		assertEquals(101010L, found.getTime());
		assertFalse(found.isMarginal());
		assertEquals(12.34d, found.getAccruedInt(), 0.01d);
		assertEquals(56.78d, found.getYield(), 0.01d);
		assertEquals(90.10d, found.getTsCommission(), 0.01d);
		assertEquals(222.33d, found.getClearingCenterCommission(), 0.01d);
		assertEquals(111.22d, found.getExchangeCommission(), 0.01d);
		assertEquals(555.22d, found.getTradingSystemCommission(), 0.01d);
		assertEquals(18.29d, found.getPrice2(), 0.01d);
		assertEquals(11.98d, found.getRepoRate(), 0.01d);
		assertEquals(78.29d, found.getRepoValue(), 0.01d);
		assertEquals(5.39d, found.getRepo2Value(), 0.01d);
		assertEquals(67.15d, found.getAccruedInt2(), 0.01d);
		assertEquals(24L, found.getRepoTerm());
		assertEquals(0.5d, found.getStartDiscount(), 0.01d);
		assertEquals(0.8d, found.getLowerDiscount(), 0.01d);
		assertEquals(0.9d, found.getUpperDiscount(), 0.01d);
		assertTrue(found.getBlockSecurities());
		assertEquals("USD", found.getCurrency());
		assertEquals("RUR", found.getSettleCurrency());
		assertEquals("ABC", found.getSettleCode());
		assertEquals("LX01", found.getAccount());
		assertEquals("Comment", found.getBrokerRef());
		assertEquals("8972", found.getClientCode());
		assertEquals("XXXX", found.getUserId());
		assertEquals("FIRM", found.getFirmId());
		assertEquals("PFIRM", found.getPartnerFirmId());
		assertEquals("EXCH", found.getExchangeCode());
		assertEquals("SID", found.getStationId());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "T2QTrade[Sell, sec=SBER@EQBR, price=8.19, "
			+ "qty=100, val=819.0, id=234, orderId=567, "
			+ "date=20130313, time=101010]";
		assertEquals(expected, trade.toString());
	}

}
