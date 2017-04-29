package com.skeeper.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import com.skeeper.variables.ImageVariable;

public class IFrame extends JFrame {

	private static final long serialVersionUID = 24578603513238239L;

	private JPanel m_rootPanel;
	protected JLayeredPane m_layeredPane;
	private IFrameTitle m_titlePanel;
	private JPanel m_contentPanel;
	private Point m_lastMouseLocation = null;
	private final IFrame m_frame;
	private JMenuBar m_menuBar = null;
	private String m_orientation;
	private MouseListener m_rootMouseListener;
	private MouseMotionListener m_rootMouseMotionListener;
	private boolean m_frameResizing = false;
	private boolean m_frameDragging = true;
	private boolean m_showTitle = true;
	private Rectangle m_savedBounds;
	private int m_currentState = NORMAL_STATE;
	private static final int NORMAL_STATE = 0;
	private static final int MAXIMIZED_STATE = 1;
	// The height of standard operating system bar
	private static final int OS_BAR_HEIGHT = 30;
	// Sensing resize border width
	private static final int BORDER_WIDTH = 3;
	// The padding space between title buttons
	private static final int TITLE_BUTTON_PADDING = 2;
	// Mouse point to border relations
	private static final String BORDER_TOP = "BORDER_TOP";
	private static final String BORDER_LEFT = "BORDER_LEFT";
	private static final String BORDER_RIGHT = "BORDER_RIGHT";
	private static final String BORDER_BOTTOM = "BORDER_BOTTOM";
	private static final String BORDER_CORNER = "BORDER_CORNER";
	private static final String BORDER_OUT = "BORDER_OUT";
	// Possible title bar orientations
	public static final String TITLE_NORTH = "NORTH";
	public static final String TITLE_SOUTH = "SOUTH";
	public static final String TITLE_EAST = "EAST";
	public static final String TITLE_WEST = "WEST";
	private final Dimension m_minSize = new Dimension(10, 10);
	// Mouse Listener which can be defined and added externally
	// to extend (not overwrite) standard title bar MouseListener:
	private MouseListener m_externalTitleMouseListener = null;

	/** IFrame constructor. */
	public IFrame(String title) {
		super(title);
		m_frame = this;
		if (title == null)
			title = "";
		initFrame(title, true, true, true);
		initListeners();
	}

	/** IFrame constructor. */
	public IFrame(String title, boolean collapse, boolean expand, boolean close) {
		super(title);
		m_frame = this;
		if (title == null)
			title = "";
		initFrame(title, collapse, expand, close);
		initListeners();
	}

