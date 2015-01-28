package ru.prolib.aquila.core.BusinessEntities.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.getter.GAccount;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-27<br>
 * $Id: GAccountTest.java 332 2012-12-09 12:06:25Z whirlwind $
 */
public class GAccountTest {
	private static final Object src = new Object();
	private static IMocksControl control;
	private static G<String> gCode,gSubCode,gSubCode2;
	private static GAccount getter;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gCode = control.createMock(G.class);
		gSubCode = control.createMock(G.class);
		gSubCode2 = control.createMock(G.class);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		getter = new GAccount(gCode, gSubCode, gSubCode2);
	}
	
	@Test
	public void testGet_Construct1() throws Exception {
		getter = new GAccount(gCode);
		assertSame(gCode, getter.getCodeGetter());
		assertNull(getter.getSubCodeGetter());
		assertNull(getter.getSubCode2Getter());
		expect(gCode.get(same(src))).andReturn("LX001");
		control.replay();
		assertEquals(new Account("LX001"), getter.get(src));
		control.verify();
	}
	
	@Test
	public void testGet_Construct2() throws Exception {
		getter = new GAccount(gCode, gSubCode);
		assertSame(gCode, getter.getCodeGetter());
		assertSame(gSubCode, getter.getSubCodeGetter());
		assertNull(getter.getSubCode2Getter());
		expect(gCode.get(same(src))).andReturn("LX001");
		expect(gSubCode.get(same(src))).andReturn("C500");
		control.replay();
		assertEquals(new Account("LX001", "C500"), getter.get(src));
		control.verify();
	}
	
	@Test
	public void testGet_Construct3() throws Exception {
		getter = new GAccount(gCode, gSubCode, gSubCode2);
		assertSame(gCode, getter.getCodeGetter());
		assertSame(gSubCode, getter.getSubCodeGetter());
		assertSame(gSubCode2, getter.getSubCode2Getter());
		expect(gCode.get(same(src))).andReturn("LX001");
		expect(gSubCode.get(same(src))).andReturn("C500");
		expect(gSubCode2.get(same(src))).andReturn("ZX5");
		control.replay();
		assertEquals(new Account("LX001", "C500", "ZX5"), getter.get(src));
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<G<String>> vCode = new Variant<G<String>>()
			.add(null)
			.add(gCode)
			.add(gSubCode);
		Variant<G<String>> vSubCode = new Variant<G<String>>(vCode)
			.add(gSubCode)
			.add(gSubCode2)
			.add(null);
		Variant<G<String>> vSubCode2 = new Variant<G<String>>(vSubCode)
			.add(null)
			.add(gSubCode2)
			.add(gCode);
		Variant<?> iterator = vSubCode2;
		int foundCnt = 0;
		GAccount x = null, found = null;
		do {
			x = new GAccount(vCode.get(), vSubCode.get(), vSubCode2.get());
			if ( getter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gCode, found.getCodeGetter());
		assertSame(gSubCode, found.getSubCodeGetter());
		assertSame(gSubCode2, found.getSubCode2Getter());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/45555)
			.append(gCode)
			.append(gSubCode)
			.append(gSubCode2)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "GAccount[code=" + gCode + ", "
			+ "subCode=" + gSubCode + ", "
			+ "subCode2=" + gSubCode2 + "]";
		assertEquals(expected, getter.toString());
	}

}
