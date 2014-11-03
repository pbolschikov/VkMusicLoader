package com.home.vkmusicloader.services;

public interface INetworkStateProvider {
	void setNetworkStateListener(INetworkStateListener networkStateListener);
	boolean isOnline();
}
