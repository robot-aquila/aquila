package ru.prolib.aquila.ta.ds.quik;

import ru.prolib.aquila.rxltdde.Xlt;

public interface RXltDdeTableHandler {
	
	public void onTable(Xlt.ITable table);
	
	public void registerHandler(RXltDdeDispatcher dispatcher);
	
	public void unregisterHandler(RXltDdeDispatcher dispatcher);
	
}