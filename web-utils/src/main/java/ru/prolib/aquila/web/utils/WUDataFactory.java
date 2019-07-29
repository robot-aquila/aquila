package ru.prolib.aquila.web.utils;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.utils.PriceScaleDB;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.data.DataSourceImpl;
import ru.prolib.aquila.data.MDUpdateSourceStub;
import ru.prolib.aquila.web.utils.finam.data.FinamData;
import ru.prolib.aquila.web.utils.moex.data.MoexData;

public class WUDataFactory {
	
	/**
	 * Create a data source to replay symbol & L1 updates stored in combined MOEX/FINAM storage.
	 * <p>
	 * @param scheduler - scheduler to replay updates. Usually simulated (for example PROBE).
	 * @param dataRootDir - path to directory of combined data storage which contains MOEX
	 * and FINAM recorded data.
	 * @param scaleDB - the price scale DB is required to properly replay L1 data. Usually
	 * combination of lazy instance with underlying terminal-based DB.  
	 * @return combined data provider which may be used to build a terminal
	 */
	public DataSource createForSymbolAndL1DataReplayFM(
			Scheduler scheduler,
			File dataRootDir,
			PriceScaleDB scaleDB)
	{
		DataSourceImpl data_source = new DataSourceImpl();
		data_source.setSymbolUpdateSource(new MoexData().createSymbolUpdateSource(dataRootDir, scheduler));
		data_source.setL1UpdateSource(new FinamData().createL1UpdateSource(dataRootDir, scheduler, scaleDB));
		data_source.setMDUpdateSource(new MDUpdateSourceStub());
		return data_source;
	}

}
