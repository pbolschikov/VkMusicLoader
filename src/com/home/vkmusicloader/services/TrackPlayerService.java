package com.home.vkmusicloader.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.home.vkmusicloader.R;
import com.home.vkmusicloader.data.VKDataOpenHelper;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class TrackPlayerService extends Service implements IPlayer {
	private MediaPlayer m_Player;
	private final Handler m_Handler = new Handler();
	private int m_CurrentTrackId;
	private boolean m_IsPlaing;
	private IPlayerListener m_PlayerListener;
	private int m_CurrentPlayListId = VKDataOpenHelper.DEFAULTTRACKLIST_ID;
	private boolean m_CanPlayRemote;
	
	@Override 
	public void onCreate() {
		m_Player = new MediaPlayer();
		m_Player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				m_IsPlaing = false;
				m_CurrentTrackId = 0;
				raiseStateChangedFromSeparateThread();
				return false;
			}
		});
		m_Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (m_IsPlaing)
				{
					playNext();
				}
				raiseStateChangedFromSeparateThread();
			}
		});
        m_Player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
			}
		});
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);           
        registerReceiver(new BroadcastReceiver()
		{
		  @Override
		  public void onReceive( Context context, Intent intent )
		  {
		    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
		    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		    m_CanPlayRemote = activeNetInfo != null;
		    raiseStateChangedFromSeparateThread();
		  }
		}
		, filter);
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder<IPlayer>(this);
	}

	@Override
	public void play(int trackId) {
		if (m_CurrentTrackId == trackId)
		{
			if (!m_Player.isPlaying())
			{
				m_Player.start();
			}
		}
		else
		{
		VKDataOpenHelper dbHelper = new VKDataOpenHelper(this);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		
        
		Cursor tracksCursor = sdb.query(VKDataOpenHelper.TRACK_TABLE, 
				new String[]{ VKDataOpenHelper.TRACK_TABLE_URL_COLUMN }, VKDataOpenHelper._ID +"=?", new String[]{Integer.toString(trackId)}, null, null, null);
		Cursor locationCursor = sdb.query(VKDataOpenHelper.TRACK_UPLOAD_TABLE, 
				new String[]{ VKDataOpenHelper.TRACK_UPLOAD_TABLE_LOCATION_COLUMN }, VKDataOpenHelper._ID +"=? AND " + VKDataOpenHelper.TRACK_UPLOAD_TABLE_STATE_COLUMN + "=" + VKDataOpenHelper.TRACK_UPLOAD_TABLE_STATE_COLUMN_UPLOADED, new String[]{Integer.toString(trackId)}, null, null, null);
		
		try {
			if (!tracksCursor.moveToFirst())
			{
				return;
			}
			m_CurrentTrackId = trackId;
			m_Player.reset();
			m_Player.setDataSource(locationCursor.moveToFirst() ? locationCursor.getString(0) : tracksCursor.getString(0));
			
		} catch (IllegalArgumentException e) {
			stop();
			return;
		} catch (SecurityException e) {
			stop();
			return;
		} catch (IllegalStateException e) {
			stop();
			return;
		}
		catch (FileNotFoundException e)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.file_removed_error), Toast.LENGTH_SHORT).show();
			//TODO update file location
			stop();
			return;
		}
		catch (IOException e) {
			Toast.makeText(getApplicationContext(), getString(R.string.track_io_error), Toast.LENGTH_SHORT).show();
			stop();
			return;
		}
		finally{
			tracksCursor.close();
			locationCursor.close();
		}
		m_Player.prepareAsync();
		}
		m_IsPlaing = true;
		raiseStateChanged();
	}

	@Override
	public void pause() {
		m_Player.pause();
		m_IsPlaing = false;
		raiseStateChanged();
	}

	@Override
	public void stop() {
		m_Player.stop();
		m_IsPlaing = false;
		m_CurrentTrackId = 0;
		raiseStateChanged();
	}
	
	private void raiseStateChangedFromSeparateThread()
	{
		if (m_PlayerListener != null)
		{
			m_Handler.post(new Runnable() {
				
				@Override
				public void run() {
					raiseStateChanged();
				}
			});	
		}
	}
	
	private void raiseStateChanged()
	{
		if (m_PlayerListener != null)
		{
			m_PlayerListener.onStateChanged(new PlayerInfo(m_CurrentTrackId, m_IsPlaing));
		}
	}

	@Override
	public PlayerInfo getCurrentTrack() {
		return new PlayerInfo(m_CurrentTrackId, m_IsPlaing);
	}

	@Override
	public void setPlayerListener(IPlayerListener playerListener) {
		m_PlayerListener = playerListener;
	}

	@Override
	public void playNext() {
		VKDataOpenHelper dbHelper = new VKDataOpenHelper(this);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        String playListId = Integer.toString(m_CurrentPlayListId);
        Cursor cursor = m_CurrentTrackId != 0 
        		? sdb.rawQuery(VKDataOpenHelper.NEXT_TRACK_SELECT, new String[]{ playListId, Integer.toString(m_CurrentTrackId), playListId})
        		: sdb.rawQuery(VKDataOpenHelper.FIRST_TRACK_SELECT, new String[]{ playListId });
		if (cursor.moveToFirst())
		{
			play(cursor.getInt(0));
		}
		else
		{
			stop();
		}
		cursor.close();
	}

	@Override
	public void playPrevious() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean canPlay(int trackId) {
		return m_CanPlayRemote || isUploaded(trackId);
	}

	private boolean isUploaded(int trackId) {
		VKDataOpenHelper dbHelper = new VKDataOpenHelper(this);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		Cursor uploadCursor = sdb.query(VKDataOpenHelper.TRACK_UPLOAD_TABLE, 
				new String[]{ VKDataOpenHelper.TRACK_UPLOAD_TABLE_LOCATION_COLUMN }, VKDataOpenHelper._ID +"=? AND " + VKDataOpenHelper.TRACK_UPLOAD_TABLE_STATE_COLUMN + "=" + VKDataOpenHelper.TRACK_UPLOAD_TABLE_STATE_COLUMN_UPLOADED, new String[]{Integer.toString(trackId)}, null, null, null);
		try
		{
		return uploadCursor.moveToFirst();
		}
		finally{
			uploadCursor.close();
		}
	}
}
