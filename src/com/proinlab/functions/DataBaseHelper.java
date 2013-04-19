/*****************************************************************************
 * @author PROIN LAB [ DB ±¸Á¶ ]
 *         -------------------------------------------------------------------
 *         TABLE : FILE_LIST, CATEGORY_LIST
 *         -------------------------------------------------------------------
 *         FILE_LIST ROW : FILENAME LATESTTIME FIRSTTIME CATEGORY FILEDIR
 *         FILETYPE
 *         -------------------------------------------------------------------
 *         CATEGORY_LIST ROW : CATEGORY
 *****************************************************************************/

package com.proinlab.functions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASENAME = B.SDCARD_DIRECTORY+"FILE_INFO.db";

	public static final String DB_TABLE_NAME = "FILE_LIST";
	public static final String DB_TABLE_CATEGORY = "CATEGORY_LIST";

	public static final String DB_ROW_FILENAME = "FILENAME";
	public static final String DB_ROW_LATESTTIME = "LATESTTIME";
	public static final String DB_ROW_FIRSTTIME = "FIRSTTIME";
	public static final String DB_ROW_CATEGORY = "CATEGORY";
	public static final String DB_ROW_FILEDIR = "FILEDIR";
	public static final String DB_ROW_FILETYPE = "FILETYPE";

	public static final String DB_ROW_CATEGORY_TAG = "CATEGORY_TAG";
	public static final String DB_ROW_CATEGORY_DIR = "CATEGORY_DIR";
	public static final String DB_ROW_CATEGORY_ORDER = "CATEGORY_ORDER";
	
	public static final int CATEGORY = 0;
	public static final int FILEDIR = 1;
	public static final int FILENAME = 2;
	public static final int FILETYPE = 3;
	public static final int FIRSTTIME = 4;
	public static final int LATESTTIME = 5;

	public DataBaseHelper(Context context) {
		super(context, DATABASENAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DB_TABLE_NAME
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DB_ROW_CATEGORY + " TEXT, "
				+ DB_ROW_FILEDIR + " TEXT, "
				+ DB_ROW_FILENAME + " TEXT, "
				+ DB_ROW_FILETYPE + " TEXT, "
				+ DB_ROW_FIRSTTIME + " TEXT, "
				+ DB_ROW_LATESTTIME + " TEXT);");
		db.execSQL("CREATE TABLE " + DB_TABLE_CATEGORY
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DB_ROW_CATEGORY_ORDER + " TEXT, "
				+ DB_ROW_CATEGORY_TAG + " TEXT, "
				+ DB_ROW_CATEGORY_DIR + " TEXT, "
				+ DB_ROW_CATEGORY + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CATEGORY);
		onCreate(db);
	}

}
