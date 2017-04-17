package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class QFPortfolioChangeUpdateTest {
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
		assertSame(update, update.setInitialBalance(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalBalance(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeBalance());
	}

	@Test
	public void testGetChangeBalance_ByChangeValue() {
		assertSame(update, update.setChangeBalance(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeBalance());
	}

	@Test
	public void testGetChangeEquity_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialEquity(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalEquity(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeEquity());
	}

	@Test
	public void testGetChangeEquity_ByChangeValue() {
		assertSame(update, update.setChangeEquity(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeEquity());
	}

	@Test
	public void testGetChangeFreeMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialFreeMargin(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalFreeMargin(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeFreeMargin());
	}

	@Test
	public void testGetChangeFreeMargin_ByChangeValue() {
		assertSame(update, update.setChangeFreeMargin(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeFreeMargin());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialProfitAndLoss(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalProfitAndLoss(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeProfitAndLoss_ByChangeValue() {
		assertSame(update, update.setChangeProfitAndLoss(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeProfitAndLoss());
	}

	@Test
	public void testGetChangeUsedMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialUsedMargin(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalUsedMargin(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeUsedMargin_ByChangeValue() {
		assertSame(update, update.setChangeUsedMargin(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeUsedMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMargin(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalVarMargin(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMargin_ByChangeValue() {
		assertSame(update, update.setChangeVarMargin(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeVarMargin());
	}

	@Test
	public void testGetChangeVarMarginClose_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginClose(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalVarMarginClose(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginClose_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginClose(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeVarMarginClose());
	}

	@Test
	public void testGetChangeVarMarginInter_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialVarMarginInter(FMoney.ofRUB2(115.01)));
		assertSame(update, update.setFinalVarMarginInter(FMoney.ofRUB2(205.34)));
		
		assertEquals(FMoney.ofRUB2(90.33), update.getChangeVarMarginInter());
	}

	@Test
	public void testGetChangeVarMarginInter_ByChangeValue() {
		assertSame(update, update.setChangeVarMarginInter(FMoney.ofRUB2(982.0)));
		
		assertEquals(FMoney.ofRUB2(982.0), update.getChangeVarMarginInter());
	}
	
	@Test
	public void testGetFinalBalance_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialBalance(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeBalance(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalBalance());
	}
	
	@Test
	public void testGetFinalBalance_ByFinalValue() {
		assertSame(update, update.setFinalBalance(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalBalance());
	}

	@Test
	public void testGetFinalEquity_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialEquity(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeEquity(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalEquity());
	}
	
	@Test
	public void testGetFinalEquity_ByFinalValue() {
		assertSame(update, update.setFinalEquity(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalEquity());
	}

	@Test
	public void testGetFinalFreeMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialFreeMargin(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeFreeMargin(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalFreeMargin());
	}
	
	@Test
	public void testGetFinalFreeMargin_ByFinalValue() {
		assertSame(update, update.setFinalFreeMargin(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalFreeMargin());
	}

	@Test
	public void testGetFinalProfitAndLoss_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialProfitAndLoss(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeProfitAndLoss(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalProfitAndLoss());
	}
	
	@Test
	public void testGetFinalProfitAndLoss_ByFinalValue() {
		assertSame(update, update.setFinalProfitAndLoss(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalProfitAndLoss());
	}

	@Test
	public void testGetFinalUsedMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialUsedMargin(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeUsedMargin(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalUsedMargin_ByFinalValue() {
		assertSame(update, update.setFinalUsedMargin(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalUsedMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMargin(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeVarMargin(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalVarMargin());
	}
	
	@Test
	public void testGetFinalVarMargin_ByFinalValue() {
		assertSame(update, update.setFinalVarMargin(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalVarMargin());
	}

	@Test
	public void testGetFinalVarMarginClose_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginClose(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeVarMarginClose(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalVarMarginClose());
	}
	
	@Test
	public void testGetFinalVarMarginClose_ByFinalValue() {
		assertSame(update, update.setFinalVarMarginClose(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalVarMarginClose());
	}

	@Test
	public void testGetFinalVarMarginInter_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialVarMarginInter(FMoney.ofRUB2(86.19)));
		assertSame(update, update.setChangeVarMarginInter(FMoney.ofRUB2(-2.14)));
		
		assertEquals(FMoney.ofRUB2(84.05), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetFinalVarMarginInter_ByFinalValue() {
		assertSame(update, update.setFinalVarMarginInter(FMoney.ofRUB2(76.98)));
		
		assertEquals(FMoney.ofRUB2(76.98), update.getFinalVarMarginInter());
	}
	
	@Test
	public void testGetInitialBalance_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeBalance(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalBalance(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialBalance());
	}
	
	@Test
	public void testGetInitialBalance_ByInitialValue() {
		assertSame(update, update.setInitialBalance(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialBalance());
	}

	@Test
	public void testGetInitialEquity_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeEquity(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalEquity(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialEquity());
	}
	
	@Test
	public void testGetInitialEquity_ByInitialValue() {
		assertSame(update, update.setInitialEquity(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialEquity());
	}
	
	@Test
	public void testGetInitialFreeMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeFreeMargin(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalFreeMargin(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialFreeMargin());
	}
	
	@Test
	public void testGetInitialFreeMargin_ByInitialValue() {
		assertSame(update, update.setInitialFreeMargin(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialFreeMargin());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeProfitAndLoss(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalProfitAndLoss(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialProfitAndLoss());
	}
	
	@Test
	public void testGetInitialProfitAndLoss_ByInitialValue() {
		assertSame(update, update.setInitialProfitAndLoss(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialProfitAndLoss());
	}

	@Test
	public void testGetInitialUsedMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeUsedMargin(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalUsedMargin(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialUsedMargin());
	}
	
	@Test
	public void testGetInitialUsedMargin_ByInitialValue() {
		assertSame(update, update.setInitialUsedMargin(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialUsedMargin());
	}

	@Test
	public void testGetInitialVarMargin_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMargin(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalVarMargin(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialVarMargin());
	}
	
	@Test
	public void testGetInitialVarMargin_ByInitialValue() {
		assertSame(update, update.setInitialVarMargin(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialVarMargin());
	}

	@Test
	public void testGetInitialVarMarginClose_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginClose(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalVarMarginClose(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialVarMarginClose());
	}
	
	@Test
	public void testGetInitialVarMarginClose_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginClose(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialVarMarginClose());
	}

	@Test
	public void testGetInitialVarMarginInter_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeVarMarginInter(FMoney.ofRUB2(-872.98)));
		assertSame(update, update.setFinalVarMarginInter(FMoney.ofRUB2(10034.0)));
		
		assertEquals(FMoney.ofRUB2(10906.98), update.getInitialVarMarginInter());
	}
	
	@Test
	public void testGetInitialVarMarginInter_ByInitialValue() {
		assertSame(update, update.setInitialVarMarginInter(FMoney.ofRUB2(48.15)));
		
		assertEquals(FMoney.ofRUB2(48.15), update.getInitialVarMarginInter());
	}

	@Test
	public void testToString() {
		update.setInitialBalance(FMoney.ofRUB2(10000.0))
			.setFinalBalance(FMoney.ofRUB2(11000.0))
			.setChangeEquity(FMoney.ofRUB2(500.0))
			.setFinalEquity(FMoney.ofRUB2(800.0))
			.setInitialFreeMargin(FMoney.ofRUB2(256.0))
			.setFinalFreeMargin(FMoney.ofRUB2(300.0))
			.setChangeProfitAndLoss(FMoney.ofRUB2(-582.72))
			.setFinalProfitAndLoss(FMoney.ofRUB2(995.11))
			.setInitialUsedMargin(FMoney.ofRUB2(504.08))
			.setFinalUsedMargin(FMoney.ofRUB2(507.12))
			.setInitialVarMargin(FMoney.ofRUB5(12.28601))
			.setFinalVarMargin(FMoney.ofRUB5(9.15))
			.setInitialVarMarginClose(FMoney.ofRUB5(44.98002))
			.setFinalVarMarginClose(FMoney.ofRUB5(9.46012))
			.setInitialVarMarginInter(FMoney.ofRUB5(0.01012))
			.setChangeVarMarginInter(FMoney.ofRUB5(1.0))
			.getOrCreatePositionUpdate(symbol1)
				.setChangeCurrentPrice(FDecimal.of2(14.01));
		
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
		update.setChangeBalance(FMoney.ofRUB2(12.0))
			.setChangeEquity(FMoney.ofRUB2(1.0))
			.setChangeFreeMargin(FMoney.ofRUB2(15.02))
			.setChangeProfitAndLoss(FMoney.ofRUB2(408.0))
			.setChangeUsedMargin(FMoney.ofRUB2(206.0))
			.setChangeVarMargin(FMoney.ofRUB2(404.0))
			.setChangeVarMarginClose(FMoney.ofRUB2(48.0))
			.setChangeVarMarginInter(FMoney.ofRUB2(2.6))
			.getOrCreatePositionUpdate(symbol3)
				.setChangeBalance(FMoney.ofRUB2(81.0));
		
		Variant<Account> vAcc = new Variant<>(account1, account2);
		Variant<FMoney> vcBal = new Variant<>(vAcc, FMoney.ofRUB2(12.0), FMoney.ofRUB2(5.16)),
			vcEq = new Variant<>(vcBal, FMoney.ofRUB2(1.0), FMoney.ofRUB2(0.5)),
			vcFrMgn = new Variant<>(vcEq, FMoney.ofRUB2(15.02), FMoney.ofRUB2(7.15)),
			vcPL = new Variant<>(vcFrMgn, FMoney.ofRUB2(408.0), FMoney.ofRUB2(-9.65)),
			vcUsMgn = new Variant<>(vcPL, FMoney.ofRUB2(206.0), FMoney.ofRUB2(108.32)),
			vcVMgn = new Variant<>(vcUsMgn, FMoney.ofRUB2(404.0), FMoney.ofRUB2(7.12)),
			vcVMgnC = new Variant<>(vcVMgn, FMoney.ofRUB2(48.0), FMoney.ofRUB2(84.01)),
			vcVMgnI = new Variant<>(vcVMgnC, FMoney.ofRUB2(2.6), FMoney.ofRUB2(2.4)),
			vpcBal = new Variant<>(vcVMgnI, FMoney.ofRUB2(81.0), FMoney.ofRUB2(42.69));
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
		assertEquals(FMoney.ofRUB2(12.0), found.getChangeBalance());
		assertEquals(FMoney.ofRUB2(1.0), found.getChangeEquity());
		assertEquals(FMoney.ofRUB2(15.02), found.getChangeFreeMargin());
		assertEquals(FMoney.ofRUB2(408.0), found.getChangeProfitAndLoss());
		assertEquals(FMoney.ofRUB2(206.0), found.getChangeUsedMargin());
		assertEquals(FMoney.ofRUB2(404.0), found.getChangeVarMargin());
		assertEquals(FMoney.ofRUB2(48.0), found.getChangeVarMarginClose());
		assertEquals(FMoney.ofRUB2(2.6), found.getChangeVarMarginInter());
		assertEquals(FMoney.ofRUB2(81.0), found.getPositionUpdate(symbol3)
				.getChangeBalance());
	}

}
