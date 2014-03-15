package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SMInputTest {
	private IMocksControl control;
	private SMState state;
	private SMExit exit;
	private SMInputAction inputAction;
	private SMInput input;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		state = new SMState();
		exit = new SMExit(state);
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

}
