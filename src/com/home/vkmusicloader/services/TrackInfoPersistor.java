package com.home.vkmusicloader.services;

import java.util.List;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.methods.VKApiAudio;
import com.vk.sdk.api.model.VKApiAudioInfo;

public class TrackInfoPersistor extends Service {	
	@Override
    public void onCreate() {
        // The service is being created
    }
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
    	VKApiAudio audioApi = VKApi.audio();
        @SuppressWarnings("serial")
		final VKRequestListener mRequestListener = new VKRequestListener(){
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
        		stopSelf(startId);
        	}
        };
        audioApi.get().executeWithListener(mRequestListener);
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
