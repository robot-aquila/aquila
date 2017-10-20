package ru.prolib.aquila.core.data;


import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2013-01-07<br>
 * $Id: SetterArgsTest.java 399 2013-01-06 23:29:15Z whirlwind $
 */
public class SetterArgsTest {
	private final Object object = this;
	private final Object value = new String("zulu");
	private SetterArgs args;

	@Before
	public void setUp() throws Exception {
		args = new SetterArgs(object, value);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(object, args.getObject());
		assertSame(value, args.getValue());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(args.equals(args));
		assertTrue(args.equals(new SetterArgs(this, "zulu")));
		assertTrue(args.equals(new SetterArgs(this, value)));
		assertFalse(args.equals(new SetterArgs("aaa", "zulu")));
		assertFalse(args.equals(new SetterArgs(this, "zulu4")));
		assertFalse(args.equals(null));
		assertFalse(args.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 30019)
			.append(object)
			.append(value)
			.toHashCode(), args.hashCode());
	}

}
