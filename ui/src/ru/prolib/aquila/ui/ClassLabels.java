package ru.prolib.aquila.ui;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * $Id: ClassLabels.java 541 2013-02-21 21:27:39Z huan.kaktus $
 *
 */
public class ClassLabels {
	private static Logger logger = LoggerFactory.getLogger(ClassLabels.class);
	private Properties labels;
	private final String name;
	
	public ClassLabels(String name, Properties labels) {
		this.name = name;
		this.labels = labels;
	}
	
	public ClassLabels(String name) {
		this.name = name;
		labels = new Properties();
	}
	
	public String get(String label) {
		if(labels.getProperty(label) == null) {
			logger.error("Text for label {}.{} not found!", name, label);
			return label;
		}
		return labels.getProperty(label);
	}
	
	public Properties getLabels() {
		return labels;
	}
	
	public String getName() {
		return name;
	}
	
}
