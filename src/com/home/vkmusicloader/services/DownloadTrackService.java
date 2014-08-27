package com.home.vkmusicloader.services;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.home.vkmusicloader.R;
import com.home.vkmusicloader.data.VKDataOpenHelper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

public class DownloadTrackService extends IntentService {
	public static final String ExtraName = "downloadData";
	private static final int m_NotificationId = 1;
	public DownloadTrackService() {
		super("DownloadTrackService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//TODO implement wi-fi lock
		//TODO check if media mounted http://developer.android.com/training/basics/data-storage/files.html
		
//		F-ile artistDirectory = new File(Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES), trackInfo.getArtist());
//		if (!artistDirectory.exists() && !artistDirectory.mkdirs())
//		{
//			//TODO notify activity that directory can not be created
//			return;
//		}
//		File file = new File(artistDirectory, trackInfo.getTitle() + ".mp3");
//		try {
//			file.createNewFile();
//		} catch (IOException e) {
//			return;
//		}
//
//		
//		DownloadData data = new DownloadData(0, trackInfo.getUrl(), file, String.format("%s - %s", trackInfo.getArtist(), trackInfo.getTitle()));
//		
//		DownloadData downloadData = (DownloadData)intent.getSerializableExtra(ExtraName);
//		File outputFile = downloadData.getFile(); 
//		
//		NotificationManager notifyManager =
//		        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//		
//		HttpURLConnection urlConnection = null;
//		   InputStream in = null;
//		   FileOutputStream fos = null;
//		try
//		{
//			builder.setContentTitle(downloadData.getTitle())
//		    .setContentText("Download in progress").setSmallIcon(R.drawable.download_notification);
//			outputFile.createNewFile();
//		   URL url = new URL(downloadData.getUrl());
//		   urlConnection = (HttpURLConnection) url.openConnection();
//		     in = new BufferedInputStream(urlConnection.getInputStream());
//		     int size =  urlConnection.getContentLength();
//		     fos = new FileOutputStream(outputFile, false);
//			int total = 0;
//		     byte[] buffer = new byte[8192];
//		     while (true)
//		     {
//		    	 int bytesRead = in.read(buffer);
//		    	 total += bytesRead;
//		    	 builder.setProgress(size, total, false);
//                 // Displays the progress bar for the first time.
//                 notifyManager.notify(m_NotificationId, builder.build());
//		    	 if (bytesRead <= 0)
//		    	 {
//		    		 break;
//		    	 }
//		    	 fos.write(buffer, 0, bytesRead);
//		     }
//		   }
//		   catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		   }
//		   catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		   }
//		   finally {
//		    safeClose(in);
//		    safeClose(fos);
//		    if (urlConnection != null)
//		    {
//		    		urlConnection.disconnect();
//		    }
//		    builder.setContentText("Download completed")
//            // Removes the progress bar
//                    .setProgress(0,0,false);
//            notifyManager.notify(m_NotificationId, builder.build());
//		}
//		ContentValues values = new ContentValues();
//		values.put(VKDataOpenHelper.LOCATION_COLUMN, outputFile.getAbsolutePath());
//		VKDataOpenHelper dbHelper = new VKDataOpenHelper(this);
//		SQLiteDatabase sdb;
//		sdb = dbHelper.getWritableDatabase();
//		sdb.update(VKDataOpenHelper.TRACK_TABLE, values, VKDataOpenHelper._ID + "=?",new String[]{ Integer.toString(downloadData.getId())});	
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

}
