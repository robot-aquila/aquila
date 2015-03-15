package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

public class HandlerFactoryTest {
	private IMocksControl control;
	private EditableOrder order;
	private HandlerFactory factory;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		factory = new HandlerFactory();
	}
	
	@Test
	public void testCreatePlaceOrder() throws Exception {
		assertEquals(new PlaceHandler(order), factory.createPlaceOrder(order));
	}
	
	@Test
	public void testCreateCancelOrder() throws Exception {
		CancelHandler expected = new CancelHandler(216, order);
		assertEquals(expected, factory.createCancelOrder(216, order));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(factory.equals(factory));
		assertTrue(factory.equals(new HandlerFactory()));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

}
