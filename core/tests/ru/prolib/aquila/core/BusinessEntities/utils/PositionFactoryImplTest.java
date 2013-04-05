package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PositionImpl;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionFactoryImpl;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-03<br>
 * $Id: PositionFactoryImplTest.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class PositionFactoryImplTest {
	private IMocksControl control;
	private EventSystem eventSystem;
	private Account account;
	private EditableTerminal terminal;
	private PositionFactoryImpl factory;
	private SecurityDescriptor descr;

	@Before
	public void setUp() throws Exception {
		descr = new SecurityDescriptor("SBER", "EQBR", "RUR", SecurityType.STK);
		control = createStrictControl();
		eventSystem = control.createMock(EventSystem.class);
		account = new Account("TEST");
		terminal = control.createMock(EditableTerminal.class);		
		factory = new PositionFactoryImpl(eventSystem, account, terminal);
	}
	
	@Test
	public void testConstruct3() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(null)
			.add(eventSystem);
		Variant<Account> vAcc = new Variant<Account>(vEs)
			.add(account)
			.add(null);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vAcc)
			.add(terminal)
			.add(null);
		Variant<?> iterator = vTerm;
		int exceptionCnt = 0;
		PositionFactoryImpl found = null;
		do {
			try {
				found = new PositionFactoryImpl(vEs.get(), vAcc.get(),
						vTerm.get());
			} catch ( NullPointerException e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
		assertSame(eventSystem, found.getEventSystem());
		assertSame(account, found.getAccount());
		assertSame(terminal, found.getTerminal());
	}
	
	@Test
	public void testCreatePosition() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType etChanged = control.createMock(EventType.class);
		expect(eventSystem.createEventDispatcher("Position[" + descr + "]"))
			.andReturn(dispatcher);
		expect(eventSystem.createGenericType(dispatcher, "OnChanged"))
			.andReturn(etChanged);
		control.replay();
		
		PositionImpl position = (PositionImpl) factory.createPosition(descr);
		assertNotNull(position);
		
		control.verify();
		assertSame(etChanged, position.OnChanged());
		assertSame(dispatcher, position.getEventDispatcher());
		assertEquals(descr, position.getSecurityDescriptor());
		assertSame(account, position.getAccount());
		assertEquals(0d, position.getVarMargin(), 0.01d);
		assertEquals(0L, position.getCurrQty());
		assertEquals(0L, position.getLockQty());
		assertEquals(0L, position.getOpenQty());
		assertTrue(position.hasChanged());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(this));
		assertFalse(factory.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEt = new Variant<EventSystem>()
			.add(eventSystem)
			.add(control.createMock(EventSystem.class));
		Variant<Account> vAcc = new Variant<Account>(vEt)
			.add(account)
			.add(new Account("FOO"));
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vAcc)
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		PositionFactoryImpl found = null, x = null;
		do {
			x = new PositionFactoryImpl(vEt.get(), vAcc.get(), vTerm.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(eventSystem, found.getEventSystem());
		assertSame(account, found.getAccount());
		assertSame(terminal, found.getTerminal());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 10637)
			.append(eventSystem)
			.append(account)
			.append(terminal)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}

}
