package com.proinlab.impression;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proinlab.functions.B;

public class M00_Main extends Activity {

	private int displayWidth, displayHeight;
	private int COUNT_PROCESS;

	private TextView InitBox;
	private LinearLayout WarningBox;
	private Button ExitBtn;
	private boolean isStartBefore = false;

	@Override
	public void onPause() {
		super.onPause();
		if (!isStartBefore) {
		} else {
			SharedPreferences pref = getSharedPreferences("pref",
					Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt(B.PREF_TYPE_COUNT_PROCESS, COUNT_PROCESS);
			editor.commit();
		}
	}

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.m00_main);

		InitBox = (TextView) findViewById(R.id.initializetxt);
		WarningBox = (LinearLayout) findViewById(R.id.Warning);
		ExitBtn = (Button) findViewById(R.id.m00_Button_exit);

		ExitBtn.setOnClickListener(menuBtnClickListener);
		MainStartThread();
	}

	private View.OnClickListener menuBtnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == ExitBtn.getId()) {
				finish();
			}
		}
	};

	private Handler mainInitializeHandler = new Handler() {
		public void handleMessage(Message msg) {
			ThreadEndFunction();
		}
	};

	private void ThreadEndFunction() {
		if (Build.MANUFACTURER.equals("samsung")) {
			if (COUNT_PROCESS == 1) {
				finish();
				isStartBefore = true;
				Intent intent = new Intent(this, M01_Menu.class);
				startActivity(intent);
			} else {
				finish();
				isStartBefore = true;
				Intent intent = new Intent(this, M01_Menu.class);
				startActivity(intent);
			}
		} else {
			if (COUNT_PROCESS == 1) {
				InitBox.setVisibility(View.GONE);
				WarningBox.setVisibility(View.VISIBLE);
			} else {
				finish();
				Intent intent = new Intent(this, M01_Menu.class);
				startActivity(intent);
			}
		}
	}

	private void MainStartThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Display display = getWindowManager().getDefaultDisplay();
				displayWidth = display.getWidth();
				displayHeight = display.getHeight();

				if (displayWidth > displayHeight) {
					displayHeight = display.getWidth();
					displayWidth = display.getHeight();
				}

				// Introduction, Previous Works Setting
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();

				COUNT_PROCESS = pref.getInt(B.PREF_TYPE_COUNT_PROCESS, 0);
				COUNT_PROCESS++;

				editor.putInt(B.PREF_TYPE_DISPLAY_WIDTH, displayWidth);
				editor.putInt(B.PREF_TYPE_DISPLAY_HEIGHT, displayHeight);

				editor.commit();

				mainInitializeHandler.post(new Runnable() {
					public void run() {
						mainInitializeHandler.sendEmptyMessage(0);
					}
				});
			}
		}).start();
	}

}
