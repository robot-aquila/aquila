package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.ValidatorEq;

/**
 * 2012-08-25<br>
 * $Id: MStdTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class MStdTest implements S<MStdTest> {
	private G<Integer> getter;
	private MStd<MStdTest> modifier;
	private int attribute = 0;

	@Before
	public void setUp() throws Exception {
		getter = new GCond<Integer>(new ValidatorEq("foo"),
				new GConst<Integer>(1),
				new GConst<Integer>(2));
		modifier = new MStd<MStdTest>(getter, this);
		attribute = 0;
	}
	
	@Override
	public void set(MStdTest object, Object value) {
		object.attribute = (Integer) value;
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(getter, modifier.getGetter());
		assertSame(this, modifier.getSetter());
	}
	
	@Test
	public void testSet() throws Exception {
		modifier.set(this, "foo");
		assertEquals(1, attribute);
		modifier.set(this, "bar");
		assertEquals(2, attribute);
	}
	
	@Test
	public void testEquals() throws Exception {
		G<Integer> getter2 = new GConst<Integer>(12345);
		S<MStdTest> setter2 = new S<MStdTest>() { @Override
				public void set(MStdTest object, Object value) { }};
		
		assertTrue(modifier.equals(new MStd<MStdTest>(getter, this)));
		assertFalse(modifier.equals(new MStd<MStdTest>(getter, setter2)));
		assertFalse(modifier.equals(new MStd<MStdTest>(getter2, setter2)));
		assertFalse(modifier.equals(new MStd<MStdTest>(getter2, this)));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(modifier.equals(modifier));
		assertFalse(modifier.equals(null));
		assertFalse(modifier.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, /*0*/90537)
			.append(getter)
			.append(this)
			.toHashCode();
		assertEquals(hashCode, modifier.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		modifier = new MStd<MStdTest>(
			new G<Integer>() {
				@Override public Integer get(Object source) { return null; }
				@Override public String toString() { return "getter"; } 
			},
			new S<MStdTest>() {
				@Override public void set(MStdTest object, Object value) { }
				@Override public String toString() { return "setter"; } 
			});
		assertEquals("MStd[getter => setter]", modifier.toString());
	}

}
