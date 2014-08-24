package com.home.vkmusicloader;

import java.io.IOException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.home.vkmusicloader.services.DownloadTrackService;
import com.home.vkmusicloader.services.TrackInfoPersistor;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity
{
	@InjectView(R.id.track_list)
	ListView m_TrackListView;
	MediaPlayer m_Player;
	SimpleCursorAdapter m_TracksAdapter;
	private int m_CurrentTrackId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Player = new MediaPlayer();
        m_Player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
							}
		});
        Intent intent = new Intent(this, TrackInfoPersistor.class);
		Toast.makeText(this, "Updating tracks", Toast.LENGTH_SHORT).show();
		startService(intent);
		
		VKDataOpenHelper dbHelper = new VKDataOpenHelper(this);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		Cursor tracksCursor = sdb.query(VKDataOpenHelper.TRACK_TABLE, new String[]{ VKDataOpenHelper._ID,VKDataOpenHelper.ARTIST_COLUMN ,VKDataOpenHelper.TITLE_COLUMN }, null, null, null, null, null);
		
		m_TracksAdapter = new SimpleCursorAdapter(this, R.layout.track_textview, tracksCursor, new String[0], new int[0], 0);
		m_TracksAdapter.setViewBinder(new ViewBinder(){

			@Override
			public boolean setViewValue(View trackView, Cursor cursor, int index) {
		        
		        TextView artist =(TextView)trackView.findViewById(R.id.track_artist);
		        TextView title =(TextView)trackView.findViewById(R.id.track_title);
		        ToggleButton playButton = (ToggleButton)trackView.findViewById(R.id.play_button);
		        View downloadButton = trackView.findViewById(R.id.download_button);
		        final int trackId = cursor.getInt(cursor.getColumnIndex(VKDataOpenHelper._ID));
		        downloadButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						downloadTrack(trackId);
					}
				});
		        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked)
						{
							play(trackId);
						}
						else
						{
							pause(trackId);
						}
					}
				});
		        playButton.setChecked(trackId == m_CurrentTrackId && m_Player.isPlaying());
		        title.setText(cursor.getString(2));
		        artist.setText(cursor.getString(1));
				return true;
			}
        	
        });
    }

	public void play(int trackId) {
		if (m_CurrentTrackId == trackId)
		{
			if (m_Player.isPlaying())
			{
				m_Player.start();
			}
			return;
		}
		VKDataOpenHelper dbHelper = new VKDataOpenHelper(this);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		
		Cursor tracksCursor = sdb.query(VKDataOpenHelper.TRACK_TABLE, 
				new String[]{ VKDataOpenHelper.URL_COLUMN }, "id=?", new String[]{Integer.toString(trackId)}, null, null, null);
		String url = tracksCursor.getString(0);
		tracksCursor.close();
		m_Player.reset();
		try {
			m_Player.setDataSource(url);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (SecurityException e) {
			e.printStackTrace();
			return;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}	
		m_Player.prepareAsync();	
	}

	public void pause(int trackID) {
		m_Player.pause();
		m_TracksAdapter.notifyDataSetChanged();
	}
	
	public void downloadTrack(int trackId) {		
		Intent intent = new Intent(this, DownloadTrackService.class);
		intent.putExtra(DownloadTrackService.ExtraName, trackId);

		Toast.makeText(this, "Downloading track", Toast.LENGTH_SHORT).show();
		startService(intent);
	}
    
    
}
