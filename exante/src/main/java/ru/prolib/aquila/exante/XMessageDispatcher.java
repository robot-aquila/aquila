package ru.prolib.aquila.exante;

import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.fix44.Message;

public class XMessageDispatcher {
	private final SessionID sessionID;
	
	public XMessageDispatcher(SessionID session_id) {
		this.sessionID = session_id;
	}
	
	public void send(Message message) throws SessionNotFound {
		Session.sendToTarget(message, sessionID);
	}

}
