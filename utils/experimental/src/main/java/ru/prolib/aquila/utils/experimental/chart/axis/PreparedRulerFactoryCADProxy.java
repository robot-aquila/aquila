package ru.prolib.aquila.utils.experimental.chart.axis;

public class PreparedRulerFactoryCADProxy implements PreparedRulerFactory {
	private final CategoryAxisDriverProxy proxy;
	private final CategoryAxisDisplayMapper mapper;
	private final Object device;
	
	public PreparedRulerFactoryCADProxy(CategoryAxisDriverProxy proxy, CategoryAxisDisplayMapper mapper, Object device) {
		this.proxy = proxy;
		this.mapper = mapper;
		this.device = device;
	}

	@Override
	public PreparedRuler prepareRuler(RulerRendererID rulerRendererID) {
		return proxy.getRulerRenderer(rulerRendererID.getRendererID()).prepareRuler(mapper, device);
	}

}
