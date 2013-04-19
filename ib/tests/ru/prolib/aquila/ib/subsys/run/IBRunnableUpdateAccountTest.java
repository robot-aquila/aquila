package ru.prolib.aquila.ib.subsys.run;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.subsys.run.IBRunnableUpdateAccount;

/**
 * 2013-01-09<br>
 * $Id: IBRunnableUpdateAccountTest.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRunnableUpdateAccountTest {
	private static IMocksControl control;
	private static EditableTerminal terminal;
	private static PortfolioFactory fport;
	private static S<EditablePortfolio> modifier;
	private static IBEventUpdateAccount event;
	private static IBRunnableUpdateAccount runnable;
	private static final Account acc = new Account("TEST");
	private EditablePortfolio port;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		fport = control.createMock(PortfolioFactory.class);
		modifier = control.createMock(S.class);
		event = control.createMock(IBEventUpdateAccount.class);
		port = control.createMock(EditablePortfolio.class);
		runnable = new IBRunnableUpdateAccount(terminal,
				fport, modifier, event);
		expect(event.getAccount()).andStubReturn("TEST");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, runnable.getTerminal());
		assertSame(fport, runnable.getPortfolioFactory());
		assertSame(modifier, runnable.getPortfolioModifier());
		assertSame(event, runnable.getEvent());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(runnable.equals(runnable));
		assertFalse(runnable.equals(null));
		assertFalse(runnable.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(control.createMock(EditableTerminal.class))
			.add(terminal);
		Variant<PortfolioFactory> vFact = new Variant<PortfolioFactory>(vTerm)
			.add(control.createMock(PortfolioFactory.class))
			.add(fport);
		Variant<S<EditablePortfolio>> vMod =
				new Variant<S<EditablePortfolio>>(vFact)
			.add(control.createMock(S.class))
			.add(modifier);
		Variant<IBEventUpdateAccount> vEvt =
				new Variant<IBEventUpdateAccount>(vMod)
			.add(control.createMock(IBEventUpdateAccount.class))
			.add(event);
		Variant<?> iterator = vEvt;
		int foundCnt = 0;
		IBRunnableUpdateAccount x = null, found = null;
		do {
			x = new IBRunnableUpdateAccount(vTerm.get(),
					vFact.get(), vMod.get(), vEvt.get());
			if ( runnable.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(fport, found.getPortfolioFactory());
		assertSame(modifier, found.getPortfolioModifier());
		assertSame(event, found.getEvent());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130109, 14255)
			.append(terminal)
			.append(fport)
			.append(modifier)
			.append(event)
			.toHashCode(), runnable.hashCode());
		
	}
	
	@Test
	public void testRun_PortfolioExists() throws Exception {
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(port);
		modifier.set(same(port), same(event));
		control.replay();
		runnable.run();
		control.verify();
	}
	
	@Test
	public void testRun_NewPortfolio() throws Exception {
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(false);
		expect(fport.createPortfolio(eq(acc))).andReturn(port);
		terminal.registerPortfolio(same(port));
		modifier.set(same(port), same(event));
		control.replay();
		runnable.run();
		control.verify();
	}
	
	@Test
	public void testRun_Exception1() throws Exception {
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc)))
			.andThrow(new PortfolioException("Test exception"));
		terminal.firePanicEvent(1, "IBRunnableUpdateAccount#run");
		control.replay();
		runnable.run();
		control.verify();
	}
	
	@Test
	public void testRun_Exception2() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(false);
		expect(fport.createPortfolio(eq(acc))).andReturn(port);
		terminal.registerPortfolio(same(port));
		expectLastCall().andThrow(new PortfolioException("Test exception"));
		terminal.firePanicEvent(1, "IBRunnableUpdateAccount#run");
		control.replay();
		runnable.run();
		control.verify();
	}
	
	@Test
	public void testRun_HandlingModifierException() throws Exception {
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(port);
		modifier.set(same(port), same(event));
		expectLastCall().andThrow(new ValueException("test"));
		control.replay();
		
		runnable.run();
		
		control.verify();
	}

}
