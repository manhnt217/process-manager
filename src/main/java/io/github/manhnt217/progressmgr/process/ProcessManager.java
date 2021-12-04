package io.github.manhnt217.progressmgr.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author manhnt
 */
public final class ProcessManager {

	private static ProcessManager INSTANCE = new ProcessManager();
	private final IntentStoreImpl intentStore;
	private final Map<Intent, List<ProcessControl>> intentProcessAssociations;

	public static ProcessManager get() {
		return INSTANCE;
	}

	private List<IntentAllocationPolicy> policies = new ArrayList<>();

	private ProcessManager() {
		this.intentStore = new IntentStoreImpl();
		this.intentProcessAssociations = new HashMap<>();
	}

	public void registerPolicy(IntentAllocationPolicy p) {
		this.policies.add(p);
	}

	public synchronized ProcessControl allocate(Resource r, Action a) throws IntentAllocationException {
		ProcessControl pc = new ProcessControl();
		for (IntentAllocationPolicy policy : policies) {
			Intent intent = policy.allocateIntent(r, a, intentStore);
			if (intent != null) {
				associate(intent, pc);
				return pc;
			}
		}

		throw new IntentAllocationException("No IntentAllocationPolicy found for resource " + r + " and action " + a);
	}

	public synchronized void execute(ProcessControl pc) {

		// TODO: Consider using ExecutorService
		new Thread(
				() -> {
					Intent intent = findIntentByProcessControl(pc);
					intent.lock();
					try {
						pc.run();
						pc.setStatus(Status.COMPLETED);
					} catch (Exception e) {
						pc.setStatus(Status.FAILED);
					} finally {
						intent.unlock();
						dissociate(pc);
						// TODO: Store the log for ProcessControl
					}
				}
		).start();
	}

	private Intent findIntentByProcessControl(ProcessControl pc) {
		for (Map.Entry<Intent, List<ProcessControl>> entry : intentProcessAssociations.entrySet()) {
			if (entry.getValue().contains(pc)) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("Intent not found for given process");
	}

	private void associate(Intent intent, ProcessControl pc) {
		List<ProcessControl> pcs = this.intentProcessAssociations
				.computeIfAbsent(intent, i -> new ArrayList<>());
		pcs.add(pc);
	}

	private synchronized void dissociate(ProcessControl pc) {
		Intent intent = findIntentByProcessControl(pc);
		List<ProcessControl> processControls = this.intentProcessAssociations.get(intent);
		processControls.remove(pc);
		if (processControls.isEmpty()) {
			this.intentProcessAssociations.remove(intent);
			this.intentStore.removeIntent(intent);
		}
	}
}
