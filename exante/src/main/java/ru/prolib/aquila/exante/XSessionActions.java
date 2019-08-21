package ru.prolib.aquila.exante;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import quickfix.SessionID;

public class XSessionActions {
	private final Lock lock;
	private final Condition cond;
	private final Map<SessionID, List<XLogonAction>> logonActions;
	private final Map<SessionID, List<XLogoutAction>> logoutActions;
	private boolean currently_executing = false;
	
	XSessionActions(
			Map<SessionID, List<XLogonAction>> logon_actions,
			Map<SessionID, List<XLogoutAction>> logout_actions)
	{
		this.lock = new ReentrantLock();
		this.cond = lock.newCondition();
		this.logonActions = logon_actions;
		this.logoutActions = logout_actions;
	}
	
	public XSessionActions() {
		this(new HashMap<>(), new HashMap<>());
	}
	
	public void addLogonAction(SessionID session_id, XLogonAction action) {
		lock.lock();
		try {
			List<XLogonAction> actions = logonActions.get(session_id);
			if ( actions == null ) {
				actions = new ArrayList<>();
				logonActions.put(session_id, actions);
			}
			actions.add(action);
		} finally {
			lock.unlock();
		}
	}
	
	public void addLogoutAction(SessionID session_id, XLogoutAction action) {
		lock.lock();
		try {
			List<XLogoutAction> actions = logoutActions.get(session_id);
			if ( actions == null ) {
				actions = new ArrayList<>();
				logoutActions.put(session_id, actions);
			}
			actions.add(action);
		} finally {
			lock.unlock();
		}
	}
	
	public void onLogon(SessionID session_id) {
		List<XLogonAction> actions = null, actions_ro = null;
		lock.lock();
		try {
			while ( currently_executing ) {
				cond.awaitUninterruptibly();
			}
			actions = logonActions.get(session_id);
			if ( actions == null ) {
				return;
			}
			actions_ro = new ArrayList<>(actions);
			currently_executing = true;
		} finally {
			lock.unlock();
		}
		
		LinkedList<Integer> to_remove = new LinkedList<>();
		int count = actions_ro.size();
		for ( int i = 0; i < count; i ++ ) {
			XLogonAction action = actions_ro.get(i);
			if ( action.onLogon(session_id) ) {
				to_remove.addFirst(i);
			}
		}
		
		lock.lock();
		try {
			for ( int index : to_remove ) {
				actions.remove(index);
			}
			currently_executing = false;
			cond.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public void onLogout(SessionID session_id) {
		List<XLogoutAction> actions = null, actions_ro = null;
		lock.lock();
		try {
			while ( currently_executing ) {
				cond.awaitUninterruptibly();
			}
			actions = logoutActions.get(session_id);
			if ( actions == null ) {
				return;
			}
			actions_ro = new ArrayList<>(actions);
			currently_executing = true;
		} finally {
			lock.unlock();
		}
		
		LinkedList<Integer> to_remove = new LinkedList<>();
		int count = actions_ro.size();
		for ( int i = 0; i < count; i ++ ) {
			XLogoutAction action = actions_ro.get(i);
			if ( action.onLogout(session_id) ) {
				to_remove.addFirst(i);
			}
		}
		
		lock.lock();
		try {
			for ( int index : to_remove ) {
				actions.remove(index);
			}
			currently_executing = false;
			cond.signalAll();
		} finally {
			lock.unlock();
		}
	}

}
