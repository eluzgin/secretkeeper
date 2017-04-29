package com.skeeper.ui;

import com.skeeper.ui.IMouseHandler;
import java.awt.*;
import java.awt.event.*;
import java.util.Dictionary;
import javax.swing.JComponent;

public class IButton extends JComponent implements ComponentListener, IControl {

	private static final long serialVersionUID = 92382302383643738L;

	private IMouseHandler m_listener = null;

	public static final int ORIGINAL = 0;
	public static final int ACTIVE = 1;
	public static final int DOWN = 2;
	public static final int DISABLED = 3;
	public static final int STATES_NUMS = 4;

	public static final int MOUSEOVER = 1;
	public static final int MOUSEDOWN = 2;

	private final Color m_bg_colors[] = new Color[STATES_NUMS];
	private final Color m_fg_colors[] = new Color[STATES_NUMS];
	private final Image m_imgs[] = new Image[STATES_NUMS];

	private String m_text = null;
	// private Font m_font = null;

	private int m_state = ORIGINAL;
	private boolean m_stateLocked = false;
	// private boolean m_painted = false;

	private int m_mouse_state = 0;

	private Rectangle m_rectangle = new Rectangle();
	private final Rectangle m_rect = new Rectangle();

	private int m_text_x_offs = 5;
	private int m_text_y_offs = 2;

	private int m_width = 0;
	private int m_height = 0;

	private int m_text_width = 0;
	private int m_text_height = 0;
	private int m_text_ascent = 0;

	private int m_img_width = 0;
	private int m_img_height = 0;

	private boolean m_isPressed = false;

	public final void setTextXOffset(int offs) {
		m_text_x_offs = offs;
	}

	public final void setTextYOffset(int offs) {
		m_text_y_offs = offs;
	}

	private final void resetImageParams() {
		m_img_width = 0;
		m_img_height = 0;
	}

	private final void refreshImageParams() {
		Image img = getImage();
		if (img != null) {
			m_img_width = img.getWidth(null);
			m_img_height = img.getHeight(null);
		}
	}

	private final void resetTextParams() {
		m_text_width = 0;
		m_text_ascent = 0;
		m_text_height = 0;
	}

	private final void refreshTextParams() {
		Graphics g = getGraphics();
		if (g != null) {
			FontMetrics fm = g.getFontMetrics(getFont());
			if (fm != null) {
				m_text_width = fm.stringWidth(m_text);
				m_text_ascent = fm.getAscent();
				m_text_height = fm.getAscent() + fm.getDescent();
			}
			g.dispose();
		}
	}

	private final int getImgX() {
		return 0;
	}

	private final int getImgY() {
		return 0;
	}

	private final int getImgWidth() {
		if (m_img_width <= 0) {
			refreshImageParams();
		}
		if ((getImage() == null) || (m_img_width <= 0)) {
			return 0;
		} else {
			return m_img_width;
		}
	}

	private final int getImgHeight() {
		if (m_img_height <= 0) {
			refreshImageParams();
		}
		if ((getImage() == null) || (m_img_height <= 0)) {
			return 0;
		} else {
			return m_img_height;
		}
	}

	private final int getTextXOffs() {
		return m_text_x_offs;
	}

	private final int getTextYOffs() {
		return m_text_y_offs;
	}

	private final int getTextX() {
		return m_text_x_offs + getImgWidth() + getImgX();
	}

	private final int getTextY() {
		return (calcHeight() - getTextHeight()) / 2 + getImgY();
	}

	public final String getText() {
		return m_text;
	}

	private final int getTextHeight() {
		int res = 0;
		if (m_text != null) {
			if (m_text_height <= 0) {
				refreshTextParams();
			}
			res += m_text_height;
		}
		return res;
	}

	private final int getTextWidth() {
		int res = 0;
		if (m_text != null) {
			if (m_text_width <= 0) {
				refreshTextParams();
			}
			res += m_text_width;
		}
		return res;
	}

	private final int calcWidth() {
		int res = 0;
		res += 2 * getTextXOffs() + getTextWidth();
		res += getImgWidth() + 2 * getImgX();
		return res;
	}

