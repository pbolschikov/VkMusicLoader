package com.home.vkmusicloader.services;

public interface ITrackDownloader {
	int getTrackState(int trackId);
	void removeTrack(int trackId, Runnable removedCallback);
	void downloadTrack(int trackId, Runnable callback,
			Runnable downloadedCallback);
}