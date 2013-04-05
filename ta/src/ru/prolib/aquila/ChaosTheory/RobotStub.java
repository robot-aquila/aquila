package ru.prolib.aquila.ChaosTheory;

public class RobotStub implements Robot {
	private final ServiceLocator locator;
	
	public RobotStub(ServiceLocator locator) {
		super();
		this.locator = locator;
	}

	@Override
	public void init() throws Exception {
		locator.getMarketData().prepare();
	}

	@Override
	public void pass() throws Exception {
		locator.getMarketData().update();
	}

	@Override
	public void clean() {

	}

}
