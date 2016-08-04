package ru.prolib.aquila.web.utils.finam;

import ru.prolib.aquila.data.storage.file.FileSetService;

/**
 * The fileset of daily tick data downloaded from FINAM site.
 */
public class FINAMFileSetService implements FileSetService {

	@Override
	public String getRegularSuffix() {
		return ".csv.gz";
	}

	@Override
	public String getTemporarySuffix() {
		return ".part.csv.gz";
	}
	

}
