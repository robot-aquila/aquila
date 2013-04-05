package ru.prolib.aquila.ib.subsys.contract;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

/**
 * 2013-01-05<br>
 * $Id: IBContractUtilsImplTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBContractUtilsImplTest {
	private IBContractUtilsImpl utils;

	@Before
	public void setUp() throws Exception {
		utils = new IBContractUtilsImpl();
	}
	
	/**
	 * Создать детали контракта.
	 * <p>
	 * @param id идентификатор контракта
	 * @param symbol код (тикер)
	 * @param type тип инструмента в формате IB
	 * @param validExchanges список валидных бирж
	 * @param currency код валюты
	 * @param primaryExch код первичной биржи
	 * @return детали контракта
	 */
	private ContractDetails getDetails(int id, String symbol, String type,
			String validExchanges, String currency, String primaryExch)
	{
		ContractDetails details = new ContractDetails();
		details.m_validExchanges = validExchanges;
		details.m_summary = new Contract();
		details.m_summary.m_symbol = symbol;
		details.m_summary.m_conId = id;
		details.m_summary.m_primaryExch = primaryExch;
		details.m_summary.m_secType = type;
		details.m_summary.m_currency = currency;
		return details;
	}

	@Test
	public void testGetAppropriateDescriptor_CachesData() throws Exception {
		ContractDetails d1,d2;
		d1 = getDetails(1, "AAPL", "STK", "SMART, ARCA", "USD", "NASDAQ");
		d2 = getDetails(1, "OTHR", "CASH", "ARCA", "EUR", "BUSY");
		SecurityDescriptor exp, act;
		exp = new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
		act = utils.getAppropriateSecurityDescriptor(d1);
		assertEquals(exp, act);
		// Идентификатор тот же, но детали отличаются. Дескриптор должен
		// остатья неизменным, так как закеширован после генерации.
		assertSame(act, utils.getAppropriateSecurityDescriptor(d2));
	}
	
	@Test
	public void testGetAppropriateDescriptor_TypeConversion() throws Exception {
		SecurityDescriptor sd[] = {
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK),
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.OPT),
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.FUT),
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.CASH),
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.UNK),
		};
		String types[] = {"STK","OPT","FUT","CASH","OTHER"};
		for ( int i = 0; i < sd.length; i ++ ) {
			SecurityDescriptor exp = sd[i],
				act = utils.getAppropriateSecurityDescriptor(getDetails(i, "AAPL",
						types[i], "SMART", "USD", null));
			assertEquals("At #" + i, exp, act);
		}
	}
	
	@Test
	public void testGetAppropriateDescriptor_ExchConversion() throws Exception {
		String s = "AAPL";
		String c = "EUR";
		SecurityType t = SecurityType.STK;
		SecurityDescriptor sd[] = {
			new SecurityDescriptor(s, "SMART",  c, t),
			new SecurityDescriptor(s, "ARCA",   c, t),
			new SecurityDescriptor(s, "SMART",  c, t),
			new SecurityDescriptor(s, "NASDAQ", c, t),
		};
		String fix[][] = {
				// validExchanges, primaryExch
				{ "SMART,FWB2,ZAPPA", null },
				{ "ARCA,FWB2", "ZAPPA" },
				{ "ZAPPA,SMART", "NASDAQ" },
				{ null, "NASDAQ" },
		};
		for ( int i = 0; i < sd.length; i ++ ) {
			SecurityDescriptor exp = sd[i],
				act = utils.getAppropriateSecurityDescriptor(getDetails(i, s,
						"STK", fix[i][0], c, fix[i][1]));
			assertEquals("At #" + i, exp, act);
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		IBContractUtils utils2 = new IBContractUtilsImpl();
		assertTrue(utils.equals(utils));
		assertTrue(utils.equals(utils2));
		assertFalse(utils.equals(null));
		assertFalse(utils.equals(this));
		
		ContractDetails details = new ContractDetails();
		details.m_validExchanges = "SMART";
		details.m_summary.m_symbol = "AAPL";
		details.m_summary.m_currency = "USD";
		details.m_summary.m_secType = "STK";
		
		utils.getAppropriateSecurityDescriptor(details);
		assertFalse(utils.equals(utils2));
		utils2.getAppropriateSecurityDescriptor(details);
		assertTrue(utils.equals(utils2));
	}

}
