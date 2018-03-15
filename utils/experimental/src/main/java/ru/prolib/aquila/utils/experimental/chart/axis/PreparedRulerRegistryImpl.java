package ru.prolib.aquila.utils.experimental.chart.axis;

import java.util.HashMap;
import java.util.Map;

/**
 * This is caching registry of prepared rulers based on factory and map.
 */
public class PreparedRulerRegistryImpl implements PreparedRulerRegistry {
	private final PreparedRulerFactory factory;
	private final Map<RulerRendererID, PreparedRuler> rulers;
	
	PreparedRulerRegistryImpl(PreparedRulerFactory factory, Map<RulerRendererID, PreparedRuler> rulers) {
		this.factory = factory;
		this.rulers = rulers;
	}
	
	public PreparedRulerRegistryImpl(PreparedRulerFactory factory) {
		this(factory, new HashMap<>());
	}

	@Override
	public PreparedRuler getPreparedRuler(RulerRendererID rulerRendererID) {
		PreparedRuler r = rulers.get(rulerRendererID);
		if ( r == null ) {
			r = factory.prepareRuler(rulerRendererID);
			rulers.put(rulerRendererID, r);
		}
		return r;
	}

	@Override
	public PreparedRuler getPreparedRuler(RulerID rulerID) {
		return getPreparedRuler(rulerID.getRulerRendererID());
	}

}
