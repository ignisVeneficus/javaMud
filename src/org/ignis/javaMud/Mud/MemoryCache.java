package org.ignis.javaMud.Mud;


import org.apache.commons.collections4.map.LRUMap;
import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.Core.Singleton;


public class MemoryCache
        extends LRUMap<String, Singleton>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MemoryCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected boolean removeLRU(LinkEntry<String, Singleton> entry) {
		return entry.getValue()._destrObject();
	}
	
}
