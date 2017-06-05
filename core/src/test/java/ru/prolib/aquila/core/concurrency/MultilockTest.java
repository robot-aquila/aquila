package ru.prolib.aquila.core.concurrency;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class MultilockTest {
	private IMocksControl control;
	private LID lid1, lid2;
	private Lock lock1Mock, lock2Mock, lock3Mock, lock4Mock, lock5Mock;
	private Lockable l1, l2, l3, l4, l5;
	private Set<Lockable> set1, set2;
	private Multilock multilock;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		lid1 = LID.createInstance();
		lid2 = LID.createInstance();
		l1 = new LockableStub(lock1Mock = control.createMock(Lock.class));
		l2 = new LockableStub(lock2Mock = control.createMock(Lock.class));
		l3 = new LockableStub(lock3Mock = control.createMock(Lock.class));
		l4 = new LockableStub(lock4Mock = control.createMock(Lock.class));
		l5 = new LockableStub(lock5Mock = control.createMock(Lock.class));
		set1 = new HashSet<>();
		set1.add(l1);
		set1.add(l2);
		set1.add(l3);
		set1.add(l4);
		set1.add(l5);
		set2 = new HashSet<>();
		set2.add(l3);
		set2.add(l4);
		set2.add(l5);
	}
	
	@Test
	public void testCtor1_Set() {
		multilock = new Multilock(set2);
		assertTrue(LID.isLastCreatedLID(multilock.getLID()));
		List<Lockable> expected = new ArrayList<>();
		expected.add(l3);
		expected.add(l4);
		expected.add(l5);
		assertEquals(expected, multilock.getObjects());
	}
	
	@Test
	public void testCtor2_LIDAndSet() {
		multilock = new Multilock(lid2, set2);
		assertSame(lid2, multilock.getLID());
		List<Lockable> expected = new ArrayList<>();
		expected.add(l3);
		expected.add(l4);
		expected.add(l5);
		assertEquals(expected, multilock.getObjects());
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
		Set<Lockable> objects = new HashSet<>();
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
	
	@Test
	public void testEquals_SpecialCases() {
		multilock = new Multilock(lid1, set1);
		assertTrue(multilock.equals(multilock));
		assertFalse(multilock.equals(null));
		assertFalse(multilock.equals(this));
	}
	
	@Test
	public void testEquals() {
		multilock = new Multilock(lid1, set1);
		Variant<LID> vLID = new Variant<>(lid1, lid2);
		Variant<Set<Lockable>> vSet = new Variant<>(vLID, set1, set2);
		Variant<?> iterator = vSet;
		int foundCnt = 0;
		Multilock x, found = null;
		do {
			x = new Multilock(vLID.get(), vSet.get());
			if ( multilock.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(lid1, found.getLID());
		List<Lockable> expected = new ArrayList<>();
		expected.add(l1);
		expected.add(l2);
		expected.add(l3);
		expected.add(l4);
		expected.add(l5);
		assertEquals(expected, found.getObjects());
	}

}
