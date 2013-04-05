package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;


/**
 * 2012-08-28<br>
 * $Id: MListImplTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class MListImplTest {
	private IMocksControl control;
	private S<MListImplTest> m1,m2,m3;
	private List<S<MListImplTest>> list;
	private MListImpl<MListImplTest> modifier;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		m1 = control.createMock(S.class);
		m2 = control.createMock(S.class);
		m3 = control.createMock(S.class);
		list = new LinkedList<S<MListImplTest>>();
		list.add(m1); list.add(m2); list.add(m3);
		modifier = new MListImpl<MListImplTest>(list);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertSame(list, modifier.getModifiers());
	}
	
	@Test
	public void testConstruct0() throws Exception {
		modifier = new MListImpl<MListImplTest>();
		List<S<MListImplTest>> list = modifier.getModifiers();
		assertNotNull(list);
		assertEquals(LinkedList.class, list.getClass());
		assertEquals(0, list.size());
	}
	
	@Test
	public void testSet() throws Exception {
		Object source = new Object();
		m1.set(same(this), same(source));
		m2.set(same(this), same(source));
		m3.set(same(this), same(source));
		control.replay();
		
		modifier.set(this, source);
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		List<S<MListImplTest>> list2 = new LinkedList<S<MListImplTest>>();
		list2.add(m1); list2.add(m2); list2.add(m3);
		List<S<MListImplTest>> list3 = new LinkedList<S<MListImplTest>>();
		list3.add(m2);
		
		assertTrue(modifier.equals(new MListImpl<MListImplTest>(list2)));
		assertFalse(modifier.equals(new MListImpl<MListImplTest>(list3)));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(modifier.equals(modifier));
		assertFalse(modifier.equals(null));
		assertFalse(modifier.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, 110623)
			.append(list)
			.toHashCode();
		assertEquals(hashCode, modifier.hashCode());
	}
	
	@Test
	public void testAdd1() throws Exception {
		modifier = new MListImpl<MListImplTest>();
		assertSame(modifier, modifier.add(m1).add(m2).add(m3));
		assertEquals(list, modifier.getModifiers());
	}
	
	@Test
	public void testAdd2() throws Exception {
		G<String> g1 = new GConst<String>("a");
		G<String> g2 = new GConst<String>("b");
		modifier = new MListImpl<MListImplTest>();
		assertSame(modifier, modifier.add(g1, m1).add(g2, m2));
		
		List<S<MListImplTest>> list2 = modifier.getModifiers();
		assertEquals(2, list2.size());
		MStd<MListImplTest> m = (MStd<MListImplTest>) list2.get(0);
		assertSame(g1, m.getGetter());
		assertSame(m1, m.getSetter());
		m = (MStd<MListImplTest>) list2.get(1);
		assertSame(g2, m.getGetter());
		assertSame(m2, m.getSetter());
	}
	
	@Test
	public void testToString() throws Exception {
		modifier = new MListImpl<MListImplTest>();
		modifier.add(new S<MListImplTest>() {
			public void set(MListImplTest object, Object value) { }
			public String toString() { return "one"; }
		});
		modifier.add(new S<MListImplTest>() {
			public void set(MListImplTest object, Object value) { }
			public String toString() { return "two"; }
		});
		modifier.add(new S<MListImplTest>() {
			public void set(MListImplTest object, Object value) { }
			public String toString() { return "three"; }
		});
		
		assertEquals("MListImpl[one,\ntwo,\nthree]", modifier.toString());
	}

}
