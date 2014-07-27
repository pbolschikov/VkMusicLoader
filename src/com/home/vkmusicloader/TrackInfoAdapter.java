package com.home.vkmusicloader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.home.vkmusicloader.model.TrackInfo;


public class TrackInfoAdapter extends ArrayAdapter<TrackInfo> 
{
	final int resource;
	private final Context m_Context;
    
    //Initialize adapter
    public TrackInfoAdapter(Context context, int resource, List<TrackInfo> items) {
        super(context, resource, items);
        this.resource=resource;
        m_Context = context;
    }
         
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final TrackInfo trackInfo = getItem(position);
        
        ViewGroup trackView = (ViewGroup)(convertView !=null ? convertView : LayoutInflater.from(m_Context).inflate(resource, null));
        
        TextView artist =(TextView)trackView.findViewById(R.id.track_artist);
        TextView title =(TextView)trackView.findViewById(R.id.track_title);
        ToggleButton playButton = (ToggleButton)trackView.findViewById(R.id.play_button);
        
        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
				{
					((IMainActivity)m_Context).play(trackInfo);
				}
				else
				{
					((IMainActivity)m_Context).pause(trackInfo);
				}
			}
		});
        playButton.setChecked(trackInfo.getIsPlaying());
        title.setText(trackInfo.getTitle());
        artist.setText(trackInfo.getArtist());
         
        return trackView;
    }
}

