package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.util.concurrent.locks.Lock;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class DataHandlerImplTest {
	private IMocksControl control;
	private Lock lockMock;
	private DataHandlerImpl handler;

	@Before
	public void setUp() throws Exception {
		control = EasyMock.createStrictControl();
		lockMock = control.createMock(Lock.class);
		handler = new DataHandlerImpl("foobar");
	}
	
	@Test
	public void testCtor1() {
		assertEquals("foobar", handler.getDescriptor());
		assertEquals(DataHandlerState.PENDING, handler.getState());
	}
	
	@Test
	public void testCtor3() {
		handler = new DataHandlerImpl("charlie", DataHandlerState.AVAILABLE);
		assertEquals("charlie", handler.getDescriptor());
		assertEquals(DataHandlerState.AVAILABLE, handler.getState());
	}
	
	@Test
	public void testLockUnlock() {
		handler = new DataHandlerImpl("zulu", DataHandlerState.ERROR, lockMock);
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		handler.lock();
		handler.unlock();
		
		control.verify();
	}

	@Test
	public void testSetState() {
		handler.setState(DataHandlerState.PENDING);
		assertFalse(handler.hasChanged());
		
		handler.setState(DataHandlerState.ERROR);
		assertTrue(handler.hasChanged());
		assertEquals(DataHandlerState.ERROR, handler.getState());
	}

}
