package ru.prolib.aquila.data.replay;

import java.io.Closeable;
import java.io.IOException;

import ru.prolib.aquila.core.EventType;

public interface Replay extends Closeable {
	
	public EventType onStarted();
	
	public EventType onStopped();
	
	public boolean isStarted();
	
	public void start() throws IOException;
	
	public void stop();

}
