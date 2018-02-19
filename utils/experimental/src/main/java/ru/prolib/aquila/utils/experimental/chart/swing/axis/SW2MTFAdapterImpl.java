package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabel;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelGenerator;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapperImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelSize;

public class SW2MTFAdapterImpl implements SW2MTFAdapter {
	
	public static class LabelWidth implements MTFLabelSize {
		private final SWLabelDimensions dimensions;
		
		public LabelWidth(SWLabelDimensions dimensions) {
			this.dimensions = dimensions;
		}

		@Override
		public int getVisibleSize(String labelText) {
			return dimensions.getLabelWidth(labelText);
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != LabelWidth.class ) {
				return false;
			}
			LabelWidth o = (LabelWidth) other;
			return new EqualsBuilder()
					.append(o.dimensions, dimensions)
					.isEquals();
		}
		
	}
	
	public static class LabelHeight implements MTFLabelSize {
		private final SWLabelDimensions dimensions;
		
		public LabelHeight(SWLabelDimensions dimensions) {
			this.dimensions = dimensions;
		}

		@Override
		public int getVisibleSize(String labelText) {
			return dimensions.getLabelHeight(labelText);
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != LabelHeight.class ) {
				return false;
			}
			LabelHeight o = (LabelHeight) other;
			return new EqualsBuilder()
					.append(o.dimensions, dimensions)
					.isEquals();
		}

	}

	public static class Setting {
		private final ZTFrame tframe;
		private final int barSize;
		private final MTFLabelSize labelSize;
		
		Setting(ZTFrame tframe, int barSize, MTFLabelSize labelSize) {
			this.tframe = tframe;
			this.barSize = barSize;
			this.labelSize = labelSize;
		}
		
		public Setting(CategoryAxisDisplayMapper mapper,
				  ZTFrame tframe,
				  SWLabelDimensions labelDimensions)
		{
			this(tframe, mapper.getPlotSize() / mapper.getNumberOfVisibleBars(),
					mapper.getAxisDirection().isHorizontal() ?
							new LabelWidth(labelDimensions) : new LabelHeight(labelDimensions));
		}
		
		/**
		 * Get time frame period in minutes.
		 * <p>
		 * @return period
		 */
		public int getPeriod() {
			switch ( tframe.getUnit() ) {
			case MINUTES:
				return tframe.getLength();
			case HOURS:
				return tframe.getLength() * 60;
			default:
				throw new IllegalStateException("Unsupported time frame: " + tframe.getUnit());	
			}			
		}
		
		/**
		 * Get bar size.
		 * <p>
		 * @return bar size
		 */
		public int getBarSize() {
			return barSize;
		}
		
		/**
		 * Get label size determination strategy.
		 * <p>
		 * @return label size strategy
		 */
		public MTFLabelSize getLabelSize() {
			return labelSize;
		}
		
		/**
		 * Get time zone ID of time frame.
		 * <p>
		 * @return zone ID
		 */
		public ZoneId getZoneID() {
			return tframe.getZoneID();
		}
		
		/**
		 * Test that this setting associated with time frame supported by label generator.
		 * <p>
		 * @return true if it time frame supported by generator, false otherwise
		 */
		public boolean isSupportedTFrame() {
			switch ( tframe.getUnit() ) {
			case MINUTES:
			case HOURS:
				return true;
			default:
				return false;	
			}
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != Setting.class ) {
				return false;
			}
			Setting o = (Setting) other;
			return new EqualsBuilder()
					.append(o.barSize, barSize)
					.append(o.labelSize, labelSize)
					.append(o.tframe, tframe)
					.isEquals();
		}
		
	}
	
	private static final SW2MTFAdapter instance;
	
	static {
		instance = new SW2MTFAdapterImpl();
	}
	
	public static SW2MTFAdapter getInstance() {
		return instance;
	}

	private final MTFLabelGenerator generator;
	private Setting cachedSetting;
	private MTFLabelMapper cachedMapper;
	
	public SW2MTFAdapterImpl(MTFLabelGenerator generator) {
		this.generator = generator;
	}
	
	public SW2MTFAdapterImpl() {
		this(MTFLabelGenerator.getInstance());
	}
	
	MTFLabelGenerator getLabelGenerator() {
		return generator;
	}

	@Override
	public synchronized MTFLabelMapper getLabelMapper(CategoryAxisDisplayMapper mapper,
													  ZTFrame tframe,
													  SWLabelDimensions labelDimensions)
	{
		Setting setting = new Setting(mapper, tframe, labelDimensions);
		if ( ! setting.equals(cachedSetting) ) {
			List<MTFLabel> labels;
			if ( setting.isSupportedTFrame() ) {
				labels = generator.getIntradayLabels(setting.getBarSize(),
						setting.getPeriod(), setting.getLabelSize());
			} else {
				labels = new ArrayList<>();
				labels.add(new MTFLabel(LocalTime.MIDNIGHT, "0h", true));
			}
			cachedSetting = setting;
			cachedMapper = new MTFLabelMapperImpl(setting.getZoneID(), labels);
		}
		return cachedMapper;
	}

}
