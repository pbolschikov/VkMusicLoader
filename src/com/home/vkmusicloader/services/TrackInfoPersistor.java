package com.home.vkmusicloader.services;

import java.util.List;

import com.home.vkmusicloader.model.TrackInfo;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.methods.VKApiAudio;
import com.vk.sdk.api.model.VKApiAudioInfo;

import android.app.IntentService;
import android.content.Intent;

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
	        		for (VKApiAudioInfo vkTackInfo: (List<VKApiAudioInfo>)response.parsedModel)
	        		{
	        			m_Tracks.add(new TrackInfo(vkTackInfo.title, vkTackInfo.artist, vkTackInfo.url));
	        		}
	        	}
	        };
	        audioApi.get().executeWithListener(mRequestListener);
	}

}
