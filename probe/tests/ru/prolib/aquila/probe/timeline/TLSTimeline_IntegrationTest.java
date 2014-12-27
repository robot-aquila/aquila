package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;

/**
 * Интеграционный тест подсистемы симуляции хронологии событий.
 * <p>
 * 1) + Прогон пустой хронологии;<br>
 * 2) + Прогон с приостановкой по времени;<br>
 * 3) + Прогон с принудительным завершением;<br>
 * 4) + Прогон до конца рабочего периода;<br>
 * 5) + Прогон до конца данных;<br>
 * <p>
 * TODO: Не протестировано вброс команд пауза/финиш в процессе эмуляции. 
 */
public class TLSTimeline_IntegrationTest {
	/**
	 * Максимальный диапазон между элементами данных. 
	 */
	private static final int maxDiffMs = 1000 * 60 * 60; // one hour
	
	private static final String src1 =
		"Если бы вы стали искать на карте островок Танамаса, вы\n" +
		"нашли бы его на самом экваторе, немного к западу от Суматры.\n" +
		"Но если бы вы спросили капитана И. ван Тоха на борту судна\n" +
		"\"Кандон-Бандунг\", что, собственно, представляет собой эта\n" +
		"Танамаса, у берегов которой он только что бросил якорь, то\n" +
		"капитан сначала долго ругался бы, а потом сказал бы вам, что\n" +
		"это самая распроклятая дыра во всем Зондском архипелаге, еще\n" +
		"более жалкая, чем Танабала, и по меньшей мере такая же\n" +
		"гнусная, как Пини или Баньяк; что единственный, с позволенья\n" +
		"сказать, человек, который там живет, - если не считать,\n" +
		"конечно, этих вшивых батаков, - это вечно пьяный торговый\n" +
		"агент, помесь кубу с португальцем, еще больший вор, язычник\n" +
		"и скотина, чем чистокровный кубу и чистокровный белый вместе\n" +
		"взятые; и если есть на свете что-нибудь поистине проклятое,\n" +
		"так это, сэр, проклятущая жизнь на проклятущей Танамасе.";
	private static final String src2 =
		"После этого вы, вероятно, спросили бы капитана, зачем же он\n" +
		"в таком случае бросил здесь свои проклятые якоря, как будто\n" +
		"собирается остаться тут на несколько проклятых дней; тогда\n" +
		"он сердито засопел бы и проворчал что-нибудь в том смысле,\n" +
		"что \"Кандон-Бандунг\" не стал бы, разумеется, заходить сюда\n" +
		"только за проклятой копрой или за пальмовым маслом;";
	
	private EventSystem es;
	private Random rnd = new Random(2418); // don't change the seed
	private LinkedList<Entry> stack;
	private Result res;
	private TLSTimelineFactory factory;
	private TLSTimeline timeline;
	static private final DateTime from = new DateTime(2014,6,3,0,0,0,0);
	private DateTime to;
	private CountDownLatch finished;

	/**
	 * Элемент стека исходных данных.
	 */
	static class Entry {
		final char c;
		final DateTime time;
		Entry(char c, DateTime time) {
			this.c = c;
			this.time = time;
		}
	}
	
	/**
	 * Аккумулятор результата.
	 */
	static class Result {
		String str = "";
		void append(char c) {
			str += c;
		}
	}
	
	/**
	 * Источник событий на основании стека данных.
	 */
	static class Src implements TLEventSource {
		final LinkedList<Entry> entries;
		final Result res;
		Src(LinkedList<Entry> entries, Result res) {
			this.entries = entries;
			this.res = res;
		}
		@Override public TLEvent pullEvent() throws TLException {
			if ( closed() ) {
				return null;
			}
			final Entry e = entries.pollFirst();
			return new TLEvent(e.time, new Runnable() {
				@Override public void run() {
					res.append(e.c);
				}
			});
		}
		@Override public void close() { entries.clear(); }
		@Override public boolean closed() { return entries.size() == 0; }
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	/**
	 * Добавить содержимое строки в качестве исходных данных.
	 * <p>
	 * @param str строка для добавления
	 */
	void pushToStack(String str) {
		DateTime time = from;
		if ( stack.size() > 0 ) {
			time = stack.getLast().time.plus(rnd.nextInt(maxDiffMs));
		}
		for ( int i = 0; i < str.length(); i ++ ) {
			stack.add(new Entry(str.charAt(i), time));
			time = time.plus(rnd.nextInt(maxDiffMs));
		}
		to = stack.getLast().time.plus(1);
	}
	