	private final int calcHeight() {
		int res = 0;
		res += 2 * getTextYOffs() + getTextHeight();
		res = Math.max(getImgHeight() + 2 * getImgY(), res);
		return res;
	}

	public final int getBtnWidth() {
		if (m_width > 0) {
			return m_width;
		} else {
			return calcWidth();
		}
	}

	public final int getBtnHeight() {
		if (m_height > 0) {
			return m_height;
		} else {
			return calcHeight();
		}
	}

	public final int getState() {
		return m_state;
	}

	public final void setState(int state) {
		m_state = state;
	}

	public final void lockState(boolean locked) {
		m_stateLocked = locked;
	}

	private final void onStateChanged() {
		if (!m_stateLocked) {
			if (isMouseDown()) {
				m_state = DOWN;
			} else if (isMouseOver()) {
				m_state = ACTIVE;
			} else {
				m_state = ORIGINAL;
			}
			resetImageParams();
			repaint();
		}
	}

	public final void setPressed(boolean pressed) {
		if (m_isPressed != pressed) {
			m_isPressed = pressed;
			onStateChanged();
		}
	}

	private final Image getImage() {
		int state = getState();
		Image res = m_imgs[state];
		if (res == null) {
			res = m_imgs[ORIGINAL];
		}
		return res;
	}

	public final boolean setText(String text) {
		if (((m_text == null) && (text != null))
				|| ((text == null) && (m_text != null))
				|| ((text != null) && (m_text != null) && (m_text
						.compareTo(text) != 0))) {
			m_text = text;
			resetTextParams();
			repaint();
			setToolTipText(text);
			return true;
		}
		return false;
	}

	private final Color getBgColor() {
		Color res = m_bg_colors[getState()];
		if (res == null) {
			res = m_bg_colors[ORIGINAL];
			if (res == null) {
				res = getBackground();
			}
		}
		return res;
	}

	private final Color getFgColor() {
		Color res = m_fg_colors[getState()];
		if (res == null) {
			res = m_fg_colors[ORIGINAL];
			if (res == null) {
				res = getForeground();
			}
		}
		return res;
	}

	private final int processDict2Array(Dictionary dict, Object array[]) {
		for (int i = 0; i < array.length; i++) {
			array[i] = dict.get(new Integer(i));
		}
		return array.length;
	}

	private IMouseInput m_input = null;

	public final void setUIHandler(IMouseInput input) {
		if (m_input != null) {
			m_input.detach();
		}
		m_input = input;
		if (m_input != null) {
			m_input.attach(this, this);
		}
	}

	private final void register() {
		addComponentListener(this);
		this.setOpaque(true);
	}

	public final void setDimension(int w, int h) {
		m_width = w;
		m_height = h;
	}

	public IButton(int w, int h, IMouseHandler handler, String text) {
		setDimension(w, h);
		m_text = text;
		m_listener = handler;
		register();
		setToolTipText(text);
	}

	public IButton(int w, int h, IMouseHandler handler, String text,
			Dictionary fg_colors, Dictionary bg_colors) {
		setDimension(w, h);
		m_text = text;
		if (fg_colors != null) {
			processDict2Array(fg_colors, m_fg_colors);
		}
		if (bg_colors != null) {
			processDict2Array(bg_colors, m_bg_colors);
		}
		m_listener = handler;
		register();
	}

	public IButton(int w, int h, IMouseHandler handler, String text,
			Dictionary images) {
		setDimension(w, h);
		m_text = text;
		if (images != null) {
			processDict2Array(images, m_imgs);
		}
		m_listener = handler;
		register();
	}

	public IButton(int w, int h, IMouseHandler handler, String text,
			Dictionary fg_colors, Dictionary bg_colors, Dictionary images) {
		setDimension(w, h);
		m_text = text;
		if (fg_colors != null) {
			processDict2Array(fg_colors, m_fg_colors);
		}
		if (bg_colors != null) {
			processDict2Array(bg_colors, m_bg_colors);
		}
		if (images != null) {
			processDict2Array(images, m_imgs);
		}
		m_listener = handler;
		register();
	}

