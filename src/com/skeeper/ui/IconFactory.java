package com.skeeper.ui;

import com.skeeper.common.ResourcesHolder;
import com.skeeper.variables.CompNameVariable;
import com.skeeper.variables.ImageInfo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Hashtable;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

public class IconFactory {

	private static final IconFactory iifactory = new IconFactory();
	private static final ClassLoader loader = iifactory.getClass()
			.getClassLoader();

	public static final ImageIcon getResourceImageIcon(String name) {
		ResourceBundle imgBundle = ResourcesHolder.getInstance().getResource(
				CompNameVariable.IMAGE_RESOURCE);
		ImageInfo imgInfo = (ImageInfo) imgBundle.getObject(name);
		String imgPath = imgInfo.getImagePath();
		Dimension imgDim = imgInfo.getImageDimension();
		URL iconURL = loader.getResource(imgPath);
		if (iconURL != null) {
			BufferedImage bufImg = new BufferedImage((int) imgDim.getWidth(),
					(int) imgDim.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bufImg.createGraphics();
			g2d.drawImage(new ImageIcon(iconURL).getImage(), 0, 0, null);
			g2d.dispose();
			return new ImageIcon(bufImg);
		} else {
			return getSolidIcon((int) imgDim.getWidth(),
					(int) imgDim.getHeight(), Color.BLACK);
		}
	}

	public static final Hashtable<Integer, Image> getResourceImageMap(String name) {
		ResourceBundle imgBundle = ResourcesHolder.getInstance().getResource(
				CompNameVariable.IMAGE_RESOURCE);
		ImageInfo imgInfo = (ImageInfo) imgBundle.getObject(name);
		String imgPath = imgInfo.getImagePath();
		Dimension imgDim = imgInfo.getImageDimension();
		Hashtable<Integer, Image> imageMap = new Hashtable<Integer, Image>();
		URL iconURL = loader.getResource(imgPath);
		if (iconURL != null) {
			ImageIcon imgIcon = new ImageIcon(iconURL);
			int totalImgWidth = imgIcon.getImage().getWidth(null);
			int clipWidth = (int) imgDim.getWidth();
			int clipHeight = (int) imgDim.getHeight();
			if (totalImgWidth > 0 && clipWidth > 0) {
				int clipCount = (int) totalImgWidth / clipWidth;
				if ((totalImgWidth - clipCount * clipWidth) > clipWidth / 2) {
					clipCount++;
				}
				int sX1 = 0;
				int sY1 = 0;
				int dX1 = 0;
				int dY1 = 0;
				for (int i = 0; i < clipCount; i++) {
					BufferedImage frameImg = new BufferedImage(clipWidth,
							clipHeight, BufferedImage.TYPE_INT_ARGB);
					Graphics2D frameGrp = frameImg.createGraphics();
					int sX2 = sX1 + clipWidth;
					int sY2 = sY1 + clipHeight;
					int dX2 = dX1 + clipWidth;
					int dY2 = dY1 + clipHeight;
					frameGrp.drawImage(imgIcon.getImage(), dX1, dY1, dX2, dY2,
							sX1, sY1, sX2, sY2, null);
					frameGrp.dispose();
					imageMap.put(new Integer(i), frameImg);
					sX1 += clipWidth;
				}
			} else {
				ImageIcon dummyIcon = getSolidIcon((int) imgDim.getWidth(),
						(int) imgDim.getHeight(), Color.BLACK);
				imageMap.put(new Integer(0), dummyIcon.getImage());
			}
		} else {
			ImageIcon dummyIcon = getSolidIcon((int) imgDim.getWidth(),
					(int) imgDim.getHeight(), Color.BLACK);
			imageMap.put(new Integer(0), dummyIcon.getImage());
		}
		return imageMap;
	}

	/** Create Image Icon of specified size filled with specified color */
	public static final ImageIcon getSolidIcon(int width, int height,
			Color color) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(color);
		g2d.fillRect(0, 0, width, height);
		g2d.dispose();
		return new ImageIcon(image);
	}

	/** Draw and return chevron Icon */
	public static final ImageIcon getChevronIcon(int state) {
		BufferedImage image = new BufferedImage(10, 10,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		if (state == IButton.DOWN) {
			g.setColor(new Color(0, 0, 0));
			// Draw first >
			g.drawLine(2, 1, 4, 3);
			g.drawLine(2, 2, 4, 4);
			g.drawLine(4, 3, 6, 1);
			g.drawLine(4, 4, 6, 2);
			// Draw second >
			g.drawLine(2, 5, 4, 7);
			g.drawLine(2, 6, 4, 8);
			g.drawLine(4, 7, 6, 5);
			g.drawLine(4, 8, 6, 6);
		} else {
			if (state == IButton.ACTIVE) {
				g.setColor(new Color(93, 93, 93));
			} else {
				g.setColor(new Color(0, 0, 0));
			}
			// Draw first >
			g.drawLine(1, 2, 3, 4);
			g.drawLine(2, 2, 4, 4);
			g.drawLine(3, 4, 1, 6);
			g.drawLine(4, 4, 2, 6);
			// Draw second >
			g.drawLine(5, 2, 7, 4);
			g.drawLine(6, 2, 8, 4);
			g.drawLine(7, 4, 5, 6);
			g.drawLine(8, 4, 6, 6);
		}
		g.dispose();
		return new ImageIcon(image);
	}

}

// //////////////////////////////////////////////////////////////////////////////
// $Log: IconFactory.java,v $
// Revision 1.3 2006/02/16 07:10:30 luzgin
// Cleaned up and converted code to Java 1.5 standard.
//
// Revision 1.2 2006/02/15 04:59:08 luzgin
// CVS log added
//