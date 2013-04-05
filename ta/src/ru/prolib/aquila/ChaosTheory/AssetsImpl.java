package ru.prolib.aquila.ChaosTheory;

import java.util.HashMap;
import java.util.Observable;


public class AssetsImpl extends Observable implements Assets {
	private final HashMap<String, Asset> assets;
	
	public AssetsImpl() {
		super();
		assets = new HashMap<String, Asset>();
	}

	@Override
	public synchronized Asset getByCode(String assetCode)
		throws AssetsException
	{
		if ( ! assets.containsKey(assetCode) ) {
			throw new AssetsNotExistsException(assetCode);
		}
		return assets.get(assetCode);
	}
	
	@Override
	public synchronized boolean exists(String assetCode) {
		return assets.containsKey(assetCode);
	}
	
	public synchronized void add(Asset asset)
		throws AssetsAlreadyExistsException
	{
		if ( assets.containsKey(asset.getAssetCode()) ) {
			throw new AssetsAlreadyExistsException(asset.getAssetCode());
		}
		assets.put(asset.getAssetCode(), asset);
		setChanged();
		notifyObservers(asset);
	}
	
	public synchronized void remove(String code) {
		assets.remove(code);
	}

}
