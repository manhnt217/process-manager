package io.github.manhnt217.processmgr.example;

import io.github.manhnt217.processmgr.process.Process;
import io.github.manhnt217.processmgr.process.*;
import io.github.manhnt217.processmgr.process.exception.*;

/**
 * @author manhnt
 */
public class FooRunner implements Process {

    private final int fooNumber;

    public FooRunner(int fooNumber) {
        this.fooNumber = fooNumber;
    }

    @Override
    public void run(ProcessControl pc) {
        pc.setStatus(Status.RUNNING);
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println("Hello foo" + fooNumber + " " + i + " time(s).");
                pc.setCompletionRate(pc.getCompletionRate() + 0.01);
                Thread.sleep(1000);
                pc.cancelCheck();
            }
        } catch (InterruptedException e) {
            System.out.println("Interupted");
            e.printStackTrace();
        } catch (ProcessCanceledException e) {
            pc.setStatus(Status.FAILED);
            System.out.printf(fooNumber + " canceled");
        }
        System.out.println("====");
    }
}
