package com.skeeper.variables;

import java.util.ListResourceBundle;

public class TextBundle extends ListResourceBundle implements TextVariable {

	public Object[][] getContents() {
		return contents;
	}

	static final Object[][] contents = {
			{ NEW_PASSWORD_DIALOG_LABEL, "New Password: " },
			{ NEW_PASSWORD_DIALOG_TITLE, "Create New Password" },
			{ ADD_SECRET_DIALOG_SITE_LABEL, "Site: " },
			{ ADD_SECRET_DIALOG_LOGIN_LABEL, "Login: " },
			{ ADD_SECRET_DIALOG_PASSWORD_LABEL, "Password: " },
			{ ADD_SECRET_DIALOG_ADD_BUTTON, "Add" },
			{ ADD_SECRET_DIALOG_CANCEL_BUTTON, "Cancel" },
			{ ADD_SECRET_DIALOG_TITLE, "Add new login entry" },
			{ PASSWORD_PROMPT_DIALOG_LABEL, "Password: " },
			{ PASSWORD_PROMPT_DIALOG_TITLE, "Password Prompt" },
			{ NOT_AUTHORIZED_MESSAGE, "You are not authorized!" } };
}

// //////////////////////////////////////////////////////////////////////
// $Log: TextBundle.java,v $
// Revision 1.2 2006/02/15 04:59:12 luzgin
// CVS log added
//