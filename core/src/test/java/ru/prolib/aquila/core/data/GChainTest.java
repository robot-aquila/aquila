package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

/**
 * 2012-12-15<br>
 * $Id: GChainTest.java 338 2012-12-15 10:20:43Z whirlwind $
 */
public class GChainTest {
	private static IMocksControl control;
	private static G<Object> first;
	private static G<String> second;
	private static GChain<String> getter;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		first = control.createMock(G.class);
		second = control.createMock(G.class);
		getter = new GChain<String>(first, second);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(first, getter.getFirstGetter());
		assertSame(second, getter.getSecondGetter());
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		final Object source = new Object();
		final Object transformed = new Object();
		final String result = "test";
		expect(first.get(same(source))).andReturn(transformed);
		expect(second.get(same(transformed))).andReturn(result);
		control.replay();
		assertSame(result, getter.get(source));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		G<Object> first2 = control.createMock(G.class);
		G<String> second2 = control.createMock(G.class);
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GChain<String>(first, second)));
		assertFalse(getter.equals(new GChain<String>(first2, second)));
		assertFalse(getter.equals(new GChain<String>(first2, second2)));
		assertFalse(getter.equals(new GChain<String>(first, second2)));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121215, 141721)
			.append(first)
			.append(second)
			.toHashCode(), getter.hashCode());
	}

}
