package ru.prolib.aquila.web.utils.ahc;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class AHCAttachmentManagerFactoryTest {
	private IMocksControl control;
	private AHCClientFactoryImpl clientFactoryMock;
	private AHCAttachmentManagerFactory service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		clientFactoryMock = control.createMock(AHCClientFactoryImpl.class);
		service = new AHCAttachmentManagerFactory(clientFactoryMock);
	}
	
	@Test
	public void testCtor0() {
		service = new AHCAttachmentManagerFactory();
		
		assertEquals(AHCClientFactoryImpl.class, service.getClientFactory().getClass());
	}
	
	@Test
	public void testCtor1() {
		assertSame(clientFactoryMock, service.getClientFactory());
	}
	
	@Test
	public void testCreateAttachmentManager() {
		AHCAttachmentManager actual = (AHCAttachmentManager) service.createAttachmentManager(null);
		
		assertSame(clientFactoryMock, actual.getClientFactory());
	}

	@Test
	public void testLoadIni() throws Exception {
		expect(clientFactoryMock.loadIni(new File("foo/bar.ini"))).andReturn(clientFactoryMock);
		control.replay();
		
		assertSame(service, service.loadIni(new File("foo/bar.ini")));
		
		control.verify();
	}

}
