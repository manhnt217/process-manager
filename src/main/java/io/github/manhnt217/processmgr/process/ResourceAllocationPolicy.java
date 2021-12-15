package io.github.manhnt217.processmgr.process;

import io.github.manhnt217.processmgr.process.exception.ResourceAllocationException;

/**
 * @author manhnt
 */
public interface ResourceAllocationPolicy {

	/**
	 * @param r
	 * @param reg
	 * @return allocated holder for the resource. It can be new holder or an existing one.<br>
	 * Return <code>null</code> if this policy don't know how to deal with given {@link Resource}
	 * @throws ResourceAllocationException when it refuses to allocate new holder for the given resource
	 */
	ResourceHolder allocate(Resource r, ResourceHolderStore reg) throws ResourceAllocationException;
}
