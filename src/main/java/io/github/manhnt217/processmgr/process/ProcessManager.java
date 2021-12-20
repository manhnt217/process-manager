package io.github.manhnt217.processmgr.process;

import io.github.manhnt217.processmgr.process.exception.ProcessCanceledException;
import io.github.manhnt217.processmgr.process.exception.ResourceAllocationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author manhnt
 */
public final class ProcessManager {

    private static final ProcessManager INSTANCE = new ProcessManager();

    private static final int NUM_INITIAL_THREAD = 100;
    private static final int NUM_MAX_THREAD = 1000000;
    private final ResourceHolderStoreImpl resourceHolderStore;
    private final Map<ResourceHolder, List<ProcessControl>> resourceToProcessMap;
    private final ExecutorService executorService;

    public static ProcessManager get() {
        return INSTANCE;
    }

    private final List<ResourceAllocationPolicy> policies = new ArrayList<>();

    private ProcessManager() {
        this.resourceHolderStore = new ResourceHolderStoreImpl();
        this.resourceToProcessMap = new HashMap<>();
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        executorService = new ThreadPoolExecutor(NUM_INITIAL_THREAD, NUM_MAX_THREAD, 1, TimeUnit.MINUTES, queue);
    }

    public void registerPolicy(ResourceAllocationPolicy p) {
        this.policies.add(p);
    }

    private ProcessControl allocate(Resource r) throws ResourceAllocationException {
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

    public synchronized ProcessControl execute(Resource r, Process process) throws ResourceAllocationException {
        ProcessControl pc = this.allocate(r);
        pc.setProcess(process);

        executorService.submit(
                () -> {
                    ResourceHolder resourceHolder = pc.getResourceHolder();
                    resourceHolder.lock();
                    try {
                        pc.cancelCheck();
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
        );

        return pc;
    }

    private void associate(ResourceHolder resourceHolder, ProcessControl pc) {
        List<ProcessControl> pcs = this.resourceToProcessMap
                .computeIfAbsent(resourceHolder, i -> new ArrayList<>());
        pcs.add(pc);
        pc.setResourceHolder(resourceHolder);
    }

    private synchronized void dissociate(ProcessControl pc) {
        ResourceHolder resourceHolder = pc.getResourceHolder();
        List<ProcessControl> processControls = this.resourceToProcessMap.get(resourceHolder);
        processControls.remove(pc);
        pc.setResourceHolder(null);
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

    public List<ProcessControl> getAll() {
        return resourceToProcessMap.values().stream()
                .flatMap(l -> l.stream().filter(ProcessManager::notCanceled)).collect(Collectors.toList());
    }

    private static boolean notCanceled(ProcessControl pc) {
        try {
            pc.cancelCheck();
            return true;
        } catch (ProcessCanceledException e) {
            return false;
        }
    }
}
