package io.github.manhnt217.progressmgr.process;

import java.util.Collection;

/**
 * @author manhnt
 */
public interface IntentStore {

	Collection<Intent> listAllIntents();
	Collection<Intent> listIntentsByResource(Resource r);
	Collection<Intent> listIntentsByAction(Action a);

	void insertIntent(Intent i);
	void removeIntent(Intent i);
}
