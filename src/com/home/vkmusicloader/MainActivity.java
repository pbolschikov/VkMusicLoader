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
import android.widget.ListView;
import android.widget.Toast;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.home.vkmusicloader.services.DownloadTrackService;
import com.home.vkmusicloader.services.TrackInfoPersistor;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity
{
	@InjectView(R.id.track_list)
	ListView m_TrackListView;
	MediaPlayer m_Player;
	TracksCursorAdapter m_TracksAdapter;
	private int m_CurrentTrackId;
	private boolean m_IsPlaing;
	
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
		m_TracksAdapter = new TracksCursorAdapter(this, tracksCursor);
		m_TrackListView.setAdapter(m_TracksAdapter);
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
				new String[]{ VKDataOpenHelper.URL_COLUMN }, VKDataOpenHelper._ID +"=?", new String[]{Integer.toString(trackId)}, null, null, null);
		if (!tracksCursor.moveToFirst())
		{
			return;
		}
		m_CurrentTrackId = trackId;
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
		m_IsPlaing = true;
		m_TracksAdapter.notifyDataSetChanged();
	}

	public void pause(int trackID) {
		if (m_CurrentTrackId != trackID)
		{
			return;
		}
		m_Player.pause();
		m_IsPlaing = false;
		m_TracksAdapter.notifyDataSetChanged();
	}
	
	public void downloadTrack(int trackId) {		
		Intent intent = new Intent(this, DownloadTrackService.class);
		intent.putExtra(DownloadTrackService.ExtraName, trackId);

		Toast.makeText(this, "Downloading track", Toast.LENGTH_SHORT).show();
		startService(intent);
	}

	public boolean isPlaing(int trackId) {
		return m_CurrentTrackId == trackId && m_IsPlaing;
	}
    
    
}
