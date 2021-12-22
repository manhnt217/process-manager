package io.github.manhnt217.processmgr.example;

import io.github.manhnt217.processmgr.process.*;

/**
 * @author manhnt
 */
public class FooProcessControl extends ProcessControl {
    private final String[] resources;

    public FooProcessControl(String[] resources) {
        this.resources = resources;
    }

    public String[] getResources() {
        return resources;
    }
}
