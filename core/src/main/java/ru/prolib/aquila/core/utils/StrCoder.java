package ru.prolib.aquila.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class StrCoder {
	private static final StrCoder instance;
	
	static {
		instance = new StrCoder();
	}
	
	public static StrCoder getInstance() {
		return instance;
	}
	
	public StrCoder() {
		super();
	}
	
	public String encode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8")
					.replace(".", "%2E")
					.replace("-", "%2D")
					.replace("*", "%2A")
					.replace("_", "%5F")
					.replace("+", "%20");
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public String decode(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}