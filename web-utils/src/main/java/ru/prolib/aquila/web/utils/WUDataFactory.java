package ru.prolib.aquila.web.utils;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.utils.PriceScaleDB;
import ru.prolib.aquila.data.DataProviderComb;
import ru.prolib.aquila.web.utils.finam.data.FinamData;
import ru.prolib.aquila.web.utils.moex.data.MoexData;

public class WUDataFactory {
	
	/**
	 * Decorate a data provider to replay symbol & L1 updates stored in combined MOEX/FINAM storage.
	 * <p>
	 * @param parent - parent data provider. Usually simulated (for example QForts).
	 * @param scheduler - scheduler to replay updates. Usually simulated (for example PROBE).
	 * @param dataRootDir - path to directory of combined data storage which contains MOEX
	 * and FINAM recorded data.
	 * @param scaleDB - the price scale DB is required to properly replay L1 data. Usually
	 * combination of lazy instance with underlying terminal-based DB.  
	 * @return combined data provider which may be used to build a terminal
	 */
	public DataProvider decorateForSymbolAndL1DataReplayFM(DataProvider parent,
		Scheduler scheduler, File dataRootDir, PriceScaleDB scaleDB)
	{
		return new DataProviderComb(
				new MoexData().createSymbolUpdateSource(dataRootDir, scheduler),
				new FinamData().createL1UpdateSource(dataRootDir, scheduler, scaleDB),
				parent);
	}

}
