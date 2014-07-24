package com.home.vkmusicloader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiAudioInfo;


public class VKApiTrackInfoAdapter extends ArrayAdapter<VKApiAudioInfo> 
{
	int resource;
    String response;
    Context context;
    //Initialize adapter
    public VKApiTrackInfoAdapter(Context context, int resource, List<VKApiAudioInfo> items) {
        super(context, resource, items);
        this.resource=resource;
 
    }
         
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout alertView;
        //Get the current alert object
        VKApiAudioInfo al = getItem(position);
         
        //Inflate the view
        if(convertView==null)
        {
            alertView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, alertView, true);
        }
        else
        {
            alertView = (LinearLayout) convertView;
        }
        //Get the text boxes from the listitem.xml file
        TextView artist =(TextView)alertView.findViewById(R.id.track_artist);
        TextView title =(TextView)alertView.findViewById(R.id.track_title);
         
        //Assign the appropriate data from our alert object above
        title.setText(al.title);
        artist.setText(al.artist);
         
        return alertView;
    }
}

