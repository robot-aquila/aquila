package ru.prolib.aquila.core.config;

import org.ini4j.Profile.Section;

public class KVStoreIni implements KVStore {
	private final Section section;
	
	public KVStoreIni(Section section) {
		this.section = section;
	}

	@Override
	public boolean hasKey(String key) {
		return section.containsKey(key);
	}

	@Override
	public String get(String key) {
		return section.get(key);
	}

}
