package com.skeeper.ui;

import com.skeeper.common.TasksCollapser;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.TimerTask;

public class IFrameDocker {
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	public static final String TOP = "TOP";
	public static final String BOTTOM = "BOTTOM";
	private static final int LEFT_MARGIN = 60;
	private static final int RIGHT_MARGIN = 60;
	private static final int TOP_MARGIN = 60;
	private static final int BOTTOM_MARGIN = 60;
	private boolean m_left = false;
	private boolean m_right = false;
	private boolean m_top = false;
	private boolean m_bottom = false;
	private TasksCollapser m_collapser;
	private IFrame m_frame;
	private String m_orientation = null;
	private boolean m_docked = false;
	private boolean m_pinned = true;

	public IFrameDocker(IFrame frame, boolean left, boolean right, boolean top,
			boolean bottom) {
		m_collapser = new TasksCollapser();
		m_frame = frame;
		m_left = left;
		m_right = right;
		m_top = top;
		m_bottom = bottom;
		initListeners();
	}

	/**
	 * Return stored frame.
	 * 
	 * @return frame IFrame object
	 */
	public IFrame getFrame() {
		return m_frame;
	}

	/**
	 * Returns true if frame is docked, false otherwise.
	 * 
	 * @return docked boolean value
	 */
	public boolean isDocked() {
		return m_docked;
	}

	/**
	 * Returns current docking orientation: LEFT, RIGHT, TOP, BOTTOM or null if
	 * frame is not docked.
	 * 
	 * @return orientation String value: LEFT, RIGHT, TOP, BOTTOM
	 */
	public String getOrientation() {
		return m_orientation;
	}

	/**
	 * Enable or disable side docking.
	 * 
	 * @param pinned
	 *            boolean value
	 */
	public void setPinned(boolean pinned) {
		m_pinned = pinned;
	}

	/** Return weather this docker is enabled or not. */
	public boolean isPinned() {
		return m_pinned;
	}

	private void initListeners() {
		m_frame.getContentPane().addHierarchyBoundsListener(
				new HierarchyBoundsListener() {
					public void ancestorMoved(HierarchyEvent e) {
						checkPosition();
					}

					public void ancestorResized(HierarchyEvent e) {
						checkPosition();
					}
				});
	}

	private void checkPosition() {
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();
		final Rectangle frameBounds;
		int frameX = (int) m_frame.getX();
		int frameY = (int) m_frame.getY();
		int frameWidth = (int) m_frame.getWidth();
		int frameHeight = (int) m_frame.getHeight();
		int maxWidth = (int) maxBounds.getWidth();
		int maxHeight = (int) maxBounds.getHeight();
		if (m_left && m_frame.getX() <= LEFT_MARGIN) {
			frameBounds = new Rectangle(0, frameY, frameWidth, frameHeight);
			m_orientation = LEFT;
			m_docked = true;
		} else if (m_right && (frameX + frameWidth >= maxWidth - RIGHT_MARGIN)) {
			frameBounds = new Rectangle(maxWidth - frameWidth, frameY,
					frameWidth, frameHeight);
			m_orientation = RIGHT;
			m_docked = true;
		} else if (m_top && (frameY <= TOP_MARGIN)) {
			frameBounds = new Rectangle(frameX, 0, frameWidth, frameHeight);
			m_orientation = TOP;
			m_docked = true;
		} else if (m_bottom
				&& (frameY + frameHeight >= maxHeight - BOTTOM_MARGIN)) {
			frameBounds = new Rectangle(frameX, maxHeight - frameHeight,
					frameWidth, frameHeight);
			m_orientation = BOTTOM;
			m_docked = true;
		} else {
			frameBounds = m_frame.getBounds();
			m_orientation = null;
			m_docked = false;
		}
		// Now adjust frame:
		if (isPinned() && !frameBounds.equals(m_frame.getBounds())) {
			TimerTask task = new TimerTask() {
				public void run() {
					m_frame.setFrameDragging(false);
					m_frame.setBounds(frameBounds);
					m_frame.validate();
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							m_frame.setFrameDragging(true);
						}
					});
				}
			};
			m_collapser.addTask(task);
		}
	}
}

// //////////////////////////////////////////////////////////////////////
// $Log: IFrameDocker.java,v $
// Revision 1.2 2006/02/15 04:59:07 luzgin
// CVS log added
//