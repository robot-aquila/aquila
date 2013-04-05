package ru.prolib.aquila.ta.math;

import static org.junit.Assert.*;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.math.Alligator;

import org.junit.*;

public class AlligatorTest {
	private TestValue<Double> lips,teeth,jaw;
	private Alligator alligator;

	@Before
	public void setUp() throws Exception {
		lips = new TestValue<Double>();
		teeth = new TestValue<Double>();
		jaw = new TestValue<Double>();
		alligator = new Alligator(lips, teeth, jaw);
	}
	
	@Test
	public void testAttributes() {
		assertSame(lips, alligator.lips);
		assertSame(teeth, alligator.teeth);
		assertSame(jaw, alligator.jaw);
	}

}
