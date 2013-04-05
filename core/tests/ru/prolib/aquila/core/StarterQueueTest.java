package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Vector;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

/**
 * 2012-12-03<br>
 * $Id: StarterQueueTest.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class StarterQueueTest {
	private static IMocksControl control;
	private static Starter s1,s2,s3;
	private StarterQueue queue;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		control = createStrictControl();
		s1 = control.createMock(Starter.class);
		s2 = control.createMock(Starter.class);
		s3 = control.createMock(Starter.class);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		queue = new StarterQueue();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(queue.equals(queue));
		assertTrue(queue.equals(new StarterQueue()));
		assertFalse(queue.equals(null));
		assertFalse(queue.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		StarterQueue q1 = new StarterQueue().add(s1).add(s2),
					 q2 = new StarterQueue().add(s3),
					 q3 = new StarterQueue().add(s2).add(s1);
		queue.add(s1).add(s2);
		
		assertTrue(queue.equals(q1));
		assertFalse(queue.equals(q2));
		assertFalse(queue.equals(q3));
	}
	
	@Test
	public void testHashCode() throws Exception {
		Vector<Starter> v = new Vector<Starter>();
		v.add(s1);
		v.add(s2);
		int hashCode = new HashCodeBuilder(20121203, 93133)
			.append(v)
			.toHashCode();
		queue.add(s1).add(s2);
		assertEquals(hashCode, queue.hashCode());
	}
	
	@Test
	public void testStart() throws Exception {
		queue.add(s1).add(s2).add(s3);
		s1.start();
		s2.start();
		s3.start();
		control.replay();
		queue.start();
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		queue.add(s1).add(s2).add(s3);
		s3.stop();
		s2.stop();
		s1.stop();
		control.replay();
		queue.stop();
		control.verify();
	}
	
	@Test
	public void testStart_WithExceptions() throws Exception {
		Exception expected = new StarterException("Test throws on start");
		queue.add(s1).add(s2).add(s3);
		s1.start();
		s2.start();
		s3.start();
		expectLastCall().andThrow(expected);
		s2.stop();
		expectLastCall().andThrow(new StarterException("Test throws on stop"));
		s1.stop();
		control.replay();
		try {
			queue.start();
			fail("Expected exception: " + expected.getClass().getSimpleName());
		} catch ( Exception e ) {
			assertSame(expected, e);
		}
		control.verify();
	}
	
	@Test
	public void testStop_WithExceptions() throws Exception {
		Exception expected = new StarterException("Last test exception");
		queue.add(s1).add(s2).add(s3);
		s3.stop();
		expectLastCall().andThrow(new StarterException("First test exception"));
		s2.stop();
		expectLastCall().andThrow(expected);
		s1.stop();
		control.replay();
		try {
			queue.stop();
			fail("Expected exception: " + expected.getClass().getSimpleName());
		} catch ( Exception e ) {
			assertSame(expected, e);
		}
		control.verify();
	}

}
