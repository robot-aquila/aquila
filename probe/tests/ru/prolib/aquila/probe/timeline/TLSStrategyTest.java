package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import org.joda.time.*;
import org.joda.time.format.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TLSStrategyTest {
	private static final DateTimeFormatter df;
	private static final Logger logger;
	
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		logger = LoggerFactory.getLogger(TLSStrategyTest.class);
	}
	
	static class PushToStack implements Runnable {
		private final List<String> q;
		private final String v;
		
		public PushToStack(List<String> stack, String tag) {
			super();
			this.q = stack;
			this.v = tag;
		}

		@Override
		public void run() {
			logger.debug("push to stack [" + v + "]");
			q.add(v);
		}
		
	}
	
	private LinkedList<String> actual, expected;
	
	private DateTime T(String time) {
		return df.parseDateTime(time);
	}
	
	/**
	 * Создать тестовое событие.
	 * <p>
	 * @param time время события в формате {@link #df}
	 * @param tag строка-идентификатор события
	 * @return экземпляр события хронологии
	 */
	private TLEvent E(String time, String tag) {
		return E(T(time), tag);
	}
	
	/**
	 * Создать тестовое событие.
	 * <p> 
	 * @param time время события
	 * @param tag строка-идентификатор события
	 * @return экземпляр события хронологии
	 */
	private TLEvent E(DateTime time, String tag) {
		return new TLEvent(time, new PushToStack(actual, tag));
	}
	
	/**
	 * Создать тестовое событие и добавить его в список ожидаемых результатов.
	 * <p> 
	 * @param time время события
	 * @param tag строка-идентификатор события, которая помещается в список
	 * ожидаемых результатов
	 * @return экземпляр события хронологии
	 */
	private TLEvent EE(DateTime time, String tag) {
		expected.add(tag);
		return E(time, tag);
	}
	
	/**
	 * Создать тестовое событие и добавить его в список ожидаемых результатов.
	 * <p>
	 * @param time время события в формате {@link #df}
	 * @param tag строка-идентификатор события, которая помещается в список
	 * ожидаемых результатов
	 * @return экземпляр события хронологии
	 */
	private TLEvent EE(String time, String tag) {
		return EE(T(time), tag);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		expected = new LinkedList<String>();
		actual = new LinkedList<String>();
	}
	
	@Test
	public void testExecute_Complex() throws Exception {
		Interval interval = new Interval(T("2014-12-01 10:00:00"),
										 T("2014-12-01 10:10:00"));
		TLEventQueue queue = new TLEventQueue(interval);
		TLEventSources sources = new TLEventSources();
		TLSStrategy strategy = new TLSStrategy(sources, queue);
		
		// Несколько источников данных для теста ситуаций:
		// один из источников кидает эксепшн (для проверки разрегистрации);
		// выдает одно событие (должен быть разрегистрирован на второй итерации;
		// 2 с повторным опросами (выдают несколько событий с одной датировкой);
		// один выдает событие раньше чем ТА (должен быть разрегистрирован)
		// один выдает более позднее событие (должен быть временно задизаблен);
		// один выдает событие позже конца РП (должен быть разрегистрирован)
		TLEventSource src0;
		sources.registerSource(src0 = new TLEventSource() {
			@Override public TLEvent pullEvent() throws TLException {
				throw new TLException("Test error");
			}
			@Override public void close() { }
			@Override public boolean closed() { return false; }
		});
		TLSimpleEventSource src1, src2, src3, src4, src5, src6;
		sources.registerSource(src1 = new TLSimpleEventSource("src1"));
		sources.registerSource(src2 = new TLSimpleEventSource("src2"));
		sources.registerSource(src3 = new TLSimpleEventSource("src3"));
		sources.registerSource(src4 = new TLSimpleEventSource("src4"));
		sources.registerSource(src5 = new TLSimpleEventSource("src5"));
		sources.registerSource(src6 = new TLSimpleEventSource("src6"));
		// Pass 1 events: 
		src1.add(EE("2014-12-01 10:00:00", "01 cucumber"));
		src2.add(EE("2014-12-01 10:00:00", "02 zulu"));
		src3.add(EE("2014-12-01 10:00:00", "03 bakka"));
		src4.add( E("2014-01-01 00:00:00", "04 corrupted should removed"));
		src4.add( E("2014-12-01 10:00:00", "04.X just test that they removed"));
		src2.add(EE("2014-12-01 10:00:00", "06 zulu 24"));
		src3.add(EE("2014-12-01 10:00:00", "07 gambrinus"));
		src6.add( E("2014-12-31 23:59:59", "07.X should be permaremoved"));
		// Pass 2 events: Второй проход очень важен, так как работает с
		// измененными статусами источников событий
		src2.add(EE("2014-12-01 10:00:05", "08.0 azaza?")); // important case!
		src2.add(EE("2014-12-01 10:00:05", "08.1 duduka"));
		src2.add(EE("2014-12-01 10:00:05", "08 glen"));
		// Pass 3 events:
		src5.add(EE("2014-12-01 10:00:10", "05 should be disabled"));
		
		assertTrue(strategy.execute());
		assertEquals("07 gambrinus", actual.peekLast());
		assertFalse(sources.isRegistered(src0));
		assertFalse(sources.isRegistered(src1));
		assertTrue(sources.isRegistered(src2));
		assertEquals(T("2014-12-01 10:00:05"), sources.getDisabledUntil(src2));
		assertFalse(sources.isRegistered(src3));
		assertFalse(sources.isRegistered(src4));
		assertTrue(sources.isRegistered(src5));
		assertEquals(T("2014-12-01 10:00:10"), sources.getDisabledUntil(src5));
		assertFalse(sources.isRegistered(src6));
		
		assertTrue(strategy.execute());
		assertEquals("08 glen", actual.peekLast());
		assertTrue(sources.isRegistered(src5));
		assertEquals(T("2014-12-01 10:00:10"), sources.getDisabledUntil(src5));
		
		assertFalse(strategy.execute());
		assertEquals("05 should be disabled", actual.peekLast());
		assertFalse(sources.isRegistered(src2));
		
		assertFalse(strategy.execute());
		
		assertEquals(expected, actual);
	}

}
