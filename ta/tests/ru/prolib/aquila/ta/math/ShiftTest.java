package ru.prolib.aquila.ta.math;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.*;

public class ShiftTest {
	private TestValue<Integer> source;
	private Shift<Integer> v;

	@Before
	public void setUp() throws Exception {
		source = new TestValue<Integer>();
		v = new Shift<Integer>(source, 3);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals(3, v.getPeriods());
		assertSame(source, v.getSourceValue());
		assertEquals(ValueImpl.DEFAULT_ID, v.getId());
	}

	@Test
	public void testConstruct3() throws Exception {
		v = new Shift<Integer>(source, 5, "ganymede");
		assertEquals(5, v.getPeriods());
		assertSame(source, v.getSourceValue());
		assertEquals("ganymede", v.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		Integer fixture[] = {1, 2, 3, 4, 5, 6};
		Integer expected[] = {null, null, null, 1, 2, 3};
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.addToStackAndUpdate(fixture[i]);
			v.update();
			assertEquals("At sequence #" + i, expected[i], v.get());
		}
		assertEquals(expected.length, v.getLength());
	}

}
