package ru.prolib.aquila.core.utils;


import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;

/**
 * 2012-11-16<br>
 * $Id: SimpleCounterTest.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public class SimpleCounterTest {
	private Counter counter;

	@Before
	public void setUp() throws Exception {
		counter = new SimpleCounter();
	}
	
	@Test
	public void testConstruct0() throws Exception {
		assertEquals(0, counter.get());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		counter = new SimpleCounter(5);
		assertEquals(5, counter.get());
	}
	
	@Test
	public void testIncrement() throws Exception {
		counter.increment();
		assertEquals(1, counter.get());
		counter.increment();
		assertEquals(2, counter.get());
	}
	
	@Test
	public void testGetAndIncrement() throws Exception {
		assertEquals(0, counter.getAndIncrement());
		assertEquals(1, counter.get());
		assertEquals(1, counter.getAndIncrement());
		assertEquals(2, counter.get());
		assertEquals(2, counter.getAndIncrement());
	}
	
	@Test
	public void testSet() throws Exception {
		counter.set(200);
		assertEquals(200, counter.get());
	}
	
	@Test
	public void testIncrementAndGet() throws Exception {
		assertEquals(1, counter.incrementAndGet());
		assertEquals(1, counter.get());
		assertEquals(2, counter.incrementAndGet());
		assertEquals(2, counter.get());
		assertEquals(3, counter.incrementAndGet());
		assertEquals(3, counter.get());
	}
	
	@Test
	public void testEquals() throws Exception {
		counter.increment();
		counter.increment();
		assertTrue(counter.equals(counter));
		assertTrue(counter.equals(new SimpleCounter(2)));
		assertFalse(counter.equals(this));
		assertFalse(counter.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		counter = new SimpleCounter(15);
		int hashCode = new HashCodeBuilder(20121117, 130807)
			.append(15)
			.toHashCode();
		assertEquals(hashCode, counter.hashCode());
	}
	
	@Test
	public void testGetAndDecrement() throws Exception {
		assertEquals( 0, counter.getAndDecrement());
		assertEquals(-1, counter.getAndDecrement());
		assertEquals(-2, counter.getAndDecrement());
		assertEquals(-3, counter.getAndDecrement());
	}
	
	@Test
	public void testDecrementAndGet() throws Exception {
		assertEquals(-1, counter.decrementAndGet());
		assertEquals(-2, counter.decrementAndGet());
		assertEquals(-3, counter.decrementAndGet());
		assertEquals(-4, counter.decrementAndGet());
	}
	
	@Test
	public void testDecrement() throws Exception {
		assertEquals( 0, counter.get());
		counter.decrement();
		assertEquals(-1, counter.get());
		counter.decrement();
		assertEquals(-2, counter.get());
		counter.decrement();
	}

}
