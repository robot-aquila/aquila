package ru.prolib.aquila.core.eqs.v4;

import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

public class V4FlushIndicatorTest {
	private IMocksControl control;
	private V4Queue queueMock;
	private V4FlushIndicator service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(V4Queue.class);
		service = new V4FlushIndicator(queueMock);
	}
	
	@Test
	public void testStart() {
		control.replay();
		
		service.start();
		
		control.verify();
	}

	@Test
	public void testWaitForFlushing() throws Exception {
		queueMock.waitForFlushing(20L, TimeUnit.SECONDS);
		control.replay();
		
		service.waitForFlushing(20L, TimeUnit.SECONDS);
		service.waitForFlushing(1L, TimeUnit.MINUTES); // has no effect
		
		control.verify();
	}

}
