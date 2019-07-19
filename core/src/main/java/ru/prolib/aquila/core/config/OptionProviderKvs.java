package ru.prolib.aquila.core.config;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class OptionProviderKvs implements OptionProvider {
	private final KVStore store;
	
	public OptionProviderKvs(KVStore store) {
		this.store = store;
	}
	
	/**
	 * Get the underlying key-value store.
	 * <p>
	 * @return a key-store store instance
	 */
	public KVStore getStore() {
		return store;
	}

	@Override
	public boolean hasOption(String optionName) {
		return store.hasKey(optionName);
	}

	@Override
	public Integer getInteger(String optionName) throws ConfigException {
		String x = getString(optionName);
		if ( x == null ) {
			return null;
		}
		if ( x.length() == 0 ) {
			return null;
		}
		try {
			return Integer.parseInt(x);
		} catch ( NumberFormatException e ) {
			throw new ConfigException(optionName + " option expected to be an integer but: " + x, e);
		}
	}

	@Override
	public Integer getInteger(String optionName, Integer defaultValue) throws ConfigException {
		Integer x = getInteger(optionName);
		return x == null ? defaultValue : x;
	}

	@Override
	public Integer getIntegerPositive(String optionName) throws ConfigException {
		Integer x = getInteger(optionName);
		if ( x != null && x < 0 ) {
			throw new ConfigException(optionName + " option expected to be a positive integer but: " + x);
		}
		return x;
	}
	
	@Override
	public Integer getIntegerPositive(String optionName, Integer defaultValue) throws ConfigException {
		Integer x = getIntegerPositive(optionName);
		x = x == null ? defaultValue : x;
		if ( x != null && x < 0 ) {
			throw new ConfigException(optionName + " option expected to be a positive integer but: " + x);
		}
		return x;
	}

	@Override
	public Integer getIntegerPositiveNotNull(String optionName) throws ConfigException {
		Integer x = getIntegerPositive(optionName);
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}
	
	@Override
	public Integer getIntegerPositiveNotNull(String optionName, Integer defaultValue) throws ConfigException {
		Integer x = getIntegerPositive(optionName, defaultValue);
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}

	@Override
	public Integer getIntegerPositiveNonZero(String optionName) throws ConfigException {
		Integer x = getIntegerPositive(optionName);
		if ( x != null && x == 0 ) {
			throw new ConfigException(optionName + " option expected to be greater than zero but: " + x);
		}
		return x;
	}
	
	@Override
	public Integer getIntegerPositiveNonZero(String optionName, Integer defaultValue) throws ConfigException {
		Integer x = getIntegerPositive(optionName);
		x = x == null ? defaultValue : x;
		if ( x != null && x <= 0 ) {
			throw new ConfigException(optionName + " option expected to be greater than zero but: " + x);
		}
		return x; 
	}

	@Override
	public Integer getIntegerPositiveNonZeroNotNull(String optionName) throws ConfigException {
		Integer x = getIntegerPositiveNonZero(optionName);
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}
	
	@Override
	public Integer getIntegerPositiveNonZeroNotNull(String optionName, Integer defaultValue) throws ConfigException {
		Integer x = getIntegerPositiveNonZero(optionName, defaultValue);
		x = x == null ? defaultValue : x;
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		if ( x <= 0 ) {
			throw new ConfigException(optionName + " option expected to be greater than zero but: " + x);
		}
		return x;
	}

	@Override
	public String getString(String optionName) {
		String x = store.get(optionName);
		if ( x != null && x.length() == 0 ) {
			x = null;
		}
		return x;
	}

	@Override
	public String getString(String optionName, String defaultValue) {
		String x = getString(optionName);
		if ( x == null ) {
			x = defaultValue;
		}
		if ( x != null && x.length() == 0 ) {
			x = null;
		}
		return x;
	}

	@Override
	public String getStringNotNull(String optionName, String defaultValue) throws ConfigException {
		String x = getString(optionName, defaultValue);
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}

	@Override
	public String getStringOfList(String optionName, String... possibleValues) throws ConfigException {
		String x = getString(optionName);
		if ( x != null ) {
			List<String> list = Arrays.asList(possibleValues);
			if ( ! list.contains(x) ) {
				throw new ConfigException(optionName + " option expected to be one of list value " + list + " but: " + x);
			}
		}
		return x;
	}

	@Override
	public boolean getBoolean(String optionName) throws ConfigException {
		String x = getStringOfList(optionName, "true", "false", "1", "0");
		if ( x == null ) {
			return false;
		}
		switch ( x ) {
		case "true":
		case "1":
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean getBoolean(String optionName, boolean defaultValue) throws ConfigException {
		String x = getStringOfList(optionName, "true", "false", "1", "0");
		if ( x == null ) {
			return defaultValue;
		}
		switch ( x ) {
		case "true":
		case "1":
			return true;
		default:
			return false;
		}
	}

	@Override
	public Instant getInstant(String optionName) throws ConfigException {
		String x = getString(optionName);
		if ( x != null ) {
			try {
				return Instant.parse(x);
			} catch ( DateTimeParseException e ) {
				throw new ConfigException(optionName + " option expected to be valid UTC time but: " + x);
			}
		}
		return null;
	}

	@Override
	public Instant getInstant(String optionName, Instant defaultValue) throws ConfigException {
		Instant x = getInstant(optionName);
		return x == null ? defaultValue : x;
	}

	@Override
	public Instant getInstantNotNull(String optionName) throws ConfigException {
		Instant x = getInstant(optionName);
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}

	@Override
	public Instant getInstantNotNull(String optionName, Instant defaultValue) throws ConfigException {
		Instant x = getInstant(optionName);
		x = x == null ? defaultValue : x;
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}

	@Override
	public File getFile(String optionName) {
		String x = getString(optionName);
		return x == null ? null : new File(x);
	}

	@Override
	public File getFile(String optionName, File defaultValue) {
		File x = getFile(optionName);
		return x == null ? defaultValue : x;
	}

	@Override
	public File getFileNotNull(String optionName) throws ConfigException {
		File x = getFile(optionName);
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}

	@Override
	public File getFileNotNull(String optionName, File defaultValue) throws ConfigException {
		File x = getFile(optionName);
		if ( x == null ) {
			x = defaultValue;
		}
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}

}
