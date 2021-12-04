package io.github.manhnt217.progressmgr.process;

/**
 * @author manhnt
 */
public interface IntentAllocationPolicy {

	/**
	 *
	 * @param r
	 * @param a
	 * @param reg
	 * @return allocated intent. It can be new intent or an existing one.<br>
	 * 		 	Return <code>null</code> if this policy don't know how to deal with given {@link Resource} and {@link Action}
	 * @throws IntentAllocationException when it refuses to allocate new intent (eg: Out of resource)
	 */
	Intent allocateIntent(Resource r, Action a, IntentStore reg) throws IntentAllocationException;
}
