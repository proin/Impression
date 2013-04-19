package com.proinlab.impression;

import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.proinlab.functions.B;
import com.proinlab.functions.BitmapProcess;

public class M04_NOTE_SELECTOR extends Activity {

	private ImageButton returnbtn;

	private LinearLayout ItemListLayout;

	private int displayWidth, displayHeight;

	private String INTERNAL_DIR;
	private static Bitmap bitmap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m04_main);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		INTERNAL_DIR = getFilesDir().toString();

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		returnbtn = (ImageButton) findViewById(R.id.m04_main_Button_returnmain);
		ItemListLayout = (LinearLayout) findViewById(R.id.m04_main_LinearLayout_ItemList);

		returnbtn.setOnClickListener(mOnclickListener);

		if (getFileLists().size() != 6) {
			saveBitmapPNG_Internal("note_theme_basic1_0.png",
					R.drawable.note_theme_basic1_0);
			saveBitmapPNG_Internal("note_theme_basic1_1.png",
					R.drawable.note_theme_basic1_1);
			saveBitmapPNG_Internal("note_theme_basic1_2.png",
					R.drawable.note_theme_basic1_2);
			saveBitmapPNG_Internal("note_theme_basic2_0.png",
					R.drawable.note_theme_basic2_0);
			saveBitmapPNG_Internal("note_theme_basic2_1.png",
					R.drawable.note_theme_basic2_1);
			saveBitmapPNG_Internal("note_theme_basic2_2.png",
					R.drawable.note_theme_basic2_2);
		}

		CreateNoteThemeRow("basic1"); 
		CreateNoteThemeRow("basic2"); 

		getFileLists();
	}

	private View.OnClickListener mOnclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == returnbtn.getId()) {
				finish();
			}
		}
	};
	
	/**
	 * 노트의 테마를 표시해준다
	 * @param noteName : "/note_theme_" + noteName + "_i.png"
	 */

	private void CreateNoteThemeRow(final String noteName) {
		BitmapProcess bp = new BitmapProcess();
		LinearLayout NoteThemeROW = new LinearLayout(this);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				displayWidth / 4, displayHeight / 4);
		params.setMargins(displayWidth / 16, displayHeight / 16, 0, 0);
		
		ImageView notetheme = new ImageView(this);
		bitmap = bp.loadImg(INTERNAL_DIR + "/note_theme_"+noteName+"_0.png", 4);
		Drawable d = new BitmapDrawable(bitmap);
		notetheme.setBackgroundDrawable(d);
		notetheme.setLayoutParams(params);
		notetheme.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra(B.INTENT_04_TO_01_SELECTTHEME,
						INTERNAL_DIR+"/note_theme_"+noteName+"_0.png");
				setResult(B.REQUESTCODE_NOTESELECTOR_TO_MENU, intent);
				finish();
			}
		});
		NoteThemeROW.addView(notetheme);
		
		notetheme = new ImageView(this);
		bitmap = bp.loadImg(INTERNAL_DIR + "/note_theme_"+noteName+ "_1.png", 4);
		d = new BitmapDrawable(bitmap);
		notetheme.setBackgroundDrawable(d);
		notetheme.setLayoutParams(params);
		notetheme.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra(B.INTENT_04_TO_01_SELECTTHEME,
						INTERNAL_DIR+"/note_theme_"+noteName+"_1.png");
				setResult(B.REQUESTCODE_NOTESELECTOR_TO_MENU, intent);
				finish();
			}
		});
		NoteThemeROW.addView(notetheme);

		notetheme = new ImageView(this);
		bitmap = bp.loadImg(INTERNAL_DIR + "/note_theme_"+noteName+"_2.png", 4);
		d = new BitmapDrawable(bitmap);
		notetheme.setBackgroundDrawable(d);
		notetheme.setLayoutParams(params);
		notetheme.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra(B.INTENT_04_TO_01_SELECTTHEME,
						INTERNAL_DIR+"/note_theme_"+noteName+"_2.png");
				setResult(B.REQUESTCODE_NOTESELECTOR_TO_MENU, intent);
				finish();
			}
		});
		NoteThemeROW.addView(notetheme);

		ItemListLayout.addView(NoteThemeROW);
	}
	
	View.OnClickListener themeListOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
	
	/**
	 * 내부경로의 테마파일을 검색해준다
	 * 
	 * @return ArrayList
	 */
	private ArrayList<String> getFileLists() {
		String[] internalList = fileList();
		int Length = internalList.length;
		ArrayList<String> ListArr = new ArrayList<String>();

		for (int i = 0; i < Length; i++)
			if (internalList[i].lastIndexOf(".png") != -1)
				ListArr.add(internalList[i]);

		return ListArr;
	}

	/**
	 * Resource 이미지를 내부 경로에 저장, 노트 이미지가 없을때만 저장한다
	 * 
	 * @param FileName
	 * @param Rid
	 */
	public boolean saveBitmapPNG_Internal(String FileName, int Rid) {
		BitmapProcess bp = new BitmapProcess();
		bitmap = bp.DrawableToBitmap(getResources().getDrawable(Rid));

		OutputStream out = null;
		try {
			out = openFileOutput(FileName, Context.MODE_WORLD_READABLE);
			bitmap.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
