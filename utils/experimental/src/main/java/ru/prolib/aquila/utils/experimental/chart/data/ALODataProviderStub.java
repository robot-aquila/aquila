package ru.prolib.aquila.utils.experimental.chart.data;

import java.util.Collection;
import java.util.List;

/**
 * Simple stub implementation of set of price levels.
 */
public class ALODataProviderStub implements ALODataProvider {
	private final List<ALOData> data;
	
	public ALODataProviderStub(List<ALOData> data) {
		this.data = data;
	}

	@Override
	public Collection<ALOData> getOrderVolumes() {
		return data;
	}

}
