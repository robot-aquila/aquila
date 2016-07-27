package ru.prolib.aquila.core.concurrency;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class MultilockTest {
	private IMocksControl control;
	private Lock lock1Mock, lock2Mock, lock3Mock, lock4Mock, lock5Mock;
	private Lockable l1, l2, l3, l4, l5;
	private Multilock multilock;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		l1 = new LockableStub(lock1Mock = control.createMock(Lock.class));
		l2 = new LockableStub(lock2Mock = control.createMock(Lock.class));
		l3 = new LockableStub(lock3Mock = control.createMock(Lock.class));
		l4 = new LockableStub(lock4Mock = control.createMock(Lock.class));
		l5 = new LockableStub(lock5Mock = control.createMock(Lock.class));
	}
	
	@Test
	public void testCtor2() {
		multilock = new Multilock(l2, l1);
		lock1Mock.lock();
		lock2Mock.lock();
		control.replay();
		
		multilock.lock();
		
		control.verify();
	}

	@Test
	public void testCtor3() {
		multilock = new Multilock(l5, l1, l3);
		lock1Mock.lock();
		lock3Mock.lock();
		lock5Mock.lock();
		control.replay();
		
		multilock.lock();
		control.verify();
	}

	@Test
	public void testCtor4() {
		multilock = new Multilock(l5, l1, l4, l3);
		lock1Mock.lock();
		lock3Mock.lock();
		lock4Mock.lock();
		lock5Mock.lock();
		control.replay();
		
		multilock.lock();
		
		control.verify();
	}
	
	@Test
	public void testCtor5() {
		multilock = new Multilock(l2, l5, l1, l4, l3);
		lock1Mock.lock();
		lock2Mock.lock();
		lock3Mock.lock();
		lock4Mock.lock();
		lock5Mock.lock();
		control.replay();
		
		multilock.lock();
		
		control.verify();
	}

	@Test
	public void testLock() {
		List<Lockable> objects = new ArrayList<>();
		objects.add(l2);
		objects.add(l3);
		objects.add(l1);
		multilock = new Multilock(objects);
		lock1Mock.lock();
		lock2Mock.lock();
		lock3Mock.lock();
		control.replay();
		
		multilock.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		multilock = new Multilock(l1, l3, l5);
		lock5Mock.unlock();
		lock3Mock.unlock();
		lock1Mock.unlock();
		control.replay();
		
		multilock.unlock();
		
		control.verify();
	}

}
