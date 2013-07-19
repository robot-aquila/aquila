package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.*;

public class QUIKWrapperTest {
	private IMocksControl control;
	private QUIKMainHandler hMain;
	private QUIKTransactionHandler hTrans1, hTrans2, hTrans3;
	private QUIKWrapper wrapper;
	
	/**
	 * Вспомогательный класс фикстуры для тестирования эквивалентности.
	 */
	static class FR {
		private final int transId;
		private final QUIKTransactionHandler handler;
		FR(int transId, QUIKTransactionHandler handler) {
			this.transId = transId;
			this.handler = handler;
		}
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		hMain = control.createMock(QUIKMainHandler.class);
		hTrans1 = control.createMock(QUIKTransactionHandler.class);
		hTrans2 = control.createMock(QUIKTransactionHandler.class);
		hTrans3 = control.createMock(QUIKTransactionHandler.class);
		wrapper = new QUIKWrapper();
		wrapper.setMainHandler(hMain);
	}
	
	@Test
	public void testOnConnStatus() throws Exception {
		hMain.connectionStatus(eq(T2QConnStatus.DLL_CONN));
		control.replay();
		
		wrapper.OnConnStatus(T2QConnStatus.DLL_CONN);
		
		control.verify();
	}
	
	@Test
	public void testOnOrderStatus() throws Exception {
		T2QOrder order = control.createMock(T2QOrder.class);
		hMain.orderStatus(same(order));
		control.replay();
		
		wrapper.OnOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testOnTradeStatus() throws Exception {
		T2QTrade trade = control.createMock(T2QTrade.class);
		hMain.tradeStatus(same(trade));
		control.replay();
		
		wrapper.OnTradeStatus(trade);
		
		control.verify();
	}
	
	@Test
	public void testOnTransReply_HandlerExists() throws Exception {
		wrapper.setHandler(81, hTrans1);
		hTrans1.handle(eq(new QUIKResponse(T2QTransStatus.DONE, 81, 4L, "OK")));
		control.replay();
		
		wrapper.OnTransReply(T2QTransStatus.DONE, 81, 4L, "OK");
		
		control.verify();
	}
	
	@Test
	public void testOnTransReply_HandlerNotExists() throws Exception {
		control.replay();
		
		wrapper.OnTransReply(T2QTransStatus.DONE, 815, 342L, "OK");
		
		control.verify();
	}

	@Test
	public void testGetMainHandler() throws Exception {
		assertSame(hMain, wrapper.getMainHandler());
	}

	@Test
	public void testGetHandler() throws Exception {
		assertNull(wrapper.getHandler(815));
		wrapper.setHandler(815, hTrans1);
		assertSame(hTrans1, wrapper.getHandler(815));
	}
	
	@Test
	public void testRemoveHandler() throws Exception {
		wrapper.setHandler(815, hTrans1);
		wrapper.removeHandler(815);
		assertNull(wrapper.getHandler(815));
		control.replay();
		
		wrapper.OnTransReply(T2QTransStatus.DONE, 815, 342L, "OK");
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(wrapper.equals(wrapper));
		assertFalse(wrapper.equals(null));
		assertFalse(wrapper.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<FR> rows1 = new Vector<FR>();
		rows1.add(new FR(815, hTrans1));
		rows1.add(new FR(342, hTrans2));
		List<FR> rows2 = new Vector<FR>();
		rows2.add(new FR(342, hTrans2));
		rows2.add(new FR(528, hTrans3));
		for ( FR row : rows1 ) {
			wrapper.setHandler(row.transId, row.handler);
		}
		Variant<List<FR>> vRows = new Variant<List<FR>>()
			.add(rows1)
			.add(rows2);
		Variant<QUIKMainHandler> vMain = new Variant<QUIKMainHandler>(vRows)
			.add(hMain)
			.add(control.createMock(QUIKMainHandler.class));
		Variant<?> iterator = vMain;
		int foundCnt = 0;
		QUIKWrapper x, found = null;
		do {
			x = new QUIKWrapper();
			x.setMainHandler(vMain.get());
			for ( FR row : vRows.get() ) {
				x.setHandler(row.transId, row.handler);				
			}
			if ( wrapper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(hMain, found.getMainHandler());
		for ( FR row : rows1 ) {
			assertSame(row.handler, found.getHandler(row.transId));
		}
	}

}
