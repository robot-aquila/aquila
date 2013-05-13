package ru.prolib.aquila.core.BusinessEntities.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.PortfolioRowHandler;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-07<br>
 * $Id$
 */
public class PortfolioRowHandlerTest {
	private static final Account acc = new Account("LX01");
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditablePortfolio portfolio;
	private S<EditablePortfolio> mod;
	private PortfolioRowHandler rowHandler;
	private Row row;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		portfolio = control.createMock(EditablePortfolio.class);
		mod = control.createMock(S.class);
		rowHandler = new PortfolioRowHandler(terminal, mod);
		row = control.createMock(Row.class);
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(null)
			.add(terminal);
		Variant<S<EditablePortfolio>> vMod =
				new Variant<S<EditablePortfolio>>(vTerm)
			.add(mod)
			.add(null);
		Variant<?> iterator = vMod;
		int exceptions = 0;
		PortfolioRowHandler found = null;
		do {
			try {
				found = new PortfolioRowHandler(vTerm.get(), vMod.get());
			} catch ( NullPointerException e ) {
				exceptions ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptions);
		assertSame(terminal, found.getTerminal());
		assertSame(mod, found.getPortfolioModifier());
	}
	
	@Test
	public void testHandle_PanicIfAccountIsNull() throws Exception {
		expect(row.get(eq(Spec.PORT_ACCOUNT))).andReturn(null);
		terminal.firePanicEvent(1, "Cannot handle portfolio: NULL account");
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_IfNew() throws Exception {
		expect(row.get(Spec.PORT_ACCOUNT)).andReturn(acc);
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(false);
		expect(terminal.createPortfolio(eq(acc))).andReturn(portfolio);
		mod.set(same(portfolio), same(row));
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_IfExisting() throws Exception {
		expect(row.get(Spec.PORT_ACCOUNT)).andReturn(acc);
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(portfolio);
		mod.set(same(portfolio), same(row));
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_ThrowsIfModifierThrows() throws Exception {
		ValueException expected = new ValueException("test");
		expect(row.get(Spec.PORT_ACCOUNT)).andReturn(acc);
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(portfolio);
		mod.set(same(portfolio), same(row));
		expectLastCall().andThrow(expected);
		control.replay();
		
		try {
			rowHandler.handle(row);
			fail("Expected: " + RowException.class.getSimpleName());
		} catch ( RowException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<S<EditablePortfolio>> vMod =
				new Variant<S<EditablePortfolio>>(vTerm)
			.add(mod)
			.add(control.createMock(S.class));
		Variant<?> iterator = vMod;
		int foundCnt = 0;
		PortfolioRowHandler found = null, x = null;
		do {
			x = new PortfolioRowHandler(vTerm.get(), vMod.get());
			if ( rowHandler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(mod, found.getPortfolioModifier());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(rowHandler.equals(rowHandler));
		assertFalse(rowHandler.equals(null));
		assertFalse(rowHandler.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 150349)
			.append(terminal)
			.append(mod)
			.toHashCode();
		assertEquals(hashCode, rowHandler.hashCode());
	}

}
