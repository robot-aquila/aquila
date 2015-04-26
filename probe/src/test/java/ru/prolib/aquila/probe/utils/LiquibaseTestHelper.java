package ru.prolib.aquila.probe.utils;

import liquibase.Liquibase;

public interface LiquibaseTestHelper {

	public Liquibase getLiquibase();
	
	public void setUpBeforeTestClass() throws Exception;
	
	public void cleanUpAfterTestClass() throws Exception;

}