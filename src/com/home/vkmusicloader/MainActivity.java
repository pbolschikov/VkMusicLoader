package com.home.vkmusicloader;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ListView;
import com.home.vkmusicloader.data.VKDataOpenHelper;
import com.home.vkmusicloader.services.IUpdatesManager;
import com.home.vkmusicloader.services.LocalBinder;
import com.home.vkmusicloader.services.TrackInfoPersistorService;

@ContentView(R.layout.activity_main)
public final class MainActivity extends RoboActivity
{
	@InjectView(R.id.track_list)
	ListView m_TrackListView;
	TracksCursorAdapter m_TracksAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Intent trackPlayerService = new Intent(this, TrackInfoPersistorService.class);
	 	getApplicationContext().bindService(trackPlayerService, new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	            @SuppressWarnings("unchecked")
				LocalBinder<IUpdatesManager> binder = (LocalBinder<IUpdatesManager>) service;
	            IUpdatesManager updatesManager = binder.getService();
	            updatesManager.checkUpdates(new Runnable() {
					
					@Override
					public void run() {
						m_TracksAdapter.swapCursor(createCursor());
					}
				});
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	            
	        }
	    }, Context.BIND_AUTO_CREATE);

        m_TracksAdapter = new TracksCursorAdapter(this, createCursor());
		m_TrackListView.setAdapter(m_TracksAdapter);
    }
    
    private Cursor createCursor()
    {
    	VKDataOpenHelper dbHelper = new VKDataOpenHelper(this);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        return sdb.rawQuery(VKDataOpenHelper.TRACKLIST_SELECT, new String[]{ Integer.toString(VKDataOpenHelper.DEFAULTTRACKLIST_ID) });
    }
}
