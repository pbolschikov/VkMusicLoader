package com.home.vkmusicloader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class VKDataOpenHelper extends SQLiteOpenHelper implements BaseColumns {
	
	private static final int DATABASE_VERSION = 0;
	public static final String TITLE_COLUMN = "title";
	public static final String ARTIST_COLUMN = "artist";
	public static final String DURATION_COLUMN = "duration";
	public static final String LOCATION_COLUMN = "location";
	public static final String URL_COLUMN = "url";
	public static final String TRACK_TABLE = "Track";
	
	public static final String TRACK_TABLE_CREATE =  "CREATE TABLE " +TRACK_TABLE+" (" +
		 	BaseColumns._ID +" INTEGER, " +TITLE_COLUMN +" TEXT, "+ARTIST_COLUMN+" TEXT, "+
			DURATION_COLUMN+" INTEGER, "+LOCATION_COLUMN+" TEXT, "+URL_COLUMN+" TEXT);";
	
	public VKDataOpenHelper(Context context) {
		super(context, "VKData", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TRACK_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
