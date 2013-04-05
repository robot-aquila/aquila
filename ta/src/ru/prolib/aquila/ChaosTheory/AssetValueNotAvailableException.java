package ru.prolib.aquila.ChaosTheory;

public class AssetValueNotAvailableException extends AssetException {
	private static final long serialVersionUID = 1L;
	
	public AssetValueNotAvailableException(String attr) {
		super("Asset value not available: " + attr);
	}

}
