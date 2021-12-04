package io.github.manhnt217.progressmgr.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author manhnt
 */
class IntentStoreImpl implements IntentStore {

	// TODO: consider a better data structure
	private List<Intent> intents = new ArrayList<>();

	@Override
	public Collection<Intent> listAllIntents() {
		return new ArrayList<>(intents);
	}

	@Override
	public Collection<Intent> listIntentsByResource(Resource r) {
		return intents.stream().filter(i -> i.getResource().equals(r)).collect(Collectors.toList());
	}

	@Override
	public Collection<Intent> listIntentsByAction(Action a) {
		return intents.stream().filter(i -> i.getAction().equals(a)).collect(Collectors.toList());
	}

	@Override
	public void insertIntent(Intent i) {
		intents.add(i);
	}

	@Override
	public void removeIntent(Intent i) {
		intents.remove(i);
	}
}
