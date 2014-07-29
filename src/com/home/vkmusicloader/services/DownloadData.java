package com.home.vkmusicloader.services;

import java.io.File;
import java.io.Serializable;

public class DownloadData implements Serializable {
	
	private static final long serialVersionUID = -3470798582076769133L;
	private final int m_Id;
	private final String m_Url;
	private final File m_File;
	
	public DownloadData(int id, String url, File file)
	{
		m_Id = id;
		m_Url = url;
		m_File = file;
	}
	
	public String getUrl() {
		return m_Url;
	}
	public int getId() {
		return m_Id;
	}

	public File getFile() {
		return m_File;
	}
}
