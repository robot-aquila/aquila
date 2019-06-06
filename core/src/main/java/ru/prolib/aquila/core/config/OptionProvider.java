package ru.prolib.aquila.core.config;

import java.io.File;
import java.time.Instant;

public interface OptionProvider {
	
	public boolean hasOption(String optionName);
	public Integer getInteger(String optionName) throws ConfigException;
	public Integer getInteger(String optionName, Integer defaultValue) throws ConfigException;
	public Integer getIntegerPositive(String optionName) throws ConfigException;
	public Integer getIntegerPositive(String optionName, Integer defaultValue) throws ConfigException;
	public Integer getIntegerPositiveNotNull(String optionName) throws ConfigException;
	public Integer getIntegerPositiveNotNull(String optionName, Integer defaultValue) throws ConfigException;
	public Integer getIntegerPositiveNonZero(String optionName) throws ConfigException;
	public Integer getIntegerPositiveNonZero(String optionName, Integer defaultValue) throws ConfigException;
	public Integer getIntegerPositiveNonZeroNotNull(String optionName) throws ConfigException;
	public Integer getIntegerPositiveNonZeroNotNull(String optionName, Integer defaultValue) throws ConfigException;
	public String getString(String optionName);
	public String getString(String optionName, String defaultValue);
	public String getStringNotNull(String optionName, String defaultValue) throws ConfigException;
	public String getStringOfList(String optionName, String... possibleValues) throws ConfigException;
	public boolean getBoolean(String optionName) throws ConfigException;
	public boolean getBoolean(String optionName, boolean defaultValue) throws ConfigException;
	public Instant getInstant(String optionName) throws ConfigException;
	public Instant getInstant(String optionName, Instant defaultValue) throws ConfigException;
	public Instant getInstantNotNull(String optionName) throws ConfigException;
	public Instant getInstantNotNull(String optionName, Instant defaultValue) throws ConfigException;
	public File getFile(String optionName);
	public File getFile(String optionName, File defaultValue);
	public File getFileNotNull(String optionName) throws ConfigException;
	
	/**
	 * Get path to file or directory.
	 * <p> 
	 * @param optionName - name of option to get path to file
	 * @param defaultValue - default value. This argument is used as an alternative path if
	 * specified option is not provided. It does not tested when coming and may be null.
	 * But result is tested for null. If both the option and default value are null then exception
	 * will be thrown. 
	 * @return file instance
	 * @throws ConfigException - both values: file specified by option and default value are null
	 */
	public File getFileNotNull(String optionName, File defaultValue) throws ConfigException;

}
