package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class QFPositionChangeUpdateTest {
	private static final String RUB = "RUB";
	private static Account account = new Account("TEST");
	private static Symbol symbol = new Symbol("BEST");
	private QFPositionChangeUpdate update;

	@Before
	public void setUp() throws Exception {
		update = new QFPositionChangeUpdate(account, symbol);
	}
	
	@Test
	public void testGetters() {
		assertEquals(account, update.getAccount());
		assertEquals(symbol, update.getSymbol());
	}
	
	@Test
	public void testGetChangeBalance_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialBalance(CDecimalBD.of("100.00", RUB)));
		assertSame(update, update.setFinalBalance(CDecimalBD.of("80.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("-20.00"), update.getChangeBalance());
	}
	
	@Test
	public void testGetChangeBalance_ByChangeValue() {
		assertSame(update, update.setChangeBalance(CDecimalBD.of("65.13", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("65.13"), update.getChangeBalance());
	}
	
	@Test
	public void testGetChangeCurrentPrice_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialCurrentPrice(CDecimalBD.of("800.00")));
		assertSame(update, update.setFinalCurrentPrice(CDecimalBD.of("750.00")));
		
		assertEquals(CDecimalBD.of("-50.00"), update.getChangeCurrentPrice());
	}
	
	@Test
	public void testGetChangeCurrentPrice_ByChangeValue() {
		assertSame(update, update.setChangeCurrentPrice(CDecimalBD.of("19.34")));
		
		assertEquals(CDecimalBD.of("19.34"), update.getChangeCurrentPrice());
	}
	
	@Test
	public void testGetChangeOpenPrice_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialOpenPrice(CDecimalBD.of("200.05")));
		assertSame(update, update.setFinalOpenPrice(CDecimalBD.of("215.09")));
		
		assertEquals(CDecimalBD.of("15.04"), update.getChangeOpenPrice());
	}
	
	@Test
	public void testGetChangeOpenPrice_ByChangeValue() {
		assertSame(update, update.setChangeOpenPrice(CDecimalBD.of("14.08")));
		
		assertEquals(CDecimalBD.of("14.08"), update.getChangeOpenPrice());
	}
	
	@Test
	public void testGetChangeProfitAndLoss_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("114.02", RUB)));
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("86.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("-27.87"), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByChangeValue() {
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("76.01", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.01"), update.getChangeProfitAndLoss());
	}
	
	@Test
	public void testGetChangeUsedMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("1.11", RUB)));
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("0.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("-0.96"), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeUsedMargin_ByChangeValue() {
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("-2.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("-2.00"), update.getChangeUsedMargin());
	}
	
	@Test
	public void testGetChangeVarMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("12.9725", RUB)));
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("10.0001", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("-2.97240"), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByChangeValue() {
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("-20.6", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("-20.60000"), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMarginClose_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("112.08", RUB)));
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("500.82", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("388.74000"), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginClose_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("-1.6", "RUB")));
		
		assertEquals(CDecimalBD.ofRUB5("-1.60000"), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginInter_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("82.10", RUB)));
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("82.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("-0.10000"), update.getChangeVarMarginInter());
	}

	@Test
	public void testGetChangeVarMarginInter_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("26.00", RUB)));
		
		assertEquals(CDecimalBD.of("26.00000", RUB), update.getChangeVarMarginInter());
	}
	
	@Test
	public void testGetChangeVolume_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVolume(CDecimalBD.of(500L)));
		assertSame(update, update.setFinalVolume(CDecimalBD.of(505L)));
		
		assertEquals(CDecimalBD.of(5L), update.getChangeVolume());
	}

	@Test
	public void testGetChangeVolume_ByChangeValue() {
		assertSame(update, update.setChangeVolume(CDecimalBD.of(1000L)));
		
		assertEquals(CDecimalBD.of(1000L), update.getChangeVolume());
	}
	
	@Test
	public void testGetFinalCurrentPrice_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialCurrentPrice(CDecimalBD.of("4.27")));
		assertSame(update, update.setChangeCurrentPrice(CDecimalBD.of("-0.27")));
		
		assertEquals(CDecimalBD.of("4.00"), update.getFinalCurrentPrice());
	}

	@Test
	public void testGetFinalCurrentPrice_ByFinalValue() {
		assertSame(update, update.setFinalCurrentPrice(CDecimalBD.of("76.15")));
		
		assertEquals(CDecimalBD.of("76.15"), update.getFinalCurrentPrice());
	}

	@Test
	public void testGetFinalOpenPrice_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialOpenPrice(CDecimalBD.of("14.27")));
		assertSame(update, update.setChangeOpenPrice(CDecimalBD.of("-10.20")));
		
		assertEquals(CDecimalBD.of("4.07"), update.getFinalOpenPrice());
	}

	@Test
	public void testGetFinalOpenPrice_ByFinalValue() {
		assertSame(update, update.setFinalOpenPrice(CDecimalBD.of("118.02")));
		
		assertEquals(CDecimalBD.of("118.02"), update.getFinalOpenPrice());
	}
	
	@Test
	public void testGetFinalProfitAndLoss_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("26.01", RUB)));
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("-12.46", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("13.55"), update.getFinalProfitAndLoss());
	}
	
	@Test
	public void testGetFinalProfitAndLoss_ByChangeValue() {
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("84.51", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.51"), update.getFinalProfitAndLoss());
	}

	@Test
	public void testGetFinalUsedMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("77.52", RUB)));
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("9.26", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("86.78"), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalUsedMargin_ByChangeValue() {
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("99.91", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("99.91"), update.getFinalUsedMargin());
	}

	@Test
	public void testGetFinalVarMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("803.92", RUB)));
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("-50.07", RUB)));
		
		assertEquals(CDecimalBD.of("753.85000", RUB), update.getFinalVarMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByChangeValue() {
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("108.24", RUB)));
		
		assertEquals(CDecimalBD.of("108.24000", RUB), update.getFinalVarMargin());
	}

	@Test
	public void testGetFinalVarMarginClose_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("81.29", RUB)));
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("-5.14", RUB)));
		
		assertEquals(CDecimalBD.of("76.15000", RUB), update.getFinalVarMarginClose());
	}
	
	@Test
	public void testGetFinalVarMarginClose_ByChangeValue() {
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("202.00", RUB)));
		
		assertEquals(CDecimalBD.of("202.00000", RUB), update.getFinalVarMarginClose());
	}

	@Test
	public void testGetFinalVarMarginInter_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("56.12", RUB)));
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("8.34", RUB)));
		
		assertEquals(CDecimalBD.of("64.46000", RUB), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetFinalVarMarginInter_ByChangeValue() {
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("107.80", RUB)));
		
		assertEquals(CDecimalBD.of("107.80000", RUB), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetFinalVolume_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVolume(CDecimalBD.of(115L)));
		assertSame(update, update.setChangeVolume(CDecimalBD.of(-15L)));
		
		assertEquals(CDecimalBD.of(100L), update.getFinalVolume());
	}
	
	@Test
	public void testGetFinalVolume_ByFinalValue() {
		assertSame(update, update.setFinalVolume(CDecimalBD.of(520L)));
		
		assertEquals(CDecimalBD.of(520L), update.getFinalVolume());
	}
	
	@Test
	public void testGetFinalTickValue() {
		assertNull(update.getFinalTickValue());
		
		assertSame(update, update.setFinalTickValue(ofRUB5("12.15082")));
		
		assertEquals(ofRUB5("12.15082"), update.getFinalTickValue());
	}
	
	@Test
	public void testGetInitialCurrentPrice_ByInitialValue() {
		assertSame(update, update.setInitialCurrentPrice(CDecimalBD.of("115.01")));
		
		assertEquals(CDecimalBD.of("115.01"), update.getInitialCurrentPrice());
	}
	
	@Test
	public void testGetInitialCurrentPrice_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeCurrentPrice(CDecimalBD.of("121.64")));
		assertSame(update, update.setFinalCurrentPrice(CDecimalBD.of("200.21")));
		
		assertEquals(CDecimalBD.of("78.57"), update.getInitialCurrentPrice());
	}

	@Test
	public void testGetInitialOpenPrice_ByInitialValue() {
		assertSame(update, update.setInitialOpenPrice(CDecimalBD.of("702.14")));
		
		assertEquals(CDecimalBD.of("702.14"), update.getInitialOpenPrice());
	}
	
	@Test
	public void testGetInitialOpenPrice_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeOpenPrice(CDecimalBD.of("-202.18")));
		assertSame(update, update.setFinalOpenPrice(CDecimalBD.of("105.10")));
		
		assertEquals(CDecimalBD.of("307.28"), update.getInitialOpenPrice());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByInitialValue() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("104.20", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("104.20"), update.getInitialProfitAndLoss());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("-10.00", RUB)));
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("110.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("120.00"), update.getInitialProfitAndLoss());
	}

	@Test
	public void testGetInitialUsedMargin_ByInitialValue() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("22.89", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("22.89"), update.getInitialUsedMargin());
	}
	
	@Test
	public void testGetInitialUsedMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("-20.00", RUB)));
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("120.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("140.00"), update.getInitialUsedMargin());
	}
	
	@Test
	public void testGetInitialVarMargin_ByInitialValue() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("122.85", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("122.85000"), update.getInitialVarMargin());
	}
	
	@Test
	public void testGetInitialVarMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("20.00", RUB)));
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("140.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("120.00000"), update.getInitialVarMargin());
	}

	@Test
	public void testGetInitialVarMarginClose_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("804.12", RUB)));
		
		assertEquals(CDecimalBD.of("804.12000", RUB), update.getInitialVarMarginClose());
	}
	
	@Test
	public void testGetInitialVarMarginClose_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("65.00", RUB)));
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("15.00", RUB)));
		
		assertEquals(CDecimalBD.of("-50.00000", RUB), update.getInitialVarMarginClose());
	}

	@Test
	public void testGetInitialVarMarginInter_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("88.74", RUB)));
		
		assertEquals(CDecimalBD.of("88.74000", RUB), update.getInitialVarMarginInter());
	}
	
	@Test
	public void testGetInitialVarMarginInter_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("-15.00", RUB)));
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("15.00", RUB)));
		
		assertEquals(CDecimalBD.of("30.00000", RUB), update.getInitialVarMarginInter());
	}
	
	@Test
	public void testGetInitialVolume_ByInitialValue() {
		assertSame(update, update.setInitialVolume(CDecimalBD.of(90L)));
		
		assertEquals(CDecimalBD.of(90L), update.getInitialVolume());
	}
	
	@Test
	public void testGetInitialVolume_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVolume(CDecimalBD.of(25L)));
		assertSame(update, update.setFinalVolume(CDecimalBD.of(26L)));
		
		assertEquals(CDecimalBD.of(1L), update.getInitialVolume());
	}
	
	@Test
	public void testGetInitialTickValue() {
		assertNull(update.getInitialTickValue());
		
		assertSame(update, update.setInitialTickValue(ofRUB5("34.24550")));
		
		assertEquals(ofRUB5("34.24550"), update.getInitialTickValue());
	}
	
	@Test
	public void testToString() {
		update.setInitialBalance(CDecimalBD.of("20000.00", RUB))
			.setChangeBalance(CDecimalBD.of("-1500.00", RUB))
			.setInitialCurrentPrice(CDecimalBD.of("100.00"))
			.setFinalCurrentPrice(CDecimalBD.of("200.00"))
			.setChangeOpenPrice(CDecimalBD.of("56.00"))
			.setFinalOpenPrice(CDecimalBD.of("212.00"))
			.setInitialProfitAndLoss(CDecimalBD.of("-45.48", RUB))
			.setFinalProfitAndLoss(CDecimalBD.of("102.34", RUB))
			.setInitialUsedMargin(CDecimalBD.of("86.19", RUB))
			.setFinalUsedMargin(CDecimalBD.of("24.19", RUB))
			.setInitialVarMargin(CDecimalBD.of("240.98035", RUB))
			.setChangeVarMargin(CDecimalBD.of("24.12000", RUB))
			.setInitialVarMarginClose(CDecimalBD.of("21.15000", RUB))
			.setFinalVarMarginClose(CDecimalBD.of("1.00562", RUB))
			.setInitialVarMarginInter(CDecimalBD.of("0.00000", RUB))
			.setChangeVarMarginInter(CDecimalBD.of("200.84500", RUB))
			.setInitialVolume(CDecimalBD.of(10L))
			.setFinalVolume(CDecimalBD.of(28L))
			.setInitialTickValue(ofRUB5("12.34501"))
			.setFinalTickValue(ofRUB5("11.65002"));
		
		assertEquals("QFPositionChangeUpdate[a=TEST s=BEST "
				+ "bal[i=20000.00000 RUB c=-1500.00000 RUB f=null] "
				+ "cp[i=100.00 c=null f=200.00] "
				+ "op[i=null c=56.00 f=212.00] "
				+ "pl[i=-45.48000 RUB c=null f=102.34000 RUB] "
				+ "um[i=86.19000 RUB c=null f=24.19000 RUB] "
				+ "vm[i=240.98035 RUB c=24.12000 RUB f=null] "
				+ "vmc[i=21.15000 RUB c=null f=1.00562 RUB] "
				+ "vmi[i=0.00000 RUB c=200.84500 RUB f=null] "
				+ "vol[i=10 c=null f=28] "
				+ "tv[i=12.34501 RUB f=11.65002 RUB]"
				+ "]", update.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(update.equals(update));
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
	}

	@Test
	public void testEquals() {
		update.setChangeBalance(CDecimalBD.of("-1500.00", RUB))
			.setChangeCurrentPrice(CDecimalBD.of("100.00"))
			.setChangeOpenPrice(CDecimalBD.of("56.00"))
			.setChangeProfitAndLoss(CDecimalBD.of("-45.48", RUB))
			.setChangeUsedMargin(CDecimalBD.of("86.19", RUB))
			.setChangeVarMargin(CDecimalBD.of("240.98035", RUB))
			.setChangeVarMarginClose(CDecimalBD.of("21.15000", RUB))
			.setChangeVarMarginInter(CDecimalBD.of("0.00000", RUB))
			.setChangeVolume(CDecimalBD.of(10L))
			.setInitialTickValue(ofRUB5("5.45070"))
			.setFinalTickValue(ofRUB5("5.12408"));

		Variant<Account> vAcc = new Variant<>(account, new Account("ZULU"));
		Variant<Symbol> vSym = new Variant<>(vAcc, symbol, new Symbol("GUEST"));
		Variant<CDecimal> vcBal = new Variant<>(vSym, CDecimalBD.of("-1500.00", RUB), CDecimalBD.of("1.00", RUB)),
			vcPL = new Variant<>(vcBal, CDecimalBD.of("-45.48", RUB), CDecimalBD.of("11.02", RUB)),
			vcUM = new Variant<>(vcPL, CDecimalBD.of("86.19", RUB), CDecimalBD.of("114.00", RUB)),
			vcVM = new Variant<>(vcUM, CDecimalBD.of("240.98035", RUB), CDecimalBD.of("0.00000", RUB)),
			vcVMC = new Variant<>(vcVM, CDecimalBD.of("21.15000", RUB), CDecimalBD.of("20.00000", RUB)),
			vcVMI = new Variant<>(vcVMC, CDecimalBD.of("0.00000", RUB), CDecimalBD.of("2.00000", RUB)),
			vcCurPr = new Variant<>(vcVMI, CDecimalBD.of("100.00"), CDecimalBD.of("99.00")),
			vcOpnPr = new Variant<>(vcCurPr, CDecimalBD.of("56.00"), CDecimalBD.of("11.02")),
			vcVol = new Variant<>(vcOpnPr, CDecimalBD.of(10L), CDecimalBD.of(20L)),
			viTV = new Variant<>(vcVol, ofRUB5("5.45070"), ofUSD2("14.26")),
			vfTV = new Variant<>(viTV,  ofRUB5("5.12408"), ofUSD2("12.90"));
		Variant<?> iterator = vfTV;
		int foundCnt = 0;
		QFPositionChangeUpdate x, found = null;
		do {
			x = new QFPositionChangeUpdate(vAcc.get(), vSym.get())
				.setChangeBalance(vcBal.get())
				.setChangeCurrentPrice(vcCurPr.get())
				.setChangeOpenPrice(vcOpnPr.get())
				.setChangeProfitAndLoss(vcPL.get())
				.setChangeUsedMargin(vcUM.get())
				.setChangeVarMargin(vcVM.get())
				.setChangeVarMarginClose(vcVMC.get())
				.setChangeVarMarginInter(vcVMI.get())
				.setChangeVolume(vcVol.get())
				.setInitialTickValue(viTV.get())
				.setFinalTickValue(vfTV.get());
			if ( update.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(account, found.getAccount());
		assertEquals(symbol, found.getSymbol());
		assertEquals(CDecimalBD.ofRUB5("-1500.00"), found.getChangeBalance());
		assertEquals(CDecimalBD.of(  "100.00"), found.getChangeCurrentPrice());
		assertEquals(CDecimalBD.of(   "56.00"), found.getChangeOpenPrice());
		assertEquals(CDecimalBD.ofRUB5("-45.48"), found.getChangeProfitAndLoss());
		assertEquals(CDecimalBD.ofRUB5("86.19"), found.getChangeUsedMargin());
		assertEquals(CDecimalBD.ofRUB5("240.98035"), found.getChangeVarMargin());
		assertEquals(CDecimalBD.ofRUB5("21.15000"), found.getChangeVarMarginClose());
		assertEquals(CDecimalBD.ofRUB5("0.00000"), found.getChangeVarMarginInter());
		assertEquals(CDecimalBD.of(10L), found.getChangeVolume());
		assertEquals(ofRUB5("5.45070"), found.getInitialTickValue());
		assertEquals(ofRUB5("5.12408"), found.getFinalTickValue());
	}

}
