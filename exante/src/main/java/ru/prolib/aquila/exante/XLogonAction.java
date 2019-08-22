package ru.prolib.aquila.exante;

import quickfix.SessionID;

public interface XLogonAction {
	
	/**
	 * Process logging on.
	 * <p>
	 * @param session_id - session ID which is logged on
	 * @return true if work is done and action should be removed to avoid consecutive calls, false otherwise
	 */
	boolean onLogon(SessionID session_id);
	
}
