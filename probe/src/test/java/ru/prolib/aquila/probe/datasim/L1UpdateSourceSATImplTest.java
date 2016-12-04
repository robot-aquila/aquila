package ru.prolib.aquila.probe.datasim;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.HashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.ListenOnce;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.data.L1UpdateSource;

public class L1UpdateSourceSATImplTest {
	private static Symbol symbol1, symbol2, symbol3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol1 = new Symbol("AAPL");
		symbol2 = new Symbol("MSFT");
		symbol3 = new Symbol("SBER");
	}
	
	private IMocksControl control;
	private L1UpdateSource basicSourceMock;
	private Set<EditableSecurity> pending;
	private L1UpdateSourceSATImpl source;
	private EditableTerminal terminal;
	private EditableSecurity security1, security2, security3;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		basicSourceMock = control.createMock(L1UpdateSource.class);
		pending = new HashSet<>();
		source = new L1UpdateSourceSATImpl(basicSourceMock, pending);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		security1 = terminal.getEditableSecurity(symbol1);
		security2 = terminal.getEditableSecurity(symbol2);
		security3 = terminal.getEditableSecurity(symbol3);
	}
	
	@Test
	public void testClose() throws Exception {
		pending.add(security1);
		pending.add(security2);
		pending.add(security3);
		control.replay();
		
		source.close();
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSubscribeL1_ThrowsIfNotASecurity() throws Exception {
		L1UpdateConsumer consumerMock = control.createMock(L1UpdateConsumer.class);
		control.replay();
		
		source.subscribeL1(symbol1, consumerMock);
	}
	
	@Test
	public void testSubscribeL1_IfSecurityNotAvailable() throws Exception {
		control.replay();
		
		source.subscribeL1(symbol2, security2);
		
		control.verify();
		assertTrue(pending.contains(security2));
		EventListener actual = security2.onAvailable().getAsyncListeners().get(0);
		ListenOnce expected = new ListenOnce(security2.onAvailable(), source);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSubscribeL1_IfSecurityAvailable() throws Exception {
		security3.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.DISPLAY_NAME, "foo")
			.withToken(SecurityField.SCALE, 2)
			.withToken(SecurityField.LOT_SIZE, 1)
			.withToken(SecurityField.TICK_SIZE, 0.01d)
			.withToken(SecurityField.TICK_VALUE, 0.01d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 100.0d)
			.buildUpdate());
		basicSourceMock.subscribeL1(symbol3, security3);
		control.replay();
		
		source.subscribeL1(symbol3, security3);
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testUnsubscribeL1_ThrowsIfNotASecurity() throws Exception {
		L1UpdateConsumer consumerMock = control.createMock(L1UpdateConsumer.class);
		control.replay();
		
		source.unsubscribeL1(symbol1, consumerMock);
	}
	
	@Test
	public void testUnsubscribeL1_IfPendingSubscription() throws Exception {
		pending.add(security2);
		control.replay();
		
		source.unsubscribeL1(symbol2, security2);
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}
	
	@Test
	public void testUnsubscribeL1_IfNotPendingSubscription() throws Exception {
		basicSourceMock.unsubscribeL1(symbol1, security1);
		control.replay();
		
		source.unsubscribeL1(symbol1, security1);
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}

}
