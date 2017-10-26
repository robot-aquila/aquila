package ru.prolib.aquila.utils.experimental.sst.sdp2;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.utils.Variant;

public class SDP2KeyTest {
	private static Symbol symbol1 = new Symbol("AAPL"), symbol2 = new Symbol("MSFT");
	private static ZTFrame tframe1 = ZTFrame.M1, tframe2 = ZTFrame.M15;
	private SDP2Key key;

	@Before
	public void setUp() throws Exception {
		key = new SDP2Key(tframe1, symbol1);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(tframe1, key.getTimeFrame());
		assertEquals(symbol1, key.getSymbol());
	}
	
	@Test (expected=NullPointerException.class)
	public void testCtor2_ThrowsIfTFrameIsNull() {
		new SDP2Key(null, symbol1);
	}
	
	@Test
	public void testCtor1() {
		key = new SDP2Key(tframe2);
		assertEquals(tframe2, key.getTimeFrame());
		assertNull(key.getSymbol());
	}
	
	@Test (expected=NullPointerException.class)
	public void testCtor1_ThrowsIfTFrameIsNull() {
		new SDP2Key(null);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(key.equals(key));
		assertFalse(key.equals(null));
		assertFalse(key.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<ZTFrame> vTF = new Variant<>(tframe1, tframe2);
		Variant<Symbol> vSym = new Variant<>(vTF, symbol1, symbol2);
		Variant<?> iterator = vSym;
		int foundCnt = 0;
		SDP2Key x, found = null;
		do {
			x = new SDP2Key(vTF.get(), vSym.get());
			if ( key.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(tframe1, found.getTimeFrame());
		assertEquals(symbol1, found.getSymbol());
	}

	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(1919, 95)
				.append(tframe1)
				.append(symbol1)
				.toHashCode(), key.hashCode());
		assertEquals(new HashCodeBuilder(1919, 95)
				.append(tframe2)
				.append(symbol2)
				.toHashCode(), new SDP2Key(tframe2, symbol2).hashCode());
	}

}
