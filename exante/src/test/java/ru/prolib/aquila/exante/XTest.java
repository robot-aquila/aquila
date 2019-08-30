package ru.prolib.aquila.exante;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import quickfix.SessionID;
import ru.prolib.aquila.exante.rh.SecurityListTestHandler;

public class XTest {
	private static org.slf4j.Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(XTest.class);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getLogger("quickfix.mina").setLevel(Level.ERROR);
		Logger.getLogger("org.apache.mina").setLevel(Level.ERROR);
	}

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Ignore
	@Test
	public void testConnection() throws Exception {
		XDataProvider xdp = new XDataProviderFactory().build(new XParams(
				new File("fixture/fix_broker.ini"),
				null
			));
		final XServiceLocator service_locator = xdp.getServiceLocator();
		CountDownLatch finished = new CountDownLatch(1);
		service_locator.getSessionActions().addLogonAction(service_locator.getBrokerSessionID(), new XLogonAction() {
			@Override
			public boolean onLogon(SessionID session_id) {
				logger.debug("Logged on");
				File report_file = new File("test-output.txt");
				report_file.delete();
				service_locator.getSecurityListMessages().list(new SecurityListTestHandler(report_file) {
					@Override
					public void close() {
						super.close();
						finished.countDown();
					}
				});
				return false;
			}
		});
		service_locator.getBrokerInitiator().start();
		finished.await(30, TimeUnit.SECONDS);
		service_locator.getBrokerInitiator().stop();
	}

}
