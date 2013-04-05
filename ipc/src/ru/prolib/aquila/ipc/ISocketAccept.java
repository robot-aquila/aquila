package ru.prolib.aquila.ipc;

import java.net.Socket;

public interface ISocketAccept extends IPrimitive {
	
	public Socket getLastAccepted();
	
}
