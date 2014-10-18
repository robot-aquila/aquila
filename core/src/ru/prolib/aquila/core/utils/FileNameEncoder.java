package ru.prolib.aquila.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Кодировщик URLEncode для файловых имен.
 * <p>
 * Использует стандартный URLEncoder с кодировкой UTF-8, но выполняет
 * докодировку спецсимволов ".", "-", "*", и "_" в результате чего эти
 * спецсимволы можно использовать в качестве служебных разделителей в именах
 * файлов. Пробел кодируется в "%20".   
 */
public class FileNameEncoder {
	
	public FileNameEncoder() {
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

}
