package ru.prolib.aquila.dde.utils.table;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Dependencies;
import ru.prolib.aquila.core.utils.DependencyRule;
import ru.prolib.aquila.core.utils.Deps;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.DDETableImpl;
import ru.prolib.aquila.dde.utils.DDETableEvent;

/**
 * 2012-09-20<br>
 * $Id: DDETableOrderTest.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class DDETableOrderTest {
	private EventSystem eventSystem;
	private EventDispatcher dispatcher;
	private EventQueue queue;
	private Dependencies<String> deps;
	private DDETableOrder order;
	private Map<String, DependencyRule> rules;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
	}

	@Before
	public void setUp() throws Exception {
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		dispatcher = eventSystem.createEventDispatcher();
		deps = new Deps<String>();
		deps.setDependency("position", "portfolio")
			.setDependency("position", "securities")
			.setDependency("position.fut", "portfolio.fut")
			.setDependency("position.fut", "securities")
			.setDependency("deals", "securities");

		order = new DDETableOrder(dispatcher, deps);
		queue.start();
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		queue.join(2000);
	}
	
	private DDETable table(String topic) {
		Object[] cells = new Object[4];
		return new DDETableImpl(cells, topic, "", 4);
	}
	
	@Test
	public void testEventHandling1() throws Exception {
		List<DDETable> src = new LinkedList<DDETable>();
		src.add(table("position"));
		src.add(table("position")); // Протестим поступление критичных данных
									// два раза подряд. Порядок должен совпасть.
		src.add(table("portfolio"));
		src.add(table("position")); // Вперемешку...
		src.add(table("position.fut"));
		src.add(table("securities")); // После этого все связанные с position
									// должны быть обработаны
		src.add(table("portfolio.fut"));
		
		List<DDETable> expected = new LinkedList<DDETable>();
		expected.add(src.get(2));
		expected.add(src.get(5));
		expected.add(src.get(0));
		expected.add(src.get(1));
		expected.add(src.get(3));
		expected.add(src.get(6));
		expected.add(src.get(4));
		
		runEventHandlingTest(src, expected);
	}
	
	@Test
	public void testEventHandling_WithRules() throws Exception {
		rules = new HashMap<String, DependencyRule>();
		rules.put("deals", DependencyRule.DROP);
		rules.put("position", DependencyRule.WAIT);
		order = new DDETableOrder(dispatcher, deps, rules);
		
		List<DDETable> src = new LinkedList<DDETable>();
		src.add(table("deals")); // будет отброшена
		src.add(table("position"));
		src.add(table("portfolio"));
		src.add(table("deals")); // будет отброшена
		src.add(table("securities"));
		src.add(table("position"));
		src.add(table("deals"));
		
		List<DDETable> expected = new LinkedList<DDETable>();
		expected.add(src.get(2));
		expected.add(src.get(4));
		expected.add(src.get(1));
		expected.add(src.get(5));
		expected.add(src.get(6));
		
		runEventHandlingTest(src, expected);
	}
	
	@Test
	public void testEventHandling2() throws Exception {
		deps = new Deps<String>()
			.setDependency("two", "one")
			.setDependency("three", "one");
		order = new DDETableOrder(dispatcher, deps);
		
		List<DDETable> src = new LinkedList<DDETable>();
		src.add(table("two"));
		src.add(table("three"));
		src.add(table("one"));
		
		List<DDETable> exp = new LinkedList<DDETable>();
		exp.add(src.get(2)); // сначала one
		exp.add(src.get(0)); // затем уже two
		exp.add(src.get(1)); // и three

		runEventHandlingTest(src, exp);
	}
	
	/**
	 * Выполнить тест рабочего цикла.
	 * <p>
	 * @param src набор исходных таблиц, в порядке поступления
	 * @param exp ожидаемый упорядоченный набор таблиц 
	 */
	private void runEventHandlingTest(List<DDETable> src, List<DDETable> exp)
		throws Exception
	{
		final List<DDETable> dst = new LinkedList<DDETable>();
		final CountDownLatch finished = new CountDownLatch(exp.size());
		order.addListener(new EventListener(){
			@Override
			public void onEvent(Event event) {
				dst.add(((DDETableEvent) event).getTable());
				finished.countDown();
			}
		});
		EventDispatcher d2 = eventSystem.createEventDispatcher();
		EventType t2 = new EventTypeImpl(d2);
		t2.addListener(order);
		for ( int i = 0; i < src.size(); i ++ ) {
			d2.dispatch(new DDETableEvent(t2, "SVC", src.get(i)));
		}
		assertTrue(finished.await(1000, TimeUnit.MILLISECONDS));
		assertEquals(exp, dst);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(order.equals(order));
		assertFalse(order.equals(new DDETableOrder(dispatcher, deps)));
		assertFalse(order.equals(this));
		assertFalse(order.equals(null));
	}

}
