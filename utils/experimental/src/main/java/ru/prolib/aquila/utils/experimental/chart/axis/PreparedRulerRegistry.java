package ru.prolib.aquila.utils.experimental.chart.axis;

public interface PreparedRulerRegistry {
	
	PreparedRuler getPreparedRuler(RulerRendererID rulerRendererID);
	PreparedRuler getPreparedRuler(RulerID rulerID);

}
