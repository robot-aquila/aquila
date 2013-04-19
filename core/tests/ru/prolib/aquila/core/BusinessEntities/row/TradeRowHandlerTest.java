package ru.prolib.aquila.core.BusinessEntities.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.TradeRowHandler;
import ru.prolib.aquila.core.BusinessEntities.utils.TradeFactory;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-03<br>
 * $Id$
 */
public class TradeRowHandlerTest {
	private IMocksControl control;
	private TradeFactory factory;
	private S<Trade> modifier;
	private EditableTerminal terminal;
	private EditableSecurity esec;
	private static SecurityDescriptor descr;
	private TradeRowHandler handler;
	private Row row;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("A", "B", "EUR", SecurityType.FUT);
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		modifier = control.createMock(S.class);
		terminal = control.createMock(EditableTerminal.class);
		factory = control.createMock(TradeFactory.class);
		esec = control.createMock(EditableSecurity.class);
		row = control.createMock(Row.class);
		terminal = control.createMock(EditableTerminal.class);
		handler = new TradeRowHandler(terminal, factory, modifier);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, handler.getTerminal());
		assertSame(factory, handler.getTradeFactory());
		assertSame(modifier, handler.getTradeModifier());
	}
	
	@Test
	public void testHandle_Ok() throws Exception {
		Trade trade = new Trade(terminal);
		expect(factory.createTrade()).andReturn(trade);
		modifier.set(same(trade), same(row));
		expectLastCall().andDelegateTo(new S<Trade>() {
			@Override public void set(Trade object, Object value) throws ValueException {
				object.setSecurityDescriptor(descr);				
			}
		});
		expect(terminal.getEditableSecurity(same(descr))).andReturn(esec);
		esec.fireTradeEvent(same(trade));
		control.replay();
		handler.handle(row);
		control.verify();
	}
	
	@Test
	public void testHandle_ThrowsIfModifierThrows() throws Exception {
		ValueException expected = new ValueException("test");
		Trade trade = new Trade(terminal);
		expect(factory.createTrade()).andReturn(trade);
		modifier.set(same(trade), same(row));
		expectLastCall().andThrow(expected);
		control.replay();
		
		try {
			handler.handle(row);
			fail("Expected: " + RowException.class.getSimpleName());
		} catch ( RowException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}

	
	@Test
	public void testHandle_PanicIfSecurityDescrIsNull() throws Exception {
		Trade trade = new Trade(terminal);
		expect(factory.createTrade()).andReturn(trade);
		modifier.set(same(trade), same(row));
		terminal.firePanicEvent(eq(1),
				eq("Handle trade failed: security descriptor is NULL: {}"),
				aryEq(new Object[] { trade }));
		control.replay();
		handler.handle(row);
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vSec = new Variant<EditableTerminal>()
			.add(null)
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<TradeFactory> vFct = new Variant<TradeFactory>(vSec)
			.add(factory)
			.add(control.createMock(TradeFactory.class))
			.add(null);
		Variant<S<Trade>> vMod = new Variant<S<Trade>>(vFct)
			.add(null)
			.add(modifier)
			.add(control.createMock(S.class));
		Variant<?> iterator = vMod;
		int foundCnt = 0;
		TradeRowHandler found = null, actual = null;
		do {
			actual = new TradeRowHandler(vSec.get(), vFct.get(), vMod.get());
			if ( handler.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(factory, found.getTradeFactory());
		assertSame(modifier, found.getTradeModifier());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, /*0*/51623)
			.append(terminal)
			.append(factory)
			.append(modifier)
			.toHashCode();
		assertEquals(hashCode, handler.hashCode());
	}

}
