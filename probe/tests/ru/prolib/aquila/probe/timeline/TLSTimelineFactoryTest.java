package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import org.joda.time.*;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.probe.SimulationController;

public class TLSTimelineFactoryTest {
	private EventSystem es;
	private TLSTimelineFactory factory;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		factory = new TLSTimelineFactory(es);
	}
	
	@Test
	public void testProduce() throws Exception {
		DateTime from = new DateTime(1998, 7, 1, 13, 30, 45, 0);
		DateTime to = new DateTime(2004, 1, 1, 23, 59, 59, 999);
		SimulationController actual = factory.produce(new Interval(from, to));
		assertNotNull(actual);
		/**
		 * Непосредственно структура порожденного объекта нас не интересует.
		 * Функциональность порожденного объекта проверяется в рамках
		 * интеграционных тестов хронологии.
		 */
	}

}
