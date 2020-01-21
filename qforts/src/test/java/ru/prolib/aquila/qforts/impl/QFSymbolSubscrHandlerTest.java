package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.utils.Variant;

public class QFSymbolSubscrHandlerTest {
	private IMocksControl control;
	private QFReactor reactorMock1, reactorMock2;
	private EditableSecurity securityMock1, securityMock2;
	private QFSymbolSubscrHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		reactorMock1 = control.createMock(QFReactor.class);
		reactorMock2 = control.createMock(QFReactor.class);
		securityMock1 = control.createMock(EditableSecurity.class);
		securityMock2 = control.createMock(EditableSecurity.class);
		service = new QFSymbolSubscrHandler(reactorMock1, securityMock1, MDLevel.L2);
	}
	
	@Test
	public void testGetters() {
		assertEquals(reactorMock1, service.getReactor());
		assertEquals(securityMock1, service.getSecurity());
		assertEquals(MDLevel.L2, service.getLevel());
		assertFalse(service.isClosed());
	}
	
	@Test
	public void testClose() {
		reactorMock1.unsubscribe(securityMock1, MDLevel.L2);
		control.replay();
		
		service.close();
		
		control.verify();
		
		service.close();
		service.close();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		Variant<QFReactor> vRea = new Variant<>(reactorMock1, reactorMock2);
		Variant<EditableSecurity> vSec = new Variant<>(vRea, securityMock1, securityMock2);
		Variant<MDLevel> vLev = new Variant<>(vSec, MDLevel.L2, MDLevel.L1_BBO);
		Variant<Boolean> vCls = new Variant<>(vLev, false, true),
				vConf = new Variant<>(vCls, true, false);
		Variant<?> iterator = vConf;
		int found_cnt = 0;
		QFSymbolSubscrHandler x, found = null;
		do {
			x = new QFSymbolSubscrHandler(vRea.get(), vSec.get(), vLev.get(), vConf.get());
			if ( vCls.get() ) {
				x.close();
			}
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(reactorMock1, found.getReactor());
		assertEquals(securityMock1, found.getSecurity());
		assertEquals(MDLevel.L2, found.getLevel());
		assertFalse(found.isClosed());
		assertTrue(found.getConfirmation().get(1, TimeUnit.SECONDS));
	}

}
