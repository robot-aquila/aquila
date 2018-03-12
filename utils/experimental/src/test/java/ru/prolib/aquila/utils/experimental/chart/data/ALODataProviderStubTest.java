package ru.prolib.aquila.utils.experimental.chart.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.data.ALOData;
import ru.prolib.aquila.utils.experimental.chart.data.ALODataImpl;
import ru.prolib.aquila.utils.experimental.chart.data.ALODataProviderStub;

public class ALODataProviderStubTest {

	@Test
	public void testGetOrderVolumes() {
		List<ALOData> stubData = new ArrayList<>();
		stubData.add(new ALODataImpl(of("24.81"), of(1L), of(5L)));
		stubData.add(new ALODataImpl(of("34.79"), of(100L), of(0L)));
		stubData.add(new ALODataImpl(of("18.53"), of(50L), of(28L)));
		ALODataProviderStub service = new ALODataProviderStub(stubData);
		
		assertSame(stubData, service.getOrderVolumes());
	}

}
