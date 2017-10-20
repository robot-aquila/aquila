package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.core.*;

public class QUIKServiceLocatorTest {
	private IMocksControl control;
	private EventSystem es;
	private QUIKClient client;
	private Cache cache;
	private QUIKServiceLocator locator;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		control = createStrictControl();
		client = control.createMock(QUIKClient.class);
		cache = control.createMock(Cache.class);
		locator = new QUIKServiceLocator(client, cache);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(client, locator.getClient());
		assertSame(cache, locator.getDataCache());
	}
	
	@Test
	public void testConstructor1() throws Exception {
		assertTrue(Check.NOTWIN, Check.isWin());
		locator = new QUIKServiceLocator(es);
		assertNotNull(locator.getClient());
		assertNotNull(locator.getDataCache());
	}

}
