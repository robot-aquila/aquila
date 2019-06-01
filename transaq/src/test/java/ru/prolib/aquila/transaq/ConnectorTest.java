package ru.prolib.aquila.transaq;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;

public class ConnectorTest {
	
	static class XHandler extends JTransaqHandler {
		
		@Override
		public boolean Handle(String data) {
			System.out.println("XHandler IN> " + data);
			return true;
		}

	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FileUtils.deleteQuietly(new File("bin"));
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConnect() throws Exception {
		XHandler handler = new XHandler();
		Wini ini = new Wini(new File("fixture/transaq.ini"));
		Section config = ini.get("transaq-test");
		JTransaqServer server = new JTransaqServer(handler);
		server.Initialize(config.get("log_path"), Integer.parseInt(config.get("log_level")));
		try {
			server.SendCommand("<command id=\"connect\">"
				+ "<login>" + config.get("login") + "</login>"
				+ "<password>" + config.get("password") + "</password>"
				+ "<host>" + config.get("host") + "</host>"
				+ "<port>" + config.get("port") + "</port>"
				+ "<language>en</language>"
				+ "<autopos>true</autopos>"
				+ "<micex_registers>true</micex_registers>"
				+ "<milliseconds>true</milliseconds>"
				+ "<utc_time>false</utc_time>"
				+ "<rqdelay>1000</rqdelay>"
				+ "<push_u_limits>5</push_u_limits>"
				+ "<push_pos_equity>5</push_pos_equity>"
				+ "</command>");
		} catch ( Throwable t ) {
			System.out.println("Error: " + t);
			t.printStackTrace();
		}
		Thread.sleep(20000L);
		
		server.UnInitialize();
	}

}
