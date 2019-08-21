package ru.prolib.aquila.exante;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.field.CFICode;
import quickfix.field.MsgType;
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.Symbol;
import quickfix.fix44.Message;
import quickfix.fix44.Message.Header;
import quickfix.fix44.SecurityListRequest;
import ru.prolib.aquila.exante.rh.SecurityListTestHandler;

public class XTest {
	
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
	
	private List<SessionID> getSessionIDs(SessionSettings settings) {
		Iterator<SessionID> it = settings.sectionIterator();
		List<SessionID> result = new ArrayList<>();
		while ( it.hasNext() ) {
			result.add(it.next());
		}
		return result;
	}
	
	//@Ignore
	@Test
	public void testConnection() throws Exception {
		SessionSettings session_settings = new SessionSettings("fixture/fix_broker.ini");
		List<SessionID> session_ids = getSessionIDs(session_settings);
		assertEquals(1, session_ids.size());
		Map<SessionID, String> session_passwords = new HashMap<>();
		for ( SessionID session_id : session_ids ) {
			session_passwords.put(session_id, session_settings.getString(session_ids.get(0), "password"));
		}
		
		XSecurityListMessages security_list_messages = new XSecurityListMessages(session_ids.get(0));
		Initiator initiator = new SocketInitiator(
				new XApplication(session_passwords, security_list_messages),
				new FileStoreFactory(session_settings),
				session_settings,
				new FileLogFactory(session_settings),
				new DefaultMessageFactory()
			);
		initiator.start();

		Thread.sleep(2000L);
		
		File report_file = new File("test-output.txt");
		report_file.delete();
		security_list_messages.list(new SecurityListTestHandler(report_file));

		/*
		SessionID session_id_trade = ;
		SecurityListRequest message = new SecurityListRequest();
		message.set(new SecurityReqID("5"));

		// All
		message.set(new SecurityListRequestType(SecurityListRequestType.ALL_SECURITIES));
		
		// CFI
		//message.set(new SecurityListRequestType(SecurityListRequestType.SECURITYTYPE_AND_OR_CFICODE));
		//message.set(new CFICode("ESXXXX"));
		
		// Symbol
		//message.set(new SecurityListRequestType(SecurityListRequestType.SYMBOL));
		//message.set(new Symbol("AAPL"));
		
		Session.sendToTarget(message, session_id_trade);
		*/
		
		Thread.sleep(10000L);
		initiator.stop();
	}

	@Test
	public void test() {
		//fail("Not yet implemented");
	}

}
