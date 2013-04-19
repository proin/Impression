/************************************************************************
 * @author PROIN LAB [ DB 관리 함수 ]
 *         --------------------------------------------------------------
 *         DB_FN_INSERT / DB_FN_FIND / DB_FN_DELETE / DB_FN_CHANGEDATA /
 *         DB_FN_FIND_FILENAME_BY_CATEGORY
 *         --------------------------------------------------------------
 *         DB_FN_FIND_CATEGORY_ALL / DB_FN_FIND_CATEGORY_EXIST /
 *         DB_FN_INSERT_CATEGORY
 ************************************************************************/

package com.proinlab.impression;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.proinlab.functions.DataBaseHelper;

public class DBEditFn {

	/**
	 * 파일 정보를 생성한다.
	 * 
	 * @param mHelper
	 * @param Category
	 * @param FileName
	 * @param FileType
	 * @param FileDir
	 * @param latestTime
	 * @param firstTime
	 * @return 성공 여부
	 */
	public boolean DBFN_FILEINFO_INSERT(SQLiteOpenHelper mHelper,
			String Category, String FileName, int FileType, String FileDir,
			String latestTime, String firstTime) {

		if (DBFN_FILEINFO_EXIST(mHelper, FileDir))
			return false;

		SQLiteDatabase db;
		ContentValues row;

		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_CATEGORY, Category);
		row.put(DataBaseHelper.DB_ROW_FILEDIR, FileDir);
		row.put(DataBaseHelper.DB_ROW_FILENAME, FileName);
		row.put(DataBaseHelper.DB_ROW_FILETYPE, Integer.toString(FileType));
		row.put(DataBaseHelper.DB_ROW_FIRSTTIME, firstTime);
		row.put(DataBaseHelper.DB_ROW_LATESTTIME, latestTime);
		db.insert(DataBaseHelper.DB_TABLE_NAME, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * 해당 카테고리의 파일 이름 목록을 구함
	 * 
	 * @param mHelper
	 * @param Category
	 * @return Category의 파일 이름 목록
	 */
	public String[] DBFN_FILEINFO_FIND_FILENAME_BY_CATEGORY(
			SQLiteOpenHelper mHelper, String Category) {
		SQLiteDatabase db;

		ArrayList<String> returnStr;
		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_FILEDIR, DataBaseHelper.DB_ROW_FILENAME,
				DataBaseHelper.DB_ROW_FILETYPE,
				DataBaseHelper.DB_ROW_FIRSTTIME,
				DataBaseHelper.DB_ROW_LATESTTIME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			returnStr = new ArrayList<String>();
			while (cursor.moveToNext()) {
				if (Category.equals(cursor.getString(DataBaseHelper.CATEGORY))) {
					returnStr.add(cursor.getString(DataBaseHelper.FILENAME));
				}
			}
			result = new String[returnStr.size()];
			for (int i = 0; i < returnStr.size(); i++)
				result[i] = returnStr.get(i);
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * 파일 이름으로부터 파일 정보 불러오기
	 * 
	 * @param mHelper
	 * @param 파일이름
	 * @return String[] : Category, Dir, filename, type, firstSaveTime,
	 *         LastSaveTime
	 */
	public String[] DBFN_FILEINFO_FIND_BY_FILENAME(SQLiteOpenHelper mHelper,
			String FileName) {
		SQLiteDatabase db;

		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_FILEDIR, DataBaseHelper.DB_ROW_FILENAME,
				DataBaseHelper.DB_ROW_FILETYPE,
				DataBaseHelper.DB_ROW_FIRSTTIME,
				DataBaseHelper.DB_ROW_LATESTTIME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			result = new String[6];
			while (cursor.moveToNext()) {
				if (FileName.equals(cursor.getString(DataBaseHelper.FILENAME))) {
					for (int i = 0; i < 6; i++)
						result[i] = cursor.getString(i);
				}
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * 파일 이름 생성 함수. 0부터 생성해서 1씩 증가시킨다.
	 * 
	 * @param mHelper
	 * @return 생성된 파일 이름 String
	 */
	public String DBFN_FILEINFO_CREATE_FILENAME(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		long num = -1;
		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_FILEDIR, DataBaseHelper.DB_ROW_FILENAME,
				DataBaseHelper.DB_ROW_FILETYPE,
				DataBaseHelper.DB_ROW_FIRSTTIME,
				DataBaseHelper.DB_ROW_LATESTTIME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			num = 0;
		else {
			boolean isExist = true;
			while (isExist) {
				isExist = false;
				cursor.moveToFirst();
				num++;
				if(num== Long.parseLong(cursor.getString(DataBaseHelper.FILEDIR))) {
					isExist = true;
				}
				while (cursor.moveToNext()) {
					if (num == Long.parseLong(cursor
							.getString(DataBaseHelper.FILEDIR)))
						isExist = true;
				}

			}
		}

		cursor.close();
		mHelper.close();

		return Long.toString(num);
	}

	/**
	 * 해당 파일이 DB에 존재하는지 확인. 존재하면 true 값을 반환한다.
	 * 
	 * @param mHelper
	 * @param FileDir
	 * @return 존재여부
	 */
	public boolean DBFN_FILEINFO_EXIST(SQLiteOpenHelper mHelper, String FileDir) {
		SQLiteDatabase db;

		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_FILEDIR, DataBaseHelper.DB_ROW_FILENAME,
				DataBaseHelper.DB_ROW_FILETYPE,
				DataBaseHelper.DB_ROW_FIRSTTIME,
				DataBaseHelper.DB_ROW_LATESTTIME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			mHelper.close();
			return false;
		} else {
			while (cursor.moveToNext()) {
				if (FileDir == cursor.getString(DataBaseHelper.FILEDIR)) {
					cursor.close();
					mHelper.close();
					return true;
				}
			}
		}

		cursor.close();
		mHelper.close();
		return false;
	}

	/**
	 * 해당 파일의 이름이 DB에 존재하는지 확인. 존재하면 true 값을 반환한다.
	 * 
	 * @param mHelper
	 * @param FileName
	 * @return
	 */
	public boolean DBFN_FILEINFO_EXIST_BY_FILENAME(SQLiteOpenHelper mHelper,
			String FileName) {
		SQLiteDatabase db;

		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_FILEDIR, DataBaseHelper.DB_ROW_FILENAME,
				DataBaseHelper.DB_ROW_FILETYPE,
				DataBaseHelper.DB_ROW_FIRSTTIME,
				DataBaseHelper.DB_ROW_LATESTTIME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			mHelper.close();
			return false;
		} else {
			while (cursor.moveToNext()) {
				if (FileName.equals(cursor.getString(DataBaseHelper.FILENAME))) {
					cursor.close();
					mHelper.close();
					return true;
				}
			}
		}

		cursor.close();
		mHelper.close();
		return false;
	}

	/**
	 * 파일의 타입을 반환한다
	 * 
	 * @param mHelper
	 * @param FileName
	 * @return
	 */
	public int DBFN_FILEINFO_FIND_TYPE_BY_FILENAME(SQLiteOpenHelper mHelper,
			String FileName) {
		SQLiteDatabase db;

		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_FILEDIR, DataBaseHelper.DB_ROW_FILENAME,
				DataBaseHelper.DB_ROW_FILETYPE,
				DataBaseHelper.DB_ROW_FIRSTTIME,
				DataBaseHelper.DB_ROW_LATESTTIME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			mHelper.close();
			return -1;
		} else {
			while (cursor.moveToNext()) {
				if (FileName.equals(cursor.getString(DataBaseHelper.FILENAME))) {
					String returns = cursor.getString(DataBaseHelper.FILETYPE);
					cursor.close();
					mHelper.close();
					return Integer.parseInt(returns);
				}
			}
		}

		cursor.close();
		mHelper.close();
		return -1;
	}

	/**
	 * DB에서 파일 경로에 해당하는 정보를 삭제한다. 존재하지 않는 파일이면 false
	 * 
	 * @param mHelper
	 * @param FileDir
	 * @return
	 */
	public boolean DBFN_FILEINFO_DELETE(SQLiteOpenHelper mHelper, String FileDir) {

		if (DBFN_FILEINFO_EXIST(mHelper, FileDir))
			return false;

		SQLiteDatabase db;
		db = mHelper.getWritableDatabase();
		db.delete(DataBaseHelper.DB_TABLE_NAME, DataBaseHelper.DB_ROW_FILEDIR
				+ " = '" + FileDir + "'", null);
		mHelper.close();
		return true;
	}

	/**
	 * DB의 파일 정보를 갱신한다. 최초 저장 시간은 변경 못함
	 * 
	 * @param mHelper
	 * @param FileName
	 * @param latestTime
	 * @param FileDir
	 * @return
	 */
	public boolean DBFN_FILEINFO_CHANGEDATA(SQLiteOpenHelper mHelper,
			String FileName, String latestTime,
			String FileDir, String Categoryname) {
		if (DBFN_FILEINFO_EXIST(mHelper, FileDir))
			return false;

		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();

		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_FILENAME, FileName);
		row.put(DataBaseHelper.DB_ROW_LATESTTIME, latestTime);
		row.put(DataBaseHelper.DB_ROW_CATEGORY, Categoryname);

		db.update(DataBaseHelper.DB_TABLE_NAME, row,
				DataBaseHelper.DB_ROW_FILEDIR + " = '" + FileDir + "'", null);

		mHelper.close();
		return true;
	}

