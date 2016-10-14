package ru.prolib.aquila.core.BusinessEntities;

public interface DeltaUpdateConsumer {
	
	public abstract void consume(DeltaUpdate update);

}
