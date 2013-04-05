package ru.prolib.aquila.ta.math;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.*;

public class SubTest {
	private TestValue<Double> src1,src2;
	private Sub sub;
	
	@Before
	public void setUp() throws Exception {
		src1 = new TestValue<Double>();
		src2 = new TestValue<Double>();
		sub = new Sub(src1, src2);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(src1, sub.getSourceValue1());
		assertSame(src2, sub.getSourceValue2());
		assertEquals(ValueImpl.DEFAULT_ID, sub.getId());
	}

	@Test
	public void testConstruct3() throws Exception {
		sub = new Sub(src1, src2, "alpha");
		assertSame(src1, sub.getSourceValue1());
		assertSame(src2, sub.getSourceValue2());
		assertEquals("alpha", sub.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		double fixture[][] = {
			// src1, src2, expected
			{ 1.0d,  2.0d, -1.0d},
			{ 2.0d,  1.0d,  1.0d},
			{ 3.0d,  3.0d,  0.0d},
			{-5.0d, -1.0d, -4.0d},
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			src1.addToStackAndUpdate(fixture[i][0]);
			src2.addToStackAndUpdate(fixture[i][1]);
			sub.update();
			assertEquals(fixture[i][2], sub.get(), 0.1d);
		}
		assertEquals(fixture.length, sub.getLength());
	}

}
