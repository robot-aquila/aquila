package ru.prolib.aquila.web.utils.finam;

import ru.prolib.aquila.data.storage.file.FSService;

/**
 * The files of daily tick data downloaded from FINAM site.
 */
public class FidexpFSService implements FSService {

	@Override
	public String getRegularSuffix() {
		return ".csv.gz";
	}

	@Override
	public String getTemporarySuffix() {
		return ".part.csv.gz";
	}

}
