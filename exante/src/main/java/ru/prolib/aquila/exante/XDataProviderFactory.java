package ru.prolib.aquila.exante;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

public class XDataProviderFactory {

	private List<SessionID> getSessionIDs(SessionSettings settings) {
		Iterator<SessionID> it = settings.sectionIterator();
		List<SessionID> result = new ArrayList<>();
		while ( it.hasNext() ) {
			result.add(it.next());
		}
		return result;
	}
	
	public XDataProvider build(XParams params) throws ConfigError {
		String settings_filename = params.getSessionSettings().getAbsolutePath();
		SessionSettings session_settings = new SessionSettings(settings_filename);
		List<SessionID> session_ids = getSessionIDs(session_settings);
		SessionID broker_session_id = session_ids.get(0);
		Map<SessionID, String> session_passwords = new HashMap<>();
		session_passwords.put(broker_session_id, session_settings.getString(broker_session_id, "password"));
		XSymbolRepository symbols = new XSymbolRepository();
		XSessionActions session_actions = new XSessionActions();
		XSecurityListMessages security_list_messages = new XSecurityListMessages(broker_session_id);
		XAccountSummaryMessages account_summary_messages = new XAccountSummaryMessages(broker_session_id);
		XOrdersMessages orders_messages = new XOrdersMessages(symbols, broker_session_id);
		Initiator broker_initiator = new SocketInitiator(
				new XApplication(
						session_passwords,
						session_actions,
						security_list_messages,
						account_summary_messages,
						orders_messages
					),
				new FileStoreFactory(session_settings),
				session_settings,
				new FileLogFactory(session_settings),
				new DefaultMessageFactory()
			);
		XServiceLocator service_locator = new XServiceLocator(
				params,
				symbols,
				broker_session_id,
				session_actions,
				security_list_messages,
				account_summary_messages,
				orders_messages,
				broker_initiator
			);
		return new XDataProvider(service_locator);
	}

}
