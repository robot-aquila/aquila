package ru.prolib.aquila.probe.internal;

import ru.prolib.aquila.core.data.Tick;

public interface SecurityTasks {

	public void doInitialTask(Tick firstTick);
	
	public void doFinalTask(Tick lastTick);

	public void doDailyTask(Tick prevDateTick, Tick nextDateTick);
	
	public Runnable createTask(Tick tick);
	
}
