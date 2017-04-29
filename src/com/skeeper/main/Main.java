package com.skeeper.main;

import com.skeeper.core.Context;
import java.io.File;
import java.util.Locale;

public class Main {
	private static UIManager m_uiMgr;
	private static Context m_context;

	public static void main(String[] args) {
		final File homeDir;
		if (args.length < 1) {
			// If directory not passed - use current dir:
			homeDir = new File(".");
		} else {
			String homeDirPath = args[0];
			homeDir = new File(homeDirPath);
			if (!homeDir.isDirectory()) {
				System.err.println(homeDirPath + " is not a directory!");
				System.exit(2);
			}
			if (!homeDir.canWrite()) {
				System.err.println("Can't write to " + homeDirPath);
				System.exit(3);
			}
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				m_uiMgr = new UIManager(new Locale("en", "US"));
				m_uiMgr.start();
				try {
					m_context = new Context(homeDir);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(2);
				} finally {
					m_uiMgr.setContent(m_context.getGUI());
				}
			}
		});
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: Main.java,v $
// Revision 1.2 2006/02/15 04:59:03 luzgin
// CVS log added
//

