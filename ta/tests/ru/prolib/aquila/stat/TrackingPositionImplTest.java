package ru.prolib.aquila.stat;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorImpl;
import ru.prolib.aquila.ta.ds.MarketData;


public class TrackingPositionImplTest {
	IMocksControl control;
	MarketData data;
	ServiceLocator locator;
	TrackingPositionImpl tracking;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		data = control.createMock(MarketData.class);
		locator = new ServiceLocatorImpl();
		locator.setMarketData(data);
		tracking = new TrackingPositionImpl(locator);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(0, tracking.getChanges().size());
		assertFalse(tracking.isClosed());
	}
	
	@Test (expected=TrackingException.class)
	public void testAddChange_ThrowsIfNotLimitOrder() throws Exception {
		tracking.addChange(new OrderImpl(1L, Order.BUY, 10));
	}
	
	@Test (expected=TrackingException.class)
	public void testAddChange_ThrowsIfZeroQty() throws Exception {
		tracking.addChange(new OrderImpl(1L, Order.SELL, 0, 120d));
	}
	
	@Test (expected=TrackingException.class)
	public void testAddChange_ThrowsIfOrderNotFilled() throws Exception {
		tracking.addChange(new OrderImpl(1L, Order.SELL, 10, 120d));
	}
	
	@Test (expected=TrackingException.class)
	public void testAddChange_ThrowsIfClosed() throws Exception {
		expect(data.getLastBarIndex()).andReturn(1);
		expect(data.getLastBarIndex()).andReturn(2);
		control.replay();
		
		Order order = new OrderImpl(1L, Order.SELL, 1, 120d);
		order.fill();
		tracking.addChange(order);
		assertFalse(tracking.isClosed());

		order = new OrderImpl(2L, Order.BUY, 1, 119d);
		order.fill();
		tracking.addChange(order);
		assertTrue(tracking.isClosed());
		
		order = new OrderImpl(3L, Order.SELL, 5, 121d);
		order.fill();
		tracking.addChange(order);
	}
	
	@Test
	public void testAddChange_Ok() throws Exception {
		expect(data.getLastBarIndex()).andReturn(1);
		expect(data.getLastBarIndex()).andReturn(2);
		expect(data.getLastBarIndex()).andReturn(3);
		control.replay();

		Order order1 = new OrderImpl(1L, Order.BUY, 1, 120d);
		order1.fill();
		tracking.addChange(order1);
		assertFalse(tracking.isClosed());
		
		Order order2 = new OrderImpl(2L, Order.BUY, 4, 125d);
		order2.fill();
		tracking.addChange(order2);
		assertFalse(tracking.isClosed());
		
		Order order3 = new OrderImpl(3L, Order.SELL, 5, 121d);
		order3.fill();
		tracking.addChange(order3);
		assertTrue(tracking.isClosed());
		assertTrue(tracking.isLong());
		assertFalse(tracking.isShort());
		
		control.verify();
		List<TrackingPositionChange> changes = tracking.getChanges();
		assertEquals(3, changes.size());
		assertEquals(1, changes.get(0).getBarIndex());
		assertSame(order1, changes.get(0).getOrder());
		assertEquals(2, changes.get(1).getBarIndex());
		assertSame(order2, changes.get(1).getOrder());
		assertEquals(3, changes.get(2).getBarIndex());
		assertSame(order3, changes.get(2).getOrder());
	}
	
	@Test
	public void testGetFirstGetLastChange_Ok() throws Exception {
		expect(data.getLastBarIndex()).andReturn(1);
		expect(data.getLastBarIndex()).andReturn(2);
		expect(data.getLastBarIndex()).andReturn(3);
		control.replay();

		Order order1 = new OrderImpl(1L, Order.SELL, 1, 120d);
		order1.fill();
		tracking.addChange(order1);
		assertFalse(tracking.isClosed());
		
		Order order2 = new OrderImpl(2L, Order.SELL, 4, 125d);
		order2.fill();
		tracking.addChange(order2);
		assertFalse(tracking.isClosed());
		
		Order order3 = new OrderImpl(3L, Order.BUY, 5, 121d);
		order3.fill();
		tracking.addChange(order3);
		assertTrue(tracking.isClosed());
		
		control.verify();

		assertSame(tracking.getChanges().get(0), tracking.getFirstChange());
		assertSame(tracking.getChanges().get(2), tracking.getLastChange());
		assertTrue(tracking.isShort());
		assertFalse(tracking.isLong());
	}
	
	@Test
	public void testObserveChanges() throws Exception {
		Observer observer = control.createMock(Observer.class);
		
		expect(data.getLastBarIndex()).andReturn(1);
		final Order order1 = new OrderImpl(1L, Order.BUY, 1, 120d);
		order1.fill();
		observer.update(same(tracking), eq(TrackingPosition.EVENT_CHANGED));
		expectLastCall().andDelegateTo(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				assertEquals(1, tracking.getChanges().size());
				assertSame(order1, tracking.getFirstChange().getOrder());
				assertEquals(1, tracking.getFirstChange().getBarIndex());
				assertFalse(tracking.isClosed());
			}
		});
		
		expect(data.getLastBarIndex()).andReturn(202);
		final Order order2 = new OrderImpl(2L, Order.SELL, 1, 120d);
		order2.fill();
		observer.update(same(tracking), eq(TrackingPosition.EVENT_CLOSED));
		expectLastCall().andDelegateTo(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				assertEquals(2, tracking.getChanges().size());
				assertSame(order2, tracking.getLastChange().getOrder());
				assertEquals(202, tracking.getLastChange().getBarIndex());
				assertTrue(tracking.isClosed());
			}
		});
		tracking.addObserver(observer);
		control.replay();
		
		tracking.addChange(order1);
		tracking.addChange(order2);
		
		control.verify();
	}

}
