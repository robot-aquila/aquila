package ru.prolib.aquila.ChaosTheory;

public class AssetLazyLoadException extends AssetException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Конструктор
	 * @param assetCode
	 */
	public AssetLazyLoadException(String code, Throwable e) {
		super("Asset lazy initialization failed: " + code, e);
	}

}
