package com.home.vkmusicloader.services;

import java.util.List;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.methods.VKApiAudio;
import com.vk.sdk.api.model.VKApiAudioInfo;

public class TrackInfoPersistorService extends Service implements IUpdatesManager {
	
	private final Handler handler = new Handler();
	private boolean m_NeedUpdate = true;
	
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
    			SQLiteDatabase sdb = dbHelper.getWritableDatabase();
        		for (VKApiAudioInfo vkTackInfo: (List<VKApiAudioInfo>)response.parsedModel)
        		{
        			ContentValues values = new ContentValues();
        			values.put(VKDataOpenHelper._ID, vkTackInfo.id);
        			values.put(VKDataOpenHelper.ARTIST_COLUMN, vkTackInfo.artist);
        			values.put(VKDataOpenHelper.DURATION_COLUMN, vkTackInfo.duration);
        			values.put(VKDataOpenHelper.TITLE_COLUMN, vkTackInfo.title);
        			values.put(VKDataOpenHelper.URL_COLUMN, vkTackInfo.url);
        			sdb.insert(VKDataOpenHelper.TRACK_TABLE, null,  values);
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
