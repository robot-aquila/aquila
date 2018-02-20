package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;

public class CategoryAxisDriverProxyImpl implements CategoryAxisDriverProxy {
	private final CategoryAxisDriver driver;
	private CategoryAxisDisplayMapper mapper;
	
	public CategoryAxisDriverProxyImpl(CategoryAxisDriver driver) {
		this.driver = driver;
	}

	@Override
	public void registerForRulers(ChartSpaceManager spaceManager) {
		spaceManager.registerAxis(driver);
	}

	@Override
	public synchronized CategoryAxisDisplayMapper getCurrentMapper() {
		if ( mapper == null ) {
			throw new IllegalStateException("Display mapper not defined");
		}
		return mapper;
	}
	
	public synchronized void setCurrentMapper(CategoryAxisDisplayMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public CategoryAxisRulerRenderer getRulerRenderer(String rendererID) {
		return (CategoryAxisRulerRenderer) driver.getRenderer(rendererID);
	}

}
