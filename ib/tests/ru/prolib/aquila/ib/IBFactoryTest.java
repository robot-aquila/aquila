package ru.prolib.aquila.ib;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * 2013-01-15<br>
 * $Id: IBFactoryTest.java 435 2013-01-15 13:27:19Z whirlwind $
 */
public class IBFactoryTest {
	private IBFactory factory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		factory = new IBFactory();
		factory.equals(null);
	}
	
	@Test
	public void test_() throws Exception {
		fail("TODO: incomplete");
	}

}
