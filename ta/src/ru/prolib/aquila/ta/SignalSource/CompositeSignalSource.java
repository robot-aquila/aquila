package ru.prolib.aquila.ta.SignalSource;

import java.util.Arrays;
import ru.prolib.aquila.ta.*;

public class CompositeSignalSource implements ISignalSource {
	private final ISignalSource[] sources;
	
	public CompositeSignalSource(ISignalSource[] sources) {
		this.sources = Arrays.copyOf(sources, sources.length);
	}
	
	public ISignalSource[] getSignalSources() {
		return Arrays.copyOf(sources, sources.length); 
	}

	@Override
	public void analyze(ISignalTranslator translator) throws ValueException {
		for ( int i = 0; i < sources.length; i ++ ) {
			sources[i].analyze(translator);
		}
	}

}
