package ru.prolib.aquila.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.IMessageRegistry;

/**
 * $Id: UiTexts.java 570 2013-03-12 00:03:15Z huan.kaktus $
 *
 */
public class MessageRegistry implements IMessageRegistry {
	
	private static Logger logger = LoggerFactory.getLogger(MessageRegistry.class);
	private String defLng = "en_US";
	private String[] localesPath = {"shared", "lang"};
	private String dirSeparator = System.getProperty("file.separator");
	private Map<String, ClassLabels> labels = new HashMap<String, ClassLabels>();
	private String lang;
	
	public MessageRegistry() {
		super();		
	}
	
	public MessageRegistry(String lang) {
		super();
		if(lang != defLng) {
			this.lang = lang;
		}
	}
	
	public void load() {
		loadLocale(defLng);
		if(lang != null) {
			loadLocale(lang);
		}
	}
	
	private void loadLocale(String lang) {
		String folderPath = StringUtils.join(localesPath, dirSeparator)+dirSeparator+lang;
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		for(int i = 0; i < files.length; i++) {
			try {
				Options opt = new Options(files[i]);
				String[] name = StringUtils.split(files[i].getName(), ".");
				loadClassLabels(name[0], opt);
				
			} catch (InvalidFileFormatException e) {
				logger.error("Invalid locale file format:{}", e.getMessage());
			}catch(IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public ClassLabels get(String name) {
		if(! labels.containsKey(name)) {
			labels.put(name, createClassLabel(name));
		}
		return labels.get(name);
	}
	
	public void setClassLabels(String className, ClassLabels lbs) {
		labels.put(className, lbs);
	}
	
	private void loadClassLabels(String className, Options opt) {		
		Properties props = get(className).getLabels();
		for(String key: opt.keySet()) {
			props.setProperty(key, opt.get(key));
		}
	}
	
	private ClassLabels createClassLabel(String name) {
		return new ClassLabels(name);
	}
	
	public void setLocalesPath(String[] path) {
		localesPath = path;
	}
	
	public String[] getLocalesPath() {
		return localesPath;
	}
	
	public String getLang() {
		return lang;
	}
	
	public String getDefLang() {
		return defLng;
	}
	
	public Map<String, ClassLabels> getLabels() {
		return labels;
	}

	@Override
	public IMessages getMessages(String id) {
		return get(id);
	}
}
