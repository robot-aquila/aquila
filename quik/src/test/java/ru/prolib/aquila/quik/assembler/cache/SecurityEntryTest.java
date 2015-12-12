package ru.prolib.aquila.quik.assembler.cache;

import static org.junit.Assert.*;

import java.util.Currency;
import java.util.Date;

import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.cache.SecurityEntry;

public class SecurityEntryTest {
	private QUIKSymbol expected;
	private SecurityEntry row;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		row = new SecurityEntry(20, 180.13d, 160.24d, 0.01d, 0.02d, 2,
				150.82d, 153.14d, 153.12d, "test security", "test",
				150.84d, 150.90d, /*151.12d*/null, 149.82d,
				"SBER", "EQBR", ISO4217.RUB, SymbolType.STK,
				201.12d, 20.10d); // init price, initmargin
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		long d = Math.abs(new Date().getTime() - row.getEntryTime().getTime());
		assertTrue(d <= 1000);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> vLot = new Variant<Integer>()
			.add(20)
			.add(10);
		Variant<Double> vMaxPr = new Variant<Double>(vLot)
			.add(180.13d)
			.add(213.54d);
		Variant<Double> vMinPr = new Variant<Double>(vMaxPr)
			.add(160.24d)
			.add(201.63d);
		Variant<Double> vMinStPr = new Variant<Double>(vMinPr)
			.add(0.01d)
			.add(0.005d);
		Variant<Double> vMinStSz = new Variant<Double>(vMinStPr)
			.add(0.02d)
			.add(0.0015d);
		Variant<Integer> vPrec = new Variant<Integer>(vMinStSz)
			.add(2)
			.add(4);
		Variant<Double> vLast = new Variant<Double>(vPrec)
			.add(150.82d)
			.add(215.75d);
		Variant<Double> vOpen = new Variant<Double>(vLast)
			.add(153.14d)
			.add(218.24d);
		Variant<Double> vClose = new Variant<Double>(vOpen)
			.add(153.12d)
			.add(213.11d);
		Variant<String> vDispNm = new Variant<String>(vClose)
			.add("test security")
			.add("Сбербанк");
		Variant<String> vShrtNm = new Variant<String>(vDispNm)
			.add("test")
			.add("Сбер");
		Variant<Double> vAsk = new Variant<Double>(vShrtNm)
			.add(150.84d)
			.add(160.72d);
		Variant<Double> vBid = new Variant<Double>(vAsk)
			.add(150.90d)
			.add(161.13d);
		Variant<Double> vHigh = new Variant<Double>(vBid)
			//.add(151.12d)
			.add(null)
			.add(167.19d);
		Variant<Double> vLow = new Variant<Double>(vHigh)
			.add(149.82d)
			.add(151.12d);
		Variant<String> vCode = new Variant<String>(vLow)
			.add("SBER")
			.add("GAZP");
		Variant<String> vClass = new Variant<String>(vCode)
			.add("EQBR")
			.add("SPBFUT");
		Variant<Currency> vCurr = new Variant<Currency>(vClass)
			.add(ISO4217.RUB)
			.add(ISO4217.GBP);
		Variant<SymbolType> vType = new Variant<SymbolType>(vCurr)
			.add(SymbolType.STK)
			.add(SymbolType.OPT);
		Variant<Double> vInitPrice = new Variant<Double>(vType)
			.add(201.12d)
			.add(113.28d);
		Variant<Double> vInitMargin = new Variant<Double>(vInitPrice)
			.add(20.10d)
			.add(18.34d);
		Variant<?> iterator = vInitMargin;
		int foundCnt = 0;
		SecurityEntry x = null, found = null;
		do {
			x = new SecurityEntry(vLot.get(), vMaxPr.get(), vMinPr.get(),
					vMinStPr.get(), vMinStSz.get(), vPrec.get(),
					vLast.get(), vOpen.get(), vClose.get(), vDispNm.get(),
					vShrtNm.get(), vAsk.get(), vBid.get(), vHigh.get(),
					vLow.get(),
					vCode.get(), vClass.get(), vCurr.get(), vType.get(),
					vInitPrice.get(), vInitMargin.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Integer(20), found.getLotSize());
		assertEquals(180.13d, found.getMaxPrice(), 0.001d);
		assertEquals(160.24d, found.getMinPrice(), 0.001d);
		assertEquals(0.01d, found.getMinStepPrice(), 0.001d);
		assertEquals(0.02d, found.getMinStepSize(), 0.001d);
		assertEquals(new Integer(2), found.getPrecision());
		assertEquals(150.82d, found.getLastPrice(), 0.001d);
		assertEquals(153.14d, found.getOpenPrice(), 0.001d);
		assertEquals(153.12d, found.getClosePrice(), 0.001d);
		assertEquals("test security", found.getDisplayName());
		assertEquals("test", found.getShortName());
		assertEquals(150.84d, found.getAskPrice(), 0.001d);
		assertEquals(150.90d, found.getBidPrice(), 0.001d);
		//assertEquals(151.12d, found.getHighPrice(), 0.001d);
		assertNull(found.getHighPrice());
		assertEquals(149.82d, found.getLowPrice(), 0.001d);
		assertEquals("SBER", found.getCode());
		assertEquals("EQBR", found.getClassCode());
		assertEquals(ISO4217.RUB, found.getCurrency());
		assertEquals(SymbolType.STK, found.getType());
		assertEquals(201.12d, found.getInitialPrice(), 0.001d);
		assertEquals(20.10d, found.getInitialMargin(), 0.001d);
	}
	
	@Test
	public void testGetSymbol_ForFutures() throws Exception {
		row = new SecurityEntry(0, 0d, 0d, 0d, 0d, 0, 0d, 0d, 0d,
				"RTS-12.13", "_RIZ3", 0d, 0d, 0d, 0d,
				"RIZ3", "SPBFUT", ISO4217.USD, SymbolType.FUT, 0d, 0d);
		expected = new QUIKSymbol("RTS-12.13", "SPBFUT",
				ISO4217.USD, SymbolType.FUT, "RIZ3", "_RIZ3", "RTS-12.13");
		
		assertEquals(expected, row.getSymbol());
	}
	
	@Test
	public void testGetSymbol_Default() throws Exception {
		expected = new QUIKSymbol("SBER", "EQBR", ISO4217.RUB,
				SymbolType.STK, "SBER", "test", "test security");
		
		assertEquals(expected, row.getSymbol());
	}

}
