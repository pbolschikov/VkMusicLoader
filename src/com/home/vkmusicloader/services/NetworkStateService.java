package com.home.vkmusicloader.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

public final class NetworkStateService extends Service implements INetworkStateProvider {

	boolean m_IsOnline = false;
	INetworkStateListener m_NetworkStateListner; 
	
	@Override 
	public void onCreate() {
		IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);           
        registerReceiver(new BroadcastReceiver()
		{
		  @Override
		  public void onReceive( Context context, Intent intent )
		  {
		    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
		    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		    m_IsOnline = activeNetInfo != null;
		    if (m_NetworkStateListner != null)
		    {
		    	m_NetworkStateListner.onStateChanged(activeNetInfo != null);
			}
		  }
		}
		, filter);	
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder<INetworkStateProvider>(this);
	}

	@Override
	public void setNetworkStateListener(
			INetworkStateListener networkStateListener) {
		m_NetworkStateListner = networkStateListener;
	}

	@Override
	public boolean isOnline() {
		return m_IsOnline;
	}

}
