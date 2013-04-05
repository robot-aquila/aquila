package ru.prolib.aquila.ta.indicator;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ta.*;

public class CrossTest {
	private Cross cross;
	private ValueImpl<Double> src1,src2;

	@Before
	public void setUp() throws Exception {
		src1 = new ValueImpl<Double>(); src1.add(null);
		src2 = new ValueImpl<Double>(); src2.add(null);
		cross = new Cross(src1, src2);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(src1, cross.getFirstSource());
		assertSame(src2, cross.getSecondSource());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfFirstSourceIsNull() throws Exception {
		new Cross(null, src2);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSecondSourceIsNull() throws Exception {
		new Cross(src1, null);
	}
	
	@Test
	public void testCalculate() throws Exception {
		Double fixture[][] = {
				//src1,  src2
				{ 10.0d,  12.0d}, // 1 ниже 2 -> NONE
				{ 11.0d,  15.0d}, // 1 ниже 2 -> NONE
				{ 18.0d,  14.0d}, // 1 выше 2 -> сигнал ABOVE
				{ 13.0d,  11.0d}, // 1 выше 2 -> NONE
				{ 13.0d,  11.0d}, // 1 выше 2 -> NONE
				{101.0d, 121.0d}, // 1 ниже 2 -> сигнал BELOW
				{  null,  1000d},
				{ 1000d,   null},
				{  null,   null},
		};
		Integer expected[] = {
				Cross.NONE,
				Cross.NONE,
				Cross.ABOVE,
				Cross.NONE,
				Cross.NONE,
				Cross.BELOW,
				Cross.NONE,
				Cross.NONE,
				Cross.NONE,
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			src1.set(fixture[i][0]);
			src2.set(fixture[i][1]);
			assertEquals("At sequence #" + i, expected[i], cross.calculate());
		}
	}

}
