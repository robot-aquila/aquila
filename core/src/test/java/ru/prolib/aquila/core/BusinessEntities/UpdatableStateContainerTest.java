package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.concurrent.locks.Lock;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class UpdatableStateContainerTest {
	private IMocksControl control;
	private Lock lockMock;
	private UpdatableStateContainerImpl container;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		lockMock = control.createMock(Lock.class);
		container = new UpdatableStateContainerImpl("zulu24");
	}

	@Test
	public void testConsume_DeltaUpdate() {
		container.consume(new DeltaUpdateBuilder()
			.withToken(1, 245)
			.withToken(2, "foobar")
			.withToken(3, this)
			.buildUpdate());
		
		assertEquals(new Integer(245), container.getInteger(1));
		assertEquals("foobar", container.getString(2));
		assertSame(this, container.getObject(3));
	}
	
	@Test
	public void testCtor2() {
		container = new UpdatableStateContainerImpl("foo", lockMock);
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		container.lock();
		container.unlock();
		
		control.verify();
	}

}
