package ru.prolib.aquila.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.ini4j.Wini;

/**
 * $Id: UiTexts.java 570 2013-03-12 00:03:15Z huan.kaktus $
 *
 */
public class UiTexts {
	
	private Wini ini;
	private final String[] fileName = {"shared", "uitexts.ini"};
	private Map<String, ClassLabels> labels = new HashMap<String, ClassLabels>();
	
	public UiTexts() {
		super();		
	}
	
	public void load() throws IOException {
		ConfLoader loader = new ConfLoader(fileName);
		ini = loader.load();
	}
	
	public ClassLabels get(String className) {
		if(! labels.containsKey(className)) {
			loadClassLabels(className);
		}
		return labels.get(className);
	}
	
	public void setClassLabels(String className, ClassLabels lbs) {
		labels.put(className, lbs);
	}
	
	private void loadClassLabels(String className) {
		Properties props = new Properties();
		Map<String, String> propData = ini.get(className);
		if(propData != null) {
			for(String key: propData.keySet()) {
				props.setProperty(key, propData.get(key));
			}
		}
		labels.put(className, new ClassLabels(className, props));
	}
}