	/**
	 * Set title bar orientation.
	 * 
	 * @param orientation
	 *            one of the following orientations: NORTH, SOUTH, EAST, WEST
	 * */
	public void setTitleOrientation(final String orientation) {
		// Relocate title bar in a different Thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (orientation.equals(TITLE_NORTH)) {
					m_rootPanel.remove(m_titlePanel);
					m_rootPanel.add(m_titlePanel, BorderLayout.NORTH);
				} else if (orientation.equals(TITLE_SOUTH)) {
					m_rootPanel.remove(m_titlePanel);
					m_rootPanel.add(m_titlePanel, BorderLayout.SOUTH);
				} else if (orientation.equals(TITLE_EAST)) {
					m_rootPanel.remove(m_titlePanel);
					m_rootPanel.add(m_titlePanel, BorderLayout.EAST);
				} else if (orientation.equals(TITLE_WEST)) {
					m_rootPanel.remove(m_titlePanel);
					m_rootPanel.add(m_titlePanel, BorderLayout.WEST);
				}
				m_rootPanel.validate();
			}
		});
	}

	public void setTitleTextVisible(boolean showTitle) {
		m_showTitle = showTitle;
		setTitle(this.getTitle());
	}

	/**
	 * Enable or disable frame dragging.
	 * 
	 * @param dragging
	 *            boolean value
	 **/
	public void setFrameDragging(boolean dragging) {
		m_frameDragging = dragging;
	}

	/**
	 * Add externally defined MouseListener to extend standard title bar
	 * MouseListener.
	 * 
	 * @param mouseListener
	 *            externally defined MouseListener object
	 * */
	public void setTitleMouseListener(MouseListener mouseListener) {
		m_externalTitleMouseListener = mouseListener;
	}

	// Init Frame and it's components
	private void initFrame(String title, boolean collapse, boolean expand,
			boolean close) {
		m_rootPanel = new JPanel();
		m_layeredPane = new JLayeredPane();
		m_layeredPane.setLayout(new BorderLayout());
		m_rootPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
		m_rootPanel.setLayout(new BorderLayout());
		super.setUndecorated(true);
		super.getContentPane().setLayout(new BorderLayout());
		super.getContentPane().add(m_rootPanel, BorderLayout.CENTER);
		m_titlePanel = new IFrameTitle(title, collapse, expand, close);
		m_contentPanel = new JPanel();
		m_contentPanel.setLayout(new BorderLayout());
		m_layeredPane.add(m_contentPanel, BorderLayout.CENTER);
		m_rootPanel.add(m_titlePanel, BorderLayout.NORTH);
		m_rootPanel.add(m_layeredPane, BorderLayout.CENTER);
	}

	// Init root panel mouse listeners
	private void initListeners() {
		m_rootMouseListener = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				if (m_frameResizing) {
					m_frameResizing = false;
					m_frame.repaint();
				}
			}

			public void mousePressed(MouseEvent e) {
				m_lastMouseLocation = e.getPoint();
			}

			public void mouseReleased(MouseEvent e) {
				m_lastMouseLocation = null;
				if (m_frameResizing) {
					m_frameResizing = false;
					m_frame.repaint();
				}
			}
		};
		m_rootMouseMotionListener = new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				m_orientation = getPointToBorderOrientation(e.getPoint());
				if (m_orientation.equals(BORDER_TOP)) {
					setCursor(Cursor
							.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				} else if (m_orientation.equals(BORDER_BOTTOM)) {
					setCursor(Cursor
							.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				} else if (m_orientation.equals(BORDER_LEFT)) {
					setCursor(Cursor
							.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				} else if (m_orientation.equals(BORDER_RIGHT)) {
					setCursor(Cursor
							.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				} else if (m_orientation.equals(BORDER_CORNER)) {
					setCursor(Cursor
							.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				} else {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}

			public void mouseDragged(MouseEvent e) {
				m_frameResizing = true;
				Point newMouseLocation = e.getPoint();
				int deltaX = (int) (newMouseLocation.getX() - m_lastMouseLocation
						.getX());
				int deltaY = (int) (newMouseLocation.getY() - m_lastMouseLocation
						.getY());
				int x = m_frame.getX();
				int y = m_frame.getY();
				int width = m_frame.getWidth();
				int height = m_frame.getHeight();
				boolean updateMouseLocation = false;
				if (m_orientation.equals(BORDER_TOP)) {
					y += deltaY;
					height -= deltaY;
				} else if (m_orientation.equals(BORDER_BOTTOM)) {
					height += deltaY;
					updateMouseLocation = true;
				} else if (m_orientation.equals(BORDER_LEFT)) {
					x += deltaX;
					width -= deltaX;
				} else if (m_orientation.equals(BORDER_RIGHT)) {
					width += deltaX;
					updateMouseLocation = true;
				} else if (m_orientation.equals(BORDER_CORNER)) {
					height += deltaY;
					width += deltaX;
					updateMouseLocation = true;
				}
				if (width < m_frame.getMinimumSize().width) {
					width = m_frame.getMinimumSize().width;
				}
				if (height < m_frame.getMinimumSize().height) {
					height = m_frame.getMinimumSize().height;
				}
				m_frame.setBounds(x, y, width, height);
				m_frame.validate();
				if (updateMouseLocation) {
					m_lastMouseLocation = newMouseLocation;
				}
			}
		};
		this.setResizable(true);
	}

	private String getPointToBorderOrientation(Point point) {
		String side = BORDER_OUT;
		if (point.x <= BORDER_WIDTH) {
			side = BORDER_LEFT;
		} else if (point.x >= m_frame.getWidth() - BORDER_WIDTH) {
			if (point.y >= m_frame.getHeight() - BORDER_WIDTH) {
				side = BORDER_CORNER;
			} else {
				side = BORDER_RIGHT;
			}
		} else if (point.y <= BORDER_WIDTH) {
			side = BORDER_TOP;
		} else if (point.y >= m_frame.getHeight() - BORDER_WIDTH) {
			side = BORDER_BOTTOM;
		}
		return side;
	}

	// >> Title bar implementation
	/** This class implements IFrame's Title bar */
	private class IFrameTitle extends JPanel {

		private static final long serialVersionUID = 12674843083463465L;
		// private int m_height = 16;
		private String m_title;
		private JLabel m_titleLabel;
		// private Component m_titleIcon;
		private JPanel m_ctrlPanel;
		private JPanel m_extraCtrlPanel;
		private Color m_bgColor = new Color(0, 70, 250);
		// Standard window title controls:
		private IButton m_collapseButton;
		private IButton m_expandButton;
		private IButton m_closeButton;

		protected IFrameTitle(String title, boolean collapse, boolean expand,
				boolean close) {
			m_title = title;
			initTitle();
			initControls(collapse, expand, close);
		}

		private void initTitle() {
			// Set Layout, gradient background, title:
			this.setLayout(new BorderLayout(0, 0));
			this.setBackground(m_bgColor);
			m_titleLabel = new JLabel();
			m_titleLabel.setForeground(Color.WHITE);
			if (m_showTitle) {
				this.setTitle(m_title);
			} else {
				this.setTitle("");
			}
			this.add(m_titleLabel, BorderLayout.CENTER);
			// also execute externally defined listener if exists:
			this.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					if (m_externalTitleMouseListener instanceof MouseListener) {
						m_externalTitleMouseListener.mouseClicked(e);
					}
				}

				public void mouseEntered(MouseEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					if (m_externalTitleMouseListener instanceof MouseListener) {
						m_externalTitleMouseListener.mouseEntered(e);
					}
				}

				public void mouseExited(MouseEvent e) {
					if (m_externalTitleMouseListener instanceof MouseListener) {
						m_externalTitleMouseListener.mouseExited(e);
					}
				}

				public void mousePressed(MouseEvent e) {
					m_lastMouseLocation = e.getPoint();
					if (m_externalTitleMouseListener instanceof MouseListener) {
						m_externalTitleMouseListener.mousePressed(e);
					}
				}

				public void mouseReleased(MouseEvent e) {
					m_lastMouseLocation = null;
					if (m_externalTitleMouseListener instanceof MouseListener) {
						m_externalTitleMouseListener.mouseReleased(e);
					}
				}
			});
			this.addMouseMotionListener(new MouseMotionListener() {
				public void mouseMoved(MouseEvent e) {
				}

				public void mouseDragged(MouseEvent e) {
					if (m_frameDragging) {
						Point newMouseLocation = e.getPoint();
						int deltaX = (int) (newMouseLocation.getX() - m_lastMouseLocation
								.getX());
						int deltaY = (int) (newMouseLocation.getY() - m_lastMouseLocation
								.getY());
						Point frameLocation = m_frame.getLocation();
						frameLocation.translate(deltaX, deltaY);
						m_frame.setLocation(frameLocation);
					}
				}
			});
		}

		/** Create standard window controls: collapse, expand, close */
		private void initControls(boolean collapse, boolean expand,
				boolean close) {
			m_ctrlPanel = new JPanel();
			m_extraCtrlPanel = new JPanel();
			m_ctrlPanel.setBackground(m_bgColor);
			m_extraCtrlPanel.setBackground(m_bgColor);
			m_extraCtrlPanel.setLayout(new BoxLayout(m_extraCtrlPanel,
					BoxLayout.X_AXIS));
			m_ctrlPanel.setLayout(new BoxLayout(m_ctrlPanel, BoxLayout.X_AXIS));
			// Create default collapse button:
			Hashtable<Integer, Image> collapseImgHash = IconFactory
					.getResourceImageMap(ImageVariable.WINDOW_COLLAPSE);
			m_collapseButton = IButtonFactory.createBtn(new IMouseHandler() {
				public void doAction(Component from, MouseEvent evt) {
					if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
						onCollapseAction();
					}
				}
			}, collapseImgHash);
			m_collapseButton.setOpaque(false);
			// Create default expand button
			Hashtable<Integer, Image> expandImgHash = IconFactory
					.getResourceImageMap(ImageVariable.WINDOW_EXPAND);
			m_expandButton = IButtonFactory.createBtn(new IMouseHandler() {
				public void doAction(Component from, MouseEvent evt) {
					if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
						onExpandAction();
					}
				}
			}, expandImgHash);
			m_expandButton.setOpaque(false);
			// Create default close button
			Hashtable<Integer, Image> closeImgHash = IconFactory
					.getResourceImageMap(ImageVariable.WINDOW_CLOSE);
			m_closeButton = IButtonFactory.createBtn(new IMouseHandler() {
				public void doAction(Component from, MouseEvent evt) {
					if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
						onCloseAction();
					}
				}
			}, closeImgHash);
			m_closeButton.setOpaque(false);
			// Add controls into control panel
			this.addControls(collapse, expand, close);
			// Add control panel into title bar
			this.add(m_ctrlPanel, BorderLayout.EAST);
		}

		private void addControls(boolean collapse, boolean expand, boolean close) {
			if (m_ctrlPanel.getComponentCount() > 0) {
				m_ctrlPanel.removeAll();
			}
			// padding
			m_ctrlPanel.add(Box.createHorizontalGlue());
			m_ctrlPanel.add(Box.createHorizontalStrut(TITLE_BUTTON_PADDING));
			m_ctrlPanel.add(Box.createHorizontalGlue());
			// First add a panel with extra controls
			m_ctrlPanel.add(m_extraCtrlPanel);
			// padding
			m_ctrlPanel.add(Box.createHorizontalGlue());
			m_ctrlPanel.add(Box.createHorizontalStrut(TITLE_BUTTON_PADDING));
			m_ctrlPanel.add(Box.createHorizontalGlue());
			if (collapse) {
				// add collapse button
				m_ctrlPanel.add(m_collapseButton);
				// padding
				m_ctrlPanel.add(Box.createHorizontalGlue());
				m_ctrlPanel
						.add(Box.createHorizontalStrut(TITLE_BUTTON_PADDING));
				m_ctrlPanel.add(Box.createHorizontalGlue());
			}
			if (expand) {
				// add expand button
				m_ctrlPanel.add(m_expandButton);
				// padding
				m_ctrlPanel.add(Box.createHorizontalGlue());
				m_ctrlPanel
						.add(Box.createHorizontalStrut(TITLE_BUTTON_PADDING));
				m_ctrlPanel.add(Box.createHorizontalGlue());
			}
			if (close) {
				// add close button
				m_ctrlPanel.add(m_closeButton);
				// padding
				m_ctrlPanel.add(Box.createHorizontalGlue());
				m_ctrlPanel
						.add(Box.createHorizontalStrut(TITLE_BUTTON_PADDING));
				m_ctrlPanel.add(Box.createHorizontalGlue());
			}
		}

		/** Set a new title. */
		protected void setTitle(String title) {
			m_title = title;
			if (!(m_title.equals("") || m_title.equals(" "))) {
				m_title = " " + m_title;
			}
			if (m_showTitle) {
				m_titleLabel.setText(m_title);
			} else {
				m_titleLabel.setText("");
			}
			this.validate();
		}

		/** Get the current title. */
		protected String getTitle() {
			return m_title;
		}

		/** Set title bar background color. */
		protected void setTitleBackgroundColor(Color bgColor) {
			m_bgColor = bgColor;
			this.setBackground(m_bgColor);
			m_ctrlPanel.setBackground(m_bgColor);
			m_extraCtrlPanel.setBackground(m_bgColor);
		}

		/** Get title bar background color. */
		protected Color getTitleBackgroundColor() {
			return m_bgColor;
		}

		/** Set title text color. */
		protected void setTitleForegroundColor(Color color) {
			m_titleLabel.setForeground(color);
		}

		/** Get title text color. */
		protected Color getTitleForegroundColor() {
			return m_titleLabel.getForeground();
		}

		/** Add extra IButton button into title bar. */
		protected void addExtraButton(Component extraButton) {
			m_extraCtrlPanel.add(Box.createHorizontalGlue());
			m_extraCtrlPanel.add(Box.createHorizontalStrut(2));
			m_extraCtrlPanel.add(Box.createHorizontalGlue());
			m_extraCtrlPanel.add(extraButton);
		}

		/** Clear all extra buttons. */
		protected void clearAllExtraButtons() {
			m_extraCtrlPanel.removeAll();
		}

		/** Set any component as a left Icon for title bar. */
		protected void setIcon(Component componentIcon) {
			this.add(componentIcon, BorderLayout.WEST);
			this.validate();
		}

		/** Set ImageIcon as a left Icon for title bar. */
		protected void setIcon(ImageIcon imageIcon) {
			this.add(new JLabel(imageIcon), BorderLayout.WEST);
			this.validate();
		}

	}

	// << Title bar implementation

	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		if (m_frameResizing) {
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, width - 1, height - 1);
		} else {
			super.paint(g);
		}
		g.dispose();
	}

	/** Expand button clicked action */
	private void onExpandAction() {
		if (m_currentState == NORMAL_STATE) {
			m_savedBounds = this.getBounds();
			m_currentState = MAXIMIZED_STATE;
			Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
			this.setBounds(0, 0, screenDim.width, screenDim.height
					- OS_BAR_HEIGHT);
			this.setResizable(false);
		} else if (m_currentState == MAXIMIZED_STATE) {
			m_currentState = NORMAL_STATE;
			if (m_savedBounds != null) {
				this.setBounds(m_savedBounds);
			}
			this.setResizable(true);
		}
		m_frame.validate();
	}

	/** Collapse button clicked action */
	private void onCollapseAction() {
		m_frame.setState(JFrame.ICONIFIED);
	}

	/** Close button clicked action */
	private void onCloseAction() {
		switch (this.getDefaultCloseOperation()) {
		case HIDE_ON_CLOSE:
			setVisible(false);
			break;
		case DISPOSE_ON_CLOSE:
			setVisible(false);
			dispose();
			break;
		case DO_NOTHING_ON_CLOSE:
		default:
			break;
		case EXIT_ON_CLOSE:
			System.exit(0);
			break;
		}
	}

	/** Set Title bar color. */
	public void setTitleBackgroundColor(Color bgColor) {
		m_titlePanel.setTitleBackgroundColor(bgColor);
	}

	/** Get title bar background color. */
	public Color getTitleBackgroundColor() {
		return m_titlePanel.getTitleBackgroundColor();
	}

	/** Set title text color. */
	public void setTitleForegroundColor(Color color) {
		m_titlePanel.setTitleForegroundColor(color);
	}

	/** Get title text color. */
	public Color getTitleForegroundColor() {
		return m_titlePanel.getTitleForegroundColor();
	}

	/** Add extra IButton button into title bar. */
	public void addExtraTitleButton(Component extraButton) {
		m_titlePanel.addExtraButton(extraButton);
	}

	/** Clear all extra buttons. */
	public void clearAllExtraTitleButtons() {
		m_titlePanel.clearAllExtraButtons();
	}

	// >> Inherited methods which needs to be overwritten:
	/** Set Frame title. */
	public void setTitle(String title) {
		if (title == null)
			title = "";
		m_titlePanel.setTitle(title);
	}

	/** Get current Frame title. */
	public String getTitle() {
		return m_titlePanel.getTitle();
	}

	/** Overwrites getContentPane() method. */
	public Container getContentPane() {
		return (Container) m_contentPanel;
	}

	/** Returns root panel which contains title and content panels. */
	public JPanel getRootPanel() {
		return m_rootPanel;
	}

	/** Set whether this frame is resizable or not. */
	public void setResizable(boolean resizable) {
		if (resizable) {
			m_rootPanel.addMouseListener(m_rootMouseListener);
			m_rootPanel.addMouseMotionListener(m_rootMouseMotionListener);
		} else {
			m_rootPanel.removeMouseListener(m_rootMouseListener);
			m_rootPanel.removeMouseMotionListener(m_rootMouseMotionListener);
		}
	}

	/** Set content panel background. */
	public void setBackground(Color bgColor) {
		if (m_contentPanel != null) {
			m_contentPanel.setBackground(bgColor);
		}
	}

	/** Set whether this window is decorated/resizable or not. */
	public void setUndecorated(final boolean undecorated) {
		if (undecorated) {
			m_rootPanel.remove(m_titlePanel);
			m_frame.setResizable(false);
		} else {
			if (m_titlePanel.getParent() == null) {
				m_rootPanel.add(m_titlePanel, BorderLayout.NORTH);
			}
			m_frame.setResizable(true);
			m_rootPanel.validate();
		}
	}

	/**
	 * Specifies the menu bar value.
	 * 
	 * @deprecated As of Swing version 1.0.3 replaced by
	 *             <code>setJMenuBar(JMenuBar menu)</code>.
	 * @param menu
	 *            the <code>JMenuBar</code> to add.
	 */
	public void setMenuBar(JMenuBar menu) {
		if (m_menuBar != null && m_menuBar.getParent() == m_layeredPane)
			m_layeredPane.remove(m_menuBar);
		m_menuBar = menu;
		if (m_menuBar != null)
			m_layeredPane.add(m_menuBar, BorderLayout.NORTH);
	}

	/**
	 * Sets the menubar for this frame.
	 * 
	 * @param menubar
	 *            the menubar being placed in the frame
	 */
	public void setJMenuBar(JMenuBar menu) {
		this.setMenuBar(menu);
	}

	/**
	 * Set any component as a left Icon for title bar.
	 * 
	 * @param componentIcon
	 *            any component being placed as a left icon for title bar
	 */
	public void setIcon(Component componentIcon) {
		m_titlePanel.setIcon(componentIcon);
	}

	/**
	 * Set ImageIcon as a left Icon for title bar.
	 * 
	 * @param imageIcon
	 *            an ImageIcon to be placed as a left icon for title bar
	 */
	public void setIcon(ImageIcon imageIcon) {
		m_titlePanel.setIcon(imageIcon);
	}

	/** Return some predifined minimum size. */
	public Dimension getMinimumSize() {
		return m_minSize;
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: IFrame.java,v $
// Revision 1.3 2006/02/16 07:10:24 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:06 luzgin
// CVS log added
//

