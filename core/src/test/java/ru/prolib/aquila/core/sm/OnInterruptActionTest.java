package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.sm.OnInterruptAction;
import ru.prolib.aquila.core.sm.SMExit;

public class OnInterruptActionTest {
	private IMocksControl control;
	private OnInterruptAction.Handler handlerMock1, handlerMock2;
	private OnInterruptAction service;
	private SMExit exitStub;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handlerMock1 = control.createMock(OnInterruptAction.Handler.class);
		handlerMock2 = control.createMock(OnInterruptAction.Handler.class);
		service = new OnInterruptAction(handlerMock1);
		exitStub = SMExit.STUB;
	}
	
	@Test
	public void testInput() {
		expect(handlerMock1.onInterrupt("any data")).andReturn(exitStub);
		control.replay();
		
		assertSame(exitStub, service.input("any data"));
		
		control.verify();
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(437809167, 61)
				.append(handlerMock1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new OnInterruptAction(handlerMock1)));
		assertFalse(service.equals(new OnInterruptAction(handlerMock2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
