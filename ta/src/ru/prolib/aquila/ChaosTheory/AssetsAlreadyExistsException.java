package ru.prolib.aquila.ChaosTheory;

public class AssetsAlreadyExistsException extends AssetsException {
	private static final long serialVersionUID = 1L;

	/**
	 * Конструктор
	 * @param assetCode
	 */
	public AssetsAlreadyExistsException(String assetCode) {
		super("Asset already exists: " + assetCode);
	}

}
