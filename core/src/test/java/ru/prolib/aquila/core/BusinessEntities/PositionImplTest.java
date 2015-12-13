package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionEventDispatcher;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;


/**
 * 2012-08-03<br>
 * $Id: PositionImplTest.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImplTest {
	private static Symbol symbol;
	private static Account account;
	private IMocksControl control;
	private Terminal terminal;
	private Portfolio portfolio;
	private Security security;
	private PositionEventDispatcher dispatcher;
	private PositionImpl position;
	private G<?> getter;
	private S<PositionImpl> setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account = new Account("TST01");
		symbol = new Symbol("GAZP", "EQBR", "RUB", SymbolType.STOCK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		portfolio = control.createMock(Portfolio.class);
		security = control.createMock(Security.class);
		dispatcher = control.createMock(PositionEventDispatcher.class);
		position = new PositionImpl(portfolio, security, dispatcher);
		expect(portfolio.getTerminal()).andStubReturn(terminal);
		expect(portfolio.getAccount()).andStubReturn(account);
		expect(security.getSymbol()).andStubReturn(symbol);

		getter = null;
		setter = null;
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
	public void testVersion() throws Exception {
		assertEquals(1, Position.VERSION);
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
		assertSame(portfolio, position.getPortfolio());
		assertSame(security, position.getSecurity());
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		dispatcher.fireChanged(same(position));
		control.replay();
		
		position.fireChangedEvent();
		
		control.verify();
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
	public void testGetSymbol() throws Exception {
		control.replay();
		
		assertSame(symbol, position.getSymbol());
		
		control.verify();
	}
	
	@Test
	public void testGetAccount() throws Exception {
		control.replay();
		
		assertSame(account, position.getAccount());
		
		control.verify();
	}
	
	@Test
	public void testGetTerminal() throws Exception {
		control.replay();
		
		assertSame(terminal, position.getTerminal());
		
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
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(position.equals(position));
		assertFalse(position.equals(null));
		assertFalse(position.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		position.setAvailable(true);
		position.setOpenQty(20L);
		position.setLockQty(5L);
		position.setCurrQty(8L);
		position.setVarMargin(200.00d);
		position.setMarketValue(800.00d);
		position.setBookValue(780.00d);
		
		Variant<Portfolio> vPort = new Variant<Portfolio>()
			.add(portfolio)
			.add(control.createMock(Portfolio.class));
		Variant<Security> vSec = new Variant<Security>(vPort)
			.add(security)
			.add(control.createMock(Security.class));
		Variant<Boolean> vAvl = new Variant<Boolean>(vSec)
			.add(true)
			.add(false);
		Variant<Long> vOpen = new Variant<Long>(vAvl)
			.add(20L)
			.add(10L);
		Variant<Long> vLock = new Variant<Long>(vOpen)
			.add(5L)
			.add(1L);
		Variant<Long> vCurr = new Variant<Long>(vLock)
			.add(8L)
			.add(12L);
		Variant<Double> vVarMgn = new Variant<Double>(vCurr)
			.add(200.00d)
			.add(180.00d);
		Variant<Double> vMktVal = new Variant<Double>(vVarMgn)
			.add(800.00d)
			.add(120.00d);
		Variant<Double> vBookVal = new Variant<Double>(vMktVal)
			.add(780.00d)
			.add(634.01d);
		Variant<?> iterator = vBookVal;
		int foundCnt = 0;
		PositionImpl x = null, found = null;
		do {
			x = new PositionImpl(vPort.get(), vSec.get(), dispatcher);
			x.setAvailable(vAvl.get());
			x.setOpenQty(vOpen.get());
			x.setLockQty(vLock.get());
			x.setCurrQty(vCurr.get());
			x.setVarMargin(vVarMgn.get());
			x.setMarketValue(vMktVal.get());
			x.setBookValue(vBookVal.get());
			if ( position.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(portfolio, found.getPortfolio());
		assertSame(security, found.getSecurity());
		assertTrue(found.isAvailable());
		assertEquals(20L, found.getOpenQty());
		assertEquals(5L, found.getLockQty());
		assertEquals(8L, found.getCurrQty());
		assertEquals(200.00d, found.getVarMargin(), 0.001d);
		assertEquals(800.00d, found.getMarketValue(), 0.001d);
		assertEquals(780.00d, found.getBookValue(), 0.001d);
	}
	
	@Test
	public void testOnChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, position.OnChanged());
		
		control.verify();
	}

}
