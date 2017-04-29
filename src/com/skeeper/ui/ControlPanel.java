package com.skeeper.ui;

import com.skeeper.core.Context;
import com.skeeper.variables.CompNameVariable;
import com.skeeper.variables.ImageVariable;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 4316733175321731423L;

	private int BUTTON_PADDING = 2;
	private Context m_context;
	private JPanel m_viewContainer;
	private JPanel m_lockContainer;

	public ControlPanel(Context context) {
		m_context = context;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBackground(Color.lightGray);
		initControlButtons();
	}

	private void initControlButtons() {
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalStrut(BUTTON_PADDING));
		this.add(Box.createHorizontalGlue());
		// init view container:
		m_viewContainer = new JPanel(new CardLayout());
		m_viewContainer.setOpaque(false);
		// init opened view button
		Hashtable<Integer, Image> openedViewImgHash = IconFactory
				.getResourceImageMap(ImageVariable.CONTROL_OPENED_VIEW);
		IButton openedViewButton = IButtonFactory.createBtn(
				new IMouseHandler() {
					public void doAction(Component from, MouseEvent evt) {
						if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
							onRequestViewChange();
						}
					}
				}, openedViewImgHash);
		openedViewButton.setOpaque(false);
		// init closed view button
		Hashtable<Integer, Image> closedViewImgHash = IconFactory
				.getResourceImageMap(ImageVariable.CONTROL_CLOSED_VIEW);
		IButton closedViewButton = IButtonFactory.createBtn(
				new IMouseHandler() {
					public void doAction(Component from, MouseEvent evt) {
						if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
							// onRequestViewChange();
							onRequestLockStateChange();
						}
					}
				}, closedViewImgHash);
		closedViewButton.setOpaque(false);
		// init add secret button
		Hashtable<Integer, Image> addImgHash = IconFactory
				.getResourceImageMap(ImageVariable.CONTROL_ADD);
		IButton addButton = IButtonFactory.createBtn(new IMouseHandler() {
			public void doAction(Component from, MouseEvent evt) {
				if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
					onRequestAddNewSecret();
				}
			}
		}, addImgHash);
		addButton.setOpaque(false);
		// init remove secret button
		Hashtable<Integer, Image> remImgHash = IconFactory
				.getResourceImageMap(ImageVariable.CONTROL_REMOVE);
		IButton remButton = IButtonFactory.createBtn(new IMouseHandler() {
			public void doAction(Component from, MouseEvent evt) {
				if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
					onRequestRemoveSelectedSecret();
				}
			}
		}, remImgHash);
		remButton.setOpaque(false);
		// add lock/unlock buttons to the container:
		m_viewContainer.add(CompNameVariable.OPENED_STATE_COMPONENT,
				openedViewButton);
		m_viewContainer.add(CompNameVariable.CLOSED_STATE_COMPONENT,
				closedViewButton);
		CardLayout viewCardLayout = (CardLayout) m_viewContainer.getLayout();
		viewCardLayout.show(m_viewContainer,
				CompNameVariable.CLOSED_STATE_COMPONENT);
		// add viewContainer to this control panel:
		this.add(m_viewContainer);
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalStrut(BUTTON_PADDING));
		this.add(Box.createHorizontalGlue());
		// add "add button" to this panel:
		this.add(addButton);
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalStrut(BUTTON_PADDING));
		this.add(Box.createHorizontalGlue());
		// add "remove button" to this panel:
		this.add(remButton);
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalStrut(BUTTON_PADDING));
		this.add(Box.createHorizontalGlue());
		// init lock/unlock container:
		m_lockContainer = new JPanel(new CardLayout());
		m_lockContainer.setOpaque(false);
		// init lock button
		Hashtable<Integer, Image> unlockedImgHash = IconFactory
				.getResourceImageMap(ImageVariable.CONTROL_UNLOCKED);
		IButton lockButton = IButtonFactory.createBtn(new IMouseHandler() {
			public void doAction(Component from, MouseEvent evt) {
				if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
					onRequestLockStateChange();
				}
			}
		}, unlockedImgHash);
		lockButton.setOpaque(false);
		// init unlock button
		Hashtable<Integer, Image> lockedImgHash = IconFactory
				.getResourceImageMap(ImageVariable.CONTROL_LOCKED);
		IButton unlockButton = IButtonFactory.createBtn(new IMouseHandler() {
			public void doAction(Component from, MouseEvent evt) {
				if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
					onRequestLockStateChange();
				}
			}
		}, lockedImgHash);
		unlockButton.setOpaque(false);
		// add lock/unlock buttons to the container:
		m_lockContainer
				.add(CompNameVariable.OPENED_STATE_COMPONENT, lockButton);
		m_lockContainer.add(CompNameVariable.CLOSED_STATE_COMPONENT,
				unlockButton);
		CardLayout lockCardLayout = (CardLayout) m_lockContainer.getLayout();
		lockCardLayout.show(m_lockContainer,
				CompNameVariable.CLOSED_STATE_COMPONENT);
		// add container to this control panel:
		this.add(m_lockContainer);
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalStrut(BUTTON_PADDING));
		this.add(Box.createHorizontalGlue());
	}

	private void onRequestViewChange() {
		m_context.requestViewChange();
	}

	private void onRequestLockStateChange() {
		m_context.requestLockStateChange();
	}

	private void onRequestRemoveSelectedSecret() {
		m_context.requestRemoveSelectedSecret();
	}

	private void onRequestAddNewSecret() {
		m_context.requestAddNewSecret();
	}

	public void setLockStateOpen(boolean open) {
		CardLayout lockCardLayout = (CardLayout) m_lockContainer.getLayout();
		CardLayout viewCardLayout = (CardLayout) m_viewContainer.getLayout();
		if (open) {
			lockCardLayout.show(m_lockContainer,
					CompNameVariable.OPENED_STATE_COMPONENT);
			viewCardLayout.show(m_viewContainer,
					CompNameVariable.OPENED_STATE_COMPONENT);
		} else {
			lockCardLayout.show(m_lockContainer,
					CompNameVariable.CLOSED_STATE_COMPONENT);
			viewCardLayout.show(m_viewContainer,
					CompNameVariable.CLOSED_STATE_COMPONENT);
		}
		m_lockContainer.validate();
		m_viewContainer.validate();
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: ControlPanel.java,v $
// Revision 1.3 2006/02/16 07:10:15 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:04 luzgin
// CVS log added
//