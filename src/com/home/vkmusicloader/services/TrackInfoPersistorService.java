package com.home.vkmusicloader.services;

import java.util.List;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.methods.VKApiAudio;
import com.vk.sdk.api.model.VKApiAudioInfo;

public class TrackInfoPersistorService extends Service implements IUpdatesManager {
	
	private final Handler handler = new Handler();
	private boolean m_NeedUpdate = true;
	private final String INSERT_TRACK_QUERY = "INSERT OR REPLACE INTO " + VKDataOpenHelper.TRACK_TABLE + "(" +
			VKDataOpenHelper._ID + "," +
	VKDataOpenHelper.TRACK_TABLE_ARTIST_COLUMN + "," +
	VKDataOpenHelper.TRACK_TABLE_DURATION_COLUMN + "," +
	VKDataOpenHelper.TRACK_TABLE_TITLE_COLUMN + "," +
	VKDataOpenHelper.TRACK_TABLE_URL_COLUMN + ")" + "VALUES (?,?,?,?,?)";
	
	private final String INSERT_TRACK_TO_TRACKLIST_QUERY = "INSERT OR REPLACE INTO " + VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE + "(" +
			VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN + "," +
	VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + "," +
	VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN + ")" + "VALUES (?,?,?)";
	
	
    public void checkUpdates(final Runnable updateCallback) {
    	if (!needUpdate())
    	{
    		return;
    	}
    	Toast.makeText(getApplicationContext(), "Updating tracks", Toast.LENGTH_SHORT).show();
    	
    	VKApiAudio audioApi = VKApi.audio();
        @SuppressWarnings("serial")
		final VKRequestListener mRequestListener = new VKRequestListener(){
        	@SuppressWarnings("unchecked")
			public void onComplete(com.vk.sdk.api.VKResponse response) {
    			VKDataOpenHelper dbHelper = new VKDataOpenHelper(getApplicationContext());
    			final SQLiteDatabase sdb = dbHelper.getWritableDatabase();
    			int index = 0;
    			
    		    final SQLiteStatement insertTrackStatement = sdb.compileStatement(INSERT_TRACK_QUERY);
    		    final SQLiteStatement insertTrackToTrackListStatement = sdb.compileStatement(INSERT_TRACK_TO_TRACKLIST_QUERY);
    		    sdb.beginTransaction();
    		    try {
    		    	ContentValues defaultTrackListValues = new ContentValues();
        			defaultTrackListValues.put(BaseColumns._ID, VKDataOpenHelper.DEFAULTTRACKLIST_ID);
        			defaultTrackListValues.put(VKDataOpenHelper.TRACKLIST_TABLE_TITLE_COLUMN, VKDataOpenHelper.DEFAULTTRACKLIST_TITLE);
        			sdb.insert(VKDataOpenHelper.TRACKLIST_TABLE, null, defaultTrackListValues);
    		    	for (VKApiAudioInfo vkTackInfo: (List<VKApiAudioInfo>)response.parsedModel)
            		{
            			insertTrackStatement.clearBindings();
            			insertTrackStatement.bindLong(1, vkTackInfo.id);
            			insertTrackStatement.bindString(2, vkTackInfo.artist);
            			insertTrackStatement.bindLong(3, vkTackInfo.duration);
            			insertTrackStatement.bindString(4, vkTackInfo.title);
            			insertTrackStatement.bindString(5, vkTackInfo.url);
            			insertTrackStatement.execute();
            			
            			insertTrackToTrackListStatement.clearBindings();
            			insertTrackToTrackListStatement.bindLong(1, 0);
            			insertTrackToTrackListStatement.bindLong(2,  vkTackInfo.id);
            			insertTrackToTrackListStatement.bindLong(3, index++);
            			insertTrackStatement.execute();
            		}
    		        sdb.setTransactionSuccessful();
    		    }
    		    finally {
    		        sdb.endTransaction();
    		    }
        		onTracksUpdated();
        		handler.post(updateCallback);
        	}
        };
        audioApi.get().executeWithListener(mRequestListener);
    }
    
    private void onTracksUpdated() {
    	m_NeedUpdate = false;
	}
    
    private boolean needUpdate() {
		return m_NeedUpdate;
	}
    
	@Override
	public IBinder onBind(Intent arg0) {
		return new LocalBinder<IUpdatesManager>(this);
	}
}
