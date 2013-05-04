package ru.prolib.aquila.quik.subsys.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * 2013-01-23<br>
 * $Id: QUIKGetSecurityDescriptor1Test.java 444 2013-01-24 06:17:09Z whirlwind $
 */
public class QUIKGetSecurityDescriptor1Test {
	private static IMocksControl control;
	private static QUIKServiceLocator locator;
	private static G<String> gName;
	private static PartiallyKnownObjects partiallyKnown;
	private static SecurityDescriptor descr;
	private static QUIKGetSecurityDescriptor1 getter;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("SBER","EQBR","RUB",SecurityType.STK);
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		gName = control.createMock(G.class);
		partiallyKnown = control.createMock(PartiallyKnownObjects.class);
		getter = new QUIKGetSecurityDescriptor1(locator, gName);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		expect(locator.getPartiallyKnownObjects()).andStubReturn(partiallyKnown);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, getter.getServiceLocator());
		assertSame(gName, getter.getNameGetter());
	}
	
	@Test
	public void testGet_IfNameIsNull() throws Exception {
		expect(gName.get(same(this))).andReturn(null);
		control.replay();
		
		assertNull(getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet() throws Exception {
		expect(gName.get(same(this))).andReturn("Сбербанк-АО");
		expect(partiallyKnown.getSecurityDescriptorByName(eq("Сбербанк-АО")))
			.andReturn(descr);
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
		Variant<G<String>> vGet = new Variant<G<String>>(vLoc)
			.add(gName)
			.add(control.createMock(G.class));
		Variant<?> iterator = vGet;
		int foundCnt = 0;
		QUIKGetSecurityDescriptor1 x = null, found = null;
		do {
			x = new QUIKGetSecurityDescriptor1(vLoc.get(), vGet.get());
			if ( getter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(gName, found.getNameGetter());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130123, 81557)
			.append(locator)
			.append(gName)
			.toHashCode(), getter.hashCode());
	}

}
