package ru.prolib.aquila.rxltdde;

@Deprecated
public class Dde {
	
	public interface IHandler {
		
		public void onRawData(String topic, String item, byte[] data);
		
	}

}
