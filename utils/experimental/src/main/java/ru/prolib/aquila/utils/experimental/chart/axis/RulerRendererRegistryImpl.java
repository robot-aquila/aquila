package ru.prolib.aquila.utils.experimental.chart.axis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RulerRendererRegistryImpl implements RulerRendererRegistry {
	private final LinkedHashMap<String, RulerRenderer> renderers;
	
	RulerRendererRegistryImpl(LinkedHashMap<String, RulerRenderer> renderers) {
		this.renderers = renderers;
	}
	
	public RulerRendererRegistryImpl() {
		this(new LinkedHashMap<>());
	}

	@Override
	public synchronized void registerRenderer(RulerRenderer renderer) {
		String id = renderer.getID();
		if ( renderers.containsKey(id) ) {
			throw new IllegalArgumentException("Renderer already exists: " + id);
		}
		renderers.put(id, renderer);
	}

	@Override
	public synchronized RulerRenderer getRenderer(String rendererID) {
		RulerRenderer renderer = renderers.get(rendererID);
		if ( renderer == null ) {
			throw new IllegalArgumentException("Renderer not exists: " + rendererID);
		}
		return renderer;
	}

	@Override
	public synchronized List<String> getRendererIDs() {
		return new ArrayList<>(renderers.keySet());
	}

}
