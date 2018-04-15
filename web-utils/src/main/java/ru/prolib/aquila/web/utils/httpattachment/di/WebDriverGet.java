package ru.prolib.aquila.web.utils.httpattachment.di;

import java.io.IOException;
import java.net.URI;

import org.openqa.selenium.WebDriver;

import ru.prolib.aquila.web.utils.httpattachment.HTTPDownloadInitiator;

public class WebDriverGet implements HTTPDownloadInitiator {
	private final WebDriver driver;
	private final String url;
	
	public WebDriverGet(WebDriver driver, String url) {
		this.driver = driver;
		this.url = url;
	}
	
	public WebDriverGet(WebDriver driver, URI url) {
		this(driver, url.toString());
	}

	@Override
	public void run() throws IOException {
		try {
			driver.get(url);
		} catch ( Throwable t ) {
			throw new IOException(t);
		}
	}

}
