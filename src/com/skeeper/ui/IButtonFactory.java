package com.skeeper.ui;

import com.skeeper.ui.IButton;
import com.skeeper.ui.IMouseHandler;
import com.skeeper.ui.IMouseInput;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.Hashtable;
import java.util.Iterator;

public final class IButtonFactory {

	/** Creates Text Button */
	public static final IButton createBtn(IMouseHandler handler, String name,
			Font font, Dimension size) {
		IButton res = new IButton((int) size.getWidth(),
				(int) size.getHeight(), handler, name);
		res.setFont(font);
		res.setUIHandler(new IMouseInput());
		return res;
	}

	/** Creates Image Button */
	public static final IButton createBtn(IMouseHandler handler,
			Hashtable<Integer, Image> imgMap) {
		int width = 0;
		int height = 0;
		Iterator<Integer> it = imgMap.keySet().iterator();
		while (it.hasNext()) {
			Image img =  imgMap.get(it.next());
			if (width < img.getWidth(null)) {
				width = img.getWidth(null);
			}
			if (height < img.getHeight(null)) {
				height = img.getHeight(null);
			}
		}
		IButton res = new IButton(width, height, handler, null, imgMap);
		res.setUIHandler(new IMouseInput());
		return res;
	}

}

// //////////////////////////////////////////////////////////////////////////////
// $Log: IButtonFactory.java,v $
// Revision 1.2 2006/02/15 04:59:05 luzgin
// CVS log added
//