package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Variant;


/**
 * 2012-08-03<br>
 * $Id: PositionImplTest.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImplTest {
	private static EventSystem es;
	private static EventQueue queue;
	private static IMocksControl control;
	private static EditableTerminal terminal;
	private static SecurityDescriptor descr;
	private static Account account;
	private EventType onChanged;
	private EventDispatcher disp;
	private PositionImpl position;
	private G<?> getter;
	private S<PositionImpl> setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
		es = new EventSystemImpl();
		queue = es.getEventQueue();		
		control = createStrictControl();
		account = new Account("TST01");
		terminal = control.createMock(EditableTerminal.class);
		descr = new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		disp = es.createEventDispatcher();
		onChanged = es.createGenericType(disp);
		position = new PositionImpl(account, terminal, descr, disp, onChanged);
		queue.start();
		getter = null;
		setter = null;
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	/**
	 * Проверить работу геттера/сеттера с проверкой признака изменения.
	 * <p>
	 * Метод использует текущий экземпляр {@link #position}, {@link #getter} и
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
			setter.set(position, fixture[i][0]);
			position.resetChanges();
			setter.set(position, fixture[i][1]);
			boolean expected = (Boolean) fixture[i][2];
			assertEquals(msg, expected, position.hasChanged());
			assertEquals(msg, fixture[i][1], getter.get(position));
		}
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertEquals(0l, position.getCurrQty());
		assertEquals(0.0d, position.getMarketValue(), 0.01d);
		assertEquals(0l, position.getOpenQty());
		assertEquals(0.0d, position.getBookValue(), 0.01d);
		assertEquals(0l, position.getLockQty());
		assertEquals(0.0d, position.getVarMargin(), 0.01d);
		assertEquals(PositionType.CLOSE, position.getType());
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<Account> vAcc = new Variant<Account>()
			.add(account)
			.add(null);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vAcc)
			.add(null)
			.add(terminal);
		Variant<SecurityDescriptor> vDescr =
				new Variant<SecurityDescriptor>(vTerm)
			.add(null)
			.add(descr);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vDescr)
			.add(null)
			.add(disp);
		Variant<EventType> vChang = new Variant<EventType>(vDisp)
			.add(onChanged)
			.add(null);
		Variant<?> iterator = vChang;
		int exceptionCnt = 0;
		PositionImpl found = null;
		do {
			try {
				found = new PositionImpl(vAcc.get(), vTerm.get(),
						vDescr.get(), vDisp.get(), vChang.get());
			} catch ( NullPointerException e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
		assertSame(account, found.getAccount());
		assertSame(terminal, found.getTerminal());
		assertSame(descr, found.getSecurityDescriptor());
		assertSame(disp, found.getEventDispatcher());
		assertSame(onChanged, found.OnChanged());
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final PositionEvent expected = new PositionEvent(onChanged, position);
		position.OnChanged().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		position.fireChangedEvent();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testSetGetAvailable() throws Exception {
		assertFalse(position.isAvailable());
		position.setAvailable(true);
		assertTrue(position.isAvailable());
		position.setAvailable(false);
		assertFalse(position.isAvailable());
	}
	
	@Test
	public void testSetOpenQty_SetsChanged() throws Exception {
		assertFalse(position.hasChanged());
		position.setOpenQty(120L);
		assertTrue(position.hasChanged());
		assertEquals(120L, position.getOpenQty());
		position.resetChanges();
		assertFalse(position.hasChanged());
		position.setOpenQty(120L);
		assertFalse(position.hasChanged());
	}
	
	@Test
	public void testSetLockQty_SetsChanged() throws Exception {
		assertFalse(position.hasChanged());
		position.setLockQty(120L);
		assertTrue(position.hasChanged());
		assertEquals(120L, position.getLockQty());
		position.resetChanges();
		assertFalse(position.hasChanged());
		position.setLockQty(120L);
		assertFalse(position.hasChanged());
	}
	
	@Test
	public void testSetCurrQty_SetsChanged() throws Exception {
		assertFalse(position.hasChanged());
		position.setCurrQty(120L);
		assertTrue(position.hasChanged());
		assertEquals(120L, position.getCurrQty());
		position.resetChanges();
		assertFalse(position.hasChanged());
		position.setCurrQty(120L);
		assertFalse(position.hasChanged());
	}
	
	@Test
	public void testSetVarMargin_SetsChanged() throws Exception {
		assertFalse(position.hasChanged());
		position.setVarMargin(12.01d);
		assertTrue(position.hasChanged());
		assertEquals(12.01d, position.getVarMargin(), 0.001d);
		position.resetChanges();
		assertFalse(position.hasChanged());
		position.setVarMargin(12.01d);
		assertFalse(position.hasChanged());
	}
	
	@Test
	public void testSetMarketValue() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Position) source).getMarketValue();
			}
		};
		setter = new S<PositionImpl>() {
			@Override
			public void set(PositionImpl object, Object value) throws ValueException {
				object.setMarketValue((Double) value);
			}
		};
		testGetterSetter(200.25d, 18.210d);
	}

	@Test
	public void testSetBookValue() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Position) source).getBookValue();
			}
		};
		setter = new S<PositionImpl>() {
			@Override
			public void set(PositionImpl object, Object value) throws ValueException {
				object.setBookValue((Double) value);
			}
		};
		testGetterSetter(213.25d, 180.210d);
	}
	
	@Test
	public void testSetAccount() throws Exception {
		getter = new G<Account>() {
			@Override
			public Account get(Object source) throws ValueException {
				return ((Position) source).getAccount();
			}
		};
		setter = new S<PositionImpl>() {
			@Override
			public void set(PositionImpl object, Object value) throws ValueException {
				object.setAccount((Account) value);
			}
		};
		testGetterSetter(new Account("one"), new Account("two"));
	}

	@Test
	public void testGetSecurity() throws Exception {
		Security sec = control.createMock(Security.class);
		expect(terminal.getSecurity(same(descr))).andReturn(sec);
		control.replay();
		assertSame(sec, position.getSecurity());
		control.verify();
	}
	
	@Test
	public void testGetPortfolio() throws Exception {
		Portfolio port = control.createMock(Portfolio.class);
		expect(terminal.getPortfolio(eq(account))).andReturn(port);
		control.replay();
		assertSame(port, position.getPortfolio());
		control.verify();
	}
	
	@Test
	public void testGetType() throws Exception {
		assertEquals(0, position.getCurrQty());
		assertEquals(PositionType.CLOSE, position.getType());
		
		position.setCurrQty(1);
		assertEquals(1, position.getCurrQty());
		assertEquals(PositionType.LONG, position.getType());
		
		position.setCurrQty(-1);
		assertEquals(-1, position.getCurrQty());
		assertEquals(PositionType.SHORT, position.getType());
	}

}
