package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.QUIKConfigImpl;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.quik.subsys.TableHeadersValidator;

/**
 * 2013-02-17<br>
 * $Id$
 */
public class RowSetBuilderFactoryTest {
	private IMocksControl control;
	private QUIKConfigImpl config;
	private EditableTerminal terminal;
	private RowAdapters rowAdapters;
	private QUIKServiceLocator locator;
	private G<Integer> adapter;
	private RowSetBuilderFactory factory;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		config = new QUIKConfigImpl();
		config.allDeals = "deals";
		config.portfoliosSTK = "port-stk";
		config.portfoliosFUT = "port-fut";
		config.positionsSTK = "pos-stk";
		config.positionsFUT = "pos-fut";
		config.orders = "orders";
		config.stopOrders = "stop-orders";
		config.securities = "securities";
		terminal = control.createMock(EditableTerminal.class);
		rowAdapters = control.createMock(RowAdapters.class);
		locator = control.createMock(QUIKServiceLocator.class);
		adapter = control.createMock(G.class);
		factory = new RowSetBuilderFactory(locator, rowAdapters);
		
		expect(locator.getTerminal()).andStubReturn(terminal);
		expect(locator.getConfig()).andStubReturn(config);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<RowAdapters> vAdp = new Variant<RowAdapters>(vLoc)
			.add(rowAdapters)
			.add(control.createMock(RowAdapters.class));
		Variant<?> iterator = vAdp;
		int foundCnt = 0;
		RowSetBuilderFactory x = null, found = null;
		do {
			x = new RowSetBuilderFactory(vLoc.get(), vAdp.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(rowAdapters, found.getRowAdapters());
	}
	
	@Test
	public void testCreateAllDealsRowSetBuilder() throws Exception {
		String required[] = { "foo", "bar" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("foo", adapter);
		expect(rowAdapters.getAllDealsRequiredFields()).andReturn(required);
		expect(rowAdapters.createAllDealsAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilder(
			new DDETableRowSetBuilderImpl(1, 1,
				new TableHeadersValidator(terminal, "deals", required)),
				adapters);
		control.replay();
		
		assertEquals(expected, factory.createAllDealsRowSetBuilder());
		
		control.verify();
	}
	
	@Test
	public void testCreatePortfolioStkRowSetBuilder() throws Exception {
		String required[] = { "zulu", "beta" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("zulu", adapter);
		expect(rowAdapters.getPortfolioStkRequiredFields()).andReturn(required);
		expect(rowAdapters.createPortfolioStkAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilder(
			new DDETableRowSetBuilderImpl(1, 1,
				new TableHeadersValidator(terminal, "port-stk", required)),
				adapters);
		control.replay();
		
		assertEquals(expected, factory.createPortfolioStkRowSetBuilder());
		
		control.verify();
	}
	
	@Test
	public void testCreatePortfolioFutRowSetBuilder() throws Exception {
		String required[] = { "kappa", "gamma" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("kappa", adapter);
		expect(rowAdapters.getPortfolioFutRequiredFields()).andReturn(required);
		expect(rowAdapters.createPortfolioFutAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilderFilter(
			new RowSetBuilderFilter( 
				new RowSetBuilder(
					new DDETableRowSetBuilderImpl(1, 1,
						new TableHeadersValidator(terminal, "port-fut",
							required)),
					adapters),
				new ValidateLimitType()),
			new ValidatePortfolioRow(locator));
		control.replay();
		
		assertEquals(expected, factory.createPortfolioFutRowSetBuilder());
		
		control.verify();
	}
	
	@Test
	public void testCreatePositionStkRowSetBuilder() throws Exception {
		String required[] = { "foo", "bar" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("foo", adapter);
		expect(rowAdapters.getPositionStkRequiredFields()).andReturn(required);
		expect(rowAdapters.createPositionStkAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilderFilter( 
			new RowSetBuilder(
				new DDETableRowSetBuilderImpl(1, 1,
					new TableHeadersValidator(terminal, "pos-stk", required)),
				adapters),
			new ValidatePositionRow(locator));
		control.replay();
		
		assertEquals(expected, factory.createPositionStkRowSetBuilder());
		
		control.verify();
	}
	
	@Test
	public void testCreatePositionFutRowSetBuilder() throws Exception {
		String required[] = { "one", "two" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("two", adapter);
		expect(rowAdapters.getPositionFutRequiredFields()).andReturn(required);
		expect(rowAdapters.createPositionFutAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilder(
				new DDETableRowSetBuilderImpl(1, 1,
					new TableHeadersValidator(terminal, "pos-fut", required)),
				adapters);
		control.replay();
		
		assertEquals(expected, factory.createPositionFutRowSetBuilder());
		
		control.verify();
	}
	
	@Test
	public void testCreateOrderRowSetBuilder() throws Exception {
		String required[] = { "buz", "map" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("map", adapter);
		expect(rowAdapters.getOrderRequiredFields()).andReturn(required);
		expect(rowAdapters.createOrderAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilder(
				new DDETableRowSetBuilderImpl(1, 1,
					new TableHeadersValidator(terminal, "orders", required)),
				adapters);
		control.replay();
		
		assertEquals(expected, factory.createOrderRowSetBuilder());
		
		control.verify();
	}
	
	@Test
	public void testCreateStopOrderRowSetBuilder() throws Exception {
		String required[] = { "stop", "order" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("stop", adapter);
		expect(rowAdapters.getStopOrderRequiredFields()).andReturn(required);
		expect(rowAdapters.createStopOrderAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilder(
			new DDETableRowSetBuilderImpl(1, 1,
				new TableHeadersValidator(terminal, "stop-orders", required)),
			adapters);
		control.replay();
		
		assertEquals(expected, factory.createStopOrderRowSetBuilder());
		
		control.verify();
	}
	
	@Test
	public void testCreateSecurityRowSetBuilder() throws Exception {
		String required[] = { "code", "class" };
		Map<String, G<?>> adapters = new HashMap<String, G<?>>();
		adapters.put("code", adapter);
		expect(rowAdapters.getSecurityRequiredFields()).andReturn(required);
		expect(rowAdapters.createSecurityAdapters()).andReturn(adapters);
		DDETableRowSetBuilder expected = new RowSetBuilderFilter(
			new RowSetBuilder(
				new DDETableRowSetBuilderImpl(1, 1,
					new TableHeadersValidator(terminal,"securities", required)),
					adapters),
			new ValidateSecurityRow(locator));
		control.replay();
		
		assertEquals(expected, factory.createSecurityRowSetBuilder());
		
		control.verify();
	}

}
