package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.sm.OnFinishAction;
import ru.prolib.aquila.core.sm.SMExit;

public class OnFinishActionTest {
	private IMocksControl control;
	private OnFinishAction.Handler handlerMock1, handlerMock2;
	private OnFinishAction service;
	private SMExit exitStub;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handlerMock1 = control.createMock(OnFinishAction.Handler.class);
		handlerMock2 = control.createMock(OnFinishAction.Handler.class);
		service = new OnFinishAction(handlerMock1);
		exitStub = SMExit.STUB;
	}
	
	@Test
	public void testInput() {
		expect(handlerMock1.onFinish("any data")).andReturn(exitStub);
		control.replay();
		
		assertSame(exitStub, service.input("any data"));
		
		control.verify();
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(7761527, 1785)
				.append(handlerMock1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new OnFinishAction(handlerMock1)));
		assertFalse(service.equals(new OnFinishAction(handlerMock2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
