package com.skeeper.common;

import java.util.Timer;
import java.util.TimerTask;

public class TasksCollapser {
	// Execute last task only in specified period:
	private final int m_period = 300;
	private Timer m_timer;

	public TasksCollapser() {
		m_timer = new Timer();
	}

	public void addTask(TimerTask task) {
		if (m_timer != null) {
			m_timer.cancel();
		}
		m_timer = new Timer();
		m_timer.schedule(task, m_period);
	}

}

// //////////////////////////////////////////////////////////////////////////////
// $Log: TasksCollapser.java,v $
// Revision 1.2 2006/02/15 04:58:58 luzgin
// CVS log added
//
