package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.*;

import ru.prolib.aquila.core.*;

/**
 * Интеграционный тест подсистемы симуляции хронологии событий.
 * <p>
 * 1) Прогон пустой хронологии;<br>
 * 2) Прогон с приостановкой по времени;<br>
 * 3) Прогон с принудительным завершением;<br>
 * 4) Прогон до конца последовательности;<br>
 * 5) Прогон до конца данных;<br>
 */
public class TLSTimeline_IntegrationTest {
	private EventSystem es;
	private String src =
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
	private Random rnd = new Random(2418);
	private LinkedList<Entry> stack;
	private Result res;
	private TLSTimelineFactory factory;
	private TLSTimeline timeline;
	private DateTime from,to;

	static class Entry {
		final char c;
		final DateTime time;
		Entry(char c, DateTime time) {
			this.c = c;
			this.time = time;
		}
	}
	
	static class Result {
		String str = "";
		void append(char c) {
			str += c;
		}
	}
	
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
				@Override public void run() { res.append(e.c); }
			});
		}

		@Override public void close() { }

		@Override public boolean closed() {
			return entries.size() == 0;
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		stack = new LinkedList<Entry>();
		int maxDiffMs = 1000 * 60 * 60; // one hour
		DateTime time = from = new DateTime(2014,6,3,0,0,0,0);
		for ( int i = 0; i < src.length(); i ++ ) {
			stack.add(new Entry(src.charAt(i), time));
			time = time.plus(rnd.nextInt(maxDiffMs));
		}
		to = stack.getLast().time.plus(1);
		res = new Result();
		es = new EventSystemImpl();
		es.getEventQueue().start();
		factory = new TLSTimelineFactory(es);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
		es.getEventQueue().join(5000L);
	}
	
	@Test
	public void test_Empty() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		timeline = factory.produce(new Interval(from, to));
		timeline.setDebug(true);
		timeline.OnFinish().addListener(new EventListener() {
			@Override public void onEvent(Event arg0) {
				System.err.println("finished");
				finished.countDown();
			}
		});
		timeline.OnRun().addListener(new EventListener() {
			@Override public void onEvent(Event arg0) {
				System.err.println("run");
			}
		});
		timeline.OnStep().addListener(new EventListener() {
			@Override public void onEvent(Event arg0) {
				System.err.println("step");
			}
		});
		
		timeline.run();
		System.err.println("zuza");
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void test_() throws Exception {
		//System.out.println(stack.getLast().time);
		fail("TODO: ");
	}

}
