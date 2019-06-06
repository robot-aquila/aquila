package ru.prolib.aquila.core.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class KVStoreCli implements KVStore {
	private final CommandLine data;
	
	public KVStoreCli(CommandLine data) {
		this.data = data;
	}

	@Override
	public boolean hasKey(String key) {
		return data.hasOption(key);
	}

	@Override
	public String get(String key) {
		if ( ! data.hasOption(key) ) {
			return null;
		}
		String v = data.getOptionValue(key);
		if ( v == null ) {
			for ( Option option : data.getOptions() ) {
				if ( key.equals(option.getOpt()) || key.equals(option.getLongOpt()) ) {
					return option.hasArg() ? v : "1";
				}
			}
		}
		return v;
	}

}
