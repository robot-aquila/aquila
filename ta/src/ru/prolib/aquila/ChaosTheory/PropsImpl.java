package ru.prolib.aquila.ChaosTheory;

import java.util.Properties;

public class PropsImpl implements Props {
	private static final long serialVersionUID = 1L;
	private final Properties properties;

	public PropsImpl() {
		super();
		properties = new Properties();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Props#setString(java.lang.String, java.lang.String)
	 */
	@Override
	public void setString(String name, String value) {
		properties.setProperty(name, value);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Props#size()
	 */
	@Override
	public int size() {
		return properties.size();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Props#getInt(java.lang.String)
	 */
	@Override
	public int getInt(String name) throws PropsException {
		try {
			return Integer.parseInt(getString(name));
		} catch ( NumberFormatException e ) {
			throw new PropsFormatException(name, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Props#getInt(java.lang.String, int)
	 */
	@Override
	public int getInt(String name, int defaultValue) throws PropsException {
		try {
			return getInt(name);
		} catch ( PropsNotExistsException e ) {
			return defaultValue;
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Props#getString(java.lang.String)
	 */
	@Override
	public String getString(String name) throws PropsException {
		if ( ! properties.containsKey(name) ) {
			throw new PropsNotExistsException(name);
		}
		return properties.getProperty(name);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Props#getString(java.lang.String, java.lang.String)
	 */
	@Override
	public String getString(String name, String defaultValue)
		throws PropsException
	{
		try {
			return getString(name);
		} catch ( PropsNotExistsException e ) {
			return defaultValue;
		}
	}

	@Override
	public double getDouble(String name) throws PropsException {
		try {
			return Double.parseDouble(getString(name));
		} catch ( NumberFormatException e ) {
			throw new PropsFormatException(name, e);
		}
	}

	@Override
	public double getDouble(String name, double defval) throws PropsException {
		try {
			return getDouble(name);
		} catch ( PropsNotExistsException e ) {
			return defval;
		}
	}

}
