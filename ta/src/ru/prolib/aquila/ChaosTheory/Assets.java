package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.util.Observable;

public interface Assets extends Observable {
	
	public Asset getByCode(String assetCode) throws AssetsException;

	boolean exists(String assetCode);

}
