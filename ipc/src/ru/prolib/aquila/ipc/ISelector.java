package ru.prolib.aquila.ipc;

import java.nio.channels.Selector;

public interface ISelector extends IPrimitive {
	
	public Selector getSelector();

}
