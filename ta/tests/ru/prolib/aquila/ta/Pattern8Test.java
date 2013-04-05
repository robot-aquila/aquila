package ru.prolib.aquila.ta;


import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;

import ru.prolib.aquila.core.data.Candle;

public class Pattern8Test {
	Pattern8 p;
	// from RIH2 2012-02-08 13:37
	Candle b[] = {
		new Candle(new Date(), 165675, 165720, 165625, 165690, 1),
		new Candle(new Date(), 165685, 165725, 165610, 165640, 1),
		new Candle(new Date(), 165625, 165640, 165565, 165620, 1),
		new Candle(new Date(), 165620, 165635, 165460, 165515, 1),
		new Candle(new Date(), 165505, 165520, 165440, 165515, 1),
		new Candle(new Date(), 165515, 165520, 165385, 165410, 1),
		new Candle(new Date(), 165410, 165445, 165340, 165365, 1),
		new Candle(new Date(), 165365, 165435, 165330, 165430, 1),
	};

	@Before
	public void setUp() throws Exception {
		
	}
	
	private long toBits(String bits) {
		long res = 0;
		int length = bits.length() >= 64 ? 64 : bits.length();
		for ( int i = 0; i < length; i ++ ) {
			res = res << 1;
			res = res | (bits.charAt(i) == '1' ? 1 : 0);
		}
		return res;
	}
	
	//private String toString(long bits) {
	//	String res = "";
	//	for ( int i = 0; i < 64; i ++ ) {
	//		res = ((0x1 & bits) == 1 ? "1" : "0") + res;
	//		bits = bits >>> 1;
	//	}
	//	return res;
	//}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		p = new Pattern8(b);
		assertEquals(165725, p.getMax(), 0.1d);
		assertEquals(165330, p.getMin(), 0.1d);
		assertEquals(395, 	 p.getHeight(), 0.1d);
		assertEquals(49.375, p.getLevelHeight(), 0.001d);
		long matrix = p.getMatrix();
		int expected[][] = {
				{ 1, 1, 0, 0, 0, 0, 0, 0, }, 
				{ 1, 1, 1, 1, 0, 0, 0, 0, }, 
				{ 1, 1, 1, 1, 0, 0, 0, 0, },
				{ 0, 0, 1, 1, 0, 0, 0, 0, },
				{ 0, 0, 0, 1, 1, 1, 0, 0, },
				{ 0, 0, 0, 1, 1, 1, 1, 1, },
				{ 0, 0, 0, 0, 0, 1, 1, 1, },
				{ 0, 0, 0, 0, 0, 0, 1, 1, },
		};
		for ( int level = 7; level >= 0; level -- ) {
			for ( int time = 0; time < 8; time ++ ) {
				String m = "At (" + level + "," + time + ")";
				if ( expected[7 - level][time] == 1 ) { 
					assertTrue(m, Pattern8.getMatrixBitAt(matrix, level, time));
				} else {
					assertFalse(m,Pattern8.getMatrixBitAt(matrix, level, time));
				}
			}
			System.out.println(Pattern8.matrixLevelToString(matrix,
					level, "1", "0", " "));
		}
	}
	
	@Test
	public void testGetBitAt() throws Exception {
		assertFalse(Pattern8.getBitAt(3, 2));
		assertTrue( Pattern8.getBitAt(3, 1));
		assertTrue( Pattern8.getBitAt(3, 0));
		assertTrue( Pattern8.getBitAt(toBits("1000"), 3));
		assertFalse(Pattern8.getBitAt(toBits("1000"), 4));
		assertFalse(Pattern8.getBitAt(toBits("1000"), 2));
	}
	
	@Test
	public void testSetBitAt() throws Exception {
		assertEquals(1, Pattern8.setBitAt(0, 0));
		assertEquals(2, Pattern8.setBitAt(0, 1));
		assertEquals(3, Pattern8.setBitAt(2, 0));
		assertEquals(3, Pattern8.setBitAt(1, 1));
	}
	
	@Test
	public void decodePattern() throws Exception {
		long matrix = 583242746993631424l;
		for ( int level = 7; level >= 0; level -- ) {
			System.out.println(Pattern8.matrixLevelToString(matrix,
					level, "1", "0", " "));
		}

	}
	
	@Test
	public void testGetMatrixBitAt() throws Exception {
		long bits = toBits( "01000000" +
							"00000000" +
							"10000001" +
							"00000000" +
							"11111110" +
							"00000000" +
							"00000111" +
							"11111000");
		assertFalse(Pattern8.getMatrixBitAt(bits, 7, 0));
		assertTrue( Pattern8.getMatrixBitAt(bits, 7, 1));
		assertFalse(Pattern8.getMatrixBitAt(bits, 7, 2));
		for ( int i = 0; i < 8; i ++ ) {
			assertFalse(Pattern8.getMatrixBitAt(bits, 6, i));
		}
		assertTrue( Pattern8.getMatrixBitAt(bits, 5, 0));
		assertTrue( Pattern8.getMatrixBitAt(bits, 5, 7));
		for ( int i = 1; i < 7; i ++ ) {
			assertFalse(Pattern8.getMatrixBitAt(bits, 5, i));
		}
	}
	
	@Test
	public void testSetMatrixBitAt() throws Exception {
		long bits = toBits( "01000000" +
							"00000000" +
							"10000001" +
							"00000000" +
							"11111110" +
							"00000000" +
							"00000111" +
							"11111000");
		assertFalse(Pattern8.getMatrixBitAt(bits, 7, 0));
		bits = Pattern8.setMatrixBitAt(bits, 7, 0);
		assertTrue(Pattern8.getMatrixBitAt(bits, 7, 0));
		
		assertFalse(Pattern8.getMatrixBitAt(bits, 4, 4));
		bits = Pattern8.setMatrixBitAt(bits, 4, 4);
		assertTrue(Pattern8.getMatrixBitAt(bits, 4, 4));
	}
	
}
