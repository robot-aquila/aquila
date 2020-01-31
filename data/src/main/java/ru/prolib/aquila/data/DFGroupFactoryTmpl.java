package ru.prolib.aquila.data;

import java.util.HashMap;
import java.util.Map;

public class DFGroupFactoryTmpl<KeyType, FeedType> implements DFGroupFactory<KeyType, FeedType> {
	private final Map<FeedType, DFSubscrState> template;
	
	public DFGroupFactoryTmpl(Map<FeedType, DFSubscrState> template) {
		this.template = template;
	}

	@Override
	public DFGroup<KeyType, FeedType> produce(KeyType key) {
		return new DFGroup<>(key, new HashMap<>(template));
	}

}
