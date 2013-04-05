package ru.prolib.aquila.ChaosTheory;

public class RiskManagerMultiplier implements RiskManager {
	private final RiskManager manager;
	private final int multiplier;
	
	public RiskManagerMultiplier(RiskManager manager, int multiplier) {
		super();
		this.manager = manager;
		this.multiplier = multiplier;
	}

	@Override
	public int getLongSize(double price) {
		return manager.getLongSize(price) * multiplier;
	}

	@Override
	public int getShortSize(double price) {
		return manager.getShortSize(price) * multiplier;
	}

}
