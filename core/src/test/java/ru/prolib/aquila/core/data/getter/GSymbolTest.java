package ru.prolib.aquila.core.data.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.getter.GSymbol;
import ru.prolib.aquila.core.utils.Variant;

public class GSymbolTest {
	private static IMocksControl control;
	private static GSymbol getter;
	private static G<String> gCode,gClass,gCurr;
	private static G<SymbolType> gType;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gCode = control.createMock(G.class);
		gClass = control.createMock(G.class);
		gCurr = control.createMock(G.class);
		gType = control.createMock(G.class);
		getter = new GSymbol(gCode, gClass, gCurr, gType);
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
		Variant<SymbolType> vType = new Variant<SymbolType>(vCurr)
			.add(null)
			.add(SymbolType.OPT);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		Symbol x = null, found = null;
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
		assertEquals(SymbolType.OPT, found.getType());
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
		Variant<G<SymbolType>> vType = new Variant<G<SymbolType>>(vCurr)
			.add(null)
			.add(gType);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		GSymbol found = null;
		do {
			GSymbol actual = new GSymbol(vCode.get(),
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
