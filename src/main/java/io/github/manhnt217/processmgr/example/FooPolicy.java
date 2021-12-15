package io.github.manhnt217.processmgr.example;

import io.github.manhnt217.processmgr.process.Resource;
import io.github.manhnt217.processmgr.process.ResourceAllocationPolicy;
import io.github.manhnt217.processmgr.process.ResourceHolder;
import io.github.manhnt217.processmgr.process.ResourceHolderStore;
import io.github.manhnt217.processmgr.process.exception.ResourceAllocationException;

import java.util.Collection;

/**
 * @author manhnt
 */
public class FooPolicy implements ResourceAllocationPolicy {
	public static final int PARALLEL_LEVEL = 1;

	@Override
	public ResourceHolder allocate(Resource r, ResourceHolderStore reg) throws ResourceAllocationException {
		if (!(r instanceof FooResource)) return null;

		Collection<ResourceHolder> resourceHolders = reg.searchByType(FooResource.class, false);
		if (resourceHolders.size() < PARALLEL_LEVEL) {
			ResourceHolder resourceHolder = new ResourceHolder(r);
			reg.insert(resourceHolder);
			return resourceHolder;
		} else {
			ResourceHolder[] its = resourceHolders.toArray(new ResourceHolder[0]);
			return its[Double.valueOf(Math.floor(Math.random() * its.length)).intValue()];
		}
	}
}
