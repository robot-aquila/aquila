package ru.prolib.aquila.ib.subsys.security;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.TickType;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.ib.event.IBEventTick;
import ru.prolib.aquila.ib.subsys.security.IBSecurityModifierOfTick;

/**
 * 2012-12-23<br>
 * $Id: IBSecurityModifierOfTickTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBSecurityModifierOfTickTest {
	private static IMocksControl control;
	private static EventType type;
	private static EditableSecurity sec;
	private static IBSecurityModifierOfTick mod;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		sec = control.createMock(EditableSecurity.class);
		mod = new IBSecurityModifierOfTick();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}

	@Test
	public void testEquals() throws Exception {
		assertTrue(mod.equals(mod));
		assertTrue(mod.equals(new IBSecurityModifierOfTick()));
		assertFalse(mod.equals(null));
		assertFalse(mod.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121223, 95157)
			.toHashCode(), mod.hashCode());
	}
	
	@Test
	public void testSet() throws Exception {
		Object value[] = {
			null,
			this,
			new IBEventTick(type, 0, TickType.ASK, 200.01d),
			new IBEventTick(type, 0, TickType.ASK_SIZE, 210.00d),
			new IBEventTick(type, 0, TickType.BID, 570.29d),
			new IBEventTick(type, 0, TickType.BID_SIZE, 180.00d),
			new IBEventTick(type, 0, TickType.AUCTION_PRICE, 0.00d),
			new IBEventTick(type, 0, TickType.LAST, 212.05d),
			new IBEventTick(type, 0, TickType.OPEN, 118.01d),
			new IBEventTick(type, 0, TickType.HIGH, 112.09d),
			new IBEventTick(type, 0, TickType.LOW, 78.19d),
			new IBEventTick(type, 0, TickType.CLOSE, 987.1d),
		};
		Runnable expect[] = {
			null,
			null,
			new Runnable(){public void run() { sec.setAskPrice(eq(200.01d));}},
			new Runnable(){public void run() { sec.setAskSize(eq(210l)); }},
			new Runnable(){public void run() { sec.setBidPrice(eq(570.29d));}},
			new Runnable(){public void run() { sec.setBidSize(eq(180l)); }},
			null,
			new Runnable(){public void run() { sec.setLastPrice(212.05d); }},
			new Runnable(){public void run() { sec.setOpenPrice(118.01d); }},
			new Runnable(){public void run() { sec.setHighPrice(112.09d); }},
			new Runnable(){public void run() { sec.setLowPrice(78.19d); }},
			new Runnable(){public void run() { sec.setClosePrice(987.1d); }},
		};
		for ( int i = 0; i < value.length; i ++ ) {
			control.resetToStrict();
			if ( expect[i] != null ) { expect[i].run(); }
			control.replay();
			mod.set(sec, value[i]);
			control.verify();
		}
	}

}
