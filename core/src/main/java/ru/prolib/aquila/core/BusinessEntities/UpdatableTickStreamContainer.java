package ru.prolib.aquila.core.BusinessEntities;


public interface UpdatableTickStreamContainer extends TickStreamContainer {

	public void update(Tick tick);
	
}
