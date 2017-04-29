package com.skeeper.core;

public class LoginInfo {

	private String m_loginCode;
	private String m_passCode;

	public LoginInfo(String loginCode, String passCode) {
		m_loginCode = loginCode;
		m_passCode = passCode;
	}

	public String getLoginCode() {
		return m_loginCode;
	}

	public String getPassCode() {
		return m_passCode;
	}
}

// //////////////////////////////////////////////////////////////////////
// $Log: LoginInfo.java,v $
// Revision 1.2 2006/02/15 04:59:01 luzgin
// CVS log added
//
