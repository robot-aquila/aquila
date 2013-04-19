package ru.prolib.aquila.core.BusinessEntities.row;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.SecurityRowHandler;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-13<br>
 * $Id: SecurityRowHandlerTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class SecurityRowHandlerTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditableSecurity sec;
	private Row row;
	private SecurityRowHandler handler;
	private SecurityDescriptor descr;
	private S<EditableSecurity> mod;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		sec = control.createMock(EditableSecurity.class);
		row = control.createMock(Row.class);
		mod = control.createMock(S.class);
		handler = new SecurityRowHandler(terminal, mod);
		descr = new SecurityDescriptor("HE", "LO", "USD", SecurityType.CASH);
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(null);
		Variant<S<EditableSecurity>> vMod =
				new Variant<S<EditableSecurity>>(vTerm)
			.add(mod)
			.add(null);
		Variant<?> iterator = vMod;
		int exceptionCnt = 0;
		do {
			try {
				handler = new SecurityRowHandler(vTerm.get(), vMod.get());
				assertSame(terminal, handler.getTerminal());
				assertSame(mod, handler.getModifier());
			} catch ( NullPointerException e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
	}
	
	@Test
	public void testHandle_PanicIfDescrIsNull() throws Exception {
		expect(row.get("SEC_DESCR")).andReturn(null);
		terminal.firePanicEvent(1,"Cannot handle security: descriptor is NULL");
		control.replay();
		
		handler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_Ok() throws Exception {
		expect(row.get("SEC_DESCR")).andReturn(descr);
		expect(terminal.getEditableSecurity(same(descr))).andReturn(sec);
		mod.set(same(sec), same(row));
		control.replay();
		
		handler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_ThrowsIfModifierThrows() throws Exception {
		ValueException expected = new ValueException("test");
		expect(row.get("SEC_DESCR")).andReturn(descr);
		expect(terminal.getEditableSecurity(same(descr))).andReturn(sec);
		mod.set(same(sec), same(row));
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

	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<S<EditableSecurity>> vMod =
				new Variant<S<EditableSecurity>>(vTerm)
			.add(mod)
			.add(control.createMock(S.class));
		int foundCnt = 0;
		SecurityRowHandler found = null, x = null;
		do {
			x = new SecurityRowHandler(vTerm.get(), vMod.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vMod.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(mod, found.getModifier());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(this));
		assertFalse(handler.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 155847)
			.append(terminal)
			.append(mod)
			.toHashCode();
		assertEquals(hashCode, handler.hashCode());
	}

}
