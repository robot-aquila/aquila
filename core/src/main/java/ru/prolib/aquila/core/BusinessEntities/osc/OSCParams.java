package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.concurrent.locks.Lock;

import ru.prolib.aquila.core.EventDispatcher;

public interface OSCParams {

	String getID();

	EventDispatcher getEventDispatcher();

	OSCController getController();
	
	Lock getLock();

}

