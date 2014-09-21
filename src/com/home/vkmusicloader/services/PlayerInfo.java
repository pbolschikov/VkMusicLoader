package com.home.vkmusicloader.services;

public class PlayerInfo {
	private final int m_TrackId;
	private final boolean m_IsPlaying;

	public PlayerInfo(int trackId, boolean isPlaying)
	{
		m_TrackId= trackId;
		m_IsPlaying = isPlaying;
	}

	public int getTrackId() {
		return m_TrackId;
	}

	public boolean isPlaying() {
		return m_IsPlaying;
	}
	
	public boolean isTrackSelected(){
		return m_TrackId != 0;
	}

	public boolean isPlaing(int trackId) {
		return m_TrackId == trackId && m_IsPlaying;
	}
}
