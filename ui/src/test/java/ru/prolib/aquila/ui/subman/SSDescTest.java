package ru.prolib.aquila.ui.subman;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class SSDescTest {
	private IMocksControl control;
	private OSCRepository<Integer, SSDesc> repoMock;
	private EventQueue queueMock;
	private SubscrHandler hdrMock;
	private SSDesc service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		hdrMock = control.createMock(SubscrHandler.class);
		service = new SSDescFactory(queueMock).produce(repoMock, 76543);
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(SSDesc.ID, 126)
				.withToken(SSDesc.TERM_ID, "TERM-ID")
				.withToken(SSDesc.SYMBOL, new Symbol("zulu"))
				.withToken(SSDesc.MD_LEVEL, MDLevel.L1)
				.withToken(SSDesc.HANDLER, hdrMock)
				.buildUpdate()
			);
		
		assertEquals(126, service.getID());
		assertEquals("TERM-ID", service.getTerminalID());
		assertEquals(new Symbol("zulu"), service.getSymbol());
		assertEquals(MDLevel.L1, service.getLevel());
		assertEquals(hdrMock, service.getHandler());
		
		assertEquals("SSDesc#76543", service.getContainerID());
		assertEquals(OSCControllerStub.class, service.getController().getClass());
	}

}
