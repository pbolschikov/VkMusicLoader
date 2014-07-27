package com.home.vkmusicloader.model;

public class TrackInfo {
    private final String m_Title;
	private final String m_Artist;
	private boolean m_IsPlaying;
	private final String m_Url;
	
	public TrackInfo(String title, String artist, String url)
    {
    	m_Title = title;
    	m_Artist = artist;
    	m_Url = url;
    }

	public String getTitle() {
		return m_Title;
	}

	public String getArtist() {
		return m_Artist;
	}

	public boolean getIsPlaying() {
		return m_IsPlaying;
	}

	public void setIsPlaying(boolean m_IsPlaying) {
		this.m_IsPlaying = m_IsPlaying;
	}

	public String getUrl() {
		return m_Url;
	}
}