	/**
	 * 파일 이름을 찾아서 변경해줌
	 * @param mHelper
	 * @param FileName 바꿀 이름
	 * @param OrgName 기존 이름
	 * @return
	 */
	public boolean DBFN_FILEINFO_CHANGENAME(SQLiteOpenHelper mHelper,
			String FileName, String OrgName) {
		if (DBFN_FILEINFO_EXIST_BY_FILENAME(mHelper, FileName))
			return false;

		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();

		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_FILENAME, FileName);
		row.put(DataBaseHelper.DB_ROW_LATESTTIME, Long.toString(System.currentTimeMillis()));

		db.update(DataBaseHelper.DB_TABLE_NAME, row,
				DataBaseHelper.DB_ROW_FILENAME + " = '" + OrgName + "'", null);

		mHelper.close();
		return true;
	}

	
	// XXX Category

	/**
	 * DB내의 모든 카테고리를 검색한다.
	 * 
	 * @param mHelper
	 * @return
	 */
	public String[] DBFN_CATEGORY_FIND_ALL(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		String[] returnStr;
		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CATEGORY, columns, null,
				null, null, null, null);

		if (cursor.getCount() == 0)
			returnStr = null;
		else {
			returnStr = new String[cursor.getCount()];
			int i = 0;
			while (cursor.moveToNext()) {
				returnStr[i] = cursor.getString(0);
				i++;
			}
		}
		cursor.close();
		mHelper.close();
		return returnStr;
	}

	/**
	 * DB내부에 동일한 이름의 카테고리가 있는지 확인한다.
	 * 
	 * @param mHelper
	 * @param CategoryName
	 * @return
	 */
	public boolean DBFN_CATEGORY_FIND_EXIST(SQLiteOpenHelper mHelper,
			String CategoryName) {
		SQLiteDatabase db;

		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CATEGORY, columns, null,
				null, null, null, null);

		while (cursor.moveToNext()) {
			if (CategoryName.equals(cursor.getString(0))) {
				cursor.close();
				mHelper.close();
				return true;
			}
		}

		cursor.close();
		mHelper.close();
		return false;
	}

	/**
	 * 카테고리를 추가한다
	 * 
	 * @param mHelper
	 * @param CategoryName
	 * @return
	 */
	public boolean DBFN_CATEGORY_INSERT(SQLiteOpenHelper mHelper,
			String CategoryName, int TagColor) {
		SQLiteDatabase db;
		ContentValues row;

		if (DBFN_CATEGORY_FIND_EXIST(mHelper, CategoryName))
			return false;

		String DIR = DBFN_CATEGORY_CREATE_DIR(mHelper);

		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_CATEGORY, CategoryName);
		row.put(DataBaseHelper.DB_ROW_CATEGORY_TAG, Integer.toString(TagColor));
		row.put(DataBaseHelper.DB_ROW_CATEGORY_DIR, DIR);
		db.insert(DataBaseHelper.DB_TABLE_CATEGORY, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * 카테고리를 삭제하고 파일정보중 해당 카테고리 내용을 삭제한다
	 * 
	 * @param mHelper
	 * @param CategoryName
	 * @return
	 */

	public boolean DBFN_CATEGORY_DELETE(SQLiteOpenHelper mHelper,
			String CategoryName) {
		if (!DBFN_CATEGORY_FIND_EXIST(mHelper, CategoryName))
			return false;

		SQLiteDatabase db;
		db = mHelper.getWritableDatabase();
		db.delete(DataBaseHelper.DB_TABLE_CATEGORY,
				DataBaseHelper.DB_ROW_CATEGORY + " = '" + CategoryName + "'",
				null);
		mHelper.close();

		db = mHelper.getWritableDatabase();
		db.delete(DataBaseHelper.DB_TABLE_NAME, DataBaseHelper.DB_ROW_CATEGORY
				+ " = '" + CategoryName + "'", null);
		mHelper.close();

		return true;
	}

	/**
	 * 카테고리 이름을 변경한다. 바꾸려는 이름이 이미 존재하면 false를 반환한다. 카테고리에 해당하는 파일 정보도 변경한다.
	 * 
	 * @param mHelper
	 * @param OrginalName
	 *            : 찾으려는 카테고리 명
	 * @param ChangeName
	 *            : 바꾸려는 이름
	 * @return
	 */
	public boolean DBFN_CATEGORY_CHANGEDATA(SQLiteOpenHelper mHelper,
			String OrginalName, String ChangeName) {
		if (DBFN_FILEINFO_EXIST(mHelper, ChangeName))
			return false;

		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();

		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_CATEGORY, ChangeName);

		db.update(DataBaseHelper.DB_TABLE_CATEGORY, row,
				DataBaseHelper.DB_ROW_CATEGORY + " = '" + OrginalName + "'",
				null);

		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_CATEGORY, ChangeName);

		db.update(DataBaseHelper.DB_TABLE_NAME, row,
				DataBaseHelper.DB_ROW_CATEGORY + " = '" + OrginalName + "'",
				null);

		mHelper.close();
		return true;
	}

	/**
	 * 카테고리의 태그를 반환한다
	 * 
	 * @param mHelper
	 * @param FileName
	 * @return
	 */
	public int DBFN_CATEGORY_FIND_TAG_BY_NAME(SQLiteOpenHelper mHelper,
			String Category) {
		SQLiteDatabase db;

		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_CATEGORY_TAG };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CATEGORY, columns, null,
				null, null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			mHelper.close();
			return -1;
		} else {
			while (cursor.moveToNext()) {
				if (Category.equals(cursor.getString(0))) {
					String returns = cursor.getString(1);
					cursor.close();
					mHelper.close();
					return Integer.parseInt(returns);
				}
			}
		}

		cursor.close();
		mHelper.close();
		return -1;
	}

	/**
	 * 카테고리의 경로를 반환한다
	 * 
	 * @param mHelper
	 * @param FileName
	 * @return
	 */
	public String DBFN_CATEGORY_FIND_DIR_BY_NAME(SQLiteOpenHelper mHelper,
			String Category) {
		SQLiteDatabase db;

		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_CATEGORY_DIR };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CATEGORY, columns, null,
				null, null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			mHelper.close();
			return null;
		} else {
			while (cursor.moveToNext()) {
				if (Category.equals(cursor.getString(0))) {
					String returns = cursor.getString(1);
					cursor.close();
					mHelper.close();
					return returns;
				}
			}
		}

		cursor.close();
		mHelper.close();
		return null;
	}

	private String DBFN_CATEGORY_CREATE_DIR(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		long num = 0;
		String[] columns = { DataBaseHelper.DB_ROW_CATEGORY,
				DataBaseHelper.DB_ROW_CATEGORY_TAG,
				DataBaseHelper.DB_ROW_CATEGORY_DIR };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CATEGORY, columns, null,
				null, null, null, null);

		if (cursor.getCount() == 0)
			num = 0;
		else {
			boolean isExist = true;
			while (isExist) {
				isExist = false;
				cursor.moveToFirst();
				num++;
				if(num== Long.parseLong(cursor.getString(2))) {
					isExist = true;
				}
				while (cursor.moveToNext()) {
					if (num == Long.parseLong(cursor.getString(2)))
						isExist = true;
				}

			}
		}

		cursor.close();
		mHelper.close();

		return Long.toString(num);
	}

}
