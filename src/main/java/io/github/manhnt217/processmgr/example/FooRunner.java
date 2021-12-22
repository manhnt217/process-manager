package io.github.manhnt217.processmgr.example;

import io.github.manhnt217.processmgr.process.Process;
import io.github.manhnt217.processmgr.process.ProcessControl;
import io.github.manhnt217.processmgr.process.Status;
import io.github.manhnt217.processmgr.process.dependency.ProcessDependency;
import io.github.manhnt217.processmgr.process.exception.ProcessCanceledException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author manhnt
 */
public class FooRunner implements Process {

    public static final int PARALLEL_LEVEL = 3;
    private String[] resources;

    public FooRunner(String[] resources) {
        this.resources = resources;
    }

    @Override
    public void run(ProcessControl pc) {
        pc.setStatus(Status.RUNNING);
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println("Running....");
                pc.setCompletionRate(pc.getCompletionRate() + 0.01);
                Thread.sleep(100);
                pc.cancelCheck();
            }
            pc.setStatus(Status.COMPLETED);
        } catch (InterruptedException e) {
            System.out.println("Interupted");
            e.printStackTrace();
            pc.setStatus(Status.FAILED);
        } catch (ProcessCanceledException e) {
            pc.setStatus(Status.FAILED);
        }
        System.out.println("====");
    }

	@Override
	public ProcessDependency getDependency(Collection<ProcessControl> runningProcesses) {
        if (this.resources.length == 0) return ProcessDependency.EMPTY_DEPENDENCY;
        ProcessDependency[] dependencies = runningProcesses.stream()
                .filter(pc -> pc instanceof io.github.manhnt217.processmgr.example.FooProcessControl)
                .filter(pc -> !Collections.disjoint(Arrays.asList(((io.github.manhnt217.processmgr.example.FooProcessControl) pc).getResources()), Arrays.asList(resources)))
                .toArray(ProcessDependency[]::new);
        if (dependencies.length == 0) return ProcessDependency.EMPTY_DEPENDENCY;
        return ProcessDependency.allOf(dependencies);
//        if (dependencies.length < 2) return ProcessDependency.EMPTY_DEPENDENCY;
//        return ProcessDependency.someOf(dependencies.length - 1, dependencies);
    }

    @Override
    public ProcessControl newProcessControl() {
        return new io.github.manhnt217.processmgr.example.FooProcessControl(resources);
    }
}
