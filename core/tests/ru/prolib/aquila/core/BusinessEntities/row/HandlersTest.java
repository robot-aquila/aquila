package ru.prolib.aquila.core.BusinessEntities.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.Modifiers;
import ru.prolib.aquila.core.BusinessEntities.row.Handlers;
import ru.prolib.aquila.core.BusinessEntities.row.TradeRowHandler;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderFactoryImpl;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderResolverStd;
import ru.prolib.aquila.core.BusinessEntities.utils.TradeFactoryImpl;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.row.RowHandler;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-14<br>
 * $Id$
 */
public class HandlersTest {
	private IMocksControl control;
	private EventSystem es;
	private EditableTerminal terminal;
	private Modifiers modifiers;
	private Handlers builder;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		terminal = control.createMock(EditableTerminal.class);
		modifiers = control.createMock(Modifiers.class);
		builder = new Handlers(es, terminal, modifiers);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(es, builder.getEventSystem());
		assertSame(terminal, builder.getTerminal());
		assertSame(modifiers, builder.getModifiers());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(builder.equals(builder));
		assertFalse(builder.equals(this));
		assertFalse(builder.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(es)
			.add(control.createMock(EventSystem.class));
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vEs)
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<Modifiers> vMods = new Variant<Modifiers>(vTerm)
			.add(modifiers)
			.add(control.createMock(Modifiers.class));
		Variant<?> iterator = vMods;
		int foundCnt = 0;
		Handlers x = null, found = null;
		do {
			x = new Handlers(vEs.get(), vTerm.get(), vMods.get());
			if ( builder.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(es, found.getEventSystem());
		assertSame(terminal, found.getTerminal());
		assertSame(modifiers, found.getModifiers());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTradeHandler() throws Exception {
		S<Trade> mod = control.createMock(S.class);
		expect(modifiers.createTradeModifier()).andReturn(mod);
		RowHandler expected =
			new TradeRowHandler(terminal, new TradeFactoryImpl(terminal), mod);
		control.replay();
		
		assertEquals(expected, builder.createTradeHandler());
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePortfolioHandler() throws Exception {
		S<EditablePortfolio> mod = control.createMock(S.class);
		expect(modifiers.createPortfolioModifier()).andReturn(mod);
		RowHandler expected = new PortfolioRowHandler(terminal, mod);
		control.replay();
		
		assertEquals(expected, builder.createPortfolioHandler());
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePortfolioHandler1() throws Exception {
		Validator isAvailable = control.createMock(Validator.class);
		S<EditablePortfolio> mod = control.createMock(S.class);
		expect(modifiers.createPortfolioModifier(isAvailable)).andReturn(mod);
		RowHandler expected = new PortfolioRowHandler(terminal, mod);
		control.replay();
		
		assertEquals(expected, builder.createPortfolioHandler(isAvailable));
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePositionHandler() throws Exception {
		S<EditablePosition> mod = control.createMock(S.class);
		expect(modifiers.createPositionModifier()).andReturn(mod);
		RowHandler expected = new PositionRowHandler(terminal, mod);
		control.replay();
		
		assertEquals(expected, builder.createPositionHandler());
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderHandler() throws Exception {
		S<EditableOrder> mod = control.createMock(S.class);
		EditableOrders orders = control.createMock(EditableOrders.class);
		expect(terminal.getOrdersInstance()).andReturn(orders);
		expect(modifiers.createOrderModifier()).andReturn(mod);
		RowHandler expected = new OrderRowHandler(terminal,
			new OrderResolverStd(orders, new OrderFactoryImpl(es, terminal)),
			mod);
		control.replay();
		
		assertEquals(expected, builder.createOrderHandler());
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateStopOrderHandler() throws Exception {
		S<EditableOrder> mod = control.createMock(S.class);
		EditableOrders orders = control.createMock(EditableOrders.class);
		expect(terminal.getStopOrdersInstance()).andReturn(orders);
		expect(modifiers.createStopOrderModifier()).andReturn(mod);
		RowHandler expected = new OrderRowHandler(terminal,
			new OrderResolverStd(orders, new OrderFactoryImpl(es, terminal)),
			mod);
		control.replay();
		
		assertEquals(expected, builder.createStopOrderHandler());
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecurityHandler() throws Exception {
		S<EditableSecurity> mod = control.createMock(S.class);
		expect(modifiers.createSecurityModifier()).andReturn(mod);
		RowHandler expected = new SecurityRowHandler(terminal, mod);
		control.replay();
		
		assertEquals(expected, builder.createSecurityHandler());
		
		control.verify();
	}

}
