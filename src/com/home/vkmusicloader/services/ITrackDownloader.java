package com.home.vkmusicloader.services;

public interface ITrackDownloader {
	void downloadTrack(int trackId, Runnable downloadedCallback);
}
