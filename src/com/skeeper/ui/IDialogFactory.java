package com.skeeper.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.util.Hashtable;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class IDialogFactory {

	private static final IDialogFactory m_instance = new IDialogFactory();
	private Hashtable<String, IFrame> m_dialogs;

	private IDialogFactory() {
		m_dialogs = new Hashtable<String, IFrame>(5);
	}

	private boolean registerDialog(String title, IFrame dialog) {
		if (isRegistered(title)) {
			return false;
		} else {
			m_dialogs.put(title, dialog);
			return true;
		}
	}

	private void unregisterDialog(String title) {
		IFrame dialog = m_dialogs.get(title);
		m_dialogs.remove(title);
		dialog.dispose();
	}

	private boolean isRegistered(String title) {
		if (m_dialogs.containsKey(title)) {
			return true;
		} else {
			return false;
		}
	}

	private IFrame getDialog(String title) {
		return m_dialogs.get(title);
	}

	public static IFrame createDialog(String title, Component content,
			Dimension size) {
		IFrame savedDialog = m_instance.getDialog(title);
		if (savedDialog != null) {
			if (savedDialog.isVisible()) {
				return savedDialog;
			} else {
				m_instance.unregisterDialog(title);
			}
		}
		IFrame dialog = new IFrame(title, false, false, true);
		// register:
		m_instance.registerDialog(title, dialog);

		dialog.setDefaultCloseOperation(IFrame.DISPOSE_ON_CLOSE);
		dialog.getContentPane().add(content);
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();
		dialog.setBounds((maxBounds.width - width) / 2,
				(maxBounds.height - height) / 2, width, height);
		dialog.setVisible(true);
		return dialog;
	}

	public static Object showInputDialog(Component parentComponent,
			Object message, String title, int messageType, Dimension size)
			throws HeadlessException {
		JOptionPane pane = new JOptionPane(message, messageType,
				JOptionPane.OK_CANCEL_OPTION, null, null, null);
		pane.setWantsInput(true);
		pane.setSelectionValues(null);
		pane.setInitialSelectionValue(null);
		JDialog dialog = pane.createDialog(parentComponent, title);
		pane.selectInitialValue();
		iterateTextField(dialog.getContentPane());
		dialog.setSize(size);
		dialog.setVisible(true);
		dialog.dispose();
		Object value = pane.getInputValue();
		if (value == JOptionPane.UNINITIALIZED_VALUE) {
			return null;
		}
		return value;
	}

	private static void secureTextField(final JTextField textField) {
		Container parent = textField.getParent();
		textField.setVisible(false);
		final JPasswordField pwdField = new JPasswordField(8);
		pwdField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				textField.setText(new String(pwdField.getPassword()));
			}
		});
		parent.add(pwdField, GridBagConstraints.REMAINDER);
		parent.validate();
	}

	private static void iterateTextField(Container container) {
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JTextField) {
				secureTextField((JTextField) components[i]);
			} else if (((Container) components[i]).getComponentCount() > 0) {
				iterateTextField((Container) components[i]);
			}
		}
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: IDialogFactory.java,v $
// Revision 1.3 2006/02/16 07:10:21 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:06 luzgin
// CVS log added
//