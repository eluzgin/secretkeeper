package com.skeeper.common;

import java.util.HashMap;
import java.util.ResourceBundle;

public class ResourcesHolder {
	// single instance of this class:
	private static ResourcesHolder m_instance = null;
	// Resources map:
	private HashMap<String, ResourceBundle> m_resources;

	public ResourcesHolder() {
		m_resources = new HashMap<String, ResourceBundle>();
	}

	/** Register a new resource. */
	public void addResource(String name, ResourceBundle resource) {
		m_resources.put(name, resource);
	}

	/** Get a previously registered resource. */
	public ResourceBundle getResource(String name) {
		return m_resources.get(name);
	}

	/** Lazily initialize and return a single instance of this class. */
	public static synchronized ResourcesHolder getInstance() {
		if (m_instance == null) {
			m_instance = new ResourcesHolder();
		}
		return m_instance;
	}

}

// //////////////////////////////////////////////////////////////////////////////
// $Log: ResourcesHolder.java,v $
// Revision 1.3 2006/02/16 07:10:03 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:58:58 luzgin
// CVS log added
//