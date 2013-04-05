package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityImpl;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.utils.SecurityFactoryImpl;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-07-05<br>
 * $Id: SecurityFactoryImplTest.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class SecurityFactoryImplTest {
	private IMocksControl control;
	private EventSystem eventSystem;
	private Terminal terminal;
	private SecurityFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventSystem = control.createMock(EventSystem.class);
		terminal = control.createMock(Terminal.class);
		factory = new SecurityFactoryImpl(eventSystem, terminal);
	}
	
	@Test
	public void testConstruct() throws Exception {
		Object fixture[][] = {
				// event sys, terminal, exception?
				{ eventSystem, terminal, false },
				{ eventSystem, null, true },
				{ null, terminal, true },
				{ null, null, true }
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			eventSystem = (EventSystem) fixture[i][0];
			terminal = (Terminal) fixture[i][1];
			boolean exception = false;
			try {
				factory = new SecurityFactoryImpl(eventSystem, terminal);
			} catch ( NullPointerException e ) {
				exception = true;
			}
			assertEquals(msg, (Boolean) fixture[i][2], exception);
			if ( ! exception ) {
				assertSame(msg, eventSystem, factory.getEventSystem());
				assertSame(msg, terminal, factory.getTerminal());
			}
		}
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfEventSystemIsNull() throws Exception {
		new SecurityFactoryImpl(null, terminal);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTerminalIsNull() throws Exception {
		new SecurityFactoryImpl(eventSystem, null);
	}
	
	@Test
	public void testCreateSecurity_Ok() throws Exception {
		SecurityDescriptor descr =
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType etNewTrade = control.createMock(EventType.class);
		EventType etChanged = control.createMock(EventType.class);
		expect(eventSystem.createEventDispatcher("Security[" + descr + "]"))
			.andReturn(dispatcher);
		expect(eventSystem.createGenericType(dispatcher, "OnChanged"))
			.andReturn(etChanged);
		expect(eventSystem.createGenericType(dispatcher, "OnTrade"))
			.andReturn(etNewTrade);
		control.replay();
		

		SecurityImpl sec = (SecurityImpl) factory.createSecurity(descr);
		assertNotNull(sec);
		
		control.verify();
		assertEquals("AAPL", sec.getCode());
		assertEquals("SMART", sec.getClassCode());
		assertEquals(descr, sec.getDescriptor());
		assertSame(terminal, sec.getTerminal());
		assertSame(dispatcher, sec.getEventDispatcher());
		assertSame(etChanged, sec.OnChanged());
		assertSame(etNewTrade, sec.OnTrade());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(eventSystem)
			.add(control.createMock(EventSystem.class));
		Variant<Terminal> vTerm = new Variant<Terminal>(vEs)
			.add(control.createMock(Terminal.class))
			.add(terminal);
		int foundCnt = 0;
		SecurityFactoryImpl found = null, x = null;
		do {
			x = new SecurityFactoryImpl(vEs.get(), vTerm.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vTerm.next() );
		assertEquals(1, foundCnt);
		assertSame(eventSystem, found.getEventSystem());
		assertSame(terminal, found.getTerminal());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 124513)
			.append(eventSystem)
			.append(terminal)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}

}
