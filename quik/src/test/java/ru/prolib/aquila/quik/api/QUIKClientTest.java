package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.Check;
import ru.prolib.aquila.t2q.*;
import ru.prolib.aquila.t2q.jqt.JQTService;

public class QUIKClientTest {
	private IMocksControl control;
	private T2QService service;
	private QUIKWrapper wrapper;
	private QUIKTransactionHandler hTrans;
	private QUIKMainHandler hMain;
	private QUIKClient client;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = control.createMock(T2QService.class);
		wrapper = control.createMock(QUIKWrapper.class);
		hTrans = control.createMock(QUIKTransactionHandler.class);
		hMain = control.createMock(QUIKMainHandler.class);
		client = new QUIKClient(service, wrapper);
	}
	
	@Test
	public void testSetHandler() throws Exception {
		wrapper.setHandler(eq(81), same(hTrans));
		control.replay();
		
		client.setHandler(81, hTrans);
		
		control.verify();
	}
	
	@Test
	public void testRemoveHandler() throws Exception {
		wrapper.removeHandler(eq(290));
		control.replay();
		
		client.removeHandler(290);
		
		control.verify();
	}
	
	@Test
	public void testSetMainHandler() throws Exception {
		wrapper.setMainHandler(same(hMain));
		control.replay();
		
		client.setMainHandler(hMain);
		
		control.verify();
	}
	
	@Test
	public void testConnect() throws Exception {
		service.connect(eq("path"));
		control.replay();
		
		client.connect("path");
		
		control.verify();
	}
	
	@Test
	public void testDisconnect() throws Exception {
		service.disconnect();
		control.replay();
		
		client.disconnect();
		
		control.verify();
	}
	
	@Test
	public void testSend() throws Exception {
		service.send(eq("test trans"));
		control.replay();
		
		client.send("test trans");
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(client.equals(client));
		assertFalse(client.equals(null));
		assertFalse(client.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKWrapper> vWrp = new Variant<QUIKWrapper>()
			.add(wrapper)
			.add(control.createMock(QUIKWrapper.class));
		Variant<?> iterator = vWrp;
		int foundCnt = 0;
		QUIKClient x, found = null;
		do {
			x = new QUIKClient(control.createMock(T2QService.class),vWrp.get());
			if ( client.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(wrapper, found.getWrapper());
		assertNotSame(service, found.getService()); // не сравнивается
	}
	
	@Test
	public void testConstruct0() throws Exception {
		assertTrue(Check.NOTWIN, Check.isWin());
		client = new QUIKClient();
		assertEquals(new QUIKWrapper(), client.getWrapper());
		JQTService srv = (JQTService) client.getService();
		assertSame(client.getWrapper(), srv.getHandler().getCommonHandler());
	}

}
