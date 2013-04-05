package ru.prolib.aquila.ChaosTheory;

public interface Props {

	public void setString(String name, String value);

	public int size();

	public int getInt(String name) throws PropsException;

	public int getInt(String name, int defval) throws PropsException;

	public String getString(String name) throws PropsException;

	public String getString(String name, String defval) throws PropsException;
	
	public double getDouble(String name) throws PropsException;
	
	public double getDouble(String name, double defval) throws PropsException;

}