package io.github.manhnt217.processmgr.example;

import io.github.manhnt217.processmgr.process.*;
import io.github.manhnt217.processmgr.process.exception.*;

import java.util.Collection;
import java.util.stream.*;

/**
 * @author manhnt
 */
public class FooPolicy implements ResourceAllocationPolicy {
	public static final int PARALLEL_LEVEL = 1;

	@Override
	public ResourceHolder allocate(Resource r, ResourceHolderStore reg) throws ResourceAllocationException {
		if (!(r instanceof FooResource)) return null;

		Collection<ResourceHolder> resourceHolders = reg.searchByType(FooResource.class, false)
                .stream().filter(rh -> rh.getResource().equals(r)).collect(Collectors.toList());
		if (resourceHolders.size() < PARALLEL_LEVEL) {
			ResourceHolder resourceHolder = reg.newHolder(r);
            return resourceHolder;
		} else {
			ResourceHolder[] its = resourceHolders.toArray(new ResourceHolder[0]);
			return its[Double.valueOf(Math.floor(Math.random() * its.length)).intValue()];
		}
	}
}
