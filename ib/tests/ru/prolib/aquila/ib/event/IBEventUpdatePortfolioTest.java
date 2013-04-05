package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * 2012-11-27<br>
 * $Id: IBEventUpdatePortfolioTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventUpdatePortfolioTest {
	private static IMocksControl control;
	private static EventType type;
	private static Contract contract;
	private static IBEventUpdatePortfolio event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		contract = new Contract();
		contract.m_conId = 782;
		contract.m_symbol = "SBER";
		event = new IBEventUpdatePortfolio(type, contract, 10,
				100.12, 1001.20, 100.00, 1.0, 2.0, "AC1");
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct2() throws Exception {
		EventType type2 = control.createMock(EventType.class);
		IBEventUpdatePortfolio event2 = new IBEventUpdatePortfolio(type2,event);
		assertSame(type2, event2.getType());
		assertSame(contract, event2.getContract());
		assertEquals(10, event2.getPosition());
		assertEquals(100.12, event2.getMarketPrice(), 0.01);
		assertEquals(1001.20, event2.getMarketValue(), 0.01);
		assertEquals(100.00, event2.getAverageCost(), 0.01);
		assertEquals(1.0, event2.getUnrealizedPNL(), 0.01);
		assertEquals(2.0, event2.getRealizedPNL(), 0.01);
		assertEquals("AC1", event2.getAccount());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<Contract> vCnt = new Variant<Contract>(vType)
			.add(contract)
			.add(new Contract());
		Variant<Integer> vPos = new Variant<Integer>(vCnt)
			.add(10)
			.add(1000);
		Variant<Double> vMpr = new Variant<Double>(vPos)
			.add(100.12)
			.add(230.00);
		Variant<Double> vMvl = new Variant<Double>(vMpr)
			.add(1001.20)
			.add(586.00);
		Variant<Double> vAvg = new Variant<Double>(vMvl)
			.add(100.00)
			.add(800.00);
		Variant<Double> vUpnl = new Variant<Double>(vAvg)
			.add(1.0)
			.add(100500.00);
		Variant<Double> vRpnl = new Variant<Double>(vUpnl)
			.add(2.0)
			.add(890.35);
		Variant<String> vAcc = new Variant<String>(vRpnl)
			.add("TEST")
			.add("AC1");
		int foundCnt = 0;
		IBEventUpdatePortfolio x = null, found = null;
		do {
			x = new IBEventUpdatePortfolio(vType.get(), vCnt.get(),
					vPos.get(), vMpr.get(), vMvl.get(), vAvg.get(),
					vUpnl.get(), vRpnl.get(), vAcc.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vAcc.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(contract, found.getContract());
		assertEquals(10, found.getPosition());
		assertEquals(100.12, found.getMarketPrice(), 0.01d);
		assertEquals(1001.20, found.getMarketValue(), 0.01);
		assertEquals(100.00, found.getAverageCost(), 0.01);
		assertEquals(1.0, found.getUnrealizedPNL(), 0.01);
		assertEquals(2.0, found.getRealizedPNL(), 0.01);
		assertEquals("AC1", found.getAccount());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testGetContractId() throws Exception {
		assertEquals(782, event.getContractId());
	}

}
