package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class QFPositionChangeUpdateTest {
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
		assertSame(update, update.setInitialBalance(FMoney.ofRUB2(100.0)));
		assertSame(update, update.setFinalBalance(FMoney.ofRUB2(80.0)));
		
		assertEquals(FMoney.ofRUB2(-20.0), update.getChangeBalance());
	}
	
	@Test
	public void testGetChangeBalance_ByChangeValue() {
		assertSame(update, update.setChangeBalance(FMoney.ofRUB2(65.13)));
		
		assertEquals(FMoney.ofRUB2(65.13), update.getChangeBalance());
	}
	
	@Test
	public void testGetChangeCurrentPrice_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialCurrentPrice(FDecimal.of2(800.0)));
		assertSame(update, update.setFinalCurrentPrice(FDecimal.of2(750.0)));
		
		assertEquals(FDecimal.of2(-50.0), update.getChangeCurrentPrice());
	}
	
	@Test
	public void testGetChangeCurrentPrice_ByChangeValue() {
		assertSame(update, update.setChangeCurrentPrice(FDecimal.of2(19.34)));
		
		assertEquals(FDecimal.of2(19.34), update.getChangeCurrentPrice());
	}
	
	@Test
	public void testGetChangeOpenPrice_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialOpenPrice(FDecimal.of2(200.05)));
		assertSame(update, update.setFinalOpenPrice(FDecimal.of2(215.09)));
		
		assertEquals(FDecimal.of2(15.04), update.getChangeOpenPrice());
	}
	
	@Test
	public void testGetChangeOpenPrice_ByChangeValue() {
		assertSame(update, update.setChangeOpenPrice(FDecimal.of2(14.08)));
		
		assertEquals(FDecimal.of2(14.08), update.getChangeOpenPrice());
	}
	
	@Test
	public void testGetChangeProfitAndLoss_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialProfitAndLoss(FMoney.ofRUB2(114.02)));
		assertSame(update, update.setFinalProfitAndLoss(FMoney.ofRUB2(86.15)));
		
		assertEquals(FMoney.ofRUB2(-27.87), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByChangeValue() {
		assertSame(update, update.setChangeProfitAndLoss(FMoney.ofRUB2(76.01)));
		
		assertEquals(FMoney.ofRUB2(76.01), update.getChangeProfitAndLoss());
	}
	
	@Test
	public void testGetChangeUsedMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialUsedMargin(FMoney.ofUSD3(1.119)));
		assertSame(update, update.setFinalUsedMargin(FMoney.ofUSD3(0.153)));
		
		assertEquals(FMoney.ofUSD3(-0.966), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeUsedMargin_ByChangeValue() {
		assertSame(update, update.setChangeUsedMargin(FMoney.ofEUR2(-2.0)));
		
		assertEquals(FMoney.ofEUR2(-2.0), update.getChangeUsedMargin());
	}
	
	@Test
	public void testGetChangeVarMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMargin(FMoney.ofRUB4(12.9725)));
		assertSame(update, update.setFinalVarMargin(FMoney.ofRUB4(10.0001)));
		
		assertEquals(FMoney.ofRUB4(-2.9724), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByChangeValue() {
		assertSame(update, update.setChangeVarMargin(FMoney.ofEUR1(-20.6)));
		
		assertEquals(FMoney.ofEUR1(-20.6), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMarginClose_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginClose(FMoney.ofRUB2(112.08)));
		assertSame(update, update.setFinalVarMarginClose(FMoney.ofRUB2(500.82)));
		
		assertEquals(FMoney.ofRUB2(388.74), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginClose_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginClose(FMoney.ofEUR1(-1.6)));
		
		assertEquals(FMoney.ofEUR1(-1.6), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginInter_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginInter(FMoney.ofRUB2(82.1)));
		assertSame(update, update.setFinalVarMarginInter(FMoney.ofRUB2(82.0)));
		
		assertEquals(FMoney.ofRUB2(-0.1), update.getChangeVarMarginInter());
	}

	@Test
	public void testGetChangeVarMarginInter_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginInter(FMoney.ofRUB2(26.0)));
		
		assertEquals(FMoney.ofRUB2(26.0), update.getChangeVarMarginInter());
	}
	
	@Test
	public void testGetChangeVolume_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVolume(500L));
		assertSame(update, update.setFinalVolume(505L));
		
		assertEquals(5L, update.getChangeVolume());
	}

	@Test
	public void testGetChangeVolume_ByChangeValue() {
		assertSame(update, update.setChangeVolume(1000L));
		
		assertEquals(1000L, update.getChangeVolume());
	}
	
	@Test
	public void testGetFinalCurrentPrice_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialCurrentPrice(FDecimal.of2(4.27)));
		assertSame(update, update.setChangeCurrentPrice(FDecimal.of2(-0.27)));
		
		assertEquals(FDecimal.of2(4.0), update.getFinalCurrentPrice());
	}

	@Test
	public void testGetFinalCurrentPrice_ByFinalValue() {
		assertSame(update, update.setFinalCurrentPrice(FDecimal.of2(76.15)));
		
		assertEquals(FDecimal.of2(76.15), update.getFinalCurrentPrice());
	}

	@Test
	public void testGetFinalOpenPrice_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialOpenPrice(FDecimal.of2(14.27)));
		assertSame(update, update.setChangeOpenPrice(FDecimal.of2(-10.2)));
		
		assertEquals(FDecimal.of2(4.07), update.getFinalOpenPrice());
	}

	@Test
	public void testGetFinalOpenPrice_ByFinalValue() {
		assertSame(update, update.setFinalOpenPrice(FDecimal.of2(118.02)));
		
		assertEquals(FDecimal.of2(118.02), update.getFinalOpenPrice());
	}
	
	@Test
	public void testGetFinalProfitAndLoss_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialProfitAndLoss(FMoney.ofRUB2(26.01)));
		assertSame(update, update.setChangeProfitAndLoss(FMoney.ofRUB2(-12.46)));
		
		assertEquals(FMoney.ofRUB2(13.55), update.getFinalProfitAndLoss());
	}
	
	@Test
	public void testGetFinalProfitAndLoss_ByChangeValue() {
		assertSame(update, update.setFinalProfitAndLoss(FMoney.ofRUB2(84.51)));
		
		assertEquals(FMoney.ofRUB2(84.51), update.getFinalProfitAndLoss());
	}

	@Test
	public void testGetFinalUsedMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialUsedMargin(FMoney.ofRUB2(77.52)));
		assertSame(update, update.setChangeUsedMargin(FMoney.ofRUB2(9.26)));
		
		assertEquals(FMoney.ofRUB2(86.78), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalUsedMargin_ByChangeValue() {
		assertSame(update, update.setFinalUsedMargin(FMoney.ofRUB2(99.91)));
		
		assertEquals(FMoney.ofRUB2(99.91), update.getFinalUsedMargin());
	}

	@Test
	public void testGetFinalVarMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMargin(FMoney.ofRUB2(803.92)));
		assertSame(update, update.setChangeVarMargin(FMoney.ofRUB2(-50.07)));
		
		assertEquals(FMoney.ofRUB2(753.85), update.getFinalVarMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByChangeValue() {
		assertSame(update, update.setFinalVarMargin(FMoney.ofRUB2(108.24)));
		
		assertEquals(FMoney.ofRUB2(108.24), update.getFinalVarMargin());
	}

	@Test
	public void testGetFinalVarMarginClose_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginClose(FMoney.ofRUB2(81.29)));
		assertSame(update, update.setChangeVarMarginClose(FMoney.ofRUB2(-5.14)));
		
		assertEquals(FMoney.ofRUB2(76.15), update.getFinalVarMarginClose());
	}
	
	@Test
	public void testGetFinalVarMarginClose_ByChangeValue() {
		assertSame(update, update.setFinalVarMarginClose(FMoney.ofRUB2(202.0)));
		
		assertEquals(FMoney.ofRUB2(202.0), update.getFinalVarMarginClose());
	}

	@Test
	public void testGetFinalVarMarginInter_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginInter(FMoney.ofRUB2(56.12)));
		assertSame(update, update.setChangeVarMarginInter(FMoney.ofRUB2(8.34)));
		
		assertEquals(FMoney.ofRUB2(64.46), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetFinalVarMarginInter_ByChangeValue() {
		assertSame(update, update.setFinalVarMarginInter(FMoney.ofRUB2(107.8)));
		
		assertEquals(FMoney.ofRUB2(107.8), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetFinalVolume_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVolume(115L));
		assertSame(update, update.setChangeVolume(-15L));
		
		assertEquals(100L, update.getFinalVolume());
	}
	
	@Test
	public void testGetFinalVolume_ByFinalValue() {
		assertSame(update, update.setFinalVolume(520L));
		
		assertEquals(520L, update.getFinalVolume());
	}
	
	@Test
	public void testGetInitialCurrentPrice_ByInitialValue() {
		assertSame(update, update.setInitialCurrentPrice(FDecimal.of2(115.01)));
		
		assertEquals(FDecimal.of2(115.01), update.getInitialCurrentPrice());
	}
	
	@Test
	public void testGetInitialCurrentPrice_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeCurrentPrice(FDecimal.of2(121.64)));
		assertSame(update, update.setFinalCurrentPrice(FDecimal.of2(200.21)));
		
		assertEquals(FDecimal.of2(78.57), update.getInitialCurrentPrice());
	}

	@Test
	public void testGetInitialOpenPrice_ByInitialValue() {
		assertSame(update, update.setInitialOpenPrice(FDecimal.of2(702.14)));
		
		assertEquals(FDecimal.of2(702.14), update.getInitialOpenPrice());
	}
	
	@Test
	public void testGetInitialOpenPrice_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeOpenPrice(FDecimal.of2(-202.18)));
		assertSame(update, update.setFinalOpenPrice(FDecimal.of2(105.1)));
		
		assertEquals(FDecimal.of2(307.28), update.getInitialOpenPrice());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByInitialValue() {
		assertSame(update, update.setInitialProfitAndLoss(FMoney.ofRUB3(104.208)));
		
		assertEquals(FMoney.ofRUB3(104.208), update.getInitialProfitAndLoss());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeProfitAndLoss(FMoney.ofRUB2(-10.0)));
		assertSame(update, update.setFinalProfitAndLoss(FMoney.ofRUB2(110.0)));
		
		assertEquals(FMoney.ofRUB2(120.0), update.getInitialProfitAndLoss());
	}

	@Test
	public void testGetInitialUsedMargin_ByInitialValue() {
		assertSame(update, update.setInitialUsedMargin(FMoney.ofRUB2(22.89)));
		
		assertEquals(FMoney.ofRUB2(22.89), update.getInitialUsedMargin());
	}
	
	@Test
	public void testGetInitialUsedMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeUsedMargin(FMoney.ofRUB2(-20.0)));
		assertSame(update, update.setFinalUsedMargin(FMoney.ofRUB2(120.0)));
		
		assertEquals(FMoney.ofRUB2(140.0), update.getInitialUsedMargin());
	}
	
	@Test
	public void testGetInitialVarMargin_ByInitialValue() {
		assertSame(update, update.setInitialVarMargin(FMoney.ofRUB2(122.85)));
		
		assertEquals(FMoney.ofRUB2(122.85), update.getInitialVarMargin());
	}
	
	@Test
	public void testGetInitialVarMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMargin(FMoney.ofRUB2(20.0)));
		assertSame(update, update.setFinalVarMargin(FMoney.ofRUB2(140.0)));
		
		assertEquals(FMoney.ofRUB2(120.0), update.getInitialVarMargin());
	}

	@Test
	public void testGetInitialVarMarginClose_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginClose(FMoney.ofRUB2(804.12)));
		
		assertEquals(FMoney.ofRUB2(804.12), update.getInitialVarMarginClose());
	}
	
	@Test
	public void testGetInitialVarMarginClose_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginClose(FMoney.ofRUB2(65.0)));
		assertSame(update, update.setFinalVarMarginClose(FMoney.ofRUB2(15.0)));
		
		assertEquals(FMoney.ofRUB2(-50.0), update.getInitialVarMarginClose());
	}

	@Test
	public void testGetInitialVarMarginInter_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginInter(FMoney.ofRUB2(88.74)));
		
		assertEquals(FMoney.ofRUB2(88.74), update.getInitialVarMarginInter());
	}
	
	@Test
	public void testGetInitialVarMarginInter_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginInter(FMoney.ofRUB2(-15.0)));
		assertSame(update, update.setFinalVarMarginInter(FMoney.ofRUB2(15.0)));
		
		assertEquals(FMoney.ofRUB2(30.0), update.getInitialVarMarginInter());
	}
	
	@Test
	public void testGetInitialVolume_ByInitialValue() {
		assertSame(update, update.setInitialVolume(90L));
		
		assertEquals(90L, update.getInitialVolume());
	}
	
	@Test
	public void testGetInitialVolume_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVolume(25L));
		assertSame(update, update.setFinalVolume(26L));
		
		assertEquals(1L, update.getInitialVolume());
	}
	
	@Test
	public void testToString() {
		update.setInitialBalance(FMoney.ofRUB2(20000.0))
			.setChangeBalance(FMoney.ofRUB2(-1500.0))
			.setInitialCurrentPrice(FDecimal.of2(100.0))
			.setFinalCurrentPrice(FDecimal.of2(200.0))
			.setChangeOpenPrice(FDecimal.of2(56.0))
			.setFinalOpenPrice(FDecimal.of2(212.0))
			.setInitialProfitAndLoss(FMoney.ofRUB2(-45.48))
			.setFinalProfitAndLoss(FMoney.ofRUB2(102.34))
			.setInitialUsedMargin(FMoney.ofRUB2(86.19))
			.setFinalUsedMargin(FMoney.ofRUB2(24.19))
			.setInitialVarMargin(FMoney.ofRUB5(240.98035))
			.setChangeVarMargin(FMoney.ofRUB5(24.12))
			.setInitialVarMarginClose(FMoney.ofRUB5(21.15))
			.setFinalVarMarginClose(FMoney.ofRUB5(1.00562))
			.setInitialVarMarginInter(FMoney.ofRUB5(0.0))
			.setChangeVarMarginInter(FMoney.ofRUB5(200.845))
			.setInitialVolume(10L)
			.setFinalVolume(28L);
		
		assertEquals("QFPositionChangeUpdate[a=TEST s=BEST "
				+ "bal[i=20000.00 RUB c=-1500.00 RUB f=null] "
				+ "cp[i=100.00 c=null f=200.00] "
				+ "op[i=null c=56.00 f=212.00] "
				+ "pl[i=-45.48 RUB c=null f=102.34 RUB] "
				+ "um[i=86.19 RUB c=null f=24.19 RUB] "
				+ "vm[i=240.98035 RUB c=24.12000 RUB f=null] "
				+ "vmc[i=21.15000 RUB c=null f=1.00562 RUB] "
				+ "vmi[i=0.00000 RUB c=200.84500 RUB f=null] "
				+ "vol[i=10 c=null f=28]]", update.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(update.equals(update));
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
	}

	@Test
	public void testEquals() {
		update.setChangeBalance(FMoney.ofRUB2(-1500.0))
			.setChangeCurrentPrice(FDecimal.of2(100.0))
			.setChangeOpenPrice(FDecimal.of2(56.0))
			.setChangeProfitAndLoss(FMoney.ofRUB2(-45.48))
			.setChangeUsedMargin(FMoney.ofRUB2(86.19))
			.setChangeVarMargin(FMoney.ofRUB5(240.98035))
			.setChangeVarMarginClose(FMoney.ofRUB5(21.15))
			.setChangeVarMarginInter(FMoney.ofRUB5(0.0))
			.setChangeVolume(10L);

		Variant<Account> vAcc = new Variant<>(account, new Account("ZULU"));
		Variant<Symbol> vSym = new Variant<>(vAcc, symbol, new Symbol("GUEST"));
		Variant<FMoney> vcBal = new Variant<>(vSym, FMoney.ofRUB2(-1500.0), FMoney.ofRUB2(1.0)),
			vcPL = new Variant<>(vcBal, FMoney.ofRUB2(-45.48), FMoney.ofRUB2(11.02)),
			vcUM = new Variant<>(vcPL, FMoney.ofRUB2(86.19), FMoney.ofRUB2(114.0)),
			vcVM = new Variant<>(vcUM, FMoney.ofRUB5(240.98035), FMoney.ofRUB5(0.0)),
			vcVMC = new Variant<>(vcVM, FMoney.ofRUB5(21.15), FMoney.ofRUB5(20.0)),
			vcVMI = new Variant<>(vcVMC, FMoney.ofRUB5(0.0), FMoney.ofRUB5(2.0));
		Variant<FDecimal> vcCurPr = new Variant<>(vcVMI, FDecimal.of2(100.0), FDecimal.of2(99.0)),
			vcOpnPr = new Variant<>(vcCurPr, FDecimal.of2(56.0), FDecimal.of2(11.02));
		Variant<Long> vcVol = new Variant<>(vcOpnPr, 10L, 20L);
		Variant<?> iterator = vcVol;
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
				.setChangeVolume(vcVol.get());
			if ( update.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(account, found.getAccount());
		assertEquals(symbol, found.getSymbol());
		assertEquals(FMoney.ofRUB2(-1500.0), found.getChangeBalance());
		assertEquals(FDecimal.of2(100.0), found.getChangeCurrentPrice());
		assertEquals(FDecimal.of2(56.0), found.getChangeOpenPrice());
		assertEquals(FMoney.ofRUB2(-45.48), found.getChangeProfitAndLoss());
		assertEquals(FMoney.ofRUB2(86.19), found.getChangeUsedMargin());
		assertEquals(FMoney.ofRUB5(240.98035), found.getChangeVarMargin());
		assertEquals(FMoney.ofRUB5(21.15), found.getChangeVarMarginClose());
		assertEquals(FMoney.ofRUB5(0.0), found.getChangeVarMarginInter());
		assertEquals(10L, found.getChangeVolume());
	}

}
