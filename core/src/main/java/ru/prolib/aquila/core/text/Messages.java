package ru.prolib.aquila.core.text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * $Id: UiTexts.java 570 2013-03-12 00:03:15Z huan.kaktus $
 *
 */
public class Messages implements IMessages {
	private static Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Messages.class);
	}
	
	private String defLng = "en_US";
	private String[] localesPath = {"shared", "lang"};
	private String dirSeparator = System.getProperty("file.separator");
	private Map<String, Options> data = new HashMap<String, Options>();
	private String lang;
	
	public Messages() {
		super();		
	}
	
	public Messages(String lang) {
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
				String[] name = StringUtils.split(files[i].getName(), ".");
				Options section = get(name[0]), loaded = new Options(files[i]);
				for( String key: loaded.keySet() ) {
					section.put(key, loaded.get(key));
				}
			} catch (InvalidFileFormatException e) {
				logger.error("Invalid locale file format:{}", e.getMessage());
			}catch(IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void set(String sectionId, Options values) {
		data.put(sectionId, values);
	}
	
	private Options get(String sectionId) {
		if(! data.containsKey(sectionId)) {
			data.put(sectionId, new Options());
		}
		return data.get(sectionId);
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

	@Override
	public String get(MsgID msgId) {
		Options section = get(msgId.getSectionId());
		String id = msgId.getMessageId();
		if ( section.containsKey(id) ) {
			return section.get(id);	
		} else {
			logger.warn("Message not found: {}", msgId.toString());
			return id;
		}
	}

	@Override
	public String format(MsgID msgId, Object... args) {
		return get(msgId);
	}
}
