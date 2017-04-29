package com.skeeper.ui;

import com.skeeper.variables.CompNameVariable;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ViewPanel extends JPanel {

	private static final long serialVersionUID = 12784638743210343L;

	private CardLayout m_cardLayout = new CardLayout();
	private JPanel m_selectPanel;
	private JComboBox<String> m_comboBox;
	private JPanel m_secretPanel;
	private String m_visiblePanelName;

	public ViewPanel() {
		this.setLayout(m_cardLayout);
		this.setBackground(Color.WHITE);
		m_selectPanel = new JPanel(new BorderLayout());
		m_comboBox = new JComboBox<String>();
		m_comboBox.setBackground(Color.WHITE);
		m_selectPanel.add(m_comboBox, BorderLayout.CENTER);
		m_comboBox.setBorder(BorderFactory.createEmptyBorder());
		m_secretPanel = new JPanel(new BorderLayout());
		m_secretPanel.setBackground(Color.WHITE);
		this.add(m_selectPanel, CompNameVariable.SELECT_PANEL);
		this.add(m_secretPanel, CompNameVariable.SECRET_PANEL);
		m_cardLayout.show(this, CompNameVariable.SELECT_PANEL);
		m_visiblePanelName = CompNameVariable.SELECT_PANEL;
	}

	public void updateList(Set<String> values) {
		m_comboBox.removeAllItems();
		Iterator<String> it = values.iterator();
		while (it.hasNext()) {
			m_comboBox.addItem(it.next());
		}
		m_comboBox.validate();
	}

	public String getVisiblePanelName() {
		return m_visiblePanelName;
	}

	public void showSecret(String secret) {
		m_secretPanel.add(new JLabel(" " + secret));
		m_cardLayout.show(this, CompNameVariable.SECRET_PANEL);
		m_visiblePanelName = CompNameVariable.SECRET_PANEL;
	}

	public void showSelection() {
		m_cardLayout.show(this, CompNameVariable.SELECT_PANEL);
		m_visiblePanelName = CompNameVariable.SELECT_PANEL;
		m_secretPanel.removeAll();
	}

	public String getSelection() {
		return (String) m_comboBox.getSelectedItem();
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: ViewPanel.java,v $
// Revision 1.3 2006/02/16 07:10:33 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:09 luzgin
// CVS log added
//