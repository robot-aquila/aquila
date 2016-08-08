package ru.prolib.aquila.utils.experimental;

import org.apache.commons.cli.CommandLine;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;

public interface Experiment {
	
	public int run(Scheduler scheduler, CommandLine cmd);
	
	public int getExitCode();

}
