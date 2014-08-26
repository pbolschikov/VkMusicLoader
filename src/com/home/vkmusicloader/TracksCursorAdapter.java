package com.home.vkmusicloader;

import com.home.vkmusicloader.data.VKDataOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TracksCursorAdapter extends CursorAdapter {

	private final LayoutInflater m_Inflater;
	private final MainActivity m_MainActivity;

	public TracksCursorAdapter(MainActivity mainActivity, Cursor c) {
		super(mainActivity, c, 0);
		m_MainActivity = mainActivity;
		m_Inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View trackView, Context context, Cursor cursor) {
		TextView artist =(TextView)trackView.findViewById(R.id.track_artist);
        TextView title =(TextView)trackView.findViewById(R.id.track_title);
        ToggleButton playButton = (ToggleButton)trackView.findViewById(R.id.play_button);
        View downloadButton = trackView.findViewById(R.id.download_button);
        final int trackId = cursor.getInt(cursor.getColumnIndex(VKDataOpenHelper._ID));
        downloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_MainActivity.downloadTrack(trackId);
			}
		});
        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
				{
					m_MainActivity.play(trackId);
				}
				else
				{
					m_MainActivity.pause(trackId);
				}
			}
		});
        playButton.setChecked(m_MainActivity.isPlaing(trackId));
        title.setText(cursor.getString(2));
        artist.setText(cursor.getString(1));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return m_Inflater.inflate(R.layout.track_textview, viewGroup, false);
	}

}
