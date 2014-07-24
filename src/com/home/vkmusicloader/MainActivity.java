package com.home.vkmusicloader;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.methods.VKApiAudio;
import com.vk.sdk.api.model.VKApiAudioInfo;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity 
{
	@InjectView(R.id.track_list)
	ListView mTrackLsit;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKApiAudio audioApi = VKApi.audio();
        final Context context = this;
        final VKRequestListener mRequestListener = new VKRequestListener(){
        	@Override
        	public void onComplete(com.vk.sdk.api.VKResponse response) {
        		List<VKApiAudioInfo> model = (List<VKApiAudioInfo>)response.parsedModel;
        	    
        		ArrayAdapter<VKApiAudioInfo> adapter = new VKApiTrackInfoAdapter(context, R.layout.track_textview, model);
        		mTrackLsit.setAdapter(adapter);
        	}
        };  
        
        audioApi.get().executeWithListener(mRequestListener);
    }
    
    
}
