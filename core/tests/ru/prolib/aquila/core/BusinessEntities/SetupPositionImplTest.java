package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-12-26<br>
 * $Id: SetupPositionImplTest.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class SetupPositionImplTest {
	private static IMocksControl control;
	private static SecurityDescriptor descr;
	private SetupPositionImpl setup;
	private G<?> getter;
	private S<SetupPosition> setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		descr = new SecurityDescriptor("GAZP", "EQBR", "RUB", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		setup = new SetupPositionImpl(descr);
		getter = null;
		setter = null;
	}
	
	/**
	 * Протестировать геттер/сеттер атрибута.
	 * <p>
	 * @param firstValue первое значение атрибута
	 * @param secondValue второе значение атрибута
	 */
	private void testGetterSetter(Object firstValue, Object secondValue) {
		Object fixture[][] = {
				// initial value, new value
				{ null, 	  null			},
				{ firstValue, firstValue	},
				{ null,		  secondValue	},
				{ firstValue, secondValue	},
				{ firstValue, null			},
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			setter.set(setup, fixture[i][0]);
			setter.set(setup, fixture[i][1]);
			assertEquals(msg, fixture[i][1], getter.get(setup));
		}
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertEquals(new Price(PriceUnit.PERCENT, 0d), setup.getQuota());
		assertEquals(PositionType.CLOSE, setup.getType());
	}
	
	@Test
	public void testSetQuota() throws Exception {
		getter = new G<Price>() {
			@Override
			public Price get(Object source) {
				return ((SetupPosition) source).getQuota();
			}
		};
		setter = new S<SetupPosition>() {
			@Override
			public void set(SetupPosition object, Object value) {
				object.setQuota((Price) value);
			}
		};
		testGetterSetter(new Price(PriceUnit.POINT, 10.25d),
						 new Price(PriceUnit.MONEY, 1000.01d));
	}
	
	@Test
	public void testSetType() throws Exception {
		getter = new G<PositionType>() {
			@Override
			public PositionType get(Object source) {
				return ((SetupPosition) source).getType();
			}
		};
		setter = new S<SetupPosition>() {
			@Override
			public void set(SetupPosition object, Object value) {
				object.setType((PositionType) value);
			}
		};
		testGetterSetter(PositionType.SHORT, PositionType.CLOSE);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(setup.equals(setup));
		assertFalse(setup.equals(this));
		assertFalse(setup.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		// Начальное состояние
		setup.setQuota(new Price(PriceUnit.MONEY, 200.00d));
		setup.setType(PositionType.LONG);
		
		Variant<SecurityDescriptor> vSec = new Variant<SecurityDescriptor>()
			.add(descr)
			.add(control.createMock(SecurityDescriptor.class));
		Variant<PriceUnit> vUnit = new Variant<PriceUnit>(vSec)
			.add(PriceUnit.MONEY)
			.add(PriceUnit.POINT);
		Variant<Double> vVal = new Variant<Double>(vUnit)
			.add(200.0d)
			.add(1200.00d);
		Variant<PositionType> vType = new Variant<PositionType>(vVal)
			.add(PositionType.LONG)
			.add(PositionType.CLOSE);
		Variant<?> iterator = vType;;
		int foundCnt = 0;
		SetupPositionImpl x = null, found = null;
		do {
			x = new SetupPositionImpl(vSec.get());
			x.setQuota(new Price(vUnit.get(), vVal.get()));
			x.setType(vType.get());
			if ( setup.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(descr, found.getSecurityDescriptor());
		assertEquals(new Price(PriceUnit.MONEY, 200.00d), found.getQuota());
		assertEquals(PositionType.LONG, found.getType());
	}
	
	@Test
	public void testHashCode() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setType(PositionType.LONG);
		assertEquals(new HashCodeBuilder(20121231, 165329)
			.append(descr)
			.append(new Price(PriceUnit.POINT, 50d))
			.append(PositionType.LONG)
			.toHashCode(), setup.hashCode());
	}
	
	@Test
	public void testClone() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setType(PositionType.LONG);
		
		SetupPosition clone = setup.clone();
		assertNotNull(clone);
		assertNotSame(clone, setup);
		assertEquals(setup, clone);
		assertEquals(new Price(PriceUnit.POINT, 50d), clone.getQuota());
		assertEquals(PositionType.LONG, clone.getType());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setType(PositionType.LONG);

		SetupPosition setup2 = new SetupPositionImpl(descr,
				new Price(PriceUnit.POINT, 50d), PositionType.LONG);
		assertEquals(setup, setup2);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setType(PositionType.CLOSE);

		SetupPosition setup2 = new SetupPositionImpl(descr,
				new Price(PriceUnit.POINT, 50d));
		assertEquals(setup, setup2);
	}
	
	@Test
	public void testConstruct1Copy() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setType(PositionType.CLOSE);

		SetupPosition setup2 = new SetupPositionImpl(setup);
		assertEquals(setup, setup2);
		assertNotSame(setup, setup2);
	}

}
