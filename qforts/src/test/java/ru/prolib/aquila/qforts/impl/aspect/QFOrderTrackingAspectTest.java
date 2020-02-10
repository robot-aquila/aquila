package ru.prolib.aquila.qforts.impl.aspect;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.aspectj.lang.ProceedingJoinPoint;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.qforts.impl.QFOrderTracker;

public class QFOrderTrackingAspectTest {
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private QFOrderTracker trackerMock;
	private EditableOrder eorderMock;
	private Order orderMock;
	private ProceedingJoinPoint jpMock;
	private QFOrderTrackingAspect service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		trackerMock = control.createMock(QFOrderTracker.class);
		eorderMock = control.createMock(EditableOrder.class);
		orderMock = control.createMock(Order.class);
		jpMock = control.createMock(ProceedingJoinPoint.class);
		service = new QFOrderTrackingAspect(trackerMock);
	}
	
	@Test
	public void aroundRegisterOrder_RegistrationComfirmed() throws Throwable {
		expect(eorderMock.getID()).andStubReturn(123998L);
		expect(jpMock.proceed()).andReturn(true);
		trackerMock.startTrackingOrder(eorderMock);
		control.replay();
		
		assertEquals(Boolean.TRUE, service.aroundRegisterOrder(jpMock, eorderMock));
		
		control.verify();
	}
	
	@Test
	public void aroundRegisterOrder_RegistrationNotConfirmed() throws Throwable {
		expect(eorderMock.getID()).andStubReturn(226412L);
		expect(jpMock.proceed()).andReturn(false);
		control.replay();
		
		assertEquals(Boolean.FALSE, service.aroundRegisterOrder(jpMock, eorderMock));
		
		control.verify();
	}
	
	@Test
	public void testAroundPurgeOrder_RemovalConfirmed() throws Throwable {
		expect(orderMock.getID()).andStubReturn(77541L);
		expect(jpMock.proceed()).andReturn(true);
		trackerMock.stopTrackingOrder(orderMock);
		control.replay();
		
		assertEquals(Boolean.TRUE, service.aroundPurgeOrder(jpMock, orderMock));
		
		control.verify();
	}

	@Test
	public void testAroundPurgeOrder_RemovalNotConfirmed() throws Throwable {
		expect(orderMock.getID()).andStubReturn(75441L);
		expect(jpMock.proceed()).andReturn(false);
		control.replay();
		
		assertEquals(Boolean.FALSE, service.aroundPurgeOrder(jpMock, orderMock));
		
		control.verify();
	}

}
