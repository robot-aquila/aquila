package ru.prolib.aquila.finam.tools.storage.file;

import ru.prolib.aquila.data.storage.file.FileSetService;

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
