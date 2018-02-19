package ru.prolib.aquila.utils.experimental.chart.axis;

import java.util.List;

public abstract class AxisDriverImpl implements AxisDriver {
	protected final String id;
	protected final AxisDirection dir;
	protected final RulerRendererRegistry rulerRenderers;
	
	public AxisDriverImpl(String axisID, AxisDirection dir, RulerRendererRegistry rulerRenderers) {
		this.id = axisID;
		this.dir = dir;
		this.rulerRenderers = rulerRenderers;
	}
	
	public AxisDriverImpl(String axisID, AxisDirection dir) {
		this(axisID, dir, new RulerRendererRegistryImpl());
	}

	@Override
	public void registerRenderer(RulerRenderer ruler) {
		rulerRenderers.registerRenderer(ruler);
	}

	@Override
	public RulerRenderer getRenderer(String rulerID) {
		return rulerRenderers.getRenderer(rulerID);
	}

	@Override
	public List<String> getRendererIDs() {
		return rulerRenderers.getRendererIDs();
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public AxisDirection getAxisDirection() {
		return dir;
	}

}
