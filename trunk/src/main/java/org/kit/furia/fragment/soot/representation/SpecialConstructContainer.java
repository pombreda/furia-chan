package org.kit.furia.fragment.soot.representation;

import java.util.List;

/**
 * A class that contains Type SootMethodRef SootClassRef
 * and the like ;)
 * @author amuller
 *
 */
public interface SpecialConstructContainer {
	/**
	 * Returns all the contained special constructs
	 * @return
	 */
	List getContainedSpecialConstructs();

}
