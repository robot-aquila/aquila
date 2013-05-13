package ru.prolib.aquila.core.BusinessEntities.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-17<br>
 * $Id: PositionRowHandlerTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PositionRowHandlerTest {
	private final SecurityDescriptor descr =
			new SecurityDescriptor("SB","G","USD",SecurityType.BOND);
	private final Account acc = new Account("LX000");
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditablePortfolio portfolio;
	private EditablePosition position;
	private Security security;
	private S<EditablePosition> mod;
	private PositionRowHandler rowHandler;
	private Row row;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		portfolio = control.createMock(EditablePortfolio.class);
		position = control.createMock(EditablePosition.class);
		mod = control.createMock(S.class);
		security = control.createMock(Security.class);
		rowHandler = new PositionRowHandler(terminal, mod);
		row = control.createMock(Row.class);
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(null);
		Variant<S<EditablePosition>> vMod =
				new Variant<S<EditablePosition>>(vTerm)
			.add(mod)
			.add(null);
		Variant<?> iterator = vMod;
		int exceptions = 0;
		int index = 0;
		do {
			String msg = "At #" + index;
			try {
				rowHandler = new PositionRowHandler(vTerm.get(), vMod.get());
				assertSame(msg, terminal, rowHandler.getTerminal());
				assertSame(msg, mod, rowHandler.getPositionModifier());
			} catch ( NullPointerException e ) {
				exceptions ++;
			}
			index ++;
		} while ( iterator.next() );
		assertEquals(vMod.count() - 1, exceptions);
	}
	
	@Test
	public void testHandle_PanicIfAccountIsNull() throws Exception {
		expect(row.get("POS_ACC")).andReturn(null);
		expect(row.get("POS_SECDESCR")).andReturn(descr);
		terminal.firePanicEvent(eq(1),
				eq("Cannot handle position: account is NULL"));
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_PanicIfDescriptorIsNull() throws Exception {
		expect(row.get("POS_ACC")).andReturn(acc);
		expect(row.get("POS_SECDESCR")).andReturn(null);
		terminal.firePanicEvent(eq(1),
				eq("Cannot handle position: security descriptor is NULL"));
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_PanicIfSecurityNotExists() throws Exception {
		expect(row.get("POS_ACC")).andReturn(acc);
		expect(row.get("POS_SECDESCR")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(false);
		terminal.firePanicEvent(eq(1),
				eq("Cannot handle position: security not exists: "),
				aryEq(new Object[] { descr }));
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test (expected=RuntimeException.class)
	public void testHandle_ThrowsIfPortfolioNotExists() throws Exception {
		expect(row.get("POS_ACC")).andReturn(acc);
		expect(row.get("POS_SECDESCR")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc)))
			.andThrow(new PortfolioNotExistsException());
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test (expected=RuntimeException.class)
	public void testHandle_ThrowsIfSecurityNotExists() throws Exception {
		expect(row.get("POS_ACC")).andReturn(acc);
		expect(row.get("POS_SECDESCR")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(portfolio);
		expect(terminal.getSecurity(descr))
			.andThrow(new SecurityNotExistsException(descr));
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_Ok() throws Exception {
		expect(row.get("POS_ACC")).andReturn(acc);
		expect(row.get("POS_SECDESCR")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(portfolio);
		expect(terminal.getSecurity(descr)).andReturn(security);
		expect(portfolio.getEditablePosition(security)).andReturn(position);
		mod.set(same(position), same(row));
		control.replay();
		
		rowHandler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_ThrowsIfModifierThrows() throws Exception {
		ValueException expected = new ValueException("test");
		expect(row.get("POS_ACC")).andReturn(acc);
		expect(row.get("POS_SECDESCR")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(portfolio);
		expect(terminal.getSecurity(descr)).andReturn(security);
		expect(portfolio.getEditablePosition(security)).andReturn(position);
		mod.set(same(position), same(row));
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
		Variant<S<EditablePosition>> vMod =
				new Variant<S<EditablePosition>>(vTerm)
			.add(mod)
			.add(control.createMock(S.class));
		int foundCnt = 0;
		PositionRowHandler found = null, x = null;
		do {
			x = new PositionRowHandler(vTerm.get(), vMod.get());
			if ( rowHandler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vMod.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(mod, found.getPositionModifier());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(rowHandler.equals(rowHandler));
		assertFalse(rowHandler.equals(null));
		assertFalse(rowHandler.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 153109)
			.append(terminal)
			.append(mod)
			.toHashCode();
		assertEquals(hashCode, rowHandler.hashCode());
	}
	
}
