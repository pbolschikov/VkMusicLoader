package com.home.vkmusicloader.services;

public interface IPlayer {
	void play(int trackId);
	void playNext();
	void playPrevious();
	void pause();
	void stop();
	void setPlayerListener(IPlayerListener playerListener);
	PlayerInfo getCurrentTrack();
}
