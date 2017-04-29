package com.skeeper.core;

import java.util.Hashtable;
import java.util.Set;

public class SecretTable {

	private String m_rootPassCode = null;
	private Hashtable<String, LoginInfo> m_table;

	public SecretTable() {
		m_table = new Hashtable<String, LoginInfo>(10);
	}

	public void addEntry(String key, LoginInfo loginInfo) {
		m_table.put(key, loginInfo);
	}

	public void removeEntry(String key) {
		m_table.remove(key);
	}

	public LoginInfo getLoginInfo(String key) {
		return m_table.get(key);
	}

	public void setRootPassCode(String rootPassCode) {
		m_rootPassCode = rootPassCode;
	}

	public String getRootPassCode() {
		return m_rootPassCode;
	}

	public void reset() {
		m_table.clear();
	}

	public Set<String> getKeys() {
		return m_table.keySet();
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: SecretTable.java,v $
// Revision 1.3 2006/02/16 07:10:09 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:01 luzgin
// CVS log added
//
