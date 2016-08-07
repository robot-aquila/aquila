package ru.prolib.aquila.web.utils.finam;

import ru.prolib.aquila.data.storage.file.Files;

/**
 * The files of daily tick data downloaded from FINAM site.
 */
public class FidexpFiles implements Files {

	@Override
	public String getRegularSuffix() {
		return ".csv.gz";
	}

	@Override
	public String getTemporarySuffix() {
		return ".part.csv.gz";
	}
	

}
