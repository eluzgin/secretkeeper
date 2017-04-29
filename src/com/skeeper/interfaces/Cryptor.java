package com.skeeper.interfaces;

public interface Cryptor {

	public String encode(String txt, String key);

	public String decode(String txt, String key);

}

// //////////////////////////////////////////////////////////////////////
// $Log: Cryptor.java,v $
// Revision 1.2 2006/02/15 04:59:02 luzgin
// CVS log added
//