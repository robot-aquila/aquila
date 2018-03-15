package ru.prolib.aquila.utils.experimental.chart.axis;

/**
 * Prepared ruler factory based on value axis driver and its data.
 */
public class PreparedRulerFactoryVAD implements PreparedRulerFactory {
	private final ValueAxisDriver driver;
	private final ValueAxisDisplayMapper mapper;
	private final Object device;
	
	public PreparedRulerFactoryVAD(ValueAxisDriver driver, ValueAxisDisplayMapper mapper, Object device) {
		this.driver = driver;
		this.mapper = mapper;
		this.device = device;
	}

	@Override
	public PreparedRuler prepareRuler(RulerRendererID rulerRendererID) {
		String axisID = rulerRendererID.getAxisID();
		if ( ! axisID.equals(driver.getID()) ) {
			throw new IllegalArgumentException("This factory does not support axis: " + axisID);
		}
		return ((ValueAxisRulerRenderer) driver.getRenderer(rulerRendererID.getRendererID()))
				.prepareRuler(mapper, device);
	}

}
