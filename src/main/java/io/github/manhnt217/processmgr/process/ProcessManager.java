package io.github.manhnt217.processmgr.process;

import io.github.manhnt217.processmgr.process.exception.ResourceAllocationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author manhnt
 */
public final class ProcessManager {

	private static final ProcessManager INSTANCE = new ProcessManager();
	private final ResourceHolderStoreImpl resourceHolderStore;
	private final Map<ResourceHolder, List<ProcessControl>> resourceToProcessMap;

	public static ProcessManager get() {
		return INSTANCE;
	}

	private final List<ResourceAllocationPolicy> policies = new ArrayList<>();

	private ProcessManager() {
		this.resourceHolderStore = new ResourceHolderStoreImpl();
		this.resourceToProcessMap = new HashMap<>();
	}

	public void registerPolicy(ResourceAllocationPolicy p) {
		this.policies.add(p);
	}

	public synchronized ProcessControl allocate(Resource r) throws ResourceAllocationException {
		ProcessControl pc = new ProcessControl();
		for (ResourceAllocationPolicy policy : policies) {
			ResourceHolder resourceHolder = policy.allocate(r, resourceHolderStore);
			if (resourceHolder != null) {
				associate(resourceHolder, pc);
				return pc;
			}
		}

		throw new ResourceAllocationException("No ResourceAllocationPolicy found for resource " + r);
	}

	public synchronized void execute(ProcessControl pc) {

		// TODO: Consider using ExecutorService
		new Thread(
				() -> {
					ResourceHolder resourceHolder = findResourceByProcess(pc);
					resourceHolder.lock();
					try {
						pc.run();
						pc.setStatus(Status.COMPLETED);
					} catch (Exception e) {
						pc.setStatus(Status.FAILED);
					} finally {
						resourceHolder.unlock();
						dissociate(pc);
						// TODO: Store the log (in db) for ProcessControl
					}
				}
		).start();
	}

	private ResourceHolder findResourceByProcess(ProcessControl pc) {
		for (Map.Entry<ResourceHolder, List<ProcessControl>> entry : resourceToProcessMap.entrySet()) {
			if (entry.getValue().contains(pc)) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("Resource not found for given process");
	}

	private void associate(ResourceHolder resourceHolder, ProcessControl pc) {
		List<ProcessControl> pcs = this.resourceToProcessMap
				.computeIfAbsent(resourceHolder, i -> new ArrayList<>());
		pcs.add(pc);
	}

	private synchronized void dissociate(ProcessControl pc) {
		ResourceHolder resourceHolder = findResourceByProcess(pc);
		List<ProcessControl> processControls = this.resourceToProcessMap.get(resourceHolder);
		processControls.remove(pc);
		if (processControls.isEmpty()) {
			this.resourceToProcessMap.remove(resourceHolder);
			this.resourceHolderStore.remove(resourceHolder);
		}
	}

	public ProcessControl getProcessByUUID(String uuid) {
		for (List<ProcessControl> pcs : resourceToProcessMap.values()) {
			for (ProcessControl pc : pcs) {
				if (pc.getUuid().equals(uuid)) {
					return pc;
				}
			}
		}

		throw new IllegalArgumentException("Process not found");
	}
}
