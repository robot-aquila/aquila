package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UpdatableStateContainerTest {
	private UpdatableStateContainerImpl container;

	@Before
	public void setUp() throws Exception {
		container = new UpdatableStateContainerImpl("zulu24");
	}

	@Test
	public void testConsume_DeltaUpdate() {
		container.consume(new DeltaUpdateBuilder()
			.withToken(1, 245)
			.withToken(2, "foobar")
			.withToken(3, this)
			.buildUpdate());
		
		assertEquals(new Integer(245), container.getInteger(1));
		assertEquals("foobar", container.getString(2));
		assertSame(this, container.getObject(3));
	}

}
