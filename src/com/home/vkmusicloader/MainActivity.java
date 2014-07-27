package com.home.vkmusicloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ListView;

import com.home.vkmusicloader.model.TrackInfo;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.methods.VKApiAudio;
import com.vk.sdk.api.model.VKApiAudioInfo;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity implements IMainActivity
{
	@InjectView(R.id.track_list)
	ListView m_TrackListView;
	MediaPlayer m_Player;
	final List<TrackInfo> m_Tracks = new ArrayList<TrackInfo>();
	TrackInfoAdapter m_TracksAdapter;
	TrackInfo m_LastTrack;
	
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
        m_TracksAdapter = new TrackInfoAdapter(this, R.layout.track_textview, m_Tracks);
        VKApiAudio audioApi = VKApi.audio();
        m_TrackListView.setAdapter(m_TracksAdapter);
        final VKRequestListener mRequestListener = new VKRequestListener(){
        	@Override
        	public void onComplete(com.vk.sdk.api.VKResponse response) {
        		for (VKApiAudioInfo vkTackInfo: (List<VKApiAudioInfo>)response.parsedModel)
        		{
        			m_Tracks.add(new TrackInfo(vkTackInfo.title, vkTackInfo.artist, vkTackInfo.url));
        		}
        		m_TracksAdapter.notifyDataSetChanged();
        	}
        };  
        
        audioApi.get().executeWithListener(mRequestListener);
    }

	@Override
	public void play(TrackInfo trackInfo) {
		if (trackInfo.getIsPlaying())
		{
			return;
		}
		if (m_LastTrack != trackInfo)
		{
			if (m_LastTrack != null)
			{
				m_LastTrack.setIsPlaying(false);
			}
			m_Player.reset();
			try {
				m_Player.setDataSource(trackInfo.getUrl());
				
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
		else{
			m_Player.start();
		}
		trackInfo.setIsPlaying(true);
		m_LastTrack = trackInfo;
		m_TracksAdapter.notifyDataSetChanged();
	}

	@Override
	public void pause(TrackInfo trackInfo) {
		if (trackInfo.getIsPlaying())
		{
			m_Player.pause();
			trackInfo.setIsPlaying(false);
			m_TracksAdapter.notifyDataSetChanged();
		}
	}
    
    
}
