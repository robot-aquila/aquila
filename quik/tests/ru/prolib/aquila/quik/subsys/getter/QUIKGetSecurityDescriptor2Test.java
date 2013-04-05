package ru.prolib.aquila.quik.subsys.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.quik.subsys.security.QUIKSecurityDescriptors;

/**
 * 2013-01-23<br>
 * $Id: QUIKGetSecurityDescriptor2Test.java 444 2013-01-24 06:17:09Z whirlwind $
 */
public class QUIKGetSecurityDescriptor2Test {
	private static IMocksControl control;
	private static QUIKServiceLocator locator;
	private static G<String> gCode, gClass;
	private static QUIKSecurityDescriptors descrs;
	private static SecurityDescriptor descr;
	private static QUIKGetSecurityDescriptor2 getter;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("RIZ2","SPBFUT","USD",SecurityType.OPT);
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		gCode = control.createMock(G.class);
		gClass = control.createMock(G.class);
		descrs = control.createMock(QUIKSecurityDescriptors.class);
		getter = new QUIKGetSecurityDescriptor2(locator, gCode, gClass);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		expect(locator.getDescriptors()).andStubReturn(descrs);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, getter.getServiceLocator());
		assertSame(gCode, getter.getCodeGetter());
		assertSame(gClass, getter.getClassGetter());
	}
	
	@Test
	public void testGet_IfSomeIsNull() throws Exception {
		String[][] fix = {
				{ "RIZ2",	null	},
				{ null, 	"SPBFUT"},
				{ null,		null	},
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(gCode.get(this)).andReturn(fix[i][0]);
			expect(gClass.get(this)).andReturn(fix[i][1]);
			control.replay();
			assertNull("At #" + i, getter.get(this));
			control.verify();
		}
	}
	
	@Test
	public void testGet() throws Exception {
		expect(gCode.get(this)).andReturn("RIZ2");
		expect(gClass.get(this)).andReturn("SPBFUT");
		expect(descrs.getByCodeAndClass("RIZ2", "SPBFUT")).andReturn(descr);
		control.replay();
		assertSame(descr, getter.get(this));
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<G<String>> vCo = new Variant<G<String>>(vLoc)
			.add(gCode)
			.add(control.createMock(G.class));
		Variant<G<String>> vCl = new Variant<G<String>>(vCo)
			.add(gClass)
			.add(control.createMock(G.class));
		Variant<?> iterator = vCl;
		int foundCnt = 0;
		QUIKGetSecurityDescriptor2 x = null, found = null;
		do {
			x = new QUIKGetSecurityDescriptor2(vLoc.get(),vCo.get(),vCl.get());
			if ( getter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(gCode, found.getCodeGetter());
		assertSame(gClass, found.getClassGetter());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130123, 102129)
			.append(locator)
			.append(gCode)
			.append(gClass)
			.toHashCode(), getter.hashCode());
	}

}
