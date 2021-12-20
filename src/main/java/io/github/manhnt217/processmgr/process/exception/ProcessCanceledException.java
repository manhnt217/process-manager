package io.github.manhnt217.processmgr.process.exception;

/**
 * @author manhnt
 */
public class ProcessCanceledException extends ProcessException {
    public ProcessCanceledException() {
        super("Canceled by user");
    }
}
