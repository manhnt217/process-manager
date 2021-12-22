package io.github.manhnt217.processmgr.process;

import io.github.manhnt217.processmgr.process.dependency.ProcessDependency;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author manhnt
 */
public final class ProcessManager {

    private static final ProcessManager INSTANCE = new ProcessManager();

    private static final int NUM_INITIAL_THREAD = 100;
    private static final int NUM_MAX_THREAD = 1000000;
    private static final ThreadLocal<ProcessControl> currentPC = new ThreadLocal<>();

    private final ExecutorService executorService;
    private final Set<ProcessControl> processes;

    public static ProcessManager get() {
        return INSTANCE;
    }

    private ProcessManager() {
        this.processes = new LinkedHashSet<>();
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        executorService = new ThreadPoolExecutor(NUM_INITIAL_THREAD, NUM_MAX_THREAD, 1, TimeUnit.MINUTES, queue);
    }

    public synchronized ProcessControl execute(Process process) {
        ProcessControl pc = process.newProcessControl();
        ProcessDependency dependency = process.getDependency(this.getAll());
        this.processes.add(pc);

        executorService.submit(
                () -> {
                    currentPC.set(pc);
                    try {
                        dependency.waitForDone();
                        pc.cancelCheck();
                        process.run(pc);
                    } catch (Exception e) {
                        pc.setStatus(Status.FAILED);
                    } finally {
                        this.processes.remove(pc);
                        currentPC.remove();
                        // TODO: Store the log (in db) for ProcessControl
                    }
                }
        );

        return pc;
    }

    public ProcessControl getProcessByUUID(String uuid) {
        for (ProcessControl pc : this.processes) {
            if (pc.getUuid().equals(uuid)) {
                return pc;
            }
        }

        throw new IllegalArgumentException("Process not found");
    }

    public Collection<ProcessControl> getAll() {
        return processes.stream().filter(ProcessManager::notCanceled).collect(Collectors.toList());
    }

    private static boolean notCanceled(ProcessControl pc) {
        return !pc.isCanceled();
    }

    public void shutdown() throws InterruptedException {
        this.executorService.shutdown();
        this.executorService.awaitTermination(1, TimeUnit.MINUTES);
        this.executorService.shutdownNow();
    }

    public static ProcessControl getCurrentPC() {
        ProcessControl pc = currentPC.get();
        if (pc == null) {
            throw new IllegalStateException("Not called in any process");
        }
        return pc;
    }
}
