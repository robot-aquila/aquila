package ru.prolib.aquila.exante;

import quickfix.SessionID;

public interface XLogoutAction {
	boolean onLogout(SessionID session_id);
}
