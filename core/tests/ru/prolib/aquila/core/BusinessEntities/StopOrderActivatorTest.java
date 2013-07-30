package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderActivatorLink;
import ru.prolib.aquila.core.utils.Variant;

public class StopOrderActivatorTest {
	private IMocksControl control;
	private OrderActivatorLink link;
	private EditableOrder order;
	private Security security;
	private EventType onChanged, onTrade;
	private StopOrderActivator activator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		link = control.createMock(OrderActivatorLink.class);
		order = control.createMock(EditableOrder.class);
		security = control.createMock(Security.class);
		onChanged = control.createMock(EventType.class);
		onTrade = control.createMock(EventType.class);
		activator = new StopOrderActivator(link, 18.24d);
		
		expect(link.getOrder()).andStubReturn(order);
		expect(order.getSecurity()).andStubReturn(security);
		expect(order.getId()).andStubReturn(245);
		expect(security.OnChanged()).andStubReturn(onChanged);
		expect(security.OnTrade()).andStubReturn(onTrade);
	}
	
	@Test
	public void testStart() throws Exception {
		link.link(same(activator), same(order));
		onChanged.addListener(same(activator));
		onTrade.addListener(same(activator));
		control.replay();
		
		activator.start(order);
		
		control.verify();
		assertSame(order, activator.getOrder());
		assertSame(security, activator.getSecurity());
	}
	
	@Test
	public void testStop() throws Exception {
		activator.setSecurity(security);
		onTrade.removeListener(same(activator));
		onChanged.removeListener(same(activator));
		control.replay();
		
		activator.stop();
		
		control.verify();
		assertNull(activator.getSecurity());
	}
	
	static class FR {
		private final Direction dir;
		private final Double price;
		private final boolean expected;
		FR(Direction dir, Double price, boolean expected) {
			this.dir = dir;
			this.price = price;
			this.expected = expected;
		}
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		FR fix[] = {
				new FR(Direction.BUY, null,	false),
				new FR(Direction.BUY, 18.24d, true),
				new FR(Direction.BUY, 18.50d, true),
				new FR(Direction.BUY, 18.10d, false),
				new FR(Direction.SELL, null, false),
				new FR(Direction.SELL, 18.24d, true),
				new FR(Direction.SELL, 18.10d, true),
				new FR(Direction.SELL, 18.50d, false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			activator.setSecurity(security);
			expect(security.getLastPrice()).andStubReturn(fix[i].price);
			expect(order.getDirection()).andStubReturn(fix[i].dir);
			if ( fix[i].expected ) link.activate();
			control.replay();
			
			activator.onEvent(new EventImpl(onChanged));
			
			control.verify();
		}
	}
	
	@Test
	public void testOnEvent_OnTrade() throws Exception {
		FR fix[] = {
				new FR(Direction.BUY, null,	false),
				new FR(Direction.BUY, 18.24d, true),
				new FR(Direction.BUY, 18.50d, true),
				new FR(Direction.BUY, 18.10d, false),
				new FR(Direction.SELL, null, false),
				new FR(Direction.SELL, 18.24d, true),
				new FR(Direction.SELL, 18.10d, true),
				new FR(Direction.SELL, 18.50d, false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			Trade trade = control.createMock(Trade.class);
			expect(trade.getPrice()).andStubReturn(fix[i].price);
			activator.setSecurity(security);
			expect(order.getDirection()).andStubReturn(fix[i].dir);
			if ( fix[i].expected ) link.activate();
			control.replay();
			
			activator.onEvent(new SecurityTradeEvent(onTrade, security, trade));
			
			control.verify();
		}
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("StopPrice=18.24", activator.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(activator.equals(activator));
		assertFalse(activator.equals(null));
		assertFalse(activator.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<OrderActivatorLink> vLnk = new Variant<OrderActivatorLink>()
			.add(link)
			.add(control.createMock(OrderActivatorLink.class));
		Variant<Double> vStpPr = new Variant<Double>(vLnk)
			.add(18.24d)
			.add(180.15d);
		Variant<Security> vSec = new Variant<Security>(vStpPr)
			.add(security)
			.add(control.createMock(Security.class));
		Variant<?> iterator = vSec;
		activator.setSecurity(security);
		int foundCnt = 0;
		StopOrderActivator x, found = null;
		do {
			x = new StopOrderActivator(vLnk.get(), vStpPr.get());
			x.setSecurity(vSec.get());
			if ( activator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(link, found.getLink());
		assertEquals(18.24d, found.getStopPrice(), 0.01d);
		assertSame(security, found.getSecurity());
	}
	
}
