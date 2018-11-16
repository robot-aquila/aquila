package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class CDecimalBDPerformanceIT {
	private static final long SEED = 7192638903451L;
	private static final int MIN_SCALE = 1;
	private static final int MAX_SCALE = 9;
	private static final int NUM_ELEMENTS = 15000;
	private List<CDecimal> fixture1, fixture2;

	private Random rnd = new Random(SEED);
	
	private CDecimal newCD() {
		int scale = rnd.nextInt((MAX_SCALE - MIN_SCALE) + 1) + MIN_SCALE;
		int pow = BigInteger.valueOf(10).pow(scale).intValueExact();
		CDecimal cd = CDecimalBD.of(rnd.nextLong()).withScale(scale).divide((long)pow);
		return cd;
	}
	
	@Before
	public void setUp() throws Exception {
		fixture1 = new ArrayList<>();
		fixture2 = new ArrayList<>();
		for ( int i = 0; i < NUM_ELEMENTS; i ++ ) {
			fixture1.add(newCD());
			fixture2.add(newCD());
		}
	}

	@Test
	public void test() {
		String pfx = "CDecimaBD performance test: ";
		System.out.println(pfx + "started");
		long start = System.currentTimeMillis();
		for ( int i = 0; i < NUM_ELEMENTS; i ++ ) {
			CDecimal one = fixture1.get(i);
			for ( int j = 0; j < NUM_ELEMENTS; j ++ ) {
				one.compareTo(fixture2.get(j));
			}
		}
		long est = System.currentTimeMillis() - start;
		System.out.println(pfx + "finished in " + est + " ms");
		
	}

}
