package com.skeeper.variables;

import java.awt.Dimension;
import java.util.ListResourceBundle;

public class ImageBundle extends ListResourceBundle implements ImageVariable {

	private static final Dimension D16X16 = new Dimension(16, 16);
	private static final Dimension D32X32 = new Dimension(32, 32);

	public Object[][] getContents() {
		return contents;
	}

	static final Object[][] contents = {
			{ WINDOW_COLLAPSE,
					new ImageInfo("com/skeeper/resources/window_collapse.gif", D16X16) },
			{ WINDOW_EXPAND,
					new ImageInfo("com/skeeper/resources/window_expand.gif", D16X16) },
			{ WINDOW_CLOSE,
					new ImageInfo("com/skeeper/resources/window_close.gif",	D16X16) },
			{ WINDOW_UNDOCK,
					new ImageInfo("com/skeeper/resources/window_undock.gif", D16X16) },
			{ WINDOW_DOCK,
					new ImageInfo("com/skeeper/resources/window_dock.gif", D16X16) },
			{ WINDOW_UNPIN,
					new ImageInfo("com/skeeper/resources/window_unpin.gif",	D16X16) },
			{ WINDOW_PIN,
					new ImageInfo("com/skeeper/resources/window_pin.gif", D16X16) },
			{ WINDOW_ICON,
					new ImageInfo("com/skeeper/resources/key.gif", D16X16) },
			{ WINDOW_ICON_32X32,
						new ImageInfo("com/skeeper/resources/key_32x32.gif", D32X32) },
			{ CONTROL_OPENED_VIEW,
					new ImageInfo("com/skeeper/resources/eye.gif", D16X16) },
			{ CONTROL_CLOSED_VIEW,
					new ImageInfo("com/skeeper/resources/eye_close.gif", D16X16) },
			{ CONTROL_LOCKED,
					new ImageInfo("com/skeeper/resources/lock.gif", D16X16) },
			{ CONTROL_UNLOCKED,
					new ImageInfo("com/skeeper/resources/unlock.gif", D16X16) },
			{ CONTROL_ADD,
					new ImageInfo("com/skeeper/resources/add.gif", D16X16) },
			{ CONTROL_REMOVE,
					new ImageInfo("com/skeeper/resources/remove.gif", D16X16) } };
}

// //////////////////////////////////////////////////////////////////////
// $Log: ImageBundle.java,v $
// Revision 1.2 2006/02/15 04:59:10 luzgin
// CVS log added
//