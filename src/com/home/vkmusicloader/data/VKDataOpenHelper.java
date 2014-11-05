package com.home.vkmusicloader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class VKDataOpenHelper extends SQLiteOpenHelper implements BaseColumns {
	
	private static final int DATABASE_VERSION = 1;
	public static final String TRACK_TABLE_TITLE_COLUMN = "title";
	public static final String TRACK_TABLE_ARTIST_COLUMN = "artist";
	public static final String TRACK_TABLE_DURATION_COLUMN = "duration";
	public static final String TRACK_TABLE_URL_COLUMN = "url";
	public static final String TRACK_TABLE = "track";
	
	public static final String TRACK_UPLOAD_TABLE = "trackUpload";
	public static final String TRACK_UPLOAD_TABLE_LOCATION_COLUMN = "location";
	public static final String TRACK_UPLOAD_TABLE_STATE_COLUMN = "state";
	
	public static final String TRACK_TO_TRACKLIST_TABLE = "trackToTrackList";
	public static final String TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN = "trackId";
	public static final String TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN = "playListId";
	public static final String TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN = "trackIndex";
	
	public static final String TRACKLIST_TABLE = "trackList";
	public static final String TRACKLIST_TABLE_TITLE_COLUMN = "title";
	
	public static final String TRACK_UPLOAD_TABLE_CREATE =  "CREATE TABLE " +TRACK_UPLOAD_TABLE+" (" +
		 	BaseColumns._ID +" INTEGER PRIMARY KEY ASC, " +TRACK_UPLOAD_TABLE_LOCATION_COLUMN +" TEXT, "+TRACK_UPLOAD_TABLE_STATE_COLUMN+" INTEGER, UNIQUE(" + BaseColumns._ID +") ON CONFLICT REPLACE, FOREIGN KEY(" + BaseColumns._ID + ") REFERENCES "+TRACK_TABLE+"("+BaseColumns._ID+"));";
	
	public static final String TRACK_TABLE_CREATE =  "CREATE TABLE " +TRACK_TABLE+" (" +
		 	BaseColumns._ID +" INTEGER PRIMARY KEY ASC, " +TRACK_TABLE_TITLE_COLUMN +" TEXT, "+TRACK_TABLE_ARTIST_COLUMN+" TEXT, "+
			TRACK_TABLE_DURATION_COLUMN+" INTEGER, "+TRACK_TABLE_URL_COLUMN+" TEXT, UNIQUE(" + BaseColumns._ID +") ON CONFLICT REPLACE);";
	
	
	public static final String TRACK_TO_TRACKLIST_TABLE_CREATE =  "CREATE TABLE " + TRACK_TO_TRACKLIST_TABLE +" (" +
			TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN +" INTEGER, "+TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN +" INTEGER, "+
			TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN + " INTEGER, FOREIGN KEY(" + TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + ") REFERENCES "+TRACK_TABLE+"("+BaseColumns._ID+"), FOREIGN KEY(" + TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN + ") REFERENCES "+TRACKLIST_TABLE+"("+BaseColumns._ID+"), UNIQUE(" + TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + "," + TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN +")  ON CONFLICT REPLACE);";
	
	public static final String TRACKLIST_TABLE_CREATE =  "CREATE TABLE " +TRACKLIST_TABLE+" (" +
		 	BaseColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + TRACKLIST_TABLE_TITLE_COLUMN +" TEXT);";
	
	public static final String DEFAULTTRACKLIST_TITLE = "Default";
	public static final int DEFAULTTRACKLIST_ID = 1;
	
	public static final int TRACK_UPLOAD_TABLE_STATE_COLUMN_UPLOADING = 1;
	public static final int TRACK_UPLOAD_TABLE_STATE_COLUMN_UPLOADED = 2;
	public static final int TRACK_UPLOAD_TABLE_STATE_COLUMN_NEW = 0;
	
	public static final String TRACKLIST_SELECT = "SELECT " +BaseColumns._ID + "," + VKDataOpenHelper.TRACK_TABLE_TITLE_COLUMN +"," + VKDataOpenHelper.TRACK_TABLE_ARTIST_COLUMN + " FROM "+
			VKDataOpenHelper.TRACK_TABLE + " track INNER JOIN "+ VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE + " trackToTrackList ON track."+BaseColumns._ID + " =trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + " WHERE trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN+
			"=? ORDER BY trackToTrackList." + VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN;
	
	public static final String NEXT_TRACK_SELECT = "SELECT " + BaseColumns._ID + " FROM "+
			VKDataOpenHelper.TRACK_TABLE + " track INNER JOIN "+ VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE + " trackToTrackList ON track."+BaseColumns._ID + " = trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + " WHERE trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN+
			"=? AND trackToTrackList."+TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN +">(SELECT " + TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN + " FROM " + TRACK_TO_TRACKLIST_TABLE +" WHERE " + TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + "=? AND "+ VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN +"=?) ORDER BY trackToTrackList." + VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN + " ASC LIMIT 1";
	
	public static final String PREVIOUS_TRACK_SELECT = "SELECT " + BaseColumns._ID + " FROM "+
			VKDataOpenHelper.TRACK_TABLE + " track INNER JOIN "+ VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE + " trackToTrackList ON track."+BaseColumns._ID + " = trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + " WHERE trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN+
			"=? AND trackToTrackList."+TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN +"<(SELECT " + TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN + " FROM " + TRACK_TO_TRACKLIST_TABLE +" WHERE " + TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + "=? AND "+ VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN +"=?) ORDER BY trackToTrackList." + VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN + " DESC LIMIT 1";
	
	public static final String FIRST_TRACK_SELECT = "SELECT " + BaseColumns._ID + " FROM "+
			VKDataOpenHelper.TRACK_TABLE + " track INNER JOIN "+ VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE + " trackToTrackList ON track."+BaseColumns._ID + " = trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKID_COLUMN + " WHERE trackToTrackList."+VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_PLAYLISTID_COLUMN+
			"=? ORDER BY trackToTrackList." + VKDataOpenHelper.TRACK_TO_TRACKLIST_TABLE_TRACKINDEX_COLUMN + " ASC LIMIT 1";
	
	public VKDataOpenHelper(Context context) {
		super(context, "VKData", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TRACK_TABLE_CREATE);
		db.execSQL(TRACK_UPLOAD_TABLE_CREATE);
		db.execSQL(TRACKLIST_TABLE_CREATE);
		db.execSQL(TRACK_TO_TRACKLIST_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
