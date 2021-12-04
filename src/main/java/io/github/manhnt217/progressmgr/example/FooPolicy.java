package io.github.manhnt217.progressmgr.example;

import io.github.manhnt217.progressmgr.process.*;

import java.util.Collection;

/**
 * @author manhnt
 */
public class FooPolicy implements IntentAllocationPolicy {
	public static final int PARALLEL_LEVEL = 1;
	//
//	@Override
//	public Intent allocateIntent(Resource r, Action a, IntentStore reg) throws IntentAllocationException {
//		Intent intent = new Intent(a, r);
//		reg.insertIntent(intent);
//		return intent;
//	}

	@Override
	public Intent allocateIntent(Resource r, Action a, IntentStore reg) throws IntentAllocationException {
		Collection<Intent> intents = reg.listIntentsByAction(new FooAction("doFoo"));
		if (intents.size() < PARALLEL_LEVEL) {
			Intent intent = new Intent(a, r);
			reg.insertIntent(intent);
			return intent;
		} else {
			Intent[] its = intents.toArray(new Intent[0]);
			return its[Double.valueOf(Math.floor(Math.random() * its.length)).intValue()];
		}
	}
}
