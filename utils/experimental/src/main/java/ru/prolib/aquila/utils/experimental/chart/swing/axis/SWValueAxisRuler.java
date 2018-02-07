package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.util.List;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.Ruler;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

public class SWValueAxisRuler implements Ruler {
	private final SWValueAxisRulerBuilderImpl builder;
	private final ValueAxisDisplayMapper mapper;
	private final List<SWValueAxisRulerBuilderImpl.Label> labels;
	
	public SWValueAxisRuler(SWValueAxisRulerBuilderImpl builder,
			ValueAxisDisplayMapper mapper,
			List<SWValueAxisRulerBuilderImpl.Label> labels)
	{
		this.builder = builder;
		this.mapper = mapper;
		this.labels = labels;
	}

	@Override
	public void drawRuler(AxisPosition position, Rectangle target, Object device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGrid(Rectangle plot, Object device) {
		// TODO Auto-generated method stub

	}

}
