package ru.prolib.aquila.ChaosTheory;

public class AssetsNotExistsException extends AssetsException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Конструктор
	 * @param assetCode
	 */
	public AssetsNotExistsException(String assetCode) {
		super("Asset not exists: " + assetCode);
	}

}
