package ru.prolib.aquila.ib.subsys.run;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;
import ru.prolib.aquila.ib.subsys.run.IBRunnableUpdatePosition;

/**
 * 2013-01-06<br>
 * $Id: IBRunnableUpdatePositionTest.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRunnableUpdatePositionTest {
	private static IMocksControl control;
	private static EditableTerminal terminal;
	private static IBContracts contracts;
	private static S<EditablePosition> modifier;
	private static Contract contract;
	private static IBEventUpdatePortfolio event;
	private static IBRunnableUpdatePosition runnable;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		contract = new Contract();
		contract.m_conId = 1172;
		contract.m_symbol = "AAPL";
		contract.m_secType = "STK";
		contract.m_currency = "USD";
		contract.m_exchange = "SMART";
		contract.m_primaryExch = "NASDAQ";
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		contracts = control.createMock(IBContracts.class);
		modifier = control.createMock(S.class);
		event = new IBEventUpdatePortfolio(control.createMock(EventType.class),
				contract, -100, 500.0d, 50000.0d, 498.0d, 0.1d, 0.2d, "TEST");
		runnable = new IBRunnableUpdatePosition(terminal, contracts,
				modifier, event);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, runnable.getTerminal());
		assertSame(contracts, runnable.getContracts());
		assertSame(modifier, runnable.getPositionModifier());
		assertSame(event, runnable.getEvent());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("Update position for STK AAPL@SMART (primary: NASDAQ) "
				+ "-100 pcs. x 498.0 USD", runnable.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(runnable.equals(runnable));
		assertFalse(runnable.equals(this));
		assertFalse(runnable.equals(null));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<IBContracts> vConts = new Variant<IBContracts>(vTerm)
			.add(contracts)
			.add(control.createMock(IBContracts.class));
		Variant<S<EditablePosition>> vMod =
				new Variant<S<EditablePosition>>(vConts)
			.add(modifier)
			.add(control.createMock(S.class));
		Variant<IBEventUpdatePortfolio> vEvt =
				new Variant<IBEventUpdatePortfolio>(vMod)
			.add(event)
			.add(control.createMock(IBEventUpdatePortfolio.class));
		Variant<?> iterator = vEvt;
		int foundCnt = 0;
		IBRunnableUpdatePosition found = null, x = null;
		do {
			x = new IBRunnableUpdatePosition(vTerm.get(), vConts.get(),
					vMod.get(), vEvt.get());
			if ( runnable.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(contracts, found.getContracts());
		assertSame(modifier, found.getPositionModifier());
		assertSame(event, found.getEvent());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 15315)
			.append(terminal)
			.append(contracts)
			.append(modifier)
			.append(event)
			.toHashCode(), runnable.hashCode());
	}
	
	@Test
	public void testRun_Ok() throws Exception {
		EditablePortfolio portfolio=control.createMock(EditablePortfolio.class);
		EditablePosition position = control.createMock(EditablePosition.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
		expect(terminal.getEditablePortfolio(eq(new Account("TEST"))))
				.andReturn(portfolio);
		expect(contracts.getAppropriateSecurityDescriptor(eq(1172)))
				.andReturn(descr);
		expect(portfolio.getEditablePosition(eq(descr))).andReturn(position);
		modifier.set(same(position), same(event));
		control.replay();
		
		runnable.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_Exception1() throws Exception {
		expect(terminal.getEditablePortfolio(eq(new Account("TEST"))))
			.andThrow(new PortfolioException("Test exception"));
		terminal.firePanicEvent(1, "IBRunnableUpdatePosition#run");
		control.replay();
		
		runnable.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_Exception2() throws Exception {
		EditablePortfolio portfolio=control.createMock(EditablePortfolio.class);
		expect(terminal.getEditablePortfolio(eq(new Account("TEST"))))
			.andReturn(portfolio);
		expect(contracts.getAppropriateSecurityDescriptor(eq(1172)))
			.andThrow(new IBException("Test exception"));
		terminal.firePanicEvent(1, "IBRunnableUpdatePosition#run");
		control.replay();
		
		runnable.run();
		
		control.verify();
	}

}