	public void setFgColor(int state, Color color) {
		m_fg_colors[state] = color;
	}

	public void setBgColor(int state, Color color) {
		m_bg_colors[state] = color;
	}

	public void setImage(int state, Image img) {
		m_imgs[state] = img;
	}

	public void update(Graphics g) {
		paint(g);
	}

	public final Rectangle getButtonRect() {
		m_rect.x = 0;
		m_rect.y = 0;
		m_rect.width = m_rectangle.width;
		m_rect.height = m_rectangle.height;
		return m_rect;
	}

	public final boolean isPressed() {
		return m_isPressed;
	}

	public final boolean isBtnDown() {
		return isMouseDown() || isPressed();
	}

	public void paint(Graphics g) {
		Rectangle rect = getButtonRect();
		Color color = g.getColor();

		if (this.isOpaque()) {
			g.setColor(getBackground());
			g.fill3DRect(0, 0, m_rectangle.width, m_rectangle.height, true);
			g.setColor(getBgColor());
			if (isMouseOver() || isBtnDown()) {
				g.fill3DRect(rect.x, rect.y, rect.width, rect.height,
						!isBtnDown());
			} else {
				g.fillRect(rect.x, rect.y, rect.width, rect.height);
			}
		}

		Image img = getImage();
		if (img != null) {
			int yPos = (int) (rect.height / 2 - getImgHeight() / 2);
			g.drawImage(img, getImgX(), yPos, null);
		}

		if (m_text != null) {
			g.setColor(getFgColor());
			g.setFont(getFont());
			int x = getTextX();
			int y = getTextY();
			g.drawString(m_text, x, y + m_text_ascent);
		}

		g.setColor(color);
		super.paint(g);
	}

	public Insets getInsets() {
		return new Insets(0, 0, 0, 0);
	}

	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		if (m_text == null || d.width < getBtnWidth()
				|| d.height < getBtnHeight()) {
			return getMinimumSize();
		}
		return d;
	}

	public Dimension getMinimumSize() {
		if (m_text != null) {
			return new Dimension(getBtnWidth(), getBtnHeight());
		} else {
			return new Dimension(getImgWidth(), getBtnHeight());
		}
	}

	public Dimension getSize() {
		if (m_text != null) {
			return new Dimension(getBtnWidth(), getBtnHeight());
		} else {
			return new Dimension(getImgWidth(), getBtnHeight());
		}
	}

	public final boolean isMouseOver() {
		return (m_mouse_state & MOUSEOVER) == MOUSEOVER;
	}

	public final boolean isMouseDown() {
		return (m_mouse_state & MOUSEDOWN) == MOUSEDOWN;
	}

	public final void mouseOver(boolean over) {
		if (over && (!isMouseOver())) {
			m_mouse_state |= MOUSEOVER;
			onStateChanged();
		} else if (over == false) {
			if (isMouseOver()) {
				m_mouse_state &= ~MOUSEOVER;
				onStateChanged();
			}
			mouseDown(false);
		}
	}

	public final void mouseDown(boolean down) {
		if (down && (!isMouseDown())) {
			m_mouse_state |= MOUSEDOWN;
			onStateChanged();
		} else if ((!down) && isMouseDown()) {
			m_mouse_state &= ~MOUSEDOWN;
			onStateChanged();
		}
	}

	public final void mouseClick(MouseEvent e) {
	}

	public final void doAction(MouseEvent e) {
		if (m_listener != null) {
			m_listener.doAction(this, e);
		}
	}

	public Rectangle getControlRectangle() {
		return getButtonRect();
	}

	// >>ComponentListener implementation
	public void componentResized(ComponentEvent e) {
		m_rectangle = this.getBounds();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}
	// <<ComponentListener implementaqtion

}

// //////////////////////////////////////////////////////////////////////
// $Log: IButton.java,v $
// Revision 1.3 2006/02/16 07:10:18 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:04 luzgin
// CVS log added
//