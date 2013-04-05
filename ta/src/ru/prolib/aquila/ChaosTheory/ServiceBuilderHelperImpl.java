package ru.prolib.aquila.ChaosTheory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class ServiceBuilderHelperImpl implements ServiceBuilderHelper {
	
	public ServiceBuilderHelperImpl() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getAttribute(java.lang.String, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	public String getAttribute(String attr, HierarchicalStreamReader reader)
		throws ServiceBuilderAttributeNotExistsException
	{
		String value = reader.getAttribute(attr);
		if ( value == null ) {
			throw new ServiceBuilderAttributeNotExistsException(attr);
		}
		return value;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getInt(java.lang.String, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public int getInt(String attr, HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return Integer.parseInt(getAttribute(attr, reader));
		} catch ( NumberFormatException e ) {
			throw new ServiceBuilderFormatException(attr, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getInt(java.lang.String, int, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public int getInt(String attr, int def, HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return getInt(attr, reader);
		} catch ( ServiceBuilderAttributeNotExistsException e ) {
			return def;
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getInt(com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public int getInt(HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return Integer.parseInt(reader.getValue());
		} catch ( NumberFormatException e ) {
			throw new ServiceBuilderFormatException(e);
		}
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getLong(java.lang.String, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public long getLong(String attr, HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return Long.parseLong(getAttribute(attr, reader));
		} catch ( NumberFormatException e ) {
			throw new ServiceBuilderFormatException(attr, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getLong(java.lang.String, long, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public long getLong(String attr, long def, HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return getLong(attr, reader);
		} catch ( ServiceBuilderAttributeNotExistsException e ) {
			return def;
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getLong(com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public long getLong(HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return Long.parseLong(reader.getValue());
		} catch ( NumberFormatException e ) {
			throw new ServiceBuilderFormatException(e);
		}
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getDouble(java.lang.String, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public double getDouble(String attr, HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return Double.parseDouble(getAttribute(attr, reader));
		} catch ( NumberFormatException e ) {
			throw new ServiceBuilderFormatException(attr, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getDouble(java.lang.String, double, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public double getDouble(String attr, double def,
							HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return getDouble(attr, reader);
		} catch ( ServiceBuilderAttributeNotExistsException e ) {
			return def;
		}
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getDouble(com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public double getDouble(HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return Double.parseDouble(reader.getValue());
		} catch ( NumberFormatException e ) {
			throw new ServiceBuilderFormatException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getString(java.lang.String, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public String getString(String attr, HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		return getAttribute(attr, reader);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getString(java.lang.String, java.lang.String, com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public String getString(String attr, String def,
							HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		try {
			return getString(attr, reader);
		} catch ( ServiceBuilderAttributeNotExistsException e ) {
			return def;
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getString(com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public String getString(HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		return reader.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper#getProps(com.thoughtworks.xstream.io.HierarchicalStreamReader)
	 */
	@Override
	public Props getProps(HierarchicalStreamReader reader)
		throws ServiceBuilderException
	{
		Props props = new PropsImpl();
		while ( reader.hasMoreChildren() ) {
			reader.moveDown();
			props.setString(reader.getNodeName(), reader.getValue());			
			reader.moveUp();
		}
		return props;
	}

}
