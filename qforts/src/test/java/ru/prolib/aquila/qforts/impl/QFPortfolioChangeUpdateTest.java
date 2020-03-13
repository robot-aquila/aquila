package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class QFPortfolioChangeUpdateTest {
	private static final String RUB = "RUB";
	private static Account account1, account2;
	private static Symbol symbol1, symbol2, symbol3;
	private QFPortfolioChangeUpdate update;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account1 = new Account("TEST1");
		account2 = new Account("TEST2");
		symbol1 = new Symbol("SBER");
		symbol2 = new Symbol("GAZP");
		symbol3 = new Symbol("LKOH");
	}

	@Before
	public void setUp() throws Exception {
		update = new QFPortfolioChangeUpdate(account1);
	}
	
	@Test
	public void testGetters() {
		assertEquals(account1, update.getAccount());
	}
	
	@Test
	public void testGetOrCreatePositionUpdate() {
		QFPositionChangeUpdate dummy = update.getOrCreatePositionUpdate(symbol1);
		assertNotNull(dummy);
		assertSame(dummy, update.getOrCreatePositionUpdate(symbol1));
		assertEquals(account1, dummy.getAccount());
		assertEquals(symbol1, dummy.getSymbol());
	}
	
	@Test
	public void testSetPositionUpdate() {
		QFPositionChangeUpdate dummy1 = new QFPositionChangeUpdate(account1, symbol1);
		QFPositionChangeUpdate dummy2 = new QFPositionChangeUpdate(account1, symbol2);
		
		assertSame(update, update.setPositionUpdate(dummy1));
		assertSame(update, update.setPositionUpdate(dummy2));
		
		assertEquals(dummy1, update.getPositionUpdate(symbol1));
		assertEquals(dummy2, update.getPositionUpdate(symbol2));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetPositionUpdate_ThrowsIfAccountMismatch() {
		update.setPositionUpdate(new QFPositionChangeUpdate(account2, symbol1));
	}
	
	@Test
	public void testGetPositionUpdates() {
		List<QFPositionChangeUpdate> expected = new ArrayList<>();
		expected.add(update.getOrCreatePositionUpdate(symbol1));
		expected.add(update.getOrCreatePositionUpdate(symbol2));
		expected.add(update.getOrCreatePositionUpdate(symbol3));
		
		assertEquals(expected, update.getPositionUpdates());
	}
	
	@Test
	public void testGetPositionUpdate() {
		QFPositionChangeUpdate expected = update.getOrCreatePositionUpdate(symbol2);
		
		assertSame(expected, update.getPositionUpdate(symbol2));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetPositionUpdate_ThrowsIfUpdateNotExists() {
		update.getPositionUpdate(symbol2);
	}
	
	@Test
	public void testGetChangeBalance_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialBalance(ofRUB2("115.01")));
		assertSame(update, update.setFinalBalance(ofRUB2("205.34")));
		
		assertEquals(ofRUB5("90.33"), update.getChangeBalance());
	}

	@Test
	public void testGetChangeBalance_ByChangeValue() {
		assertSame(update, update.setChangeBalance(ofRUB2("982.00")));
		
		assertEquals(ofRUB5("982.00"), update.getChangeBalance());
	}

	@Test
	public void testGetChangeEquity_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialEquity(CDecimalBD.ofRUB2("115.01")));
		assertSame(update, update.setFinalEquity(CDecimalBD.ofRUB2("205.34")));
		
		assertEquals(CDecimalBD.ofRUB5("90.33"), update.getChangeEquity());
	}

	@Test
	public void testGetChangeEquity_ByChangeValue() {
		assertSame(update, update.setChangeEquity(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("982.00"), update.getChangeEquity());
	}

	@Test
	public void testGetChangeFreeMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialFreeMargin(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalFreeMargin(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("90.33"), update.getChangeFreeMargin());
	}

	@Test
	public void testGetChangeFreeMargin_ByChangeValue() {
		assertSame(update, update.setChangeFreeMargin(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("982.00"), update.getChangeFreeMargin());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("90.33"), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByChangeValue() {
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("982.00"), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeUsedMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("90.33"), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeUsedMargin_ByChangeValue() {
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("982.00"), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("90.33000"), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByChangeValue() {
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("982.00000"), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMarginClose_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("90.33000"), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginClose_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("982", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("982.00000"), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginInter_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("90.33000"), update.getChangeVarMarginInter());
	}

	@Test
	public void testGetChangeVarMarginInter_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("982", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("982.00000"), update.getChangeVarMarginInter());
	}
	
	@Test
	public void testGetFinalBalance_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialBalance(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeBalance(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05"), update.getFinalBalance());
	}
	
	@Test
	public void testGetFinalBalance_ByFinalValue() {
		assertSame(update, update.setFinalBalance(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98"), update.getFinalBalance());
	}

	@Test
	public void testGetFinalEquity_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialEquity(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeEquity(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05"), update.getFinalEquity());
	}
	
	@Test
	public void testGetFinalEquity_ByFinalValue() {
		assertSame(update, update.setFinalEquity(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98"), update.getFinalEquity());
	}

	@Test
	public void testGetFinalFreeMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialFreeMargin(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeFreeMargin(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05"), update.getFinalFreeMargin());
	}
	
	@Test
	public void testGetFinalFreeMargin_ByFinalValue() {
		assertSame(update, update.setFinalFreeMargin(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98"), update.getFinalFreeMargin());
	}

	@Test
	public void testGetFinalProfitAndLoss_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05"), update.getFinalProfitAndLoss());
	}
	
	@Test
	public void testGetFinalProfitAndLoss_ByFinalValue() {
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98"), update.getFinalProfitAndLoss());
	}

	@Test
	public void testGetFinalUsedMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05"), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalUsedMargin_ByFinalValue() {
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98"), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05000"), update.getFinalVarMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByFinalValue() {
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98000"), update.getFinalVarMargin());
	}

	@Test
	public void testGetFinalVarMarginClose_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05000"), update.getFinalVarMarginClose());
	}
	
	@Test
	public void testGetFinalVarMarginClose_ByFinalValue() {
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98000"), update.getFinalVarMarginClose());
	}

	@Test
	public void testGetFinalVarMarginInter_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("84.05000"), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetFinalVarMarginInter_ByFinalValue() {
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("76.98000"), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetInitialBalance_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeBalance(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalBalance(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98"), update.getInitialBalance());
	}
	
	@Test
	public void testGetInitialBalance_ByInitialValue() {
		assertSame(update, update.setInitialBalance(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15"), update.getInitialBalance());
	}

	@Test
	public void testGetInitialEquity_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeEquity(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalEquity(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98"), update.getInitialEquity());
	}
	
	@Test
	public void testGetInitialEquity_ByInitialValue() {
		assertSame(update, update.setInitialEquity(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15"), update.getInitialEquity());
	}
	
	@Test
	public void testGetInitialFreeMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeFreeMargin(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalFreeMargin(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98"), update.getInitialFreeMargin());
	}
	
	@Test
	public void testGetInitialFreeMargin_ByInitialValue() {
		assertSame(update, update.setInitialFreeMargin(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15"), update.getInitialFreeMargin());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98"), update.getInitialProfitAndLoss());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByInitialValue() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15"), update.getInitialProfitAndLoss());
	}

	@Test
	public void testGetInitialUsedMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98"), update.getInitialUsedMargin());
	}
	
	@Test
	public void testGetInitialUsedMargin_ByInitialValue() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15"), update.getInitialUsedMargin());
	}

	@Test
	public void testGetInitialVarMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98000"), update.getInitialVarMargin());
	}
	
	@Test
	public void testGetInitialVarMargin_ByInitialValue() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15000"), update.getInitialVarMargin());
	}

	@Test
	public void testGetInitialVarMarginClose_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98000"), update.getInitialVarMarginClose());
	}
	
	@Test
	public void testGetInitialVarMarginClose_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15000"), update.getInitialVarMarginClose());
	}

	@Test
	public void testGetInitialVarMarginInter_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("10906.98000"), update.getInitialVarMarginInter());
	}
	
	@Test
	public void testGetInitialVarMarginInter_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("48.15000"), update.getInitialVarMarginInter());
	}

	@Test
	public void testToString() {
		update.setInitialBalance(CDecimalBD.ofRUB5("10000"))
			.setFinalBalance(CDecimalBD.ofRUB5("11000"))
			.setChangeEquity(CDecimalBD.ofRUB5("500"))
			.setFinalEquity(CDecimalBD.ofRUB5("800"))
			.setInitialFreeMargin(CDecimalBD.ofRUB5("256"))
			.setFinalFreeMargin(CDecimalBD.ofRUB5("300"))
			.setChangeProfitAndLoss(CDecimalBD.ofRUB5("-582.72"))
			.setFinalProfitAndLoss(CDecimalBD.ofRUB5("995.11"))
			.setInitialUsedMargin(CDecimalBD.ofRUB5("504.08"))
			.setFinalUsedMargin(CDecimalBD.ofRUB5("507.12"))
			.setInitialVarMargin(CDecimalBD.ofRUB5("12.28601"))
			.setFinalVarMargin(CDecimalBD.ofRUB5("9.15000"))
			.setInitialVarMarginClose(CDecimalBD.ofRUB5("44.98002"))
			.setFinalVarMarginClose(CDecimalBD.ofRUB5("9.46012"))
			.setInitialVarMarginInter(CDecimalBD.ofRUB5("0.01012"))
			.setChangeVarMarginInter(CDecimalBD.ofRUB5("1.00000"))
			.getOrCreatePositionUpdate(symbol1)
				.setChangeCurrentPrice(CDecimalBD.of("14.01"));
		
		String expected = "QFPortfolioChangeUpdate[a=TEST1"
				+ " bal[i=10000.00000 RUB c=null f=11000.00000 RUB]"
				+ " eq[i=null c=500.00000 RUB f=800.00000 RUB]"
				+ " fm[i=256.00000 RUB c=null f=300.00000 RUB]"
				+ " pl[i=null c=-582.72000 RUB f=995.11000 RUB]"
				+ " um[i=504.08000 RUB c=null f=507.12000 RUB]"
				+ " vm[i=12.28601 RUB c=null f=9.15000 RUB]"
				+ " vmc[i=44.98002 RUB c=null f=9.46012 RUB]"
				+ " vmi[i=0.01012 RUB c=1.00000 RUB f=null]"
				+ update.getPositionUpdates() + "]";
		assertEquals(expected, update.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(update.equals(update));
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
	}
	
	@Test
	public void testEquals() {
		update.setChangeBalance(ofRUB5("12"))
			.setChangeEquity(ofRUB5("1"))
			.setChangeFreeMargin(ofRUB5("15.02"))
			.setChangeProfitAndLoss(ofRUB5("408"))
			.setChangeUsedMargin(ofRUB5("206"))
			.setChangeVarMargin(ofRUB5("404"))
			.setChangeVarMarginClose(ofRUB5("48"))
			.setChangeVarMarginInter(ofRUB5("2.6"))
			.getOrCreatePositionUpdate(symbol3)
				.setChangeBalance(ofRUB5("81"));
		
		Variant<Account> vAcc = new Variant<>(account1, account2);
		Variant<CDecimal> vcBal = new Variant<>(vAcc, ofRUB5("12.00"), ofRUB5("5.16")),
			vcEq = new Variant<>(vcBal, ofRUB5("1.00"), ofRUB5("0.50")),
			vcFrMgn = new Variant<>(vcEq, ofRUB5("15.02"), ofRUB5("7.15")),
			vcPL = new Variant<>(vcFrMgn, ofRUB5("408.00"), ofRUB5("-9.65")),
			vcUsMgn = new Variant<>(vcPL, ofRUB5("206.00"), ofRUB5("108.32")),
			vcVMgn = new Variant<>(vcUsMgn, ofRUB5("404.00"), ofRUB5("7.12")),
			vcVMgnC = new Variant<>(vcVMgn, ofRUB5("48.00"), ofRUB5("84.01")),
			vcVMgnI = new Variant<>(vcVMgnC, ofRUB5("2.60"), ofRUB5("2.40")),
			vpcBal = new Variant<>(vcVMgnI, ofRUB5("81.00"), ofRUB5("42.69"));
		Variant<Symbol> vpSym = new Variant<>(vpcBal, symbol3, symbol1);
		Variant<?> iterator = vpSym;
		int foundCnt = 0;
		QFPortfolioChangeUpdate x, found = null;
		do {
			x = new QFPortfolioChangeUpdate(vAcc.get())
				.setChangeBalance(vcBal.get())
				.setChangeEquity(vcEq.get())
				.setChangeFreeMargin(vcFrMgn.get())
				.setChangeProfitAndLoss(vcPL.get())
				.setChangeUsedMargin(vcUsMgn.get())
				.setChangeVarMargin(vcVMgn.get())
				.setChangeVarMarginClose(vcVMgnC.get())
				.setChangeVarMarginInter(vcVMgnI.get());
			x.getOrCreatePositionUpdate(vpSym.get())
				.setChangeBalance(vpcBal.get());
			if ( update.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(CDecimalBD.ofRUB5( "12.00"), found.getChangeBalance());
		assertEquals(CDecimalBD.ofRUB5(  "1.00"), found.getChangeEquity());
		assertEquals(CDecimalBD.ofRUB5( "15.02"), found.getChangeFreeMargin());
		assertEquals(CDecimalBD.ofRUB5("408.00"), found.getChangeProfitAndLoss());
		assertEquals(CDecimalBD.ofRUB5("206.00"), found.getChangeUsedMargin());
		assertEquals(CDecimalBD.ofRUB5("404.00000"), found.getChangeVarMargin());
		assertEquals(CDecimalBD.ofRUB5( "48.00000"), found.getChangeVarMarginClose());
		assertEquals(CDecimalBD.ofRUB5(  "2.60000"), found.getChangeVarMarginInter());
		assertEquals(CDecimalBD.ofRUB5( "81.00"), found.getPositionUpdate(symbol3)
				.getChangeBalance());
	}

}
