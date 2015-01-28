package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrderActivatorLinkTest {
	private IMocksControl control;
	private Terminal terminal;
	private EditableOrder order;
	private OrderActivator activator;
	private OrderActivatorLink link;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		order = control.createMock(EditableOrder.class);
		activator = control.createMock(OrderActivator.class);
		link = new OrderActivatorLink();
		
		expect(order.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testLink_Ok() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.PENDING);
		order.setActivator(same(activator));
		order.setStatus(eq(OrderStatus.CONDITION));
		order.setChanged(eq(EditableOrder.ACTIVATOR_CHANGED));
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		link.link(activator, order);
		
		control.verify();
		assertSame(order, link.getOrder());
		assertSame(activator, link.getActivator());
	}
	
	@Test (expected=OrderException.class)
	public void testLink_ThrowsIfAlreadyLinked() throws Exception {
		link.setOrder(order);
		control.replay();
		
		link.link(activator, order);
		
		control.verify();
	}
	
	@Test
	public void testLink_ThrowsIfOrderNotPending() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.ACTIVE,
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.CONDITION,
				OrderStatus.FILLED,
				OrderStatus.REJECTED,
				OrderStatus.SENT,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(expected[i]);
			control.replay();
			try {
				link.link(activator, order);
				fail("Expected: " + OrderException.class);
			} catch ( OrderException e ) {
				control.verify();
			}
		}
	}
	
	@Test
	public void testFireChanged() throws Exception {
		link.setOrder(order);
		order.setChanged(eq(EditableOrder.ACTIVATOR_CHANGED));
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		link.fireChanged();
		
		control.verify();
	}
	
	@Test
	public void testActivate_Ok() throws Exception {
		link.setOrder(order);
		expect(order.getStatus()).andReturn(OrderStatus.CONDITION);
		terminal.placeOrder(same(order));
		control.replay();
		
		link.activate();
		
		control.verify();
	}

	@Test
	public void testActivate_SkipByStatus() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.ACTIVE,
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.PENDING,
				OrderStatus.FILLED,
				OrderStatus.REJECTED,
				OrderStatus.SENT,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			link.setOrder(order);
			expect(order.getStatus()).andReturn(expected[i]);
			expect(order.getId()).andStubReturn(819);
			control.replay();
			
			link.activate();
			
			control.verify();
		}
	}
	
	@Test
	public void testActivate_TerminalThrows() throws Exception {
		link.setOrder(order);
		expect(order.getStatus()).andReturn(OrderStatus.CONDITION);
		terminal.placeOrder(same(order));
		expectLastCall().andThrow(new OrderException("test error"));
		control.replay();
		
		link.activate();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(link.equals(link));
		assertFalse(link.equals(null));
		assertFalse(link.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EditableOrder> vOrd = new Variant<EditableOrder>()
			.add(order)
			.add(control.createMock(EditableOrder.class));
		Variant<OrderActivator> vAct = new Variant<OrderActivator>(vOrd)
			.add(activator)
			.add(control.createMock(OrderActivator.class));
		link.setActivator(activator);
		link.setOrder(order);
		Variant<?> iterator = vAct;
		int foundCnt = 0;
		OrderActivatorLink x, found = null;
		do {
			x = new OrderActivatorLink();
			x.setOrder(vOrd.get());
			x.setActivator(vAct.get());
			if ( link.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(activator, found.getActivator());
		assertSame(order, found.getOrder());
	}

}
