package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;
import ru.prolib.aquila.quik.dde.Cache;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

public class QUIKTerminalImplTest {
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private QUIKTerminalImpl decorator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		decorator = new QUIKTerminalImpl();
		decorator.setServiceLocator(locator);
	}
	
	@Test
	public void testDerivedOfTerminalDecorator() {
		assertTrue(decorator instanceof TerminalDecorator);
	}
	
	@Test
	public void testGetDdeCache() throws Exception {
		Cache cache = control.createMock(Cache.class);
		expect(locator.getDdeCache()).andReturn(cache);
		control.replay();
		
		assertSame(cache, decorator.getDdeCache());
		
		control.verify();
	}

}
