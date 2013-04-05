package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionFactory;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionFactoryImpl;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-04<br>
 * $Id: PositionsImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PositionsImplTest {
	private static IMocksControl control;
	private static EventSystem eventSystem;
	private static EventQueue queue;
	private static Account account = new Account("TST01");
	private static EditableTerminal terminal;
	private static PositionFactoryImpl factory;
	private static SecurityDescriptor descr1,descr2;
	private EventDispatcher dispatcher;
	private EventType onAvailable,onChanged;
	private PositionsImpl positions;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		descr1 = new SecurityDescriptor("SBER", "ONE", "RUR", SecurityType.STK);
		descr2 = new SecurityDescriptor("GAZP", "TWO", "USD", SecurityType.FUT);
		factory = new PositionFactoryImpl(eventSystem, account, terminal);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		dispatcher = eventSystem.createEventDispatcher();
		onAvailable = eventSystem.createGenericType(dispatcher);
		onChanged = eventSystem.createGenericType(dispatcher);
		positions = new PositionsImpl(factory, dispatcher,
				onAvailable, onChanged);
		queue.start();
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<PositionFactory> vFact = new Variant<PositionFactory>()
			.add(factory)
			.add(null);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vFact)
			.add(dispatcher)
			.add(null);
		Variant<EventType> vAvail = new Variant<EventType>(vDisp)
			.add(null)
			.add(onAvailable);
		Variant<EventType> vChang = new Variant<EventType>(vAvail)
			.add(onChanged)
			.add(null);
		Variant<?> iterator = vChang;
		int exceptionCnt = 0;
		PositionsImpl x = null, found = null;
		do {
			try {
				x = new PositionsImpl(vFact.get(), vDisp.get(),
						vAvail.get(), vChang.get());
				found = x;
			} catch ( NullPointerException e ) {
				exceptionCnt++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
		assertSame(factory, found.getPositionFactory());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onAvailable, found.OnPositionAvailable());
		assertSame(onChanged, found.OnPositionChanged());
	}
	
	@Test
	public void testFirePositionAvailableEvent() throws Exception {
		EditablePosition pos = factory.createPosition(descr1);
		final CountDownLatch finished = new CountDownLatch(1);
		final PositionEvent expected = new PositionEvent(onAvailable, pos);
		positions.OnPositionAvailable().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		positions.firePositionAvailableEvent(pos);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testGetEditablePosition() throws Exception {
		EditablePosition p1 = positions.getEditablePosition(descr1);
		assertSame(p1, positions.getEditablePosition(descr1));
		EditablePosition p2 = positions.getEditablePosition(descr2);
		assertSame(p2, positions.getEditablePosition(descr2));
		
		assertTrue(p1.OnChanged().isListener(positions));
		assertTrue(p2.OnChanged().isListener(positions));
	}
	
	@Test
	public void testGetPosition_ByDescr() throws Exception {
		EditablePosition p1 = positions.getEditablePosition(descr1);
		EditablePosition p2 = positions.getEditablePosition(descr2);

		assertSame(p1, positions.getEditablePosition(descr1));
		assertSame(p1, positions.getPosition(descr1));
		assertSame(p2, positions.getPosition(descr2));
		assertSame(p2, positions.getEditablePosition(descr2));
	}
	
	@Test
	public void testGetPosition_BySec() throws Exception {
		Security sec1 = control.createMock(Security.class);
		Security sec2 = control.createMock(Security.class);
		expect(sec1.getDescriptor()).andStubReturn(descr1);
		expect(sec2.getDescriptor()).andStubReturn(descr2);
		control.replay();
		Position p1 = positions.getPosition(sec1);
		assertSame(p1, positions.getEditablePosition(descr1));
		assertSame(p1, positions.getPosition(descr1));
		Position p2 = positions.getPosition(sec2);
		assertSame(p2, positions.getEditablePosition(descr2));
		assertSame(p2, positions.getPosition(descr2));
		control.verify();
	}
	
	@Test
	public void testGetPositions() throws Exception {
		List<Position> list = positions.getPositions();
		assertEquals(0, list.size());
		// Каждый раз создается копия списка
		assertNotSame(list, positions.getPositions());
		
		Position p1 = positions.getPosition(descr1);
		Position p2 = positions.getEditablePosition(descr2);
		
		list = positions.getPositions();
		assertEquals(2, list.size());
		assertSame(p1, list.get(0));
		assertSame(p2, list.get(1));

	}
	
	@Test
	public void testGetPositionsCount() throws Exception {
		assertEquals(0, positions.getPositionsCount());
		positions.getEditablePosition(descr1);
		assertEquals(1, positions.getPositionsCount());
		positions.getEditablePosition(descr2);
		assertEquals(2, positions.getPositionsCount());
		positions.getEditablePosition(descr1);
		positions.getEditablePosition(descr2);
		assertEquals(2, positions.getPositionsCount());
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		EditablePosition p = positions.getEditablePosition(descr1);
		final PositionEvent e = new PositionEvent(onChanged, p);
		final CountDownLatch finished = new CountDownLatch(1);
		positions.OnPositionChanged().addListener(new EventListener(){
			@Override
			public void onEvent(Event event) {
				assertEquals(e, event);
				finished.countDown();
			}
		});
		positions.onEvent(new PositionEvent(p.OnChanged(), p));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

}
