package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class ValueAxisDriverImpl extends AxisDriverImpl implements ValueAxisDriver {
	
	public ValueAxisDriverImpl(String id,
							   AxisDirection dir,
							   RulerRendererRegistry rulerRegistry)
	{
		super(id, dir, rulerRegistry);
		if ( dir != AxisDirection.UP ) {
			throw new IllegalArgumentException("Unsupported axis direction: " + dir);
		}
	}
	
	public ValueAxisDriverImpl(String id,
							   AxisDirection dir)
	{
		this(id, dir, new RulerRendererRegistryImpl());
	}

	@Override
	public synchronized ValueAxisDisplayMapper
		createMapper(Segment1D segment, ValueAxisViewport viewport)
	{
		if ( dir.isHorizontal() ) {
			throw new IllegalStateException("Axis direction is not supported: " + dir);
		}
		Range<CDecimal> vRange = viewport.getValueRange();
		int y = segment.getStart();
		int height = segment.getLength();
		CDecimal vdiff = vRange.getMax().subtract(vRange.getMin());
		CDecimal ddiff = CDecimalBD.of((long) height).withUnit(vdiff.getUnit());
		if ( vdiff.compareTo(ddiff) >= 0 ) {
			return new ValueAxisDisplayMapperVUV(y, height, vRange);
		} else {
			return new ValueAxisDisplayMapperVUD(y, height, vRange);
		}
	}

}
