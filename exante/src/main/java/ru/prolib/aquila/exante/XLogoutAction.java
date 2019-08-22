package ru.prolib.aquila.exante;

import quickfix.SessionID;

public interface XLogoutAction {
	
	/**
	 * Process logging out.
	 * <p>
	 * @param session_id - session ID which is logged out
	 * @return true if work is done and action should be removed to avoid consecutive calls, false otherwise
	 */
	boolean onLogout(SessionID session_id);
	
}
