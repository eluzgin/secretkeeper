package com.skeeper.ui;

import java.awt.*;
import java.awt.event.*;

public class IMouseInput implements MouseListener, MouseMotionListener {
	private IControl m_owner = null;
	private Component m_comp = null;

	private final void attach() {
		m_comp.addMouseListener(this);
		m_comp.addMouseMotionListener(this);
	}

	public final void attach(IControl owner, Component comp) {
		detach();
		m_owner = owner;
		m_comp = comp;
		attach();
	}

	public final void detach() {
		if (m_comp != null) {
			m_comp.removeMouseListener(this);
			m_comp.removeMouseMotionListener(this);
			m_comp = null;
		}
		m_owner = null;
	}

	// >>MouseListener implementation
	public void mouseClicked(MouseEvent e) {
		Rectangle rect = m_owner.getControlRectangle();
		int x = e.getX();
		int y = e.getY();
		if (rect.contains(x, y)) {
			m_owner.doAction(e);
		}
	}

	public void mouseEntered(MouseEvent e) {
		Rectangle rect = m_owner.getControlRectangle();
		int x = e.getX();
		int y = e.getY();
		if (rect.contains(x, y)) {
			m_owner.mouseOver(true);
		} else {
			m_owner.mouseOver(false);
		}
		m_owner.doAction(e);
	}

	public void mouseExited(MouseEvent e) {
		m_owner.mouseOver(false);
		m_owner.doAction(e);
	}

	public void mousePressed(MouseEvent e) {
		Rectangle rect = m_owner.getControlRectangle();
		int x = e.getX();
		int y = e.getY();
		if (rect.contains(x, y)) {
			if ((e.getModifiers() | MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {
				m_owner.mouseDown(true);
			}
			m_owner.doAction(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		Rectangle rect = m_owner.getControlRectangle();
		int x = e.getX();
		int y = e.getY();

		if (rect.contains(x, y)) {
			m_owner.mouseClick(e);
		}
		if ((e.getModifiers() | MouseEvent.BUTTON1_MASK) != MouseEvent.BUTTON1_MASK) {
			m_owner.mouseDown(false);
		}
		if (!rect.contains(x, y)) {
			m_owner.mouseOver(false);
		}
		if (rect.contains(x, y)) {
			m_owner.doAction(e);
		}
	}

	// <<MouseListener implementation

	// >>MouseMotionListener implementaion
	public void mouseDragged(MouseEvent e) {
		Rectangle rect = m_owner.getControlRectangle();
		int x = e.getX();
		int y = e.getY();
		if (rect.contains(x, y)) {
			m_owner.mouseDown(true);
			m_owner.mouseOver(true);
			m_owner.doAction(e);
		} else {
			m_owner.mouseDown(false);
		}
	}

	public void mouseMoved(MouseEvent e) {
		Rectangle rect = m_owner.getControlRectangle();
		int x = e.getX();
		int y = e.getY();
		if (rect.contains(x, y)) {
			m_owner.mouseOver(true);
			m_owner.doAction(e);
		} else {
			m_owner.mouseOver(false);
		}
	}
	// <<MouseMotionListener implementaion

}

// //////////////////////////////////////////////////////////////////////
// $Log: IMouseInput.java,v $
// Revision 1.3 2006/02/16 07:10:26 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:08 luzgin
// CVS log added
//