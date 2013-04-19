/*****************************************************************************
 * @author PROIN LAB [ BASICS ]
 *         -------------------------------------------------------------------
 *         LAYOUT : ID_main / ID_subID_name
 *         -------------------------------------------------------------------
 *         INTENT : INTENT_fromID_TO_toID_NAME
 *****************************************************************************/

package com.proinlab.functions;

import java.io.File;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

public class B {
	
	public static final String LOG_TAG = "PROINLAB";
	
	public static final String MANUFACTURER = Build.MANUFACTURER;
	public static final String MODEL = Build.MODEL;

	public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/.Impression/";
	
	public static final String DIRECTORY_SAVE_CANVAS = "/CANVAS/";
	public static final String DIRECTORY_EXPORT_CANVAS = Environment.getExternalStorageDirectory().toString()+ "/DCIM/Camera/";
	public static final String DIRECTORY_SAVE_PDF = "/PDF/";
	public static final String DIRECTORY_SAVE_NOTE = "/NOTE/";
	public static final String DIRECTORY_SAVE_CALENDAR = "/CALENDAR/";
		
	public static final String PREF_TYPE_COUNT_PROCESS = "COUNT_PROCESS"; // 실행 횟수
	public static final String PREF_TYPE_DISPLAY_WIDTH = "DISPLAY_WIDTH"; // 화면 넓이
	public static final String PREF_TYPE_DISPLAY_HEIGHT = "DISPLAY_HEIGHT"; // 화면 높이
	
	// Activity RequestCode
	public static final int REQUESTCODE_TAKE_GALLERY = 0;
	public static final int REQUESTCODE_TAKE_CAMERA = 1;
	public static final int REQUESTCODE_NOTESELECTOR_TO_MENU = 2;
	
	public static final int EDIT_TYPE_IMG = 0;
	public static final int EDIT_TYPE_MEMO = 2;

	public static final String INTENT_01_TO_02_FILENAME = "SELECTEDFILENAME";
	public static final String INTENT_02_TO_01_PROCESSBOOL = "PROCESSBOOL"; // 전송인지 아닌지 구분
	public static final String INTENT_04_TO_01_SELECTTHEME = "SELECTTHEME";
	
	public static final String SAVE_FILE_NAME_CANVAS_BG = "background.png";
	public static final String SAVE_FILE_NAME_CANVAS_FG = "foreground.png";
	
	public static final int DIALOG_NOTE_SUBMENU = 0;
	public static final int DIALOG_CANVAS_SUBMENU = 1;
	public static final int DIALOG_BITMAP_PROCESS = 2;
	public static final int DIALOG_CANVAS_BG_IMPORT = 3;
	public static final int DIALOG_CANVAS_BG_ALPHA = 4;
	
	public static void CreateToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.show();
	}

	public static String stringCheck(String str) {
		StringBuilder strbuilder = new StringBuilder();

		int size = str.length();
		for (int i = 0; i < size; i++) {
			char curChar = str.charAt(i);
			if (curChar == '\\' || curChar == '/' || curChar == ':'
					|| curChar == '*' || curChar == '?' || curChar == '"'
					|| curChar == '<' || curChar == '>' || curChar == '|') {
				strbuilder.append('_');
			} else
				strbuilder.append(curChar);
		}
		return strbuilder.toString();
	}

	public static String getUniqueFilename(File folder, String filename) {
		if (folder == null)
			return null;

		String curFileName;
		File curFile;

		if (filename.length() > 20) {
			filename = filename.substring(0, 19);
		}

		filename = stringCheck(filename);

		do {
			curFileName = filename;
			curFile = new File(folder, curFileName);
			if (curFile.exists())
				return "<FileExist>";
		} while (curFile.exists());

		return curFileName;
	}
}
