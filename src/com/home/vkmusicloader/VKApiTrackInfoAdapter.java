package com.home.vkmusicloader;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vk.sdk.api.model.VKApiAudioInfo;


public class VKApiTrackInfoAdapter extends ArrayAdapter<VKApiAudioInfo> 
{
	final int resource;
    final MediaPlayer m_Player = new MediaPlayer();
    
    //Initialize adapter
    public VKApiTrackInfoAdapter(Context context, int resource, List<VKApiAudioInfo> items) {
        super(context, resource, items);
        this.resource=resource;
 
    }
         
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout trackView;
        //Get the current alert object
        final VKApiAudioInfo trackInfo = getItem(position);
         
        //Inflate the view
        if(convertView==null)
        {
            trackView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, trackView, true);
        }
        else
        {
            trackView = (LinearLayout) convertView;
        }
        //Get the text boxes from the listitem.xml file
        TextView artist =(TextView)trackView.findViewById(R.id.track_artist);
        TextView title =(TextView)trackView.findViewById(R.id.track_title);
        ToggleButton playButton = (ToggleButton)trackView.findViewById(R.id.play_button);
        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked)
				{
					m_Player.pause();
					return;
				}
				try {
					m_Player.reset();
					m_Player.setDataSource(trackInfo.url);
					m_Player.prepare();
					m_Player.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        //Assign the appropriate data from our alert object above
        title.setText(trackInfo.title);
        artist.setText(trackInfo.artist);
         
        return trackView;
    }
}

