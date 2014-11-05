package com.home.vkmusicloader;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.home.vkmusicloader.services.DownloadTrackService;
import com.home.vkmusicloader.services.INetworkStateProvider;
import com.home.vkmusicloader.services.IPlayer;
import com.home.vkmusicloader.services.IPlayerListener;
import com.home.vkmusicloader.services.ITrackDownloader;
import com.home.vkmusicloader.services.LocalBinder;
import com.home.vkmusicloader.services.NetworkStateService;
import com.home.vkmusicloader.services.PlayerInfo;
import com.home.vkmusicloader.services.TrackPlayerService;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.IBinder;

public final class TracksCursorAdapter extends CursorAdapter {

	private final LayoutInflater m_Inflater;
	private IPlayer m_Player;
	private ITrackDownloader m_TrackDownloader;
	private final ContextWrapper m_MainActivity;
	private int m_SelectedTrackId = 0;
	private INetworkStateProvider m_NetworkStateProvider;
	
	public TracksCursorAdapter(ContextWrapper mainActivity, Cursor c) {
		super(mainActivity, c, 0);
		m_MainActivity = mainActivity;
		m_Inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Intent trackPlayerService = new Intent(mainActivity, TrackPlayerService.class);
		mainActivity.getApplicationContext().bindService(trackPlayerService, new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	            @SuppressWarnings("unchecked")
				LocalBinder<IPlayer> binder = (LocalBinder<IPlayer>) service;
	            m_Player = binder.getService();
	            m_Player.setPlayerListener(new IPlayerListener() {
					
					@Override
					public void onStateChanged(PlayerInfo playerInfo) {
						notifyDataSetChanged();
					}
				});
	            notifyDataSetChanged();
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	            m_Player = null;
	            m_Player.setPlayerListener(null);
	        }
	    }, Context.BIND_AUTO_CREATE);
		
		final Intent downloadTrackService = new Intent(m_MainActivity, DownloadTrackService.class);
		m_MainActivity.getApplicationContext().bindService(downloadTrackService, new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	            @SuppressWarnings("unchecked")
				LocalBinder<ITrackDownloader> binder = (LocalBinder<ITrackDownloader>) service;
	            m_TrackDownloader = binder.getService();
	            notifyDataSetChanged();
	            }

	        @Override
	        public void onServiceDisconnected(ComponentName className) {
	        	m_TrackDownloader = null;
	        }
	    }, Context.BIND_AUTO_CREATE);
		
		final Intent networkStateService = new Intent(m_MainActivity, NetworkStateService.class);
		m_MainActivity.getApplicationContext().bindService(networkStateService, new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	            @SuppressWarnings("unchecked")
				LocalBinder<INetworkStateProvider> binder = (LocalBinder<INetworkStateProvider>) service;
	            m_NetworkStateProvider = binder.getService();
	            notifyDataSetChanged();
	            }

	        @Override
	        public void onServiceDisconnected(ComponentName className) {
	        	m_NetworkStateProvider = null;
	        }
	    }, Context.BIND_AUTO_CREATE);
	}
	
	private void setTrackSelected(int trackId)
	{
		m_SelectedTrackId = trackId;
		notifyDataSetChanged();
	}
	
	private boolean isTrackSelected(int trackId)
	{
		return m_SelectedTrackId == trackId;
	}
	
	private boolean isPalyingOrPaused(int trackId)
	{
		return m_Player != null && m_Player.getCurrentTrack().getTrackId() == trackId;
	}
	
	
	@Override
	public void bindView(View trackView, Context context, Cursor cursor) {
		
		final TextView artist =(TextView)trackView.findViewById(R.id.track_artist);
        final TextView title =(TextView)trackView.findViewById(R.id.track_title);
        final ToggleButton playButton = (ToggleButton)trackView.findViewById(R.id.play_button);
        final View downloadButton = trackView.findViewById(R.id.download_button);
        final View removeButton = trackView.findViewById(R.id.remove_button);
        final View downloadProgressBar = trackView.findViewById(R.id.download_progressbar);
        final int trackId = cursor.getInt(cursor.getColumnIndex(VKDataOpenHelper._ID));
        trackView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setTrackSelected(trackId);
			}
		});
        downloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_TrackDownloader.downloadTrack(trackId, new Runnable(){
					@Override
					public void run() {
						notifyDataSetChanged();
					}	
				},
				new Runnable(){
					@Override
					public void run() {
						notifyDataSetChanged();
						Toast.makeText(m_MainActivity, "Track has been successfully downloaded", Toast.LENGTH_SHORT).show();
					}	
				});
				Toast.makeText(m_MainActivity, "Downloading track", Toast.LENGTH_SHORT).show();
			}
		});
        removeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_TrackDownloader.removeTrack(trackId, new Runnable(){
				@Override
					public void run() {
						notifyDataSetChanged();
						Toast.makeText(m_MainActivity, "Track has been successfully removed", Toast.LENGTH_SHORT).show();
					}	
				});
			}
		});
        
        downloadProgressBar.setVisibility(m_TrackDownloader != null && m_NetworkStateProvider.isOnline() && m_TrackDownloader.getTrackState(trackId) == VKDataOpenHelper.TRACK_UPLOAD_TABLE_STATE_COLUMN_UPLOADING  ? View.VISIBLE : View.INVISIBLE);
        downloadButton.setVisibility(m_TrackDownloader != null && m_NetworkStateProvider.isOnline() && m_TrackDownloader.getTrackState(trackId) == VKDataOpenHelper.TRACK_UPLOAD_TABLE_STATE_COLUMN_NEW  ? View.VISIBLE : View.INVISIBLE);
        removeButton.setVisibility(m_TrackDownloader != null && m_TrackDownloader.getTrackState(trackId) == VKDataOpenHelper.TRACK_UPLOAD_TABLE_STATE_COLUMN_UPLOADED ? View.VISIBLE : View.INVISIBLE);
        playButton.setVisibility(m_Player != null && m_Player.canPlay(trackId) ? View.VISIBLE : View.INVISIBLE);
        playButton.setOnCheckedChangeListener(null);
        playButton.setChecked(m_Player != null && m_Player.getCurrentTrack().isPlaing(trackId));
        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (m_Player == null)
				{
					return;
				}
				if (isChecked)
				{
					m_Player.play(trackId);
				}
				else
				{
					m_Player.pause();
				}
			}
		});
        
        title.setSingleLine(!isTrackSelected(trackId));
        trackView.setBackgroundColor(m_MainActivity.getResources().getColor(isTrackSelected(trackId) ? R.color.track_selected : R.color.track_default));
        setColor(title, isPalyingOrPaused(trackId));
        setColor(artist, isPalyingOrPaused(trackId));
        title.setText(cursor.getString(1));
        artist.setText(cursor.getString(2));
	}
	
	private void setColor(TextView textView, boolean isSelected)
	{
		textView.setTextColor(m_MainActivity.getResources().getColor(isSelected ? R.color.vk_color : R.color.vk_smoke_white));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return m_Inflater.inflate(R.layout.track_textview, viewGroup, false);
	}

}
