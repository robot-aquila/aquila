package ru.prolib.aquila.ui;

import java.io.IOException;

import org.ini4j.Wini;

/**
 * Конфиг.
 * <p>
 * $Id: Config.java 554 2013-03-01 13:43:04Z whirlwind $
 * 2013-02-28<br>
 */
@Deprecated
public class Config {
	private final Wini ini;
	
	public Config(Wini ini) {
		super();
		this.ini = ini;
	}
	
	public Config(String fileName) throws IOException {
		super();
		ini = new ConfLoader(fileName).load();
	}
	
	public String property(String group, String key) {
		return ini.get(group).get(key);
	}
	
	public static Config get() throws IOException {
		return new Config("main.ini");
	}

}
