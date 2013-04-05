package ru.prolib.aquila.ChaosTheory;


public class AssetsLazy extends AssetsImpl {
	private final Assets storage;
	
	public AssetsLazy(Assets assets) {
		super();
		this.storage = assets;
	}

	@Override
	public Asset getByCode(String assetCode) throws AssetsException {
		if ( ! super.exists(assetCode) ) {
			add(new AssetLazy(storage, assetCode));
		}
		return super.getByCode(assetCode);		
	}

	@Override
	public boolean exists(String assetCode) {
		return true;
	}

}
