package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class QFValidatorTest {
	private static final String RUB = "RUB";
	private static Account account = new Account("TEST");
	private QFValidator service;

	@Before
	public void setUp() throws Exception {
		service = new QFValidator();
	}

	@Test
	public void testCanChangePosition_Ok() {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(account)
			.setFinalFreeMargin(CDecimalBD.of("100", RUB));
		
		assertEquals(QFResult.OK, service.canChangePositon(update));
	}
	
	@Test
	public void testCanChangePosition_InsufficientFunds() {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(account)
			.setFinalFreeMargin(CDecimalBD.of("-100", RUB));
	
		assertEquals(QFResult.INSUFFICIENT_FUNDS, service.canChangePositon(update));
	}

}
