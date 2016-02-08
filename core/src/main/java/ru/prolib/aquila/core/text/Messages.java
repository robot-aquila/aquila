package ru.prolib.aquila.core.text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Text messages facade.
 * <p>
 * $Id: UiTexts.java 570 2013-03-12 00:03:15Z huan.kaktus $
 */
public class Messages implements IMessages {
	private static final Logger logger;
	private static final HashMap<String, ClassLoader> resourceLoaders;
	private static final File DEFAULT_ROOT = new File("shared", "lang");
	private static final String DEFAULT_LANG = "en_US";
	private static final String RESOURCE_EXT = ".ini";
	
	static {
		logger = LoggerFactory.getLogger(Messages.class);
		resourceLoaders = new HashMap<String, ClassLoader>();
	}

	/**
	 * Register resource loader of section messages.
	 * <p>
	 * @param sectionId - section ID
	 * @param classLoader - class loader to load resource file
	 */
	public synchronized static
		void registerLoader(String sectionId, ClassLoader classLoader)
	{
		resourceLoaders.put(sectionId, classLoader);
	}
	
	/**
	 * Remove registered resource loader.
	 * <p>
	 * @param sectionId - section ID to remove
	 */
	public synchronized static void removeLoader(String sectionId) {
		resourceLoaders.remove(sectionId);
	}
	
	/**
	 * Remove all registered resource loaders.
	 */
	public synchronized static void removeLoaders() {
		resourceLoaders.clear();
	}
	
	private synchronized static ClassLoader getLoader(String sectionId) {
		return resourceLoaders.get(sectionId);
	}
	
	private final File root;
	private final Map<String, Options> data = new HashMap<String, Options>();
	private String lang;
	
	public Messages() {
		this(DEFAULT_ROOT, DEFAULT_LANG);
	}
	
	public Messages(String lang) {
		this(DEFAULT_ROOT, lang);
	}
	
	public Messages(File root) {
		this(root, DEFAULT_LANG);
	}
	
	public Messages(File root, String lang) {
		super();
		this.root = root;
		if( lang != null && ! lang.equals(DEFAULT_LANG) ) {
			this.lang = lang;
		} else {
			this.lang = DEFAULT_LANG;
		}
		load();
	}
	
	public File getRootFolder() {
		return root;
	}
	
	private void load() {
		loadLocale(DEFAULT_LANG);
		if ( lang != null && ! lang.equals(DEFAULT_LANG) ) {
			loadLocale(lang);
		}
	}
	
	private void loadLocale(String lang) {
		File folder = new File(root, lang);
		if ( ! folder.exists() ) {
			logger.warn("Cannot load messages. Directory not exists: {}", folder);
			return;
		}
		File[] files = folder.listFiles();
		for(int i = 0; i < files.length; i++) {
			loadMessages(files[i]);
		}
	}
	
	private void loadMessages(File file) {
		try {
			String[] name = StringUtils.split(file.getName(), ".");
			mergeToSection(name[0], new Options(file));
		} catch (InvalidFileFormatException e) {
			logger.error("Invalid locale file format:{}", e.getMessage());
		}catch(IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	private void loadMessages(String sectionId, URL url) {
		try {
			mergeToSection(sectionId, new Options(url));
		} catch ( InvalidFileFormatException e ) {
			logger.error("Invalid locale file format:{}", e.getMessage());
		} catch(IOException e) {
			logger.error(e.getMessage());
		} catch ( NullPointerException e ) {
			logger.error("Cannot load resource: " + url, e);
		}
	}
	
	private void mergeToSection(String sectionId, Options loaded) {
		Options section = data.get(sectionId);
		if ( section == null ) {
			data.put(sectionId, loaded);
		} else {
			for ( String key : loaded.keySet() ) {
				section.put(key, loaded.get(key));
			}
		}
	}
	
	public void set(String sectionId, Options values) {
		data.put(sectionId, values);
	}
	
	private Options get(String sectionId) {
		if ( ! data.containsKey(sectionId) ) {
			data.put(sectionId, new Options());
			ClassLoader loader = getLoader(sectionId);
			if ( loader != null ) {
				File f = new File(root, lang);
				f = new File(f, sectionId + RESOURCE_EXT);
				String path = f.getPath().replace('\\', '/');
				URL url = loader.getResource(path);
				if ( url == null ) {
					logger.error("Cannot obtain resource URL: {}", path);
				} else {
					loadMessages(sectionId, url);
				}
			}
		}
		return data.get(sectionId);
	}

	public String getLang() {
		return lang;
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
