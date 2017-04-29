package com.skeeper.variables;

import java.awt.Dimension;

public class ImageInfo {

	private final String m_imgPath;
	public final Dimension m_imgDimension;

	public ImageInfo(String imgPath, Dimension imgDimension) {
		m_imgPath = imgPath;
		m_imgDimension = imgDimension;
	}

	public String getImagePath() {
		return m_imgPath;
	}

	public Dimension getImageDimension() {
		return m_imgDimension;
	}

}

// //////////////////////////////////////////////////////////////////////////////
// $Log: ImageInfo.java,v $
// Revision 1.2 2006/02/15 04:59:11 luzgin
// CVS log added
//