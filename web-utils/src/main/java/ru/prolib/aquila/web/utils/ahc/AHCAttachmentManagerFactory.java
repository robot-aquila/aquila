package ru.prolib.aquila.web.utils.ahc;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;

import ru.prolib.aquila.web.utils.HTTPAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;

public class AHCAttachmentManagerFactory implements HTTPAttachmentManagerFactory {
	private final AHCClientFactoryImpl clientFactory;
	
	public AHCAttachmentManagerFactory(AHCClientFactoryImpl clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	public AHCAttachmentManagerFactory() {
		this(new AHCClientFactoryImpl());
	}

	@Override
	public HTTPAttachmentManager createAttachmentManager(WebDriver driver) {
		return new AHCAttachmentManager(clientFactory);
	}
	
	public AHCAttachmentManagerFactory loadIni(File file) throws IOException {
		clientFactory.loadIni(file);
		return this;
	}
	
	public AHCAttachmentManagerFactory loadIni(File file, boolean required) throws IOException {
		if ( required || file.exists() ) {
			loadIni(file);
		}
		return this;
	}
	
	public AHCClientFactoryImpl getClientFactory() {
		return clientFactory;
	}

}
