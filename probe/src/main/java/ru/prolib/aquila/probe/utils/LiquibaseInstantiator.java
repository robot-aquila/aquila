package ru.prolib.aquila.probe.utils;

import liquibase.Liquibase;

public interface LiquibaseInstantiator {

	public abstract Liquibase getLiquibase();

}