package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;

/**
 * 2012-09-06
 */
public class PortfolioImplTest {
	private static Account account;
	private IMocksControl control;
	private PortfolioEventDispatcher dispatcher;
	private Positions positions;
	private Terminal terminal;
	private PortfolioImpl portfolio;
	private G<?> getter;
	private S<PortfolioImpl> setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("LX01", "865");
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		positions = control.createMock(Positions.class);
		terminal = control.createMock(Terminal.class);
		dispatcher = control.createMock(PortfolioEventDispatcher.class);
		
		portfolio = new PortfolioImpl(terminal, account, dispatcher);
		portfolio.setPositionsInstance(positions);
		getter = null;
		setter = null;
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
	public void testVersion() throws Exception {
		assertEquals(1, Portfolio.VERSION);
		assertEquals(3, PortfolioImpl.VERSION);
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertNull(portfolio.getVariationMargin());
		assertNull(portfolio.getCash());
		assertNull(portfolio.getBalance());
		assertFalse(portfolio.isAvailable());
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		dispatcher.fireChanged(same(portfolio));
		control.replay();
		
		portfolio.fireChangedEvent();
		
		control.verify();
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
	public void testGetPosition() throws Exception {
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
	public void testFireEvents() throws Exception {
		EditablePosition p = control.createMock(EditablePosition.class);
		positions.fireEvents(same(p));
		control.replay();
		
		portfolio.fireEvents(p);
		
		control.verify();
	}

	@Test
	public void testGetEditablePosition() throws Exception {
		EditablePosition p = control.createMock(EditablePosition.class);
		Security sec = control.createMock(Security.class);
		expect(positions.getEditablePosition(same(sec))).andReturn(p);
		control.replay();
		
		assertSame(p, portfolio.getEditablePosition(sec));
		
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
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(portfolio.equals(portfolio));
		assertFalse(portfolio.equals(null));
		assertFalse(portfolio.equals(this));
	}
	
	@Test
	public void testOnChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, portfolio.OnChanged());
		
		control.verify();
	}
		
}
