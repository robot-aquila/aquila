package ru.prolib.aquila.utils.experimental.chart.axis;

import java.util.List;

public interface RulerRendererRegistry {
	
	/**
	 * Register new ruler renderer.
	 * <p>
	 * @param renderer - the ruler renderer instance
	 * @throws IllegalArgumentException if renderer with such ID already
	 * registered
	 */
	void registerRenderer(RulerRenderer renderer);
	
	/**
	 * Get registered renderer by its ID.
	 * <p>
	 * @param rendererID - renderer ID
	 * @return ruler renderer instance
	 * @throw IllegalArgumentException if renderer with such ID does not exists
	 */
	RulerRenderer getRenderer(String rendererID);
	
	/**
	 * Get identifiers of all registered renderers.
	 * <p>
	 * @return IDs of all registered renderers
	 */
	List<String> getRendererIDs();

}
