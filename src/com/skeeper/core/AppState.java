package com.skeeper.core;

import com.skeeper.interfaces.StateListener;
import java.util.Timer;
import java.util.TimerTask;

public class AppState {

	private static final AppState m_instance = new AppState();
	private boolean m_open = false;
	private char[] m_password = null;
	private final Timer m_timer = new Timer();
	private StateListener m_listener = null;

	/** Return shared instance of this AppState. */
	public static final AppState getInstance() {
		return m_instance;
	}

	/**
	 * Set open access state to this application for duration.
	 * 
	 * @param duration
	 *            long milliseconds
	 */
	public void setOpenState(char[] password, long duration) {
		m_password = password;
		m_open = true;
		if (m_listener != null) {
			m_listener.onStateOpen();
		}
		m_timer.schedule(new TimerTask() {
			public void run() {
				setStateClosed();
			}
		}, duration);
	}

	public void setStateClosed() {
		m_open = false;
		m_password = null;
		if (m_listener != null) {
			m_listener.onStateClose();
		}
	}

	/** Set State Listener */
	public void setStateListener(StateListener listener) {
		m_listener = listener;
	}

	/**
	 * Get open state of this app.
	 * 
	 * @return open boolean value
	 */
	public boolean isOpenState() {
		return m_open;
	}

	public char[] getPassword() {
		return m_password;
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: AppState.java,v $
// Revision 1.2 2006/02/15 04:58:59 luzgin
// CVS log added
//