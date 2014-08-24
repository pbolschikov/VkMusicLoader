package com.home.vkmusicloader.services;

import java.util.List;

import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.home.vkmusicloader.model.TrackInfo;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.methods.VKApiAudio;
import com.vk.sdk.api.model.VKApiAudioInfo;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class TrackInfoPersistor extends IntentService {

	public TrackInfoPersistor() {
		super("TrackInfoPersistor");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		 VKApiAudio audioApi = VKApi.audio();
	        @SuppressWarnings("serial")
			final VKRequestListener mRequestListener = new VKRequestListener(){
	        	public void onComplete(com.vk.sdk.api.VKResponse response) {
        			VKDataOpenHelper dbHelper = new VKDataOpenHelper(getApplicationContext());
        			SQLiteDatabase sdb = dbHelper.getWritableDatabase();
	        		for (VKApiAudioInfo vkTackInfo: (List<VKApiAudioInfo>)response.parsedModel)
	        		{
	        			ContentValues values = new ContentValues();
	        			values.put(VKDataOpenHelper.ARTIST_COLUMN, vkTackInfo.artist);
	        			values.put(VKDataOpenHelper.DURATION_COLUMN, vkTackInfo.duration);
	        			values.put(VKDataOpenHelper.TITLE_COLUMN, vkTackInfo.title);
	        			values.put(VKDataOpenHelper.URL_COLUMN, vkTackInfo.url);
	        			sdb.insert(VKDataOpenHelper.TRACK_TABLE, null,  values);
	        		}
	        	}
	        };
	        audioApi.get().executeWithListener(mRequestListener);
	}

}
