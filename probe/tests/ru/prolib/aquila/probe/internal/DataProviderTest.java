package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;
import ru.prolib.aquila.probe.timeline.TLSTimeline;

public class DataProviderTest {
	private static final SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("AXE", "BLAH", "USD", SecurityType.BOND);
	}
	
	private IMocksControl control;
	private DataProvider dp;
	private PROBETerminal terminal;
	private PROBEServiceLocator locator;
	private XFactory x;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = new XFactory().newTerminal("charlie");
		locator = terminal.getServiceLocator();
		x = control.createMock(XFactory.class);
		dp = new DataProvider(terminal, x);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStartSupply() throws Exception {
		PROBEDataStorage ds = control.createMock(PROBEDataStorage.class);
		TLSTimeline tl = control.createMock(TLSTimeline.class);
		Aqiterator<Tick> it = control.createMock(Aqiterator.class);
		SecurityProperties props = control.createMock(SecurityProperties.class);
		EditableSecurity es = terminal.getEditableSecurity(descr);
		SecurityHandlerFORTS h = control.createMock(SecurityHandlerFORTS.class);
		TickDataDispatcher th = control.createMock(TickDataDispatcher.class);
		DateTime time = DateTime.now();
		locator.setDataStorage(ds);
		locator.setTimeline(tl);
		expect(ds.getIterator(descr, time)).andReturn(it);
		expect(ds.getSecurityProperties(descr)).andReturn(props);
		expect(x.newSecurityHandlerFORTS(terminal, es, props)).andReturn(h);
		expect(x.newTickDataDispatcher(it, h)).andReturn(th);
		tl.registerSource(th);
		control.replay();
		
		dp.startSupply(descr, time);
		
		control.verify();
	}
	
}
