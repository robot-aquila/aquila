package ru.prolib.aquila.core.concurrency;

import static org.junit.Assert.*;

import java.util.Comparator;

import org.junit.Test;

public class LockableComparatorTest {

	@Test
	public void testCompare() {
		Lockable l1 = new LockableStub(),
				l2 = new LockableStub(),
				l3 = new LockableStub();
		Comparator<Lockable> cmp = LockableComparator.getInstance();
		
		assertEquals( 0, cmp.compare(l1, l1));
		assertEquals(-1, cmp.compare(l1, l2));
		assertEquals( 1, cmp.compare(l2, l1));
		assertEquals(-1, cmp.compare(l1, l3));
		assertEquals( 1, cmp.compare(l3, l1));
	}

}
