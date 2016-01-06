package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.*;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.*;

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
		LocalDateTime from = LocalDateTime.of(1998, 7, 1, 13, 30, 45, 0);
		LocalDateTime to = LocalDateTime.of(2004, 1, 1, 23, 59, 59, 999);
		TLSTimeline actual = factory.produce(Interval.of(from.toInstant(ZoneOffset.UTC),
				to.toInstant(ZoneOffset.UTC)));
		assertNotNull(actual);
		/**
		 * Непосредственно структура порожденного объекта нас не интересует.
		 * Функциональность порожденного объекта проверяется в рамках
		 * интеграционных тестов хронологии.
		 */
		assertTrue(actual.paused());// Но диспатчер должен быть запущен,
									// а автомат должен находиться в стартовом
									// состоянии
	}

}
