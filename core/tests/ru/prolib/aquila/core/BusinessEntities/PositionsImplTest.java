package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-04<br>
 * $Id: PositionsImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PositionsImplTest {
	private static EventQueue queue = new SimpleEventQueue();
	private static Account account;
	private static SecurityDescriptor descr1,descr2;
	private IMocksControl control;
	private EventSystem es;
	private EditableTerminal terminal;
	private Portfolio portfolio;
	private EditablePosition position1, position2;
	private Security security1, security2;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onAvailable, onChanged;
	private PositionsImpl positions;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("T01");
		descr1 = new SecurityDescriptor("SBER", "ONE", "RUR", SecurityType.STK);
		descr2 = new SecurityDescriptor("GAZP", "TWO", "USD", SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcherMock = control.createMock(EventDispatcher.class);
		terminal = control.createMock(EditableTerminal.class);
		portfolio = control.createMock(Portfolio.class);
		security1 = control.createMock(Security.class);
		security2 = control.createMock(Security.class);
		position1 = control.createMock(EditablePosition.class);
		position2 = control.createMock(EditablePosition.class);
		
		es = new EventSystemImpl(queue);
		dispatcher = es.createEventDispatcher("Test");
		onAvailable = dispatcher.createType("OnAvailable");
		onChanged = dispatcher.createType("OnChanged");
		positions = new PositionsImpl(portfolio, dispatcher,
				onAvailable, onChanged);
		
		expect(terminal.getEventSystem()).andStubReturn(es);
		expect(portfolio.getAccount()).andStubReturn(account);
		expect(portfolio.getTerminal()).andStubReturn(terminal);
		expect(security1.getDescriptor()).andStubReturn(descr1);
		expect(security2.getDescriptor()).andStubReturn(descr2);
		expect(position1.getSecurityDescriptor()).andStubReturn(descr1);
		expect(position2.getSecurityDescriptor()).andStubReturn(descr2);
	}
	
	@Test
	public void testFirePositionAvailableEvent() throws Exception {
		positions = new PositionsImpl(portfolio, dispatcherMock,
				onAvailable, onChanged);
		dispatcherMock.dispatch(eq(new PositionEvent(onAvailable, position1)));
		control.replay();
		
		positions.firePositionAvailableEvent(position1);
		
		control.verify();
	}
	
	@Test
	public void testGetEditablePosition_New() throws Exception {
		String id = "Position[T01:GAZP@TWO(FUT/USD)]";
		EventDispatcher d = new EventDispatcherImpl(queue, id);
		Position expected = new PositionImpl(portfolio, security2,
				d, d.createType("OnChanged"));
		expected.OnChanged().addListener(positions);
		control.replay();

		Position actual = positions.getEditablePosition(security2);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(expected, actual);
		assertNotSame(expected, actual);
		assertSame(actual, positions.getEditablePosition(security2));
	}
	
	@Test
	public void testGetEditablePosition_Existing() throws Exception {
		positions.setPosition(descr1, position1);
		positions.setPosition(descr2, position2);
		control.replay();
		
		assertSame(position1, positions.getEditablePosition(security1));
		assertSame(position2, positions.getEditablePosition(security2));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_New() throws Exception {
		String id = "Position[T01:SBER@ONE(STK/RUR)]";
		EventDispatcher d = new EventDispatcherImpl(queue, id);
		Position expected = new PositionImpl(portfolio, security1,
				d, d.createType("OnChanged"));
		expected.OnChanged().addListener(positions);
		control.replay();
		
		Position actual = positions.getPosition(security1);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(expected, actual);
		assertNotSame(expected, actual);
		assertSame(actual, positions.getPosition(security1));
	}
	
	@Test
	public void testGetPosition_Existing() throws Exception {
		positions.setPosition(descr1, position1);
		positions.setPosition(descr2, position2);
		control.replay();
		
		assertSame(position1, positions.getPosition(security1));
		assertSame(position2, positions.getPosition(security2));
		
		control.verify();
	}
	
	@Test
	public void testGetPositions() throws Exception {
		List<Position> expected = new Vector<Position>();
		assertEquals(expected, positions.getPositions());
		positions.setPosition(descr1, position1);
		expected.add(position1);
		assertEquals(expected, positions.getPositions());
		positions.setPosition(descr2, position2);
		expected.add(position2);
		assertEquals(expected, positions.getPositions());
	}
	
	@Test
	public void testGetPositionsCount() throws Exception {
		assertEquals(0, positions.getPositionsCount());
		positions.setPosition(descr1, position1);
		assertEquals(1, positions.getPositionsCount());
		positions.setPosition(descr2, position2);
		assertEquals(2, positions.getPositionsCount());
	}
	
	@Test
	public void testOnEvent_DispatchPositionChangedEvent() throws Exception {
		EventType onPosChanged = control.createMock(EventType.class);
		expect(position1.OnChanged()).andStubReturn(onPosChanged);
		dispatcher.dispatch(new PositionEvent(onChanged, position1));
		control.replay();
		
		positions.onEvent(new PositionEvent(onPosChanged, position1));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(positions.equals(positions));
		assertFalse(positions.equals(null));
		assertFalse(positions.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		positions.setPosition(descr1, position1);
		List<Position> list1 = new Vector<Position>();
		list1.add(position1);
		List<Position> list2 = new Vector<Position>();
		list2.add(position2);
		list2.add(position1);
		Variant<Portfolio> vPort = new Variant<Portfolio>().add(portfolio);
		Variant<String> vDispId = new Variant<String>(vPort)
			.add("Test")
			.add("Best");
		Variant<String> vAvlId = new Variant<String>(vDispId)
			.add("OnAvailable")
			.add("OnBalabable");
		Variant<String> vChngId = new Variant<String>(vAvlId)
			.add("OnChanged")
			.add("OnGranged");
		Variant<List<Position>> vList = new Variant<List<Position>>(vChngId)
			.add(list1)
			.add(list2);
		Variant<?> iterator = vList;
		int foundCnt = 0;
		PositionsImpl x = null, found = null;
		control.replay();
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new PositionsImpl(vPort.get(), d,
					d.createType(vAvlId.get()), d.createType(vChngId.get()));
			for ( Position p : vList.get() ) {
				x.setPosition(p.getSecurityDescriptor(), (EditablePosition) p);
			}
			if ( positions.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(portfolio, found.getPortfolio());
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onAvailable, found.OnPositionAvailable());
		assertEquals(onChanged, found.OnPositionChanged());
		assertEquals(list1, found.getPositions());
	}

}
