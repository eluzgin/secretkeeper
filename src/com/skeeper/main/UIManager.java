package com.skeeper.main;

import com.skeeper.common.ResourcesHolder;
import com.skeeper.ui.IButton;
import com.skeeper.ui.IButtonFactory;
import com.skeeper.ui.IFrame;
import com.skeeper.ui.IFrameDocker;
import com.skeeper.ui.IMouseHandler;
import com.skeeper.ui.IconFactory;
import com.skeeper.variables.CompNameVariable;
import com.skeeper.variables.ImageBundle;
import com.skeeper.variables.ImageVariable;
import com.skeeper.variables.TextBundle;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class UIManager {
	private final static int EAR_SIZE = 2;
	private final static int AUTO_HIDE_TIME = 500; // in milliseconds
	private final static int AUTO_HIDE_DELAY = 50; // in milliseconds
	private IFrame m_frame;
	private IFrameDocker m_docker;
	private Rectangle m_maxBounds;
	// Drawer pin/unpin control:
	private JPanel m_framePinUnpinContainer;
	private IButton m_framePinButton;
	private IButton m_frameUnpinButton;
	// Frame states:
	private boolean m_frameMoving = false;
	private boolean m_frameHidden = false;
	// Timer
	private Timer m_timer = new Timer();

	public UIManager(Locale locale) {
		m_maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();
		initResources(locale);
		initGUI();
	}

	public void start() {
		m_frame.setVisible(true);
	}

	public void setContent(Component component) {
		m_frame.getContentPane().add(component);
		m_frame.getContentPane().validate();
	}

	private void initGUI() {
		// Init frame
		m_frame = new IFrame("PSWDKeeper", false, false, true);
		m_frame.setTitleOrientation(IFrame.TITLE_EAST);
		// m_frame.setTitleTextVisible(false);
		m_frame.setBounds(100, 0, 500, 24);
		m_frame.setDefaultCloseOperation(IFrame.EXIT_ON_CLOSE);
		// set image icons:
		List<Image> icons = new ArrayList<Image>();
		icons.add(IconFactory
				.getResourceImageIcon(ImageVariable.WINDOW_ICON).getImage());
		icons.add(IconFactory
				.getResourceImageIcon(ImageVariable.WINDOW_ICON_32X32).getImage());
		m_frame.setIconImages(icons);
		// init docker
		m_docker = new IFrameDocker(m_frame, false, false, true, true);
		// Add pin/unpin control the Drawer Frame:
		// init pin/unpin buttons container
		m_framePinUnpinContainer = new JPanel(new CardLayout());
		m_framePinUnpinContainer.setOpaque(false);
		// init pin/unpin buttons:
		initFrameButtons();
		// add buttons to it's container:
		m_framePinUnpinContainer.add(CompNameVariable.WIN_UNPIN_BUTTON,
				m_frameUnpinButton);
		m_framePinUnpinContainer.add(CompNameVariable.WIN_PIN_BUTTON,
				m_framePinButton);
		CardLayout pinUnpinCardLayout = (CardLayout) m_framePinUnpinContainer
				.getLayout();
		pinUnpinCardLayout.show(m_framePinUnpinContainer,
				CompNameVariable.WIN_UNPIN_BUTTON);
		m_frame.addExtraTitleButton(m_framePinUnpinContainer);
		// init listeners
		initListeners();
	}

	// Init all extra buttons for the frame
	private void initFrameButtons() {
		// init pin button
		Hashtable<Integer, Image> pinImgHash = IconFactory
				.getResourceImageMap(ImageVariable.WINDOW_PIN);
		m_framePinButton = IButtonFactory.createBtn(new IMouseHandler() {
			public void doAction(Component from, MouseEvent evt) {
				if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
					pinFrame();
				}
			}
		}, pinImgHash);
		m_framePinButton.setOpaque(false);
		// init unpin button
		Hashtable<Integer, Image> unpinImgHash = IconFactory
				.getResourceImageMap(ImageVariable.WINDOW_UNPIN);
		m_frameUnpinButton = IButtonFactory.createBtn(new IMouseHandler() {
			public void doAction(Component from, MouseEvent evt) {
				if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
					unpinFrame();
				}
			}
		}, unpinImgHash);
		m_frameUnpinButton.setOpaque(false);
	}

	// Init listeners for trigering auto-show/hide
	private void initListeners() {
		m_frame.getRootPanel().addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				if (m_frameHidden && !(m_docker.isPinned() && m_frameMoving)) {
					autoShowFrame();
				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
		m_frame.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
			}

			public void windowLostFocus(WindowEvent e) {
				onFrameLostFocus();
			}
		});
	}

	private void onFrameLostFocus() {
		if (!m_frameHidden && !m_docker.isPinned() && !m_frameMoving) {
			autoHideFrame();
		}
	}

	/** Pin Frame. */
	public void pinFrame() {
		if (m_docker.isDocked()) {
			switchFramePinUnpinButtons(true);
			autoShowFrame();
		}
	}

	/** Unpin Frame. */
	public void unpinFrame() {
		if (m_docker.isDocked()) {
			switchFramePinUnpinButtons(false);
			autoHideFrame();
		}
	}

	// Auto-hide frame
	private void autoHideFrame() {
		String dockSide = m_docker.getOrientation();
		int toolbarX = m_frame.getX();
		int toolbarY = m_frame.getY();
		int toolbarHeight = m_frame.getHeight();
		int maxHeight = (int) m_maxBounds.getHeight();
		final Point endPoint;
		final int yDelta;
		if (dockSide.equals(IFrameDocker.TOP)) {
			endPoint = new Point(toolbarX, -toolbarHeight);
			yDelta = -(toolbarHeight * AUTO_HIDE_DELAY) / AUTO_HIDE_TIME;
		} else if (dockSide.equals(IFrameDocker.BOTTOM)) {
			endPoint = new Point(toolbarX, maxHeight);
			yDelta = (toolbarHeight * AUTO_HIDE_DELAY) / AUTO_HIDE_TIME;
		} else {
			endPoint = new Point(toolbarX, toolbarY);
			yDelta = 0;
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				m_frameMoving = true;
				animateMove(m_frame, endPoint, 0, yDelta);
			}
		});
	}

	// Auto-show frame
	private void autoShowFrame() {
		String dockSide = m_docker.getOrientation();
		int frameX = m_frame.getX();
		int frameY = m_frame.getY();
		int frameHeight = m_frame.getHeight();
		int maxHeight = (int) m_maxBounds.getHeight();
		final Point endPoint;
		final int yDelta;
		if (dockSide.equals(IFrameDocker.TOP)) {
			endPoint = new Point(frameX, 0);
			yDelta = (frameHeight * AUTO_HIDE_DELAY) / AUTO_HIDE_TIME;
		} else if (dockSide.equals(IFrameDocker.BOTTOM)) {
			endPoint = new Point(frameX, maxHeight - frameHeight);
			yDelta = -(frameHeight * AUTO_HIDE_DELAY) / AUTO_HIDE_TIME;
		} else {
			endPoint = new Point(frameX, frameY);
			yDelta = 0;
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				m_frameMoving = true;
				animateMove(m_frame, endPoint, 0, yDelta);
			}
		});
	}

	// Show or hide pin or unpin buttons depending on a pinned state
	private void switchFramePinUnpinButtons(boolean pinned) {
		CardLayout cardLayout = (CardLayout) m_framePinUnpinContainer
				.getLayout();
		m_docker.setPinned(pinned);
		if (pinned) {
			cardLayout.show(m_framePinUnpinContainer,
					CompNameVariable.WIN_UNPIN_BUTTON);
			m_frameUnpinButton.setState(IButton.ORIGINAL);
		} else {
			cardLayout.show(m_framePinUnpinContainer,
					CompNameVariable.WIN_PIN_BUTTON);
			m_framePinButton.setState(IButton.ORIGINAL);
		}
	}

	// Animate hiding a Frame in a specified number of steps
	private void animateMove(final IFrame frame, final Point endPoint,
			int xDelta, int yDelta) {
		Point point = frame.getLocation();
		point.move(point.x + xDelta, point.y + yDelta);
		// check x coord move:
		if (xDelta < 0 && point.x <= endPoint.x) {
			point.x = endPoint.x;
			xDelta = 0;
		} else if (xDelta > 0 && point.x >= endPoint.x) {
			point.x = endPoint.x;
			xDelta = 0;
		}
		final int mXDelta = xDelta;
		// check y coord move:
		if (yDelta < 0 && point.y <= endPoint.y) {
			point.y = endPoint.y;
			yDelta = 0;
		} else if (yDelta > 0 && point.y >= endPoint.y) {
			point.y = endPoint.y;
			yDelta = 0;
		}
		frame.setVisible(true);
		final int mYDelta = yDelta;
		frame.setLocation(point);
		if (mXDelta != 0 || mYDelta != 0) {
			TimerTask moveTask = new TimerTask() {
				public void run() {
					animateMove(frame, endPoint, mXDelta, mYDelta);
				}
			};
			m_timer.schedule(moveTask, AUTO_HIDE_DELAY);
		} else {
			onFinishedMoving(frame);
		}
	}

	// Handle event when auto-show/hide is done for a frame
	private void onFinishedMoving(IFrame frame) {
		// int maxWidth = (int)m_maxBounds.getWidth();
		int maxHeight = (int) m_maxBounds.getHeight();
		m_frameMoving = false;
		int frameY = frame.getY();
		int frameHeight = frame.getHeight();
		if (frameY + frameHeight <= 0 || frameY >= maxHeight) {
			onFrameHidden();
		} else {
			onFrameShown();
		}
	}

	// Adjust frame after it has been moved
	private void onFrameHidden() {
		String dockSide = m_docker.getOrientation();
		int frameX = m_frame.getX();
		int frameHeight = m_frame.getHeight();
		int maxHeight = (int) m_maxBounds.getHeight();
		if (dockSide.equals(IFrameDocker.TOP)) {
			m_frame.setLocation(frameX, EAR_SIZE - frameHeight);
		} else if (dockSide.equals(IFrameDocker.BOTTOM)) {
			m_frame.setLocation(frameX, maxHeight - EAR_SIZE);
		}
		m_frameHidden = true;
	}

	private void onFrameShown() {
		m_frameHidden = false;
	}

	private void initResources(Locale locale) {
		// Initialize images resource
		String imgBundleClassPath = ImageBundle.class.getName();
		String txtBundleClassPath = TextBundle.class.getName();
		ResourceBundle imageBundle = ResourceBundle.getBundle(
				imgBundleClassPath, locale);
		ResourceBundle textBundle = ResourceBundle.getBundle(
				txtBundleClassPath, locale);
		// Add resources to ResourcesHolder singleton
		ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
		resourcesHolder.addResource(CompNameVariable.IMAGE_RESOURCE,
				imageBundle);
		resourcesHolder.addResource(CompNameVariable.TEXT_RESOURCE, textBundle);
	}
}

// //////////////////////////////////////////////////////////////////////
// $Log: UIManager.java,v $
// Revision 1.3 2006/02/16 07:10:12 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:03 luzgin
// CVS log added
//

