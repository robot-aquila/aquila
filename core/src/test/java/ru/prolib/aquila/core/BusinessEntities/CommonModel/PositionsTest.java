package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * 2012-08-04<br>
 * $Id: PositionsImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PositionsTest {
	private static Account account;
	private static SecurityDescriptor descr1,descr2;
	private IMocksControl control;
	private EventSystem es;
	private EditableTerminal terminal;
	private Portfolio portfolio;
	private EditablePosition position1, position2;
	private Security security1, security2;
	private PositionsEventDispatcher dispatcher;
	private Positions positions;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("T01");
		descr1 = new SecurityDescriptor("SBER", "ONE", "RUB", SecurityType.STK);
		descr2 = new SecurityDescriptor("GAZP", "TWO", "USD", SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(PositionsEventDispatcher.class);
		terminal = control.createMock(EditableTerminal.class);
		portfolio = control.createMock(Portfolio.class);
		security1 = control.createMock(Security.class);
		security2 = control.createMock(Security.class);
		position1 = control.createMock(EditablePosition.class);
		position2 = control.createMock(EditablePosition.class);
		
		es = new EventSystemImpl();
		positions = new Positions(portfolio, dispatcher);
		
		expect(terminal.getEventSystem()).andStubReturn(es);
		expect(portfolio.getAccount()).andStubReturn(account);
		expect(portfolio.getTerminal()).andStubReturn(terminal);
		expect(security1.getDescriptor()).andStubReturn(descr1);
		expect(security2.getDescriptor()).andStubReturn(descr2);
		expect(position1.getSecurityDescriptor()).andStubReturn(descr1);
		expect(position2.getSecurityDescriptor()).andStubReturn(descr2);
	}
	
	@Test
	public void testFireEvents_Available() throws Exception {
		expect(position1.isAvailable()).andReturn(false);
		position1.setAvailable(true);
		dispatcher.fireAvailable(same(position1));
		position1.resetChanges();
		control.replay();
		
		positions.fireEvents(position1);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Changed() throws Exception {
		expect(position1.isAvailable()).andReturn(true);
		position1.fireChangedEvent();
		position1.resetChanges();
		control.replay();
		
		positions.fireEvents(position1);
		
		control.verify();
	}
	
	@Test
	public void testGetEditablePosition_New() throws Exception {
		Position expected = new PositionImpl(portfolio, security2,
				new PositionEventDispatcher(es, account, descr1));
		dispatcher.startRelayFor((Position) anyObject());
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
		Position expected = new PositionImpl(portfolio, security1,
				new PositionEventDispatcher(es, account, descr1));
		dispatcher.startRelayFor((Position) anyObject());
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
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(positions.equals(positions));
		assertFalse(positions.equals(null));
		assertFalse(positions.equals(this));
	}
	
	@Test
	public void testOnPositionAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, positions.OnPositionAvailable());
		
		control.verify();
	}
	
	@Test
	public void testOnPositionChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, positions.OnPositionChanged());
		
		control.verify();
	}

}
