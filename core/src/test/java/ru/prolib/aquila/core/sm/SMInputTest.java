package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SMInputTest {
	private IMocksControl control;
	private SMStateHandler state;
	private SMExit exit;
	private SMInputAction inputAction;
	private SMInput input;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		state = new SMStateHandler();
		exit = state.registerExit("zulu24");
		inputAction = control.createMock(SMInputAction.class);
		input = new SMInput(state, inputAction);
	}

	@Test
	public void testInput() {
		expect(inputAction.input(same(this))).andReturn(exit);
		control.replay();
		
		assertSame(exit, input.input(this));
		
		control.verify();
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(55689123, 65)
				.append(state)
				.append(inputAction)
				.build();
		
		assertEquals(expected, input.hashCode());
	}
	
	@Test
	public void testEquals() {
		SMStateHandler state2 = new SMStateHandler();
		SMInputAction actionMock2 = control.createMock(SMInputAction.class);
		
		assertTrue(input.equals(input));
		assertTrue(input.equals(new SMInput(state, inputAction)));
		assertFalse(input.equals(new SMInput(state2, inputAction)));
		assertFalse(input.equals(new SMInput(state, actionMock2)));
		assertFalse(input.equals(new SMInput(state2, actionMock2)));
		assertFalse(input.equals(null));
		assertFalse(input.equals(this));
	}

}
