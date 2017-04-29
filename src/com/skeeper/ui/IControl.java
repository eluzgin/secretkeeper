package com.skeeper.ui;

import java.awt.event.MouseEvent;
import java.awt.Rectangle;

public interface IControl {
	public void mouseOver(boolean over);

	public void mouseDown(boolean down);

	public void mouseClick(MouseEvent e);

	public void doAction(MouseEvent e);

	public Rectangle getControlRectangle();
}

// //////////////////////////////////////////////////////////////////////
// $Log: IControl.java,v $
// Revision 1.2 2006/02/15 04:59:05 luzgin
// CVS log added
//
