package io.github.manhnt217.processmgr.process;

import io.github.manhnt217.processmgr.process.dependency.ProcessDependency;
import io.github.manhnt217.processmgr.process.exception.ProcessCanceledException;

import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author manhnt
 */
public abstract class ProcessControl implements ProcessDependency {

    private final String uuid;
    private volatile boolean cancelRequest;
    private volatile double completionRate;
    private volatile Status status;
    private Object processResult;
    private final ReentrantLock lock;
    private final Condition doneCondition;

    public ProcessControl() {
        this.uuid = UUID.randomUUID().toString();
        this.status = Status.INITIALIZED;
        this.lock = new ReentrantLock();
        this.doneCondition = lock.newCondition();
    }

    public final String getUuid() {
        return uuid;
    }

    public final Status getStatus() {
        return status;
    }

    public final void setStatus(Status status) {
        lock.lock();
        try {
            this.status = status;
            if (this.status.isDone()) {
                this.doneCondition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public final double getCompletionRate() {
        return completionRate;
    }

    public final void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public final void waitForDone() throws InterruptedException {
        if (status.isDone()) return;
        lock.lock();
        try {
            while (!status.isDone()) {
                this.doneCondition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public final synchronized void cancel() {
        cancelRequest = true;
    }

    public final synchronized void cancelCheck() throws ProcessCanceledException {
        if (cancelRequest) throw new ProcessCanceledException();
    }

    public final synchronized boolean isCanceled() {
        return cancelRequest;
    }

    public final Object getResult() {
        return processResult;
    }

    public final void setResult(Object result) {
        this.processResult = result;
    }
}
