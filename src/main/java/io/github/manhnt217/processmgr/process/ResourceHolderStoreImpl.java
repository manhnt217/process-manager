package io.github.manhnt217.processmgr.process;

import java.util.*;

/**
 * @author manhnt
 */
class ResourceHolderStoreImpl implements ResourceHolderStore {

	// TODO: consider a better data structure
	private final List<ResourceHolder> resourceHolders = new ArrayList<>();

	@Override
	public Collection<ResourceHolder> listAll() {
		return Collections.unmodifiableList(resourceHolders);
	}

	@Override
	public Collection<ResourceHolder> search(Hashtable<String, Object> query) {
		ArrayList<ResourceHolder> result = new ArrayList<>();
		for (ResourceHolder resourceHolder : resourceHolders) {
			if (testResource(query, resourceHolder.getResource())) {
				result.add(resourceHolder);
			}
		}

		return result;
	}

	private boolean testResource(Hashtable<String, Object> query, Resource resource) {
		for (Map.Entry<String, Object> entry : query.entrySet()) {
			String attrName = entry.getKey();
			Object attrValue = entry.getValue(); // guarantee to be non-null by Hashtable
			if (!attrValue.equals(resource.getAttribute(attrName))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Collection<ResourceHolder> searchByType(Class<? extends Resource> resourceType, boolean includeSubtype) {
		ArrayList<ResourceHolder> result = new ArrayList<>();
		if (includeSubtype) {
			for (ResourceHolder resourceHolder : resourceHolders) {
				if (resourceType.isAssignableFrom(resourceHolder.getResource().getClass())) {
					result.add(resourceHolder);
				}
			}
		} else {
			for (ResourceHolder resourceHolder : resourceHolders) {
				if (resourceType.equals(resourceHolder.getResource().getClass())) {
					result.add(resourceHolder);
				}
			}
		}
		return result;
	}

	@Override
	public synchronized ResourceHolder newHolder(Resource r) {
        ResourceHolder holder = new ResourceHolder(r);
        resourceHolders.add(holder);
        return holder;
	}

	@Override
	public synchronized void remove(ResourceHolder i) {
		resourceHolders.remove(i);
	}
}
