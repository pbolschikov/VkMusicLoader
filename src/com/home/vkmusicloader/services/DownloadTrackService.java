package com.home.vkmusicloader.services;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.home.vkmusicloader.R;
import com.home.vkmusicloader.data.VKDataOpenHelper;

public final class DownloadTrackService extends Service implements ITrackDownloader {
	public static final String ExtraName = "downloadData";
	private static final int m_NotificationId = 1;
	private final Handler m_Handler = new Handler();

	@Override
	public void downloadTrack(final int trackId, final Runnable downloadedCallback) {
		//TODO implement wi-fi lock
		//TODO check if media mounted http://developer.android.com/training/basics/data-storage/files.html
		new Thread(new Runnable() {
	        public void run() {
	        	VKDataOpenHelper dbHelper = new VKDataOpenHelper(DownloadTrackService.this);
	            SQLiteDatabase sdb = dbHelper.getReadableDatabase();
	    		Cursor cursor = sdb.query(VKDataOpenHelper.TRACK_TABLE, new String[]{VKDataOpenHelper.TRACK_TABLE_TITLE_COLUMN, VKDataOpenHelper.TRACK_TABLE_ARTIST_COLUMN,VKDataOpenHelper.TRACK_TABLE_URL_COLUMN},BaseColumns._ID + "=?", new String[]{Integer.toString(trackId)},null,null,null);
	    		cursor.moveToFirst();
	    		String title = cursor.getString(0);
	    		String artist = cursor.getString(1);
	    		String urlString = cursor.getString(2);
	    		cursor.close();
	    		File artistDirectory = new File(Environment.getExternalStoragePublicDirectory(
	    	            Environment.DIRECTORY_PICTURES), artist);
	    		if (!artistDirectory.exists() && !artistDirectory.mkdirs())
	    		{
	    			//TODO notify activity that directory can not be created
	    			return;
	    		}
	    		File outputFile = new File(artistDirectory, title + ".mp3");
	    		try {
	    			outputFile.createNewFile();
	    		} catch (IOException e) {
	    			return;
	    		}
	    		
	    		NotificationManager notifyManager =
	    		        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    		NotificationCompat.Builder builder = new NotificationCompat.Builder(DownloadTrackService.this);
	    		
	    		HttpURLConnection urlConnection = null;
	    		   InputStream in = null;
	    		   FileOutputStream fos = null;
	    		try
	    		{
	    			builder.setContentTitle(String.format("%s - %s", artist, title))
	    		    .setContentText("Download in progress").setSmallIcon(R.drawable.download_notification);
	    			outputFile.createNewFile();
	    		   URL url = new URL(urlString);
	    		   urlConnection = (HttpURLConnection) url.openConnection();
	    		     in = new BufferedInputStream(urlConnection.getInputStream());
	    		     int size =  urlConnection.getContentLength();
	    		     fos = new FileOutputStream(outputFile, false);
	    			int total = 0;
	    		     byte[] buffer = new byte[8192];
	    		     while (true)
	    		     {
	    		    	 int bytesRead = in.read(buffer);
	    		    	 total += bytesRead;
	    		    	 builder.setProgress(size, total, false);
	                     // Displays the progress bar for the first time.
	                     notifyManager.notify(m_NotificationId, builder.build());
	    		    	 if (bytesRead <= 0)
	    		    	 {
	    		    		 break;
	    		    	 }
	    		    	 fos.write(buffer, 0, bytesRead);
	    		    	 builder.setContentText("Download completed")
	    		            // Removes the progress bar
	    		                    .setProgress(0,0,false);
	    		     }
	    		   }
	    		   catch (Exception e) {
	    			   m_Handler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Error occured while downloading file", Toast.LENGTH_SHORT).show();
													}
					});
	    			   builder.setContentText("Error occured while downloading file")
	    	            // Removes the progress bar
	    	                    .setProgress(0,0,false).setSmallIcon(R.drawable.download_notification);
	    		   }
	    		   finally {
	    		    safeClose(in);
	    		    safeClose(fos);
	    		    if (urlConnection != null)
	    		    {
	    		    		urlConnection.disconnect();
	    		    }
	    		    notifyManager.notify(m_NotificationId, builder.build());
	    		}
	    		ContentValues values = new ContentValues();
	    		values.put(VKDataOpenHelper.TRACK_TABLE_LOCATION_COLUMN, outputFile.getAbsolutePath());
	    		
	    		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
	    		writableDatabase.update(VKDataOpenHelper.TRACK_TABLE, values, VKDataOpenHelper._ID + "=?",new String[]{ Integer.toString(trackId)});
	    		m_Handler.post(downloadedCallback);
	        }
	    }).start();
	}
	
	private static void safeClose(Closeable stream)
	{
		if (stream != null)
		{
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new LocalBinder<ITrackDownloader>(this);
	}
}
