package com.home.vkmusicloader;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.home.vkmusicloader.services.DownloadTrackService;
import com.home.vkmusicloader.services.IPlayer;
import com.home.vkmusicloader.services.IPlayerListener;
import com.home.vkmusicloader.services.LocalBinder;
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
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.IBinder;

public final class TracksCursorAdapter extends CursorAdapter {

	private final LayoutInflater m_Inflater;
	private IPlayer m_Player;
	private final ContextWrapper m_MainActivity;
	
	public TracksCursorAdapter(ContextWrapper mainActivity, Cursor c) {
		super(mainActivity, c, 0);
		m_MainActivity = mainActivity;
		m_Inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Intent trackPlayerService = new Intent(mainActivity, TrackPlayerService.class);
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
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	            m_Player = null;
	            m_Player.setPlayerListener(null);
	        }
	    }, Context.BIND_AUTO_CREATE);
        
	}

	@Override
	public void bindView(View trackView, Context context, Cursor cursor) {
		TextView artist =(TextView)trackView.findViewById(R.id.track_artist);
        TextView title =(TextView)trackView.findViewById(R.id.track_title);
        ToggleButton playButton = (ToggleButton)trackView.findViewById(R.id.play_button);
        View downloadButton = trackView.findViewById(R.id.download_button);
        final int trackId = cursor.getInt(cursor.getColumnIndex(VKDataOpenHelper._ID));;
        downloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_MainActivity, DownloadTrackService.class);
				intent.putExtra(DownloadTrackService.ExtraName, trackId);
				Toast.makeText(m_MainActivity, "Downloading track", Toast.LENGTH_SHORT).show();
				m_MainActivity.startService(intent);
			}
		});
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
        title.setText(cursor.getString(2));
        artist.setText(cursor.getString(1));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return m_Inflater.inflate(R.layout.track_textview, viewGroup, false);
	}

}
