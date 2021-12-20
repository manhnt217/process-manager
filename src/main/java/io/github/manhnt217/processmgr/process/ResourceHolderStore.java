package io.github.manhnt217.processmgr.process;

import java.util.Collection;
import java.util.Hashtable;

/**
 * @author manhnt
 */
public interface ResourceHolderStore {

	Collection<ResourceHolder> listAll();

	Collection<ResourceHolder> search(Hashtable<String, Object> query);

	Collection<ResourceHolder> searchByType(Class<? extends Resource> resourceType, boolean includeSubtype);

	ResourceHolder newHolder(Resource r);

	void remove(ResourceHolder i);
}
