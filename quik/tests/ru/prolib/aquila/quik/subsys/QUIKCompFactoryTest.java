package ru.prolib.aquila.quik.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.RowHandler;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.dde.utils.table.*;

/**
 * 2012-10-19<br>
 * $Id: QUIKCompFactoryTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class QUIKCompFactoryTest {
	private static IMocksControl control;
	private static QUIKServiceLocator locator;
	private static EditableTerminal terminal;
	private static BMFactory bfactory;
	private static QUIKCompFactory factory;
	private Validator isAvailable;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		terminal = control.createMock(EditableTerminal.class);
		bfactory = control.createMock(BMFactory.class);
		isAvailable = control.createMock(Validator.class);
		factory = new QUIKCompFactory(locator, bfactory);
		expect(locator.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, factory.getServiceLocator());
		assertSame(bfactory, factory.getBusinessModelFactory());
	}
	
	@Test
	public void testListenTable2() throws Exception {
		RowHandler handler = control.createMock(RowHandler.class);
		DDETableListener expected = new DDETableListener("foo",
			new DDETableHandlerImpl(new DDETableRowSetBuilderImpl(), handler));

		assertEquals(expected, factory.listenTable("foo", handler));
	}
	
	@Test
	public void testListenTable3() throws Exception {
		RowHandler handler = control.createMock(RowHandler.class);
		Validator validator = control.createMock(Validator.class);
		
		DDETableListener expected = new DDETableListener("bar",
			new DDETableHandlerImpl(
					new DDETableRowSetBuilderImpl(1, 1, validator), handler));

		assertEquals(expected, factory.listenTable("bar", handler, validator));
	}
	
	@Test
	public void testCreateOrderFactory() throws Exception {
		OrderFactory expected = control.createMock(OrderFactory.class);
		expect(bfactory.createOrderFactory()).andReturn(expected);
		control.replay();
		OrderFactory actual = factory.createOrderFactory();
		control.verify();
		assertSame(expected, actual);
	}
	
	@Test
	public void testCreateOrders() throws Exception {
		EditableOrders expected = control.createMock(EditableOrders.class);
		expect(bfactory.createOrders()).andReturn(expected);
		control.replay();
		EditableOrders actual = factory.createOrders();
		control.verify();
		assertSame(expected, actual);
	}
	
	@Test
	public void testCreatePortfolios() throws Exception {
		EditablePortfolios base = control.createMock(EditablePortfolios.class);
		expect(bfactory.createPortfolios()).andReturn(base);
		control.replay();
		
		EditablePortfolios actual = factory.createPortfolios();
		
		control.verify();
		assertEquals(base, actual);
	}
	
	@Test
	public void testCreateSecurities() throws Exception {
		EditableSecurities base = control.createMock(EditableSecurities.class);
		expect(bfactory.createSecurities()).andReturn(base);
		control.replay();
		
		assertSame(base, factory.createSecurities());
		
		control.verify();
	}

	@Test
	public void testCreateTradeFactory() throws Exception {
		TradeFactory exp = control.createMock(TradeFactory.class);
		expect(bfactory.createTradeFactory()).andReturn(exp);
		control.replay();
		TradeFactory actual = factory.createTradeFactory();
		control.verify();
		assertSame(exp, actual);
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<BMFactory> vBf = new Variant<BMFactory>(vLoc)
			.add(bfactory)
			.add(control.createMock(BMFactory.class));
		Variant<?> iterator = vBf;
		int foundCnt = 0;
		QUIKCompFactory found = null, x = null;
		do {
			x = new QUIKCompFactory(vLoc.get(), vBf.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(bfactory, found.getBusinessModelFactory());
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 94401)
			.append(locator)
			.append(bfactory)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderEG0() throws Exception {
		S<EditableOrder> expected = control.createMock(S.class);
		expect(bfactory.createOrderEG()).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createOrderEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderEG1() throws Exception {
		S<EditableOrder> expected = control.createMock(S.class);
		expect(bfactory.createOrderEG(isAvailable)).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createOrderEG(isAvailable));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePortfolioEG0() throws Exception {
		S<EditablePortfolio> expected = control.createMock(S.class);
		expect(bfactory.createPortfolioEG()).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createPortfolioEG());
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePortfolioEG1() throws Exception {
		S<EditablePortfolio> expected = control.createMock(S.class);
		expect(bfactory.createPortfolioEG(isAvailable)).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createPortfolioEG(isAvailable));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePositionEG0() throws Exception {
		S<EditablePosition> expected = control.createMock(S.class);
		expect(bfactory.createPositionEG()).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createPositionEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePositionEG1() throws Exception {
		S<EditablePosition> expected = control.createMock(S.class);
		expect(bfactory.createPositionEG(isAvailable)).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createPositionEG(isAvailable));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecurityEG0() throws Exception {
		S<EditableSecurity> expected = control.createMock(S.class);
		expect(bfactory.createSecurityEG()).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createSecurityEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecurityEG1() throws Exception {
		S<EditableSecurity> expected = control.createMock(S.class);
		expect(bfactory.createSecurityEG(isAvailable)).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createSecurityEG(isAvailable));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateStopOrderEG0() throws Exception {
		S<EditableOrder> expected = control.createMock(S.class);
		expect(bfactory.createStopOrderEG()).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createStopOrderEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateStopOrderEG1() throws Exception {
		S<EditableOrder> expected = control.createMock(S.class);
		expect(bfactory.createStopOrderEG(isAvailable)).andReturn(expected);
		control.replay();
		assertSame(expected, factory.createStopOrderEG(isAvailable));
		control.verify();
	}

}
