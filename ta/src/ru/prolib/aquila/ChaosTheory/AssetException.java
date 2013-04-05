package ru.prolib.aquila.ChaosTheory;

public class AssetException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AssetException() {
		super();
	}
	
	public AssetException(String msg) {
		super(msg);
	}
	
	public AssetException(Throwable t) {
		super(t);
	}
	
	public AssetException(String msg, Throwable t) {
		super(msg, t);
	}

}
