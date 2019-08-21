package ru.prolib.aquila.exante;

import quickfix.SessionID;

public interface XLogonAction {
	boolean onLogon(SessionID session_id);
}
