package ru.prolib.aquila.core.config;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Multi-layer option provider.
 */
public class OptionProviderML implements OptionProvider {
	private List<OptionProvider> layers;
	
	public OptionProviderML(List<OptionProvider> layers) {
		this.layers = layers;
	}
	
	public OptionProviderML() {
		this(new ArrayList<>());
	}
	
	/**
	 * Add new layer of options.
	 * <p>
	 * @param provider - option provider
	 * @return index of the added layer in set
	 */
	public int addLayer(OptionProvider provider) {
		int n = layers.size();
		layers.add(provider);
		return n;
	}
	
	/**
	 * Get layer of options by its index.
	 * <p>
	 * @param n - index of layer
	 * @return instance of options provider
	 */
	public OptionProvider getLayer(int n) {
		return layers.get(n);
	}

	@Override
	public boolean hasOption(String optionName) {
		for ( OptionProvider op : layers ) {
			if ( op.hasOption(optionName) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Integer getInteger(String optionName) throws ConfigException {
		for ( OptionProvider op : layers ) {
			if ( op.hasOption(optionName) ) {
				return op.getInteger(optionName);
			}
		}
		return null;
	}

	@Override
	public Integer getInteger(String optionName, Integer defaultValue) throws ConfigException {
		Integer x = getInteger(optionName);
		return x == null ? defaultValue : x;
	}

	@Override
	public Integer getIntegerPositive(String optionName) throws ConfigException {
		for ( OptionProvider op : layers ) {
			if ( op.hasOption(optionName) ) {
				return op.getIntegerPositive(optionName);
			}
		}
		return null;
	}

	@Override
	public Integer getIntegerPositive(String optionName, Integer defaultValue) throws ConfigException {
		Integer x = getIntegerPositive(optionName);
		return x == null ? defaultValue : x;
	}

	@Override
	public Integer getIntegerPositiveNotNull(String optionName) throws ConfigException {
		for ( OptionProvider op : layers ) {
			if ( op.hasOption(optionName) ) {
				return op.getIntegerPositiveNotNull(optionName);
			}
		}
		throw new ConfigException(optionName + " option expected to be not null");
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
		if ( x == null ) {
			x = defaultValue;
		}
		if ( x != null && x == 0 ) {
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
		Integer x = getIntegerPositive(optionName);
		if ( x == null ) {
			x = defaultValue;
		}
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		if ( x == 0 ) {
			throw new ConfigException(optionName + " option expected to be greater than zero but: " + x);
		}
		return x;
	}

	@Override
	public String getString(String optionName) {
		for ( OptionProvider op: layers ) {
			if ( op.hasOption(optionName) ) {
				return op.getString(optionName);
			}
		}
		return null;
	}

	@Override
	public String getString(String optionName, String defaultValue) {
		String x = getString(optionName);
		if ( x == null ) {
			x = defaultValue;
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
		for ( OptionProvider op : layers ) {
			String x = op.getStringOfList(optionName, possibleValues);
			if ( x != null ) {
				return x;
			}
		}
		return null;
	}

	@Override
	public boolean getBoolean(String optionName) throws ConfigException {
		for ( OptionProvider op : layers ) {
			if ( op.hasOption(optionName) ) {
				return op.getBoolean(optionName);
			}
		}
		return false;
	}

	@Override
	public boolean getBoolean(String optionName, boolean defaultValue) throws ConfigException {
		for ( OptionProvider op : layers ) {
			if ( op.hasOption(optionName) ) {
				return op.getBoolean(optionName);
			}
		}
		return defaultValue;
	}

	@Override
	public Instant getInstant(String optionName) throws ConfigException {
		for ( OptionProvider op : layers ) {
			if( op.hasOption(optionName) ) {
				return op.getInstant(optionName);
			}
		}
		return null;
	}

	@Override
	public Instant getInstant(String optionName, Instant defaultValue) throws ConfigException {
		Instant x = getInstant(optionName);
		if ( x == null ) {
			x = defaultValue;
		}
		return x;
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
		if ( x == null ) {
			x = defaultValue;
		}
		if ( x == null ) {
			throw new ConfigException(optionName + " option expected to be not null");
		}
		return x;
	}

	@Override
	public File getFile(String optionName) {
		for ( OptionProvider op : layers ) {
			if ( op.hasOption(optionName) ) {
				return op.getFile(optionName);
			}
		}
		return null;
	}

	@Override
	public File getFile(String optionName, File defaultValue) {
		File x = getFile(optionName);
		if ( x == null ) {
			x = defaultValue;
		}
		return x;
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
