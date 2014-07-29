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

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
		DownloadData downloadData = (DownloadData)intent.getSerializableExtra(ExtraName);
		File outputFile = downloadData.getFile(); 
		
		NotificationManager notifyManager =
		        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		
		HttpURLConnection urlConnection = null;
		   InputStream in = null;
		   FileOutputStream fos = null;
		try
		{
			builder.setContentTitle("Download Track")
		    .setContentText("Download in progress").setSmallIcon(R.drawable.download_notification);
			outputFile.createNewFile();
		   URL url = new URL(downloadData.getUrl());
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
		    	 if (bytesRead == 0)
		    	 {
		    		 break;
		    	 }
		    	 fos.write(buffer, 0, bytesRead);
		     }
		   }
		   catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		   catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		    finally {
		    safeClose(in);
		    safeClose(fos);
		    if (urlConnection != null)
		    {
		    		urlConnection.disconnect();
		    }
		    builder.setContentText("Download complete")
            // Removes the progress bar
                    .setProgress(0,0,false);
            notifyManager.notify(m_NotificationId, builder.build());
		}
		
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
