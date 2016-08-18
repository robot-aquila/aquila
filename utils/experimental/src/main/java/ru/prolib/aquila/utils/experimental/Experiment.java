package ru.prolib.aquila.utils.experimental;

import java.io.Closeable;

import org.apache.commons.cli.CommandLine;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;

public interface Experiment extends Closeable {
	
	public int run(Scheduler scheduler, CommandLine cmd);
	
	public int getExitCode();

}
