package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.Arrays;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class OrderDefinitionProviderStubTest {
	IMocksControl control;
	OrderDefinition defMock1, defMock2, defMock3;
	CloseableIteratorStub<OrderDefinition> it;
	OrderDefinitionProviderStub service;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		defMock1 = control.createMock(OrderDefinition.class);
		defMock2 = control.createMock(OrderDefinition.class);
		defMock3 = control.createMock(OrderDefinition.class);
		it = new CloseableIteratorStub<>(Arrays.asList(defMock1, defMock2, defMock3));
		service = new OrderDefinitionProviderStub(it);
	}
	
	@Test
	public void testIterate_WithData() throws Exception {
		assertSame(defMock1, service.getNextDefinition()); assertFalse(it.isClosed());
		assertSame(defMock2, service.getNextDefinition()); assertFalse(it.isClosed());
		assertSame(defMock3, service.getNextDefinition()); assertFalse(it.isClosed());
		assertNull(service.getNextDefinition()); assertTrue(it.isClosed());
		assertNull(service.getNextDefinition()); assertTrue(it.isClosed());
	}
	
	@Test
	public void testClose() throws Exception {
		assertSame(defMock1, service.getNextDefinition()); assertFalse(it.isClosed());
		
		service.close();
		
		assertTrue(it.isClosed());
	}

	@Test
	public void testIterate_NoData() throws Exception {
		service = new OrderDefinitionProviderStub();
		
		assertNull(service.getNextDefinition());
	}

}
