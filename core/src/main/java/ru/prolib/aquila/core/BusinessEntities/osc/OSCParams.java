package ru.prolib.aquila.core.BusinessEntities.osc;

import ru.prolib.aquila.core.EventDispatcher;

public interface OSCParams {

	String getID();

	EventDispatcher getEventDispatcher();

	OSCController getController();

}

