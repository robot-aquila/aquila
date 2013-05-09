package ru.prolib.aquila.quik.subsys.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * 2013-02-21<br>
 * $Id: QUIKGetOrderAccountTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class QUIKGetOrderAccountTest {
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private Cache ddeCache;
	private G<String> gSubCode, gSubCode2;
	private QUIKGetOrderAccount getter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		ddeCache = control.createMock(Cache.class);
		gSubCode = control.createMock(G.class);
		gSubCode2 = control.createMock(G.class);
		getter = new QUIKGetOrderAccount(locator, gSubCode, gSubCode2);
		expect(locator.getDdeCache()).andReturn(ddeCache);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, getter.getServiceLocator());
		assertSame(gSubCode, getter.getSubCodeGetter());
		assertSame(gSubCode2, getter.getSubCode2Getter());
	}
	
	@Test
	public void testGet() throws Exception {
		expect(gSubCode.get(same(this))).andReturn("foo");
		expect(gSubCode2.get(same(this))).andReturn("bar");
		Account account = new Account("FIRM", "foo", "bar");
		expect(ddeCache.getAccount(eq("foo"), eq("bar"))).andReturn(account);
		control.replay();
		
		assertSame(account, getter.get(this));
		
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
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<G<String>> vGtr1 = new Variant<G<String>>(vLoc)
			.add(gSubCode)
			.add(gSubCode2);
		Variant<G<String>> vGtr2 = new Variant<G<String>>(vGtr1)
			.add(gSubCode2)
			.add(gSubCode);
		Variant<?> iterator = vGtr2;
		int foundCnt = 0;
		QUIKGetOrderAccount x = null, found = null;
		do {
			x = new QUIKGetOrderAccount(vLoc.get(), vGtr1.get(), vGtr2.get());
			if ( getter.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(gSubCode, found.getSubCodeGetter());
		assertSame(gSubCode2, found.getSubCode2Getter());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "QUIKGetOrderAccount[subCode=" + gSubCode
			+ ", subCode2=" + gSubCode2 + "]";
		assertEquals(expected, getter.toString());
	}

}
