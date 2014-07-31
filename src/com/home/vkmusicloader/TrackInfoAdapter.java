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
	private final IMainActivity m_MainActivity;
	
    public TrackInfoAdapter(Context context, int resource, List<TrackInfo> items) {
        super(context, resource, items);
        this.resource=resource;
        m_Context = context;
        m_MainActivity = (IMainActivity) m_Context;
    }
         
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final TrackInfo trackInfo = getItem(position);
        
        ViewGroup trackView = (ViewGroup)(convertView !=null ? convertView : LayoutInflater.from(m_Context).inflate(resource, null));
        
        TextView artist =(TextView)trackView.findViewById(R.id.track_artist);
        TextView title =(TextView)trackView.findViewById(R.id.track_title);
        ToggleButton playButton = (ToggleButton)trackView.findViewById(R.id.play_button);
        View downloadButton = trackView.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_MainActivity.downloadTrack(trackInfo);
			}
		});
        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
				{
					m_MainActivity.play(trackInfo);
				}
				else
				{
					m_MainActivity.pause(trackInfo);
				}
			}
		});
        playButton.setChecked(trackInfo.getIsPlaying());
        title.setText(trackInfo.getTitle());
        artist.setText(trackInfo.getArtist());
         
        return trackView;
    }
}

