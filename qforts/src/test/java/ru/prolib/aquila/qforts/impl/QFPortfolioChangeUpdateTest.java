package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

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
		assertSame(update, update.setInitialBalance(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalBalance(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33", RUB), update.getChangeBalance());
	}

	@Test
	public void testGetChangeBalance_ByChangeValue() {
		assertSame(update, update.setChangeBalance(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.of("982.00", RUB), update.getChangeBalance());
	}

	@Test
	public void testGetChangeEquity_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialEquity(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalEquity(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33", RUB), update.getChangeEquity());
	}

	@Test
	public void testGetChangeEquity_ByChangeValue() {
		assertSame(update, update.setChangeEquity(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.of("982.00", RUB), update.getChangeEquity());
	}

	@Test
	public void testGetChangeFreeMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialFreeMargin(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalFreeMargin(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33", RUB), update.getChangeFreeMargin());
	}

	@Test
	public void testGetChangeFreeMargin_ByChangeValue() {
		assertSame(update, update.setChangeFreeMargin(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.of("982.00", RUB), update.getChangeFreeMargin());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33", RUB), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByChangeValue() {
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.of("982.00", RUB), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeUsedMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33", RUB), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeUsedMargin_ByChangeValue() {
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.of("982.00", RUB), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33000", RUB), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByChangeValue() {
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("982.00", RUB)));
		
		assertEquals(CDecimalBD.of("982.00000", RUB), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMarginClose_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33000", RUB), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginClose_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("982", RUB)));
		
		assertEquals(CDecimalBD.of("982.00000", RUB), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginInter_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("115.01", RUB)));
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("205.34", RUB)));
		
		assertEquals(CDecimalBD.of("90.33000", RUB), update.getChangeVarMarginInter());
	}

	@Test
	public void testGetChangeVarMarginInter_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("982", RUB)));
		
		assertEquals(CDecimalBD.of("982.00000", RUB), update.getChangeVarMarginInter());
	}
	
	@Test
	public void testGetFinalBalance_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialBalance(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeBalance(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05", RUB), update.getFinalBalance());
	}
	
	@Test
	public void testGetFinalBalance_ByFinalValue() {
		assertSame(update, update.setFinalBalance(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98", RUB), update.getFinalBalance());
	}

	@Test
	public void testGetFinalEquity_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialEquity(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeEquity(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05", RUB), update.getFinalEquity());
	}
	
	@Test
	public void testGetFinalEquity_ByFinalValue() {
		assertSame(update, update.setFinalEquity(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98", RUB), update.getFinalEquity());
	}

	@Test
	public void testGetFinalFreeMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialFreeMargin(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeFreeMargin(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05", RUB), update.getFinalFreeMargin());
	}
	
	@Test
	public void testGetFinalFreeMargin_ByFinalValue() {
		assertSame(update, update.setFinalFreeMargin(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98", RUB), update.getFinalFreeMargin());
	}

	@Test
	public void testGetFinalProfitAndLoss_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05", RUB), update.getFinalProfitAndLoss());
	}
	
	@Test
	public void testGetFinalProfitAndLoss_ByFinalValue() {
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98", RUB), update.getFinalProfitAndLoss());
	}

	@Test
	public void testGetFinalUsedMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05", RUB), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalUsedMargin_ByFinalValue() {
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98", RUB), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05000", RUB), update.getFinalVarMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByFinalValue() {
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98000", RUB), update.getFinalVarMargin());
	}

	@Test
	public void testGetFinalVarMarginClose_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05000", RUB), update.getFinalVarMarginClose());
	}
	
	@Test
	public void testGetFinalVarMarginClose_ByFinalValue() {
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98000", RUB), update.getFinalVarMarginClose());
	}

	@Test
	public void testGetFinalVarMarginInter_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("86.19", RUB)));
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("-2.14", RUB)));
		
		assertEquals(CDecimalBD.of("84.05000", RUB), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetFinalVarMarginInter_ByFinalValue() {
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("76.98", RUB)));
		
		assertEquals(CDecimalBD.of("76.98000", RUB), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetInitialBalance_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeBalance(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalBalance(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98", RUB), update.getInitialBalance());
	}
	
	@Test
	public void testGetInitialBalance_ByInitialValue() {
		assertSame(update, update.setInitialBalance(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15", RUB), update.getInitialBalance());
	}

	@Test
	public void testGetInitialEquity_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeEquity(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalEquity(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98", RUB), update.getInitialEquity());
	}
	
	@Test
	public void testGetInitialEquity_ByInitialValue() {
		assertSame(update, update.setInitialEquity(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15", RUB), update.getInitialEquity());
	}
	
	@Test
	public void testGetInitialFreeMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeFreeMargin(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalFreeMargin(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98", RUB), update.getInitialFreeMargin());
	}
	
	@Test
	public void testGetInitialFreeMargin_ByInitialValue() {
		assertSame(update, update.setInitialFreeMargin(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15", RUB), update.getInitialFreeMargin());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeProfitAndLoss(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalProfitAndLoss(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98", RUB), update.getInitialProfitAndLoss());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByInitialValue() {
		assertSame(update, update.setInitialProfitAndLoss(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15", RUB), update.getInitialProfitAndLoss());
	}

	@Test
	public void testGetInitialUsedMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeUsedMargin(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalUsedMargin(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98", RUB), update.getInitialUsedMargin());
	}
	
	@Test
	public void testGetInitialUsedMargin_ByInitialValue() {
		assertSame(update, update.setInitialUsedMargin(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15", RUB), update.getInitialUsedMargin());
	}

	@Test
	public void testGetInitialVarMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMargin(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalVarMargin(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98000", RUB), update.getInitialVarMargin());
	}
	
	@Test
	public void testGetInitialVarMargin_ByInitialValue() {
		assertSame(update, update.setInitialVarMargin(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15000", RUB), update.getInitialVarMargin());
	}

	@Test
	public void testGetInitialVarMarginClose_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginClose(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalVarMarginClose(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98000", RUB), update.getInitialVarMarginClose());
	}
	
	@Test
	public void testGetInitialVarMarginClose_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginClose(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15000", RUB), update.getInitialVarMarginClose());
	}

	@Test
	public void testGetInitialVarMarginInter_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginInter(CDecimalBD.of("-872.98", RUB)));
		assertSame(update, update.setFinalVarMarginInter(CDecimalBD.of("10034", RUB)));
		
		assertEquals(CDecimalBD.of("10906.98000", RUB), update.getInitialVarMarginInter());
	}
	
	@Test
	public void testGetInitialVarMarginInter_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginInter(CDecimalBD.of("48.15", RUB)));
		
		assertEquals(CDecimalBD.of("48.15000", RUB), update.getInitialVarMarginInter());
	}

	@Test
	public void testToString() {
		update.setInitialBalance(CDecimalBD.of("10000", RUB))
			.setFinalBalance(CDecimalBD.of("11000", RUB))
			.setChangeEquity(CDecimalBD.of("500", RUB))
			.setFinalEquity(CDecimalBD.of("800", RUB))
			.setInitialFreeMargin(CDecimalBD.of("256", RUB))
			.setFinalFreeMargin(CDecimalBD.of("300", RUB))
			.setChangeProfitAndLoss(CDecimalBD.of("-582.72", RUB))
			.setFinalProfitAndLoss(CDecimalBD.of("995.11", RUB))
			.setInitialUsedMargin(CDecimalBD.of("504.08", RUB))
			.setFinalUsedMargin(CDecimalBD.of("507.12", RUB))
			.setInitialVarMargin(CDecimalBD.of("12.28601", RUB))
			.setFinalVarMargin(CDecimalBD.of("9.15000", RUB))
			.setInitialVarMarginClose(CDecimalBD.of("44.98002", RUB))
			.setFinalVarMarginClose(CDecimalBD.of("9.46012", RUB))
			.setInitialVarMarginInter(CDecimalBD.of("0.01012", RUB))
			.setChangeVarMarginInter(CDecimalBD.of("1.00000", RUB))
			.getOrCreatePositionUpdate(symbol1)
				.setChangeCurrentPrice(CDecimalBD.of("14.01"));
		
		String expected = "QFPortfolioChangeUpdate[a=TEST1"
				+ " bal[i=10000.00 RUB c=null f=11000.00 RUB]"
				+ " eq[i=null c=500.00 RUB f=800.00 RUB]"
				+ " fm[i=256.00 RUB c=null f=300.00 RUB]"
				+ " pl[i=null c=-582.72 RUB f=995.11 RUB]"
				+ " um[i=504.08 RUB c=null f=507.12 RUB]"
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
		update.setChangeBalance(CDecimalBD.of("12", RUB))
			.setChangeEquity(CDecimalBD.of("1", RUB))
			.setChangeFreeMargin(CDecimalBD.of("15.02", RUB))
			.setChangeProfitAndLoss(CDecimalBD.of("408", RUB))
			.setChangeUsedMargin(CDecimalBD.of("206", RUB))
			.setChangeVarMargin(CDecimalBD.of("404", RUB))
			.setChangeVarMarginClose(CDecimalBD.of("48", RUB))
			.setChangeVarMarginInter(CDecimalBD.of("2.6", RUB))
			.getOrCreatePositionUpdate(symbol3)
				.setChangeBalance(CDecimalBD.of("81", RUB));
		
		Variant<Account> vAcc = new Variant<>(account1, account2);
		Variant<CDecimal> vcBal = new Variant<CDecimal>(vAcc)
				.add(CDecimalBD.of("12.00", RUB))
				.add(CDecimalBD.of("5.16", RUB)),
			vcEq = new Variant<CDecimal>(vcBal)
				.add(CDecimalBD.of("1.00", RUB))
				.add(CDecimalBD.of("0.50", RUB)),
			vcFrMgn = new Variant<CDecimal>(vcEq)
				.add(CDecimalBD.of("15.02", RUB))
				.add(CDecimalBD.of("7.15", RUB)),
			vcPL = new Variant<CDecimal>(vcFrMgn)
				.add(CDecimalBD.of("408.00", RUB))
				.add(CDecimalBD.of("-9.65", RUB)),
			vcUsMgn = new Variant<CDecimal>(vcPL)
				.add(CDecimalBD.of("206.00", RUB))
				.add(CDecimalBD.of("108.32", RUB)),
			vcVMgn = new Variant<CDecimal>(vcUsMgn)
				.add(CDecimalBD.of("404.00", RUB))
				.add(CDecimalBD.of("7.12", RUB)),
			vcVMgnC = new Variant<CDecimal>(vcVMgn)
				.add(CDecimalBD.of("48.00", RUB))
				.add(CDecimalBD.of("84.01", RUB)),
			vcVMgnI = new Variant<CDecimal>(vcVMgnC)
				.add(CDecimalBD.of("2.60", RUB))
				.add(CDecimalBD.of("2.40", RUB)),
			vpcBal = new Variant<CDecimal>(vcVMgnI)
				.add(CDecimalBD.of("81.00", RUB))
				.add(CDecimalBD.of("42.69", RUB));
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
		assertEquals(CDecimalBD.of( "12.00", RUB), found.getChangeBalance());
		assertEquals(CDecimalBD.of(  "1.00", RUB), found.getChangeEquity());
		assertEquals(CDecimalBD.of( "15.02", RUB), found.getChangeFreeMargin());
		assertEquals(CDecimalBD.of("408.00", RUB), found.getChangeProfitAndLoss());
		assertEquals(CDecimalBD.of("206.00", RUB), found.getChangeUsedMargin());
		assertEquals(CDecimalBD.of("404.00000", RUB), found.getChangeVarMargin());
		assertEquals(CDecimalBD.of( "48.00000", RUB), found.getChangeVarMarginClose());
		assertEquals(CDecimalBD.of(  "2.60000", RUB), found.getChangeVarMarginInter());
		assertEquals(CDecimalBD.of( "81.00", RUB), found.getPositionUpdate(symbol3)
				.getChangeBalance());
	}

}
