package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class SymbolSubscrCounterTest {
	private IMocksControl control;
	private OSCRepository<Symbol, SymbolSubscrCounter> repoMock;
	private EventQueue queueMock;
	private SymbolSubscrCounter service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SymbolSubscrCounterFactory(queueMock).produce(repoMock, new Symbol("ZULU"));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(Field.SYMBOL, new Symbol("BABBA"))
				.withToken(Field.NUM_L0, 25)
				.withToken(Field.NUM_L1_BBO, 15)
				.withToken(Field.NUM_L1, 7)
				.withToken(Field.NUM_L2, 0)
				.buildUpdate()
			);
		
		assertEquals(new Symbol("BABBA"), service.getSymbol());
		assertEquals(25, service.getNumL0());
		assertEquals(15, service.getNumL1_BBO());
		assertEquals( 7, service.getNumL1());
		assertEquals( 0, service.getNumL2());
		
		assertEquals("SymbolSubscrCounter#ZULU", service.getContainerID());
		assertEquals(OSCControllerStub.class, service.getController().getClass());
	}

}
