package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Securities;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-27<br>
 * $Id: GSecurityTest.java 341 2012-12-18 17:16:30Z whirlwind $
 */
public class GSecurityTest {
	private static IMocksControl control;
	private static G<SecurityDescriptor> gDescr;
	private static SecurityDescriptor descr;
	private static Securities securities;
	private static Security security;
	private static GSecurity getter;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gDescr = control.createMock(G.class);
		descr = new SecurityDescriptor("GAZP", "EQBR", "RUB", SecurityType.STK);
		securities = control.createMock(Securities.class);
		security = control.createMock(Security.class);
		getter = new GSecurity(gDescr, securities);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gDescr, getter.getDescriptorGetter());
		assertSame(securities, getter.getSecurities());
	}
	
	@Test
	public void testGet_IfNoDescriptor() throws Exception {
		expect(gDescr.get(this)).andReturn(null);
		control.replay();
		assertNull(getter.get(this));
		control.verify();
	}
	
	@Test
	public void testGet_IfNoSecurity() throws Exception {
		expect(gDescr.get(this)).andReturn(descr);
		expect(securities.isSecurityExists(descr)).andReturn(false);
		control.replay();
		assertNull(getter.get(this));
		control.verify();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		expect(gDescr.get(this)).andReturn(descr);
		expect(securities.isSecurityExists(descr)).andReturn(true);
		expect(securities.getSecurity(descr)).andReturn(security);
		control.replay();
		assertSame(security, getter.get(this));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<G<SecurityDescriptor>> vDescr =
				new Variant<G<SecurityDescriptor>>()
			.add(null)
			.add(gDescr)
			.add(control.createMock(G.class));
		Variant<Securities> vSecurities = new Variant<Securities>(vDescr)
			.add(null)
			.add(securities)
			.add(control.createMock(Securities.class));
		int foundCnt = 0;
		GSecurity found = null;
		do {
			GSecurity actual =
				new GSecurity(vDescr.get(), vSecurities.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
			
		} while ( vSecurities.next() );
		assertEquals(1, foundCnt);
		assertSame(gDescr, found.getDescriptorGetter());
		assertSame(securities, found.getSecurities());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
		assertTrue(getter.equals(getter));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, /*0*/51621)
			.append(gDescr)
			.append(securities)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
