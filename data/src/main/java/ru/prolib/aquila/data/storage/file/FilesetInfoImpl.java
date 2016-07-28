package ru.prolib.aquila.data.storage.file;

public class FilesetInfoImpl implements FilesetInfo {
	private final String regularSuffix, temporarySuffix;
	
	public FilesetInfoImpl(String regularSuffix, String temporarySuffix) {
		this.regularSuffix = regularSuffix;
		this.temporarySuffix = temporarySuffix;
	}

	@Override
	public String getRegularSuffix() {
		return regularSuffix;
	}

	@Override
	public String getTemporarySuffix() {
		return temporarySuffix;
	}

}
