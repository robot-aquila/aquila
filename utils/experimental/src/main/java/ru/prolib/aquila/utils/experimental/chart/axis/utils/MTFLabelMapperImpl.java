package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class MTFLabelMapperImpl implements MTFLabelMapper {
	private final ZoneId zoneID;
	private final List<MTFLabel> labels;
	private final Map<LocalTime, MTFLabel> labelMap;
	
	/**
	 * Constructor.
	 * <p>
	 * @param zoneID - time zone ID. All times passed to iteration method
	 * will be converted to that time zone.
	 * @param intradayLabels - complete set of intraday labels. The time
	 * matching will be performed only according to labels from this set.
	 */
	public MTFLabelMapperImpl(ZoneId zoneID, List<MTFLabel> intradayLabels) {
		this.zoneID = zoneID;
		this.labels = intradayLabels;
		this.labelMap = new HashMap<>();
		for ( MTFLabel label : labels ) {
			labelMap.put(label.getTime(), label);
		}
	}

	@Override
	public MTFLabel convertToLabel(Instant time) {
		return labelMap.get(time.atZone(zoneID).toLocalTime());
	}
	
	List<MTFLabel> getLabels() {
		return labels;
	}
	
	ZoneId getZoneID() {
		return zoneID;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MTFLabelMapperImpl.class ) {
			return false;
		}
		MTFLabelMapperImpl o = (MTFLabelMapperImpl) other;
		return new EqualsBuilder()
				.append(o.labels, labels)
				.append(o.zoneID, zoneID)
				.isEquals();
	}

}
