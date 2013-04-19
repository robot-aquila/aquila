package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.*;

/**
 * 2012-09-06<br>
 * $Id$
 */
public class PortfolioImplTest {
	private IMocksControl control;
	private EventSystem eventSystem;
	private EventQueue queue;
	private EventDispatcher dispatcher;
	private EventType onChanged;
	private EditablePositions positions;
	private Terminal terminal;
	private Account account;
	private PortfolioImpl portfolio;
	private G<?> getter;
	private S<PortfolioImpl> setter;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		positions = control.createMock(EditablePositions.class);
		terminal = control.createMock(Terminal.class);
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		dispatcher = eventSystem.createEventDispatcher();
		onChanged = eventSystem.createGenericType(dispatcher);
		account = new Account("LX01", "865");
		portfolio = new PortfolioImpl(terminal, account, positions,
									  dispatcher, onChanged);
		queue.start();
		getter = null;
		setter = null;
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
	}
	
	/**
	 * Проверить работу геттера/сеттера с проверкой признака изменения.
	 * <p>
	 * Метод использует текущий экземпляр {@link #portfolio}, {@link #getter} и
	 * {@link #setter}.
	 * <p>
	 * @param firstValue начальное значение
	 * @param secondValue конечное значение
	 */
	private void testGetterSetter(Object firstValue, Object secondValue)
			throws Exception
	{
		Object fixture[][] = {
				{ null, 		null,			false },
				{ null, 		secondValue,	true  },
				{ firstValue,	secondValue,	true  },
				{ secondValue,	secondValue,	false },
				{ firstValue,   null,			true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			setter.set(portfolio, fixture[i][0]);
			portfolio.resetChanges();
			setter.set(portfolio, fixture[i][1]);
			boolean expected = (Boolean) fixture[i][2];
			assertEquals(msg, expected, portfolio.hasChanged());
			assertEquals(msg, fixture[i][1], getter.get(portfolio));
		}
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<Terminal> vTerminal = new Variant<Terminal>()
			.add(terminal).add(null);
		Variant<Account> vAcc = new Variant<Account>(vTerminal)
			.add(new Account("LX01", "865")).add(null);
		Variant<EditablePositions> vPoss = new Variant<EditablePositions>(vAcc)
			.add(positions).add(null);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vPoss)
			.add(dispatcher).add(null);
		Variant<EventType> vChanged = new Variant<EventType>(vDisp)
			.add(onChanged).add(null);
		int exceptions = 0;
		int index = 0;
		do {
			String msg = "At #" + index;
			try {
				portfolio = new PortfolioImpl(vTerminal.get(), vAcc.get(),
						vPoss.get(), vDisp.get(), vChanged.get());
				assertSame(msg, terminal, portfolio.getTerminal());
				assertEquals(msg, account, portfolio.getAccount());
				assertSame(msg, positions, portfolio.getPositionsInstance());
				assertSame(msg, dispatcher, portfolio.getEventDispatcher());
				assertSame(msg, onChanged, portfolio.OnChanged());
			} catch ( NullPointerException e ) {
				exceptions ++;
			}
			index ++;
		} while( vChanged.next() );
		assertEquals(vChanged.count() - 1, exceptions);
	}
	
	@Test
	public void testDefaultsValues() throws Exception {
		assertNull(portfolio.getVariationMargin());
		assertNull(portfolio.getCash());
		assertNull(portfolio.getBalance());
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final PortfolioEvent expected = new PortfolioEvent(onChanged,portfolio);
		portfolio.OnChanged().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		portfolio.fireChangedEvent();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testSetCash() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Portfolio) source).getCash();
			}
		};
		setter = new S<PortfolioImpl>() {
			@Override
			public void set(PortfolioImpl object, Object value) throws ValueException {
				object.setCash((Double) value);
			}
		};
		testGetterSetter(100.25d, 88.19d);
	}
	
	@Test
	public void testSetVariationMargin() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Portfolio) source).getVariationMargin();
			}
		};
		setter = new S<PortfolioImpl>() {
			@Override
			public void set(PortfolioImpl object, Object value) throws ValueException {
				object.setVariationMargin((Double) value);
			}
		};
		testGetterSetter(12.75d, 22.15d);
	}
	
	@Test
	public void testSetBalance() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Portfolio)source).getBalance();
			}};
		setter = new S<PortfolioImpl>() {
			@Override
			public void set(PortfolioImpl object, Object value) throws ValueException {
				object.setBalance((Double) value);
			}
		};
		testGetterSetter(23.45d, 18.34d);
	}
	
	@Test
	public void testGetPositions() throws Exception {
		@SuppressWarnings("unchecked")
		List<Position> list = control.createMock(List.class);
		expect(positions.getPositions()).andReturn(list);
		control.replay();
		
		assertSame(list, portfolio.getPositions());
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_ByDescr() throws Exception {
		Position p = control.createMock(Position.class);
		SecurityDescriptor descr =
				new SecurityDescriptor("A","B","C",SecurityType.CASH);
		expect(positions.getPosition(eq(descr))).andReturn(p);
		control.replay();
		
		assertSame(p, portfolio.getPosition(descr));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_BySec() throws Exception {
		Security sec = control.createMock(Security.class);
		Position p = control.createMock(Position.class);
		expect(positions.getPosition(sec)).andReturn(p);
		control.replay();
		assertSame(p, portfolio.getPosition(sec));
		control.verify();
	}
	
	@Test
	public void testOnPositionAvailable() throws Exception {
		EventType e = control.createMock(EventType.class);
		expect(positions.OnPositionAvailable()).andReturn(e);
		control.replay();
		
		assertSame(e, portfolio.OnPositionAvailable());
		
		control.verify();
	}
	
	@Test
	public void testFirePositionAvailableEvent() throws Exception {
		EditablePosition p = control.createMock(EditablePosition.class);
		positions.firePositionAvailableEvent(same(p));
		control.replay();
		
		portfolio.firePositionAvailableEvent(p);
		
		control.verify();
	}

	@Test
	public void testGetEditablePosition() throws Exception {
		EditablePosition p = control.createMock(EditablePosition.class);
		SecurityDescriptor descr =
				new SecurityDescriptor("B","C","D",SecurityType.BOND);
		expect(positions.getEditablePosition(eq(descr))).andReturn(p);
		control.replay();
		
		assertSame(p, portfolio.getEditablePosition(descr));
		
		control.verify();
	}
	
	@Test
	public void testOnPositionChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(positions.OnPositionChanged()).andReturn(type);
		control.replay();
		assertSame(type, portfolio.OnPositionChanged());
		control.verify();
	}
	
	@Test
	public void testGetPositionsCount() throws Exception {
		expect(positions.getPositionsCount()).andReturn(200);
		control.replay();
		assertEquals(200, portfolio.getPositionsCount());
		control.verify();
	}
	
}