	@Before
	public void setUp() throws Exception {
		stack = new LinkedList<Entry>();
		pushToStack(src1);
		res = new Result();
		es = new EventSystemImpl();
		es.getEventQueue().start();
		factory = new TLSTimelineFactory(es);
		finished = new CountDownLatch(1);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
		es.getEventQueue().join(5000L);
	}
	
	@Test
	public void test_Empty() throws Exception {
		timeline = factory.produce(new Interval(from, to));
		//timeline.setDebug(true);
		final List<Event> actual = new Vector<Event>();
		timeline.OnFinish().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
				finished.countDown();
			}
		});
		timeline.OnRun().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		});
		timeline.OnPause().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		});
		
		assertTrue(timeline.paused());
		timeline.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(2, actual.size());
		assertTrue(actual.get(0).isType(timeline.OnRun()));
		assertTrue(actual.get(1).isType(timeline.OnFinish()));
		assertTrue(timeline.finished());
	}
	
	@Test
	public void testRunTo_AndContinue() throws Exception {
		final CountDownLatch paused = new CountDownLatch(1);
		// Нужной определить такое время, которое будет лежать откровенно вне
		// точек срабатывания событий: отсечка должна останавливать точно на
		// указанном времени, независимо от наличия событий.
		int index = stack.size();
		DateTime x1 = stack.getLast().time;
		pushToStack(src2);
		DateTime x2 = stack.get(index).time;
		DateTime stopAt = x1.plus((x2.getMillis() - x1.getMillis()) / 2);
		
		timeline = factory.produce(new Interval(from, to));
		timeline.OnPause().addListener(new EventListener() {
			@Override public void onEvent(Event arg0) {
				paused.countDown();
			}
		});
		timeline.OnFinish().addListener(new EventListener() {
			@Override public void onEvent(Event arg0) {
				finished.countDown();
			}
		});
		timeline.registerSource(new Src(stack, res));
		
		timeline.runTo(stopAt);
		assertTrue(paused.await(1, TimeUnit.SECONDS));
		assertTrue(timeline.paused());
		assertEquals(stopAt, timeline.getPOA());
		assertEquals(src1, res.str);
		
		timeline.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(timeline.finished());
		assertEquals(to, timeline.getPOA());
		assertEquals(src1 + src2, res.str);
	}
	
	@Test
	public void testRunTo_AndFinish() throws Exception {
		DateTime stopAt = stack.getLast().time.plus(1);
		final CountDownLatch paused = new CountDownLatch(1);
		pushToStack(src2);
		timeline = factory.produce(new Interval(from, to));
		//timeline.setDebug(true);
		timeline.OnPause().addListener(new EventListener() {
			@Override public void onEvent(Event arg0) {
				paused.countDown();
			}
		});
		timeline.OnFinish().addListener(new EventListener() {
			@Override public void onEvent(Event arg0) {
				finished.countDown();
			}
		});
		timeline.registerSource(new Src(stack, res));
		
		timeline.runTo(stopAt);
		assertTrue(paused.await(1, TimeUnit.SECONDS));
		assertTrue(timeline.paused());
		assertEquals(stopAt, timeline.getPOA());
		assertEquals(src1, res.str);
		
		timeline.finish();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(timeline.finished());
		assertEquals(stopAt, timeline.getPOA());
		assertEquals(src1, res.str);
	}
	
	@Test
	public void testRun_EndOfPeriod() throws Exception {
		timeline = factory.produce(new Interval(from, to));
		pushToStack(src2);	// Это нормально. Тестируем, что источники не
							// будут опрашиваться за пределами РП
		//timeline.setDebug(true);
		timeline.OnFinish().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				finished.countDown();
			}
		});
		timeline.registerSource(new Src(stack, res));
		timeline.registerSource(new Src(stack, res));
		timeline.registerSource(new Src(stack, res));
		
		timeline.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(src1, res.str);
		assertTrue(timeline.finished());
	}
	
	@Test
	public void testRun_EndOfData() throws Exception {
		timeline = factory.produce(new Interval(from, to.plusDays(365)));
		//timeline.setDebug(true);
		timeline.OnFinish().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				finished.countDown();
			}
		});
		timeline.registerSource(new Src(stack, res));
		timeline.registerSource(new Src(stack, res));
		
		timeline.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(src1, res.str);
		assertTrue(timeline.finished());
	}

}
