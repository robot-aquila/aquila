package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SMStateHandlerTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private SMInputAction inputAction1, inputAction2;
	private SMEnterAction enterAction;
	private SMExitAction exitAction;
	private SMStateHandler state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		inputAction1 = control.createMock(SMInputAction.class);
		inputAction2 = control.createMock(SMInputAction.class);
		enterAction = control.createMock(SMEnterAction.class);
		exitAction = control.createMock(SMExitAction.class);
		state = new SMStateHandler();
	}

	@Test
	public void testConstruct0() throws Exception {
		assertNull(state.getEnterAction());
		assertNull(state.getExitAction());
		assertEquals(new Vector<SMExit>(), state.getExits());
		assertEquals(new Vector<SMInput>(), state.getInputs());
	}
	
	@Test
	public void testConstruct1EnterAction() throws Exception {
		state = new SMStateHandler(enterAction);
		assertSame(enterAction, state.getEnterAction());
		assertNull(state.getExitAction());
		assertEquals(new Vector<SMExit>(), state.getExits());
		assertEquals(new Vector<SMInput>(), state.getInputs());
	}
	
	@Test
	public void testConstruct1ExitAction() throws Exception {
		state = new SMStateHandler(exitAction);
		assertNull(state.getEnterAction());
		assertSame(exitAction, state.getExitAction());
		assertEquals(new Vector<SMExit>(), state.getExits());
		assertEquals(new Vector<SMInput>(), state.getInputs());
	}

	@Test
	public void testConstruct2() throws Exception {
		state = new SMStateHandler(enterAction, exitAction);
		assertSame(enterAction, state.getEnterAction());
		assertSame(exitAction, state.getExitAction());
		assertEquals(new Vector<SMExit>(), state.getExits());
		assertEquals(new Vector<SMInput>(), state.getInputs());
	}
	
	@Test
	public void testCtor4() {
		state = new SMStateHandler(enterAction, exitAction, Integer.class, String.class);
		assertSame(enterAction, state.getEnterAction());
		assertSame(exitAction, state.getExitAction());
		assertEquals(new Vector<SMExit>(), state.getExits());
		assertEquals(new Vector<SMInput>(), state.getInputs());
		assertEquals(Integer.class, state.getIncomingDataType());
		assertEquals(String.class, state.getResultDataType());
	}
	
	@Test
	public void testRegisterInput() throws Exception {
		SMInput in1 = state.registerInput(inputAction1);
		SMInput in2 = state.registerInput(inputAction2);
		List<SMInput> expected = new Vector<SMInput>();
		expected.add(in1);
		expected.add(in2);
		assertEquals(expected, state.getInputs());
	}
	
	@Test
	public void testRegisterExit() throws Exception {
		SMExit ex1 = state.registerExit("foo");
		SMExit ex2 = state.registerExit("bar");
		assertEquals("foo", ex1.getId());
		assertEquals("bar", ex2.getId());
		List<SMExit> expected = new Vector<SMExit>();
		expected.add(ex1);
		expected.add(ex2);
		assertEquals(expected, state.getExits());
	}
	
	@Test (expected=NullPointerException.class)
	public void testRegisterExit_ThrowsIfNullId() throws Exception {
		state.registerExit(null);
	}
	
	@Test
	public void testRegisterExit_ReturnExistingIfRegistered() throws Exception {
		SMExit ex1 = state.registerExit("foo");
		assertSame(ex1, state.registerExit("foo"));
	}
	
	@Test
	public void testSetEnterAction() throws Exception {
		state.setEnterAction(enterAction);
		assertSame(enterAction, state.getEnterAction());
	}
	
	@Test
	public void testSetExitAction() throws Exception {
		state.setExitAction(exitAction);
		assertSame(exitAction, state.getExitAction());
	}
	
	@Test
	public void testGetExit() throws Exception {
		SMExit ex1 = state.registerExit("foo"),
			   ex2 = state.registerExit("bar");
		assertSame(ex1, state.getExit("foo"));
		assertSame(ex2, state.getExit("bar"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetExit_ThrowsIfNotExists() throws Exception {
		state.getExit("zulu42");
	}
	
	@Test
	public void testGetExit_ReturnsNullIfNullID() throws Exception {
		assertNull(state.getExit(null));
	}
	
	@Test
	public void testSetIncomingData_NullData() {
		state.setIncomingDataType(Integer.class);
		
		state.setIncomingData(null);
		
		assertNull(state.getIncomingData());
	}
	
	@Test
	public void testSetIncomingData_ThrowsIfUnexpectedType() {
		state.setIncomingDataType(Integer.class);
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Unexpected data type: " + Boolean.class);
		
		state.setIncomingData(false);
	}
	
	@Test
	public void testSetIncomingData_OK() {
		state.setIncomingDataType(Integer.class);
		
		state.setIncomingData(100500);
		
		Integer actual = state.getIncomingData();
		assertEquals(Integer.valueOf(100500), actual);
	}
	
	
	@Test
	public void testSetIncomingData_SubclassesPermitted() {
		state.setIncomingDataType(Number.class);
		
		state.setIncomingData(Double.valueOf("215.004"));
		
		assertEquals(Double.valueOf("215.004"), state.getIncomingData());
	}
	
	@Test
	public void testGetIncomingData_ThrowsIfCallSignatureMismatchDataType() {
		state.setIncomingDataType(Integer.class);
		state.setIncomingData(100500);
		eex.expect(ClassCastException.class);
		
		Boolean actual = state.getIncomingData();
		assertFalse(actual);
	}
	
	@Test
	public void testSetResultData_NullData() {
		state.setResultDataType(String.class);
		
		state.setResultData(null);
		
		assertNull(state.getResultData());
	}
	
	@Test
	public void testSetResultData_ThrowsIfUnexpectedType() {
		state.setResultDataType(String.class);
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Unexpected data type: " + Integer.class);
		
		state.setResultData(24);
	}
	
	@Test
	public void testSetResultData_OK() {
		state.setResultDataType(String.class);
		
		state.setResultData("Hello, Bobby!");
		
		String actual = state.getResultData();
		assertEquals("Hello, Bobby!", actual);
	}
	
	@Test
	public void testSetResultData_SubclassesPermitted() {
		state.setResultDataType(Number.class);
		
		state.setResultData(Long.valueOf(2396671256L));
		
		Long actual = state.getResultData();
		assertEquals(Long.valueOf(2396671256L), actual);
	}
	
	@Test
	public void testGetResultData_ThrowsIfCallSignatyreMismatchDataType() {
		state.setResultDataType(Integer.class);
		state.setResultData(429127);
		eex.expect(ClassCastException.class);
		
		Boolean actual = state.getResultData();
		assertFalse(actual);
	}
	
}
