package ru.prolib.aquila.core.rule;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.rule.RunOnceOnEvent;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-01-07<br>
 * $Id: RunOnceOnEventTest.java 400 2013-01-08 05:22:51Z whirlwind $
 */
public class RunOnceOnEventTest {
	private static IMocksControl control;
	private static EventType type;
	private static Runnable runnable;
	private static RunOnceOnEvent run;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		runnable = control.createMock(Runnable.class);
		run = new RunOnceOnEvent(type, runnable);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(type, run.getEventType());
		assertSame(runnable, run.getRunnable());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(run.equals(run));
		assertFalse(run.equals(null));
		assertFalse(run.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(control.createMock(EventType.class))
			.add(type);
		Variant<Runnable> vTask = new Variant<Runnable>(vType)
			.add(runnable)
			.add(control.createMock(Runnable.class));
		Variant<?> iterator = vTask;
		int foundCnt = 0;
		RunOnceOnEvent x = null, found = null;
		do {
			x = new RunOnceOnEvent(vType.get(), vTask.get());
			if ( run.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getEventType());
		assertSame(runnable, found.getRunnable());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 72647)
			.append(type)
			.append(runnable)
			.toHashCode(), run.hashCode());
	}
	
	@Test
	public void testRun() throws Exception {
		type.addListener(same(run));
		control.replay();
		run.run();
		control.verify();
	}
	
	@Test
	public void testOnEvent_Ok() throws Exception {
		type.removeListener(same(run));
		runnable.run();
		control.replay();
		run.onEvent(new EventImpl(type));
		control.verify();
	}
	
	@Test
	public void testOnEvent_IgnoreDiffEventTypes() throws Exception {
		EventType type2 = control.createMock(EventType.class);
		control.replay();
		run.onEvent(new EventImpl(type2));
		control.verify();
	}

}
