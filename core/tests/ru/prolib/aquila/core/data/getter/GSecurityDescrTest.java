package ru.prolib.aquila.core.data.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.getter.GSecurityDescr;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-28<br>
 * $Id: GSecurityDescrTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class GSecurityDescrTest {
	private static IMocksControl control;
	private static GSecurityDescr getter;
	private static G<String> gCode,gClass,gCurr;
	private static G<SecurityType> gType;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gCode = control.createMock(G.class);
		gClass = control.createMock(G.class);
		gCurr = control.createMock(G.class);
		gType = control.createMock(G.class);
		getter = new GSecurityDescr(gCode, gClass, gCurr, gType);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testGet() throws Exception {
		Variant<String> vCode = new Variant<String>()
			.add("SBER")
			.add("")
			.add(null);
		Variant<String> vClass = new Variant<String>(vCode)
			.add("EQBR")
			.add("")
			.add(null);
		Variant<String> vCurr = new Variant<String>(vClass)
			.add("USD");
		Variant<SecurityType> vType = new Variant<SecurityType>(vCurr)
			.add(null)
			.add(SecurityType.OPT);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		SecurityDescriptor x = null, found = null;
		final Object source = new Object();
		do {
			control.resetToStrict();
			expect(gCode.get(same(source))).andReturn(vCode.get());
			expect(gClass.get(same(source))).andReturn(vClass.get());
			expect(gCurr.get(same(source))).andReturn(vCurr.get());
			expect(gType.get(same(source))).andReturn(vType.get());
			control.replay();
			x = getter.get(source);
			control.verify();
			if ( x != null ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("SBER", found.getCode());
		assertEquals("EQBR", found.getClassCode());
		assertEquals("USD", found.getCurrencyCode());
		assertEquals(SecurityType.OPT, found.getType());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<G<String>> vCode = new Variant<G<String>>()
			.add(null)
			.add(gCode);
		Variant<G<String>> vCls = new Variant<G<String>>(vCode)
			.add(null)
			.add(gClass);
		Variant<G<String>> vCurr = new Variant<G<String>>(vCls)
			.add(null)
			.add(gCurr);
		Variant<G<SecurityType>> vType = new Variant<G<SecurityType>>(vCurr)
			.add(null)
			.add(gType);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		GSecurityDescr found = null;
		do {
			GSecurityDescr actual = new GSecurityDescr(vCode.get(),
					vCls.get(), vCurr.get(), vType.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual; 
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gCode, found.getCodeGetter());
		assertSame(gClass, found.getClassGetter());
		assertSame(gCurr, found.getCurrencyGetter());
		assertSame(gType, found.getTypeGetter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, 135927)
			.append(gCode)
			.append(gClass)
			.append(gCurr)
			.append(gType)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
