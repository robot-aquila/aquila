package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;

/**
 * 2013-01-11<br>
 * $Id: SetupPortfolioImplTest.java 562 2013-03-06 15:22:54Z whirlwind $
 */
public class SetupPortfolioImplTest {
	private static SecurityDescriptor descr1,descr2;
	private static IMocksControl control;
	private static EventDispatcher dispatcher;
	private static EventType onCommit,onRollback;
	private SetupPortfolioImpl setup;
	private SetupPositionsImpl initial,current;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("AAPL","SMART","USD",SecurityType.STK);
		descr2 = new SecurityDescriptor("SBER","EQBR","RUB",SecurityType.STK);
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		onCommit = control.createMock(EventType.class);
		onRollback = control.createMock(EventType.class);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		setup = new SetupPortfolioImpl(dispatcher, onCommit, onRollback);
		initial = (SetupPositionsImpl) setup.getInitialSetup();
		current = (SetupPositionsImpl) setup.getCurrentSetup();
	}
	
	@Test
	public void testConstruct() {
		assertNotNull(setup.getInitialSetup());
		assertNotNull(setup.getCurrentSetup());
		assertSame(dispatcher, setup.getEventDispatcher());
		assertSame(onCommit, setup.OnCommit());
		assertSame(onRollback, setup.OnRollback());
	}
	
	@Test
	public void testGetPosition() throws Exception {
		assertNotNull(setup.getPosition(descr1));
		assertSame(current.getPosition(descr1), setup.getPosition(descr1));
		assertNotNull(setup.getPosition(descr2));
		assertSame(current.getPosition(descr2), setup.getPosition(descr2));
		assertNotSame(setup.getPosition(descr1), setup.getPosition(descr2));
	}
	
	@Test
	public void testGetPositions() throws Exception {
		List<SetupPosition> expected = new Vector<SetupPosition>();
		expected.add(current.getPosition(descr1));
		expected.add(current.getPosition(descr2));
		assertEquals(expected, setup.getPositions());
	}
	
	@Test
	public void testRemovePosition() throws Exception {
		current.getPosition(descr1);
		List<SetupPosition> expected = new Vector<SetupPosition>();
		expected.add(current.getPosition(descr2));
		setup.removePosition(descr1);
		assertEquals(expected, setup.getPositions());
	}
	
	@Test
	public void testHashChanged() throws Exception {
		assertFalse(setup.hasChanged());
		initial.getPosition(descr1).setTarget(PositionType.CLOSE);
		assertTrue(setup.hasChanged());
		setup.getPosition(descr1).setTarget(PositionType.CLOSE);
		assertFalse(setup.hasChanged());
		setup.removePosition(descr1);
		assertTrue(setup.hasChanged());
	}
	
	@Test
	public void testRollback_IfChanged() throws Exception {
		initial.getPosition(descr1);
		current.getPosition(descr2);
		SetupPositionsImpl expected = initial.clone();
		SetupPositionsEvent ee = new SetupPositionsEvent(onRollback, expected);
		dispatcher.dispatch(eq(ee));
		control.replay();
		setup.rollback();
		control.verify();
		assertEquals(expected, setup.getInitialSetup());
		assertEquals(expected, setup.getCurrentSetup());
	}
	
	@Test
	public void testRollback_IfNotChanged() throws Exception {
		initial.getPosition(descr1);
		current.getPosition(descr1);
		SetupPositionsImpl expected = initial.clone();
		control.replay();
		setup.rollback();
		control.verify();
		assertEquals(expected, setup.getInitialSetup());
		assertEquals(expected, setup.getCurrentSetup());
	}

	@Test
	public void testCommit_IfChanged() throws Exception {
		initial.getPosition(descr1);
		initial.getPosition(descr2);
		SetupPositionsImpl expected = current.clone();
		SetupPositionsEvent ee = new SetupPositionsEvent(onCommit, expected);
		dispatcher.dispatch(eq(ee));
		control.replay();
		setup.commit();
		control.verify();
		assertEquals(expected, setup.getInitialSetup());
		assertEquals(expected, setup.getCurrentSetup());
	}
	
	@Test
	public void testCommit_IfNotChanged() throws Exception {
		initial.getPosition(descr1);
		current.getPosition(descr1);
		SetupPositionsImpl expected = initial.clone();
		control.replay();
		setup.commit();
		control.verify();
		assertEquals(expected, setup.getInitialSetup());
		assertEquals(expected, setup.getCurrentSetup());
	}
	
	@Test
	public void testForceCommit() throws Exception {
		initial.getPosition(descr1);
		current.getPosition(descr1);
		SetupPositionsImpl expected = initial.clone();
		SetupPositionsEvent ee = new SetupPositionsEvent(onCommit, expected);
		dispatcher.dispatch(eq(ee));
		control.replay();
		setup.forceCommit();
		control.verify();
	}
	
	@Test
	public void testResetChanges() throws Exception {
		initial.getPosition(descr1);
		control.replay();
		
		assertTrue(setup.hasChanged());
		assertFalse(setup.getInitialSetup().equals(setup.getCurrentSetup()));
		setup.resetChanges();
		assertFalse(setup.hasChanged());
		assertTrue(setup.getInitialSetup().equals(setup.getCurrentSetup()));
		
	}

}
