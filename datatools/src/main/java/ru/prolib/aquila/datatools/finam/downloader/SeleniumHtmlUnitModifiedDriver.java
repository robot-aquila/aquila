package ru.prolib.aquila.datatools.finam.downloader;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebWindow;

public class SeleniumHtmlUnitModifiedDriver extends HtmlUnitDriver {
	public static final int TIMEOUT = 300000; // 5 min
	
	public SeleniumHtmlUnitModifiedDriver() {
		super(BrowserVersion.FIREFOX_24);
		setJavascriptEnabled(true);
		WebClientOptions options = getWebClient().getOptions();
		options.setTimeout(TIMEOUT);
		//options.setThrowExceptionOnFailingStatusCode(false);
		//options.setThrowExceptionOnScriptError(false);
		//options.setPrintContentOnFailingStatusCode(false);
	}
	
	@Override
	public WebClient getWebClient() {
		return super.getWebClient();
	}
	
	@Override
	public WebWindow getCurrentWindow() {
		return super.getCurrentWindow();
	}

}
