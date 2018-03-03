package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderEvent;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class ALODataProviderImplTest {
	private IMocksControl control;
	private Order orderMock1, orderMock2, orderMock3, orderMock4;
	private ALOValidator validatorMock;
	private Terminal terminalMock1, terminalMock2;
	private Set<Terminal> terminalsStub;
	private Set<Order> ordersStub;
	private ALODataProviderImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		orderMock1 = control.createMock(Order.class);
		orderMock2 = control.createMock(Order.class);
		orderMock3 = control.createMock(Order.class);
		orderMock4 = control.createMock(Order.class);
		validatorMock = control.createMock(ALOValidator.class);
		terminalMock1 = control.createMock(Terminal.class);
		terminalMock2 = control.createMock(Terminal.class);
		terminalsStub = new LinkedHashSet<>();
		ordersStub = new LinkedHashSet<>();
		service = new ALODataProviderImpl(terminalsStub, ordersStub, validatorMock);
	}
	
	@Test
	public void testCtor3() {
		assertSame(validatorMock, service.getValidator());
		assertSame(ordersStub, service.getTrackedOrders());
		assertSame(terminalsStub, service.getTrackedTerminals());
	}
	
	@Test
	public void testCtor1() {
		service = new ALODataProviderImpl(validatorMock);
		assertSame(validatorMock, service.getValidator());
		assertEquals(new HashSet<Terminal>(), service.getTrackedTerminals());
		assertEquals(new HashSet<Order>(), service.getTrackedOrders());
	}
	
	@Test
	public void testCtor2() {
		EventType et1 = new EventTypeImpl(), et2 = new EventTypeImpl();
		expect(terminalMock1.onOrderRegistered()).andStubReturn(et1);
		expect(terminalMock1.onOrderDone()).andStubReturn(et2);
		Set<Order> orders = new LinkedHashSet<>();
		orders.add(orderMock1);
		orders.add(orderMock2);
		orders.add(orderMock3);
		expect(terminalMock1.getOrders()).andReturn(orders);
		expect(validatorMock.isValid(orderMock1)).andReturn(true);
		expect(validatorMock.isValid(orderMock2)).andReturn(false);
		expect(validatorMock.isValid(orderMock3)).andReturn(true);
		control.replay();
		
		service = new ALODataProviderImpl(validatorMock, terminalMock1);
		
		control.verify();
		Set<Order> expectedOrders = new HashSet<>();
		expectedOrders.add(orderMock1);
		expectedOrders.add(orderMock3);
		assertEquals(expectedOrders, service.getTrackedOrders());
		Set<Terminal> expectedTerminals = new HashSet<>();
		expectedTerminals.add(terminalMock1);
		assertEquals(expectedTerminals, service.getTrackedTerminals());
		assertTrue(et1.isListener(service));
		assertTrue(et2.isListener(service));
	}
	
	@Test
	public void testAddTerminal() {
		EventType et1 = new EventTypeImpl(), et2 = new EventTypeImpl();
		expect(terminalMock1.onOrderRegistered()).andStubReturn(et1);
		expect(terminalMock1.onOrderDone()).andStubReturn(et2);
		Set<Order> orders = new LinkedHashSet<>();
		orders.add(orderMock1);
		orders.add(orderMock2);
		expect(terminalMock1.getOrders()).andReturn(orders);
		expect(validatorMock.isValid(orderMock1)).andReturn(true);
		expect(validatorMock.isValid(orderMock2)).andReturn(false);
		control.replay();
		ordersStub.add(orderMock3);
		terminalsStub.add(terminalMock2);
		
		service.addTerminal(terminalMock1);
		
		control.verify();
		Set<Order> expectedOrders = new HashSet<>();
		expectedOrders.add(orderMock1);
		expectedOrders.add(orderMock3);
		assertEquals(expectedOrders, service.getTrackedOrders());
		assertEquals(expectedOrders, ordersStub);
		Set<Terminal> expectedTerminals = new HashSet<>();
		expectedTerminals.add(terminalMock1);
		expectedTerminals.add(terminalMock2);
		assertEquals(expectedTerminals, service.getTrackedTerminals());
		assertEquals(expectedTerminals, terminalsStub);
		assertTrue(et1.isListener(service));
		assertTrue(et2.isListener(service));
	}
	
	@Test
	public void testAddTerminal_SkipIfAlreadyExists() {
		terminalsStub.add(terminalMock1);
		control.replay();
		
		service.addTerminal(terminalMock1);
		
		control.verify();
	}
	
	@Test
	public void testRemoveTerminal() {
		EventType et1 = new EventTypeImpl(), et2 = new EventTypeImpl();
		expect(terminalMock1.onOrderRegistered()).andStubReturn(et1);
		expect(terminalMock1.onOrderDone()).andStubReturn(et2);
		expect(orderMock1.getTerminal()).andStubReturn(terminalMock2);
		expect(orderMock2.getTerminal()).andStubReturn(terminalMock1);
		expect(orderMock3.getTerminal()).andStubReturn(terminalMock2);
		terminalsStub.add(terminalMock1);
		terminalsStub.add(terminalMock2);
		ordersStub.add(orderMock1);
		ordersStub.add(orderMock2);
		ordersStub.add(orderMock3);
		et1.addListener(service);
		et2.addListener(service);
		control.replay();
		
		service.removeTerminal(terminalMock1);
		
		control.verify();
		Set<Order> expectedOrders = new HashSet<>();
		expectedOrders.add(orderMock1);
		expectedOrders.add(orderMock3);
		assertEquals(expectedOrders, ordersStub);
		assertEquals(expectedOrders, service.getTrackedOrders());
		Set<Terminal> expectedTerminals = new HashSet<>();
		expectedTerminals.add(terminalMock2);
		assertEquals(expectedTerminals, terminalsStub);
		assertEquals(expectedTerminals, service.getTrackedTerminals());
		assertFalse(et1.isListener(service));
		assertFalse(et2.isListener(service));
	}
	
	@Test
	public void testRemoveTerminal_SkipIfNotExists() {
		control.replay();
		
		service.removeTerminal(terminalMock1);
		
		control.verify();
	}
	
	@Test
	public void testGetOrderVolumes() {
		expect(validatorMock.isValid(orderMock1)).andReturn(true);
		expect(orderMock1.getPrice()).andReturn(of("16.84"));
		expect(orderMock1.getAction()).andReturn(OrderAction.BUY);
		expect(orderMock1.getCurrentVolume()).andReturn(of(10L));
		expect(validatorMock.isValid(orderMock2)).andReturn(true);
		expect(orderMock2.getPrice()).andReturn(of("16.84"));
		expect(orderMock2.getAction()).andReturn(OrderAction.SELL);
		expect(orderMock2.getCurrentVolume()).andReturn(of(5L));
		expect(validatorMock.isValid(orderMock3)).andReturn(true);
		expect(orderMock3.getPrice()).andReturn(of("14.51"));
		expect(orderMock3.getAction()).andReturn(OrderAction.COVER);
		expect(orderMock3.getCurrentVolume()).andReturn(of(1L));
		expect(validatorMock.isValid(orderMock4)).andReturn(true);
		expect(orderMock4.getPrice()).andReturn(of("15.10"));
		expect(orderMock4.getAction()).andReturn(OrderAction.SELL_SHORT);
		expect(orderMock4.getCurrentVolume()).andReturn(of(100L));
		control.replay();
		ordersStub.add(orderMock1);
		ordersStub.add(orderMock2);
		ordersStub.add(orderMock3);
		ordersStub.add(orderMock4);
		
		Collection<ALOData> actual = service.getOrderVolumes();
		
		control.verify();
		assertEquals(3, actual.size());
		assertTrue(actual.contains(new ALODataImpl(of("16.84"), of(10L), of(  5L))));
		assertTrue(actual.contains(new ALODataImpl(of("14.51"), of( 1L), of(  0L))));
		assertTrue(actual.contains(new ALODataImpl(of("15.10"), of( 0L), of(100L))));
	}
	
	@Test
	public void testGetOrderVolumes_SkipIfOrderIsNotValid() {
		expect(validatorMock.isValid(orderMock1)).andReturn(false);
		expect(validatorMock.isValid(orderMock2)).andReturn(false);
		expect(validatorMock.isValid(orderMock3)).andReturn(false);
		control.replay();
		ordersStub.add(orderMock1);
		ordersStub.add(orderMock2);
		ordersStub.add(orderMock3);
		
		Collection<ALOData> actual = service.getOrderVolumes();
		
		control.verify();
		assertEquals(0, actual.size());
	}
	
	@Test
	public void testOnEvent_OnOrderRegistered() {
		EventType et1 = new EventTypeImpl();
		expect(terminalMock1.onOrderRegistered()).andReturn(et1);
		expect(validatorMock.isValid(orderMock1)).andReturn(true);
		control.replay();
		terminalsStub.add(terminalMock1);
		
		service.onEvent(new OrderEvent(et1, orderMock1, Instant.now()));
		
		control.verify();
		assertTrue(ordersStub.contains(orderMock1));
	}
	
	@Test
	public void testOnEvent_OnOrderRegistered_SkipIfOrderIsNotValid() {
		EventType et1 = new EventTypeImpl();
		expect(terminalMock1.onOrderRegistered()).andReturn(et1);
		expect(validatorMock.isValid(orderMock1)).andReturn(false);
		control.replay();
		terminalsStub.add(terminalMock1);
		
		service.onEvent(new OrderEvent(et1, orderMock1, Instant.now()));
		
		control.verify();
		assertFalse(ordersStub.contains(orderMock1));
	}
	
	@Test
	public void testOnEvent_OnOrderDone() {
		EventType et1 = new EventTypeImpl(), et2 = new EventTypeImpl();
		expect(terminalMock1.onOrderRegistered()).andReturn(et1);
		expect(terminalMock1.onOrderDone()).andReturn(et2);
		control.replay();
		terminalsStub.add(terminalMock1);
		ordersStub.add(orderMock1);
		
		service.onEvent(new OrderEvent(et2, orderMock1, Instant.now()));
		
		control.verify();
		assertFalse(ordersStub.contains(orderMock1));
	}	

}
