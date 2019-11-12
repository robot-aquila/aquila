package ru.prolib.aquila.ui.subman;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class SymbolSubscrTest {
	private IMocksControl control;
	private OSCRepository<Integer, SymbolSubscr> repoMock;
	private EventQueue queueMock;
	private SymbolSubscr service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SymbolSubscrFactory(queueMock).produce(repoMock, 76543);
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(SymbolSubscr.ID, 126)
				.withToken(SymbolSubscr.TERM_ID, "TERM-ID")
				.withToken(SymbolSubscr.SYMBOL, new Symbol("zulu"))
				.withToken(SymbolSubscr.MD_LEVEL, MDLevel.L1)
				.buildUpdate()
			);
		
		assertEquals(126, service.getID());
		assertEquals("TERM-ID", service.getTerminalID());
		assertEquals(new Symbol("zulu"), service.getSymbol());
		assertEquals(MDLevel.L1, service.getLevel());
		
		assertEquals("SymbolSubscr#76543", service.getContainerID());
		assertEquals(OSCControllerStub.class, service.getController().getClass());
	}

}
