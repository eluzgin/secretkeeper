package com.skeeper.core;

import com.skeeper.common.ResourcesHolder;
import com.skeeper.core.LoginInfo;
import com.skeeper.interfaces.StateListener;
import com.skeeper.ui.ControlPanel;
import com.skeeper.ui.IDialogFactory;
import com.skeeper.ui.IFrame;
import com.skeeper.ui.SpringUtilities;
import com.skeeper.ui.ViewPanel;
import com.skeeper.variables.CompNameVariable;
import com.skeeper.variables.TextVariable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class Context {

	private static final int OPEN_TIMEOUT = 5 * 60 * 1000; // 5 minutes
	private static final String SEPARATOR = File.separator;
	private final IOXmlAdapter m_ioXmlAdapter;
	private final File m_secretFile;
	private JPanel m_rootPanel;
	private JPanel m_addNewSecretPanel;
	private ViewPanel m_viewPanel;
	private ControlPanel m_ctrlPanel;
	private AppState m_state;
	private ResourceBundle m_textBundle;

	public Context(File homeDir) throws Exception {
		m_textBundle = ResourcesHolder.getInstance().getResource(
				CompNameVariable.TEXT_RESOURCE);
		m_secretFile = new File(homeDir.getAbsolutePath() + SEPARATOR
				+ CompNameVariable.SECRETS_FILE);
		m_ioXmlAdapter = new IOXmlAdapter();
		m_state = AppState.getInstance();
		// Init content Panel for main window
		m_rootPanel = new JPanel();
		m_viewPanel = new ViewPanel();
		m_ctrlPanel = new ControlPanel(this);
		m_rootPanel.setLayout(new BorderLayout());
		m_rootPanel.add(m_viewPanel, BorderLayout.CENTER);
		m_rootPanel.add(m_ctrlPanel, BorderLayout.EAST);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					// Load xml table if file exists
					SecretTable table = null;
					if (m_secretFile.exists()) {
						table = m_ioXmlAdapter.readXmlFile(m_secretFile);
					} else { // else create a new one
						String pwd = (String) IDialogFactory.showInputDialog(
								null,
								m_textBundle
										.getString(TextVariable.NEW_PASSWORD_DIALOG_LABEL),
								m_textBundle
										.getString(TextVariable.NEW_PASSWORD_DIALOG_TITLE),
								JOptionPane.QUESTION_MESSAGE, new Dimension(
										400, 150));
						String pwdCode = CryptorImpl.encrypt(pwd,
								pwd.toCharArray());
						pwd = null;
						table = m_ioXmlAdapter.createNewXmlFile(m_secretFile,
								pwdCode);
					}
					m_viewPanel.updateList(table.getKeys());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		// Set listener
		m_state.setStateListener(new StateListener() {
			public void onStateOpen() {
				m_ctrlPanel.setLockStateOpen(true);
			}

			public void onStateClose() {
				m_ctrlPanel.setLockStateOpen(false);
				m_viewPanel.showSelection();
			}
		});

		// Create content Panel for "add new secret" dialog:
		m_addNewSecretPanel = new JPanel(new BorderLayout());
		JPanel infoPanel = new JPanel(new SpringLayout());
		JPanel ctrlPanel = new JPanel(new FlowLayout());
		m_addNewSecretPanel.add(infoPanel, BorderLayout.CENTER);
		m_addNewSecretPanel.add(ctrlPanel, BorderLayout.SOUTH);
		// Add site entry:
		final JLabel siteLabel = new JLabel(
				m_textBundle
						.getString(TextVariable.ADD_SECRET_DIALOG_SITE_LABEL),
				JLabel.TRAILING);
		infoPanel.add(siteLabel);
		final JTextField siteText = new JTextField(10);
		siteLabel.setLabelFor(siteText);
		infoPanel.add(siteText);
		// Add login entry:
		final JLabel loginLabel = new JLabel(
				m_textBundle
						.getString(TextVariable.ADD_SECRET_DIALOG_LOGIN_LABEL),
				JLabel.TRAILING);
		infoPanel.add(loginLabel);
		final JTextField loginText = new JTextField(10);
		loginLabel.setLabelFor(loginText);
		infoPanel.add(loginText);
		// Add password entry:
		final JLabel passwordLabel = new JLabel(
				m_textBundle
						.getString(TextVariable.ADD_SECRET_DIALOG_PASSWORD_LABEL),
				JLabel.TRAILING);
		infoPanel.add(passwordLabel);
		final JPasswordField passwordField = new JPasswordField(8);
		passwordLabel.setLabelFor(passwordField);
		infoPanel.add(passwordField);
		// Add add button:
		final JButton addBtn = new JButton(
				m_textBundle
						.getString(TextVariable.ADD_SECRET_DIALOG_ADD_BUTTON));
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String site = siteText.getText();
				String login = loginText.getText();
				char[] password = passwordField.getPassword();
				char[] rootPassword = m_state.getPassword();
				// Clear all text fields:
				siteText.setText("");
				loginText.setText("");
				passwordField.setText("");
				if (rootPassword == null) {
					rootPassword = requestRootPassword();
				}
				try {
					if (rootPassword != null) {
						String loginCode = CryptorImpl.encrypt(login,
								rootPassword);
						String passwordCode = CryptorImpl.encrypt(new String(
								password), rootPassword);
						LoginInfo logInfo = new LoginInfo(loginCode,
								passwordCode);
						m_ioXmlAdapter.addSecret(site, logInfo);
						m_viewPanel.updateList(m_ioXmlAdapter.getSecretTable()
								.getKeys());
						closeParentIFrame(addBtn);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		ctrlPanel.add(addBtn);
		// Add cancel button:
		final JButton cancelBtn = new JButton(
				m_textBundle
						.getString(TextVariable.ADD_SECRET_DIALOG_CANCEL_BUTTON));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// Clear all text fields:
				siteText.setText("");
				loginText.setText("");
				passwordField.setText("");
				closeParentIFrame(cancelBtn);
			}
		});
		ctrlPanel.add(cancelBtn);
		// Lay out the panel.
		SpringUtilities.makeCompactGrid(infoPanel, 3, 2, 6, 6, 6, 6);
	}

	private void closeParentIFrame(Component child) {
		Container container = child.getParent();
		if (container != null) {
			while (container.getParent() != null) {
				container = container.getParent();
				if (container instanceof IFrame) {
					((IFrame) container).dispose();
				}
			}
		}
	}

	public Component getGUI() {
		return (Component) m_rootPanel;
	}

	public void requestViewChange() {
		String viewName = m_viewPanel.getVisiblePanelName();
		if (viewName.equals(CompNameVariable.SELECT_PANEL)
				&& m_state.isOpenState()) {
			String key = m_viewPanel.getSelection();
			LoginInfo loginInfo = m_ioXmlAdapter.getSecretTable().getLoginInfo(
					key);
			try {
				m_viewPanel.showSecret(CryptorImpl.decrypt(
						loginInfo.getLoginCode(), m_state.getPassword())
						+ ", "
						+ CryptorImpl.decrypt(loginInfo.getPassCode(),
								m_state.getPassword()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (viewName.equals(CompNameVariable.SECRET_PANEL)) {
			m_viewPanel.showSelection();
		}
	}

	public void requestLockStateChange() {
		if (m_state.isOpenState()) {
			m_state.setStateClosed();
		} else {
			char[] password = requestRootPassword();
			if (password != null) {
				m_state.setOpenState(password, OPEN_TIMEOUT);
			}
		}
	}

	public void requestRemoveSelectedSecret() {
		if (m_state.isOpenState() || requestRootPassword() != null) {
			String key = m_viewPanel.getSelection();
			m_ioXmlAdapter.removeSecret(key);
			m_viewPanel.updateList(m_ioXmlAdapter.getSecretTable().getKeys());
		}
	}

	public void requestAddNewSecret() {
		IDialogFactory.createDialog(
				m_textBundle.getString(TextVariable.ADD_SECRET_DIALOG_TITLE),
				m_addNewSecretPanel, new Dimension(400, 150));
	}

	private char[] requestRootPassword() {
		String pwd = (String) IDialogFactory.showInputDialog(null, m_textBundle
				.getString(TextVariable.PASSWORD_PROMPT_DIALOG_LABEL),
				m_textBundle
						.getString(TextVariable.PASSWORD_PROMPT_DIALOG_TITLE),
				JOptionPane.QUESTION_MESSAGE, new Dimension(400, 150));
		if (validateRootPassword(pwd)) {
			return pwd.toCharArray();
		} else {
			return null;
		}
	}

	private boolean validateRootPassword(String password) {
		try {
			if (password == null) {
				return false;
			}
			String passwordCode = CryptorImpl.encrypt(password,
					password.toCharArray());
			if (passwordCode != null
					&& passwordCode.equals(m_ioXmlAdapter.getRootPassCode())) {
				return true;
			} else {
				JOptionPane.showMessageDialog(null, m_textBundle
						.getString(TextVariable.NOT_AUTHORIZED_MESSAGE));
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: Context.java,v $
// Revision 1.3 2006/02/16 07:10:06 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:58:59 luzgin
// CVS log added
//