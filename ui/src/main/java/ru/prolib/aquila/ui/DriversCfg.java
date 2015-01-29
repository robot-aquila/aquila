package ru.prolib.aquila.ui;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.ini4j.Wini;

@Deprecated
public class DriversCfg {

	/**
	 * $Id: DriversCfg.java 554 2013-03-01 13:43:04Z whirlwind $
	 */
	private String sFileName = "drivers.properties";
	private String driver = "";
	private Wini ini;
	
	public DriversCfg() {
		super();				
	}
	
	public void load() throws IOException {
		ConfLoader loader = new ConfLoader(sFileName);
		ini = loader.load();
	}
	
	public void setFile(String fileName) {
		sFileName = fileName;
	}
	
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	public String getDriver() {
		return driver;
	}

	public String[] getDrivers() {
		return ini.keySet().toArray(new String[0]);
	}
	
	public Properties getProperties() {
		Properties props = new Properties();
		Map<String, String> propData = ini.get(driver);
		for(String key: propData.keySet()) {
			props.setProperty(key, propData.get(key));
		}
		return props;
	}
}
