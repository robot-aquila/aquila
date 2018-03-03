package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.util.Collection;
import java.util.List;

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
