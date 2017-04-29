package com.skeeper.core;

import com.skeeper.variables.CompNameVariable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class IOXmlAdapter {
	private final SAXParserFactory m_factory;
	private SecretTable m_table;
	private File m_file;

	public IOXmlAdapter() {
		m_table = new SecretTable();
		m_factory = SAXParserFactory.newInstance();
	}

	public SecretTable readXmlFile(File file) throws Exception {
		SAXParser parser = m_factory.newSAXParser();
		m_table.reset();
		m_file = file;
		validateFile(file, false);
		parser.parse(file, new XmlFilter());
		return m_table;
	}

	public SecretTable getSecretTable() {
		return m_table;
	}

	public void removeSecret(String key) {
		m_table.removeEntry(key);
		onUpdate();
	}

	public void addSecret(String key, LoginInfo logInfo) {
		m_table.addEntry(key, logInfo);
		onUpdate();
	}

	public void writeXmlFile(File file) throws Exception {
		m_file = file;
		validateFile(file, true);
		BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
		bfw.write("<?xml version=\"1.0\"?>");
		bfw.newLine();
		String rootPassCode = m_table.getRootPassCode();
		bfw.write("<" + CompNameVariable.ROOT + " "
				+ CompNameVariable.PASS_CODE + "=\"" + rootPassCode + "\">");
		bfw.newLine();
		Iterator<String> keyIt = m_table.getKeys().iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			LoginInfo logInfo = m_table.getLoginInfo(key);
			bfw.write("  <" + CompNameVariable.SECRET + " "
					+ CompNameVariable.SITE_NAME + "=\"" + key + "\" "
					+ CompNameVariable.LOGIN_CODE + "=\""
					+ logInfo.getLoginCode() + "\" "
					+ CompNameVariable.PASS_CODE + "=\""
					+ logInfo.getPassCode() + "\"/>");
		}
		bfw.write("</" + CompNameVariable.ROOT + ">");
		bfw.flush();
		bfw.close();
	}

	public SecretTable createNewXmlFile(File file, String pwd) throws Exception {
		m_table.setRootPassCode(pwd);
		writeXmlFile(file);
		return m_table;
	}

	public void validateFile(File file, boolean write) throws Exception {
		if (write) { // Write file
			boolean fileExists = file.exists();
			boolean fileCanWrite = file.canWrite();
			boolean fileCanCreate = file.createNewFile();
			if (fileExists) {
				if (!fileCanWrite) {
					throw new Exception("Can't write to file "
							+ file.getAbsolutePath());
				}
			} else if (!fileCanCreate) {
				throw new Exception("Can't create file "
						+ file.getAbsolutePath());
			}
		} else { // Read file
			if (!file.exists()) {
				throw new Exception("File doesn't exists "
						+ file.getAbsolutePath());
			}
		}
	}

	public String getRootPassCode() {
		return m_table.getRootPassCode();
	}

	private void onUpdate() {
		try {
			// Backup:
			if (m_file.exists()) {
				File backupFile = new File(m_file.getAbsolutePath() + "~");
				BufferedWriter bfw = new BufferedWriter(new FileWriter(
						backupFile));
				BufferedReader bfr = new BufferedReader(new FileReader(m_file));
				String line;
				while ((line = bfr.readLine()) != null) {
					bfw.write(line);
					bfw.newLine();
				}
				bfr.close();
				bfw.close();
				// Write new content:
				writeXmlFile(m_file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class XmlFilter extends DefaultHandler {

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) {
			if (qName.equals(CompNameVariable.SECRET)) {
				String siteName = attributes
						.getValue(CompNameVariable.SITE_NAME);
				String loginCode = attributes
						.getValue(CompNameVariable.LOGIN_CODE);
				String passCode = attributes
						.getValue(CompNameVariable.PASS_CODE);
				m_table.addEntry(siteName, new LoginInfo(loginCode, passCode));
			} else if (qName.equals(CompNameVariable.ROOT)) {
				String rootPassCode = attributes
						.getValue(CompNameVariable.PASS_CODE);
				m_table.setRootPassCode(rootPassCode);
			}
		}
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: IOXmlAdapter.java,v $
// Revision 1.2 2006/02/15 04:59:00 luzgin
// CVS log added
//