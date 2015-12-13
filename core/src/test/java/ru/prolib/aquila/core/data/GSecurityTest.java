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
	private static G<Symbol> gSymbol;
	private static Symbol symbol;
	private static Securities securities;
	private static Security security;
	private static GSecurity getter;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gSymbol = control.createMock(G.class);
		symbol = new Symbol("GAZP", "EQBR", "RUB", SymbolType.STOCK);
		securities = control.createMock(Securities.class);
		security = control.createMock(Security.class);
		getter = new GSecurity(gSymbol, securities);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gSymbol, getter.getSymbolGetter());
		assertSame(securities, getter.getSecurities());
	}
	
	@Test
	public void testGet_IfNoSymbol() throws Exception {
		expect(gSymbol.get(this)).andReturn(null);
		control.replay();
		assertNull(getter.get(this));
		control.verify();
	}
	
	@Test
	public void testGet_IfNoSecurity() throws Exception {
		expect(gSymbol.get(this)).andReturn(symbol);
		expect(securities.isSecurityExists(symbol)).andReturn(false);
		control.replay();
		assertNull(getter.get(this));
		control.verify();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		expect(gSymbol.get(this)).andReturn(symbol);
		expect(securities.isSecurityExists(symbol)).andReturn(true);
		expect(securities.getSecurity(symbol)).andReturn(security);
		control.replay();
		assertSame(security, getter.get(this));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<G<Symbol>> vSymbol = new Variant<G<Symbol>>()
			.add(null)
			.add(gSymbol)
			.add(control.createMock(G.class));
		Variant<Securities> vSecurities = new Variant<Securities>(vSymbol)
			.add(null)
			.add(securities)
			.add(control.createMock(Securities.class));
		int foundCnt = 0;
		GSecurity found = null;
		do {
			GSecurity actual =
				new GSecurity(vSymbol.get(), vSecurities.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
			
		} while ( vSecurities.next() );
		assertEquals(1, foundCnt);
		assertSame(gSymbol, found.getSymbolGetter());
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
			.append(gSymbol)
			.append(securities)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
