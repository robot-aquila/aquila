package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class CategoryAxisViewportImplTest {
	private CategoryAxisViewportImpl viewport;

	@Before
	public void setUp() throws Exception {
		viewport = new CategoryAxisViewportImpl();
	}
	
	@Test
	public void testSetCategoryRangeByFirstAndNumber() {
		viewport.setCategoryRangeByFirstAndNumber(4, 5);
		
		assertEquals(4, viewport.getFirstCategory());
		assertEquals(8, viewport.getLastCategory());
		assertEquals(5, viewport.getNumberOfCategories());
	}
	
	@Test
	public void testSetCategoryRangeByFirstAndLast() {
		viewport.setCategoryRangeByFirstAndLast(4, 8);
		
		assertEquals(4, viewport.getFirstCategory());
		assertEquals(8, viewport.getLastCategory());
		assertEquals(5, viewport.getNumberOfCategories());		
	}
	
	@Test
	public void testSetCategoryRangeByLastAndNumber() {
		viewport.setCategoryRangeByLastAndNumber(8, 5);
		
		assertEquals(4, viewport.getFirstCategory());
		assertEquals(8, viewport.getLastCategory());
		assertEquals(5, viewport.getNumberOfCategories());		
	}
	
	@Test
	public void testSetPreferredNumberOfBars() {
		assertNull(viewport.getPreferredNumberOfBars());
		
		viewport.setPreferredNumberOfBars(20);
		
		assertEquals(Integer.valueOf(20), viewport.getPreferredNumberOfBars());
	}
		
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(viewport.equals(viewport));
		assertFalse(viewport.equals(null));
		assertFalse(viewport.equals(this));
	}

	@Test
	public void testEquals() {
		viewport.setCategoryRangeByFirstAndNumber(5, 10);
		Variant<Integer> vFirst = new Variant<>(5, 20);
		Variant<Integer> vNum = new Variant<>(vFirst, 10, 40);
		Variant<Integer> vPNBars = new Variant<>(vNum, null, 20);
		Variant<?> iterator = vPNBars;
		int foundCnt = 0;
		CategoryAxisViewportImpl x, found = null;
		do {
			x = new CategoryAxisViewportImpl();
			x.setCategoryRangeByFirstAndNumber(vFirst.get(), vNum.get());
			x.setPreferredNumberOfBars(vPNBars.get());
			if ( viewport.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(5, found.getFirstCategory());
		assertEquals(10, found.getNumberOfCategories());
		assertEquals(14, found.getLastCategory());
		assertNull(found.getPreferredNumberOfBars());
	}

}
