package ru.prolib.aquila.web.utils.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import ru.prolib.aquila.web.utils.HTTPAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.WebDriverFactory;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;

public class FidexpFactorySTDTest {
	private IMocksControl control;
	private WebDriverFactory driverFactoryMock;
	private HTTPAttachmentManagerFactory attMgrFactoryMock;
	private WebDriver driverMock;
	private HTTPAttachmentManager attMgrMock;
	private FidexpFactorySTD service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		driverFactoryMock = control.createMock(WebDriverFactory.class);
		attMgrFactoryMock = control.createMock(HTTPAttachmentManagerFactory.class);
		driverMock = control.createMock(WebDriver.class);
		attMgrMock = control.createMock(HTTPAttachmentManager.class);
		service = new FidexpFactorySTD(driverFactoryMock, attMgrFactoryMock);
	}
	
	@Test
	public void testCreateInstance() {
		expect(driverFactoryMock.createWebDriver()).andReturn(driverMock);
		expect(attMgrFactoryMock.createAttachmentManager(driverMock)).andReturn(attMgrMock);
		control.replay();
		
		Fidexp actual = service.createInstance();
		
		assertSame(driverMock, actual.getWebDriver());
		assertSame(attMgrMock, actual.getAttachmentManager());
	}

}
