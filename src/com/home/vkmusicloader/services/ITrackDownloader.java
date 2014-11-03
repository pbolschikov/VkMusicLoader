package com.home.vkmusicloader.services;

public interface ITrackDownloader {
	void downloadTrack(int trackId, Runnable downloadedCallback);
	boolean isDownloaded(int trackId);
	void removeTrack(int trackId, Runnable removedCallback);
}