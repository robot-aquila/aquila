package ru.prolib.aquila.ChaosTheory;

public class AssetUpdateException extends AssetException {
	private static final long serialVersionUID = 1L;
	
	public AssetUpdateException(Throwable t) {
		super("Asset update failed: " + t.getMessage(), t);
	}

}
