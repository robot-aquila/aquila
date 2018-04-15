package ru.prolib.aquila.web.utils.jbd;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;

public class JBrowserDriverFixed extends JBrowserDriver {
	
	public JBrowserDriverFixed(final Settings settings) {
		super(settings);
	}
	
	public JBrowserDriverFixed(Capabilities capabilities) {
		super(capabilities);
	}
	
	@Override
	public void quit() {
		try {
			super.quit();
		} catch ( WebDriverException e ) {
			if ( e.getCause() != null && e.getCause().getClass() == NullPointerException.class ) {
				// this is bug case
			} else {
				throw e;
			}
		}
	}

}
