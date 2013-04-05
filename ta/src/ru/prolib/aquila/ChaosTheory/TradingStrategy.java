package ru.prolib.aquila.ChaosTheory;

/**
 * Каркас торговой стратегии.
 */
abstract public class TradingStrategy {
	protected final ServiceLocator locator;
	protected final PortfolioDriver driver;
	
	public TradingStrategy(ServiceLocator locator,
						   PortfolioDriver driver)

	{
		super();
		this.locator = locator;
		this.driver = driver;
	}
	
	abstract public void prepare() throws Exception; 
	
	abstract public void nextPass() throws Exception;
	
	abstract public void clean();
	
}