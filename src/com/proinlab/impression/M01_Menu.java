package com.proinlab.impression;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.proinlab.functions.B;
import com.proinlab.functions.BitmapProcess;
import com.proinlab.functions.ColorSet;
import com.proinlab.functions.DataBaseHelper;
import com.proinlab.functions.FileListViewCustomAdapter;
import com.proinlab.functions.FileManager;
import com.proinlab.functions.ListViewCustomAdapter;

@SuppressLint("HandlerLeak")
public class M01_Menu extends Activity {

	private ColorSet colorSet = new ColorSet();
	private FileManager FILEMANAGER = new FileManager();
	private DBEditFn DBEDIT = new DBEditFn();
	private DataBaseHelper mHelper;

	private String[] CategoryList;
	private String[] FileList;

	private boolean isMenuCategory = true;

	private String DBFileNameStr;
	private String DBFileDir;
	private int DBFileType;
	private String DBFileCategory;
	private String DBCreateTime;
	private String DBReSavedTime;

	private ImageView menu_color_bar;
	private ImageButton ReturnCanvasBtn;
	private RelativeLayout MainMenu;
	private HorizontalScrollView MenuListScroll;
	private ListView CategoryListLayout, FileListLayout;
	private RelativeLayout CategoryListMenuLayout, FileListMenuLayout;
	private ImageButton AddCategoryBtn, AddFileBtn;
	private TextView CategoryTitle;
	private int displayWidth, displayHeight;

	private String CurrentFocusedFileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m01_main);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		DBFileCategory = getResources().getString(R.string.Unclassified);
		mHelper = new DataBaseHelper(this);

		DBEDIT.DBFN_CATEGORY_INSERT(mHelper,
				getResources().getString(R.string.Unclassified),
				Color.rgb(128, 128, 128));

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		displayWidth = pref.getInt(B.PREF_TYPE_DISPLAY_WIDTH, 800);
		displayHeight = pref.getInt(B.PREF_TYPE_DISPLAY_HEIGHT, 1280);

		DBFileCategory = getResources().getString(R.string.Unclassified);

		onCreateInitialize();
		getCategoryList();
		getFileList(DBFileCategory);
		CategoryTitle.setText(DBFileCategory);

		new Thread(new Runnable() {
			@Override
			public void run() {
				mainHandler.post(new Runnable() {
					public void run() {
						mainHandler.sendEmptyMessage(0);
					}
				});
			}
		}).start();

	}

	private Handler mainHandler = new Handler() {
		public void handleMessage(Message msg) {
			MenuListScroll.scrollTo(999999, 0);
			isMenuCategory = false;
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == B.REQUESTCODE_NOTESELECTOR_TO_MENU) {
			Bundle bundle = data.getExtras();
			if (bundle == null)
				return;
			String themedir = bundle.getString(B.INTENT_04_TO_01_SELECTTHEME);
			if (themedir == null)
				;
			else {
				D1_2_addNewMemoDialog(themedir);
			}
		}
	}

	/************************************************************************
	 * @author Listeners
	 *         --------------------------------------------------------------
	 *         FileListOnClickListener
	 *         --------------------------------------------------------------
	 *         FileListOnLongClickListener
	 *         --------------------------------------------------------------
	 *         CategoryListOnClickListener : 카테고리 아이템 클릭 리스너
	 *         --------------------------------------------------------------
	 *         CategoryListOnLongClickListener : 카테고리 롱클릭
	 *         --------------------------------------------------------------
	 *         mOnClickListener : 메뉴 관련 리스너
	 *         --------------------------------------------------------------
	 *         menuOnTouchListener : Scroll 막기 리스너
	 ************************************************************************/

	private OnItemClickListener FileListOnClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(M01_Menu.this, M02_Canvas.class);
			intent.putExtra(B.INTENT_01_TO_02_FILENAME, FileList[arg2]);
			startActivity(intent);
		}
	};

	private OnItemLongClickListener FileListOnLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			MenuListScroll.scrollTo(999999, 0);
			isMenuCategory = false;
			D4_FileLongClickDialog(arg2);
			return false;
		}
	};

	private OnItemClickListener CategoryListOnClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			DBFileCategory = CategoryList[arg2];
			getFileList(CategoryList[arg2]);
			CategoryTitle.setText(CategoryList[arg2]);
			MenuListScroll.scrollTo(999999, 0);
			isMenuCategory = false;

		}
	};

	private OnItemLongClickListener CategoryListOnLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			MenuListScroll.scrollTo(0, 0);
			isMenuCategory = true;
			D3_CategoryLongClickDialog(arg2);
			return false;
		}
	};

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == ReturnCanvasBtn.getId()) {
				finish();
			} else if (v.getId() == AddCategoryBtn.getId()) {
				D2_addNewCategoryDialog();
				MenuListScroll.scrollTo(0, 0);
				isMenuCategory = true;
			} else if (v.getId() == AddFileBtn.getId()) {
				D1_AddNewFileDialog();
			}
		}
	};

	int PreTouchPosX = 0;
	private View.OnTouchListener menuOnTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (PreTouchPosX == 0)
				PreTouchPosX = (int) event.getX();
			if (event.getAction() == MotionEvent.ACTION_UP) {
				int nTouchPosX = (int) event.getX();
				if (isMenuCategory) {
					if (PreTouchPosX > nTouchPosX) {
						if (PreTouchPosX > displayWidth * 7 / 10) {
							MenuListScroll.scrollTo(999999, 0);
							isMenuCategory = false;
						}
					}
				} else if (PreTouchPosX < nTouchPosX) {
					if (PreTouchPosX < displayWidth * 3 / 10) {
						MenuListScroll.scrollTo(0, 0);
						isMenuCategory = true;
					}
				}
				PreTouchPosX = 0;
			}
			return true;
		}
	};

	/************************************************************************
	 * @author Dialog
	 *         --------------------------------------------------------------
	 *         D1_AddNewFileDialog : 새로운 파일 만들기 다이얼로그
	 *         --------------------------------------------------------------
	 *         D1_1_addNewCanvasDialog : 새로운 캔버스 만들기
	 *         --------------------------------------------------------------
	 *         D2_addNewCategoryDialog : 카테고리 생성
	 *         --------------------------------------------------------------
	 *         D3_CategoryLongClickDialog : 카테고리 삭제 기능 구현
	 *         --------------------------------------------------------------
	 *         D3_1_ChangeCategoryNameDialog
	 *         --------------------------------------------------------------
	 *         D4_FileLongClickDialog
	 *         --------------------------------------------------------------
	 *         D4_1_ChangeFileNameDialog
	 ************************************************************************/

	private void D1_AddNewFileDialog() {
		new AlertDialog.Builder(this).setItems(R.array.FILE_TYPE,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) { // Canvas
							DBFileType = B.EDIT_TYPE_IMG;
							D1_1_addNewCanvasDialog();
						} else if (which == 1) { // NOTE
							DBFileType = B.EDIT_TYPE_MEMO;
							Intent intent = new Intent(M01_Menu.this,
									M04_NOTE_SELECTOR.class);
							startActivityForResult(intent,
									B.REQUESTCODE_NOTESELECTOR_TO_MENU);
						}
					}
				}).show();
	}

	private void D1_1_addNewCanvasDialog() {
		final LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.m01_04_add_canvasdialog, null);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alt_bld.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.Add),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								EditText title = (EditText) linear
										.findViewById(R.id.m01_04_EditText_title);
								DBFileNameStr = title.getText().toString();
								if (DBEDIT.DBFN_FILEINFO_EXIST_BY_FILENAME(
										mHelper, DBFileNameStr)) {
									B.CreateToast(
											M01_Menu.this,
											getResources().getString(
													R.string.File_name_exists));
									D1_1_addNewCanvasDialog();
								} else {
									DBFileDir = DBEDIT
											.DBFN_FILEINFO_CREATE_FILENAME(mHelper);
									DBCreateTime = Long.toString(System
											.currentTimeMillis());
									DBReSavedTime = Long.toString(System
											.currentTimeMillis());
									DBEDIT.DBFN_FILEINFO_INSERT(mHelper,
											DBFileCategory, DBFileNameStr,
											DBFileType, DBFileDir,
											DBReSavedTime, DBCreateTime);
									getFileList(DBFileCategory);
									// 파일 경로에 이미 파일이 존재했다면 삭제 : 없었던 파일이므로 DB 가
									// 생성될때 이전 파일 삭제, 없거나 삭제된 후 재생성
									if (FILEMANAGER
											.IS_EXIST_FILE_FODLER(DBFileDir))
										FILEMANAGER
												.DELETE_FILE_FODLER(DBFileDir);
									FILEMANAGER.CREATE_FILE_FODLER(DBFileDir);

									CurrentFocusedFileName = DBFileNameStr;

									Intent intent = new Intent(M01_Menu.this,
											M02_Canvas.class);
									intent.putExtra(B.INTENT_01_TO_02_FILENAME,
											CurrentFocusedFileName);
									startActivity(intent);
								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_bld.create();
		alert.setTitle(getResources().getString(R.string.Add_New_Canvas));
		alert.show();
	}

	private void D1_2_addNewMemoDialog(final String notethemedir) {
		final LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.m01_04_add_canvasdialog, null);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alt_bld.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.Add),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								EditText title = (EditText) linear
										.findViewById(R.id.m01_04_EditText_title);
								DBFileNameStr = title.getText().toString();
								if (DBEDIT.DBFN_FILEINFO_EXIST_BY_FILENAME(
										mHelper, DBFileNameStr)) {
									B.CreateToast(
											M01_Menu.this,
											getResources().getString(
													R.string.File_name_exists));
									D1_2_addNewMemoDialog(notethemedir);
								} else {
									DBFileDir = DBEDIT
											.DBFN_FILEINFO_CREATE_FILENAME(mHelper);
									DBCreateTime = Long.toString(System
											.currentTimeMillis());
									DBReSavedTime = Long.toString(System
											.currentTimeMillis());
									DBEDIT.DBFN_FILEINFO_INSERT(mHelper,
											DBFileCategory, DBFileNameStr,
											DBFileType, DBFileDir,
											DBReSavedTime, DBCreateTime);
									getFileList(DBFileCategory);
									// 파일 경로에 이미 파일이 존재했다면 삭제 :
									// 없었던 파일이므로 DB가 생성될때 이전 파일 삭제,
									// 없거나 삭제된 후 재생성
									if (FILEMANAGER
											.IS_EXIST_FILE_FODLER(DBFileDir))
										FILEMANAGER
												.DELETE_FILE_FODLER(DBFileDir);
									FILEMANAGER.CREATE_FILE_FODLER(DBFileDir);
									CurrentFocusedFileName = DBFileNameStr;

									String FILE_FULL_DIR = B.SDCARD_DIRECTORY
											+ "FILE/" + DBFileDir + "/";

									Log.i(B.LOG_TAG, FILE_FULL_DIR);
									BitmapProcess bp = new BitmapProcess();
									Bitmap background = bp.loadBackgroud(
											notethemedir, displayWidth,
											displayHeight);
									FileManager fm = new FileManager();
									fm.WORKSPACE_SAVE_CANVAS(null, background,
											FILE_FULL_DIR);

									Intent intent = new Intent(M01_Menu.this,
											M02_Canvas.class);
									intent.putExtra(B.INTENT_01_TO_02_FILENAME,
											CurrentFocusedFileName);
									startActivity(intent);
								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_bld.create();
		alert.setTitle(getResources().getString(R.string.Add_New_Canvas));
		alert.show();
	}

	private int SelectTag = colorSet.CategoryTagRandCreate();

	private void D2_addNewCategoryDialog() {
		final LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.m01_01_addcategorydialog, null);
		LinearLayout colortag = (LinearLayout) linear
				.findViewById(R.id.m01_01_colortagLayout);
		final TextView tagText = (TextView) linear
				.findViewById(R.id.m01_01_Textview_tag);

		int[] colorlist = colorSet.CategoryTagColorSet();

		LinearLayout.LayoutParams ColorPickParams = new LinearLayout.LayoutParams(
				80, 60);
		ColorPickParams.setMargins(10, 0, 0, 0);

		ImageView[] imgView = new ImageView[colorlist.length];
		for (int i = 0; i < colorlist.length; i++) {
			imgView[i] = new ImageView(this);
			imgView[i].setBackgroundColor(colorlist[i]);
			imgView[i].setLayoutParams(ColorPickParams);
			final int curcolor = colorlist[i];
			imgView[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SelectTag = curcolor;
					tagText.setTextColor(curcolor);
				}
			});
			colortag.addView(imgView[i]);
		}

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alt_bld.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.Add),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								EditText title = (EditText) linear
										.findViewById(R.id.m01_01_EditText_title);
								String AddCate = title.getText().toString();
								if (DBEDIT.DBFN_CATEGORY_FIND_EXIST(mHelper,
										AddCate)) {
									B.CreateToast(M01_Menu.this, getResources()
											.getString(R.string.Exists));
									D2_addNewCategoryDialog();
								}
								DBEDIT.DBFN_CATEGORY_INSERT(mHelper, AddCate,
										SelectTag);
								getCategoryList();
							}
						})
				.setNegativeButton(getResources().getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_bld.create();
		alert.setTitle(getResources().getString(R.string.Add_New_Category));
		alert.show();
	}

	private void D3_CategoryLongClickDialog(final int position) {
		new AlertDialog.Builder(this).setItems(R.array.categorylist_longclick,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							if (CategoryList[position].equals(getResources()
									.getString(R.string.Unclassified))) {
								B.CreateToast(M01_Menu.this, getResources()
										.getString(R.string.Can_not_be_changed));
							} else {
								D3_1_ChangeCategoryNameDialog(CategoryList[position]);
							}
						} else if (which == 1) {
							if (CategoryList[position].equals(getResources()
									.getString(R.string.Unclassified))) {
								B.CreateToast(M01_Menu.this, getResources()
										.getString(R.string.Can_not_be_deleted));
							} else {
								String[] catefilelist = DBEDIT
										.DBFN_FILEINFO_FIND_FILENAME_BY_CATEGORY(
												mHelper, CategoryList[position]);
								for (int i = 0; i < catefilelist.length; i++) {
									FILEMANAGER.DELETE_FILE_FODLER(DBEDIT
											.DBFN_FILEINFO_FIND_BY_FILENAME(
													mHelper, catefilelist[i])[DataBaseHelper.FILEDIR]);
								}
								DBEDIT.DBFN_CATEGORY_DELETE(mHelper,
										CategoryList[position]);
								getCategoryList();
								DBFileCategory = getResources().getString(
										R.string.Unclassified);
								getFileList(DBFileCategory);
								CategoryTitle.setText(DBFileCategory);
							}
						}
					}
				}).show();
	}

	private void D3_1_ChangeCategoryNameDialog(final String OrgCate) {
		final LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.m01_01_addcategorydialog, null);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alt_bld.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.Change),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								EditText title = (EditText) linear
										.findViewById(R.id.m01_01_EditText_title);
								String ChangeCate = title.getText().toString();
								if (DBEDIT.DBFN_CATEGORY_FIND_EXIST(mHelper,
										ChangeCate)) {
									B.CreateToast(M01_Menu.this, getResources()
											.getString(R.string.Exists));
									D3_1_ChangeCategoryNameDialog(OrgCate);
								} else {
									DBEDIT.DBFN_CATEGORY_CHANGEDATA(mHelper,
											OrgCate, ChangeCate);
									DBFileCategory = ChangeCate;
									getCategoryList();
									getFileList(DBFileCategory);
									CategoryTitle.setText(DBFileCategory);
								}

							}
						})
				.setNegativeButton(getResources().getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		alt_bld.setTitle(getResources().getString(R.string.Rename));
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	private void D4_FileLongClickDialog(final int position) {
		new AlertDialog.Builder(this).setItems(R.array.Filelist_longclick,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							D4_1_ChangeFileNameDialog(FileList[position]);
						} else if (which == 1) {
							D4_2_ChangeFileCategoryDialog(FileList[position]);
						} else if (which == 2) {
							String[] FileInfo = DBEDIT
									.DBFN_FILEINFO_FIND_BY_FILENAME(mHelper,
											FileList[position]);
							if (FILEMANAGER
									.DELETE_FILE_FODLER(FileInfo[DataBaseHelper.FILEDIR]))
								;
							else
								;
							DBEDIT.DBFN_FILEINFO_DELETE(mHelper,
									FileInfo[DataBaseHelper.FILEDIR]);

							getFileList(DBFileCategory);
						}
					}
				}).show();
	}

	private void D4_1_ChangeFileNameDialog(final String OrgName) {
		final LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.m01_04_add_canvasdialog, null);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alt_bld.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.Change),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								EditText title = (EditText) linear
										.findViewById(R.id.m01_04_EditText_title);
								String changeName = title.getText().toString();
								if (DBEDIT.DBFN_FILEINFO_EXIST_BY_FILENAME(
										mHelper, changeName)) {
									B.CreateToast(M01_Menu.this, getResources()
											.getString(R.string.Exists));
									D4_1_ChangeFileNameDialog(OrgName);
								} else {
									DBEDIT.DBFN_FILEINFO_CHANGENAME(mHelper,
											changeName, OrgName);
									getFileList(DBFileCategory);
								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		alt_bld.setTitle(getResources().getString(R.string.Rename));
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	private String changeFileCategory;

	private void D4_2_ChangeFileCategoryDialog(final String fileName) {
		final LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.m01_05_changecategorydialog, null);
		Spinner spin = (Spinner) linear
				.findViewById(R.id.m01_05_Spinner_categorylist);

		String[] catelist = DBEDIT.DBFN_CATEGORY_FIND_ALL(mHelper);
		ArrayList<String> arrlist = new ArrayList<String>();
		for (int i = 0; i < catelist.length; i++)
			arrlist.add(catelist[i]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(M01_Menu.this,
				android.R.layout.simple_spinner_item, arrlist);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);

		spin.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				changeFileCategory = CategoryList[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				changeFileCategory = DBFileCategory;
			}

		});

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alt_bld.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.Move),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if (changeFileCategory.equals(DBFileCategory))
									;
								else if (changeFileCategory == null)
									;
								else {
									String[] fileinfo = DBEDIT
											.DBFN_FILEINFO_FIND_BY_FILENAME(
													mHelper, fileName);
									DBEDIT.DBFN_FILEINFO_CHANGEDATA(mHelper,
											fileName, Long.toString(System
													.currentTimeMillis()),
											fileinfo[DataBaseHelper.FILEDIR],
											changeFileCategory);
									getCategoryList();
									getFileList(DBFileCategory);
								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		alt_bld.setTitle(getResources().getString(R.string.Move_Category));
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	/************************************************************************
	 * @author Initialize
	 *         --------------------------------------------------------------
	 *         onCreateInitialize
	 *         --------------------------------------------------------------
	 *         getCategoryList
	 ************************************************************************/

	private void onCreateInitialize() {
		// View 초기화
		ReturnCanvasBtn = (ImageButton) findViewById(R.id.m01_main_Button_returnmain);
		MainMenu = (RelativeLayout) findViewById(R.id.menu_mainmenulayout);
		MenuListScroll = (HorizontalScrollView) findViewById(R.id.menu_scrollbar);
		CategoryListLayout = (ListView) findViewById(R.id.menu_categorylist);
		CategoryListMenuLayout = (RelativeLayout) findViewById(R.id.menu_categorylist_menu);
		FileListLayout = (ListView) findViewById(R.id.menu_filelist);
		FileListMenuLayout = (RelativeLayout) findViewById(R.id.menu_filelist_menu);
		AddCategoryBtn = (ImageButton) findViewById(R.id.menu_categoryaddbtn);
		AddFileBtn = (ImageButton) findViewById(R.id.menu_fileaddbtn);
		CategoryTitle = (TextView) findViewById(R.id.menu_categorynametxt);
		menu_color_bar = (ImageView) findViewById(R.id.m01_menu_filellist_menu_color);

		MenuListScroll.setOnTouchListener(menuOnTouchListener);

		// CategoryList 디자인
		LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(
				displayWidth * 8 / 9, LayoutParams.FILL_PARENT);
		CategoryListLayout.setLayoutParams(Params);
		CategoryListLayout.setBackgroundResource(R.drawable.m01_main_list_bg);
		// Category Menu 디자인
		Params = new LinearLayout.LayoutParams(displayWidth * 8 / 9,
				displayHeight / 15);
		CategoryListMenuLayout.setLayoutParams(Params);
		CategoryListMenuLayout
				.setBackgroundResource(R.drawable.m01_main_list_topmenu_bg);
		// FileList 디자인
		Params = new LinearLayout.LayoutParams(displayWidth * 8 / 9,
				LayoutParams.FILL_PARENT);
		FileListLayout.setLayoutParams(Params);
		// File Menu 디자인
		Params = new LinearLayout.LayoutParams(displayWidth * 8 / 9,
				displayHeight / 15);
		FileListMenuLayout.setLayoutParams(Params);

		RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
				displayHeight / 15, displayHeight / 15);
		AddCategoryBtn.setLayoutParams(rParams);
		AddFileBtn.setLayoutParams(rParams);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				displayHeight / 12);
		MainMenu.setLayoutParams(rParams);

		// MainMenu Button Size 조절
		rParams = new RelativeLayout.LayoutParams(displayHeight / 15,
				displayHeight / 15);
		rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rParams.addRule(RelativeLayout.CENTER_VERTICAL);
		ReturnCanvasBtn.setLayoutParams(rParams);

		AddCategoryBtn.setOnClickListener(mOnClickListener);
		AddFileBtn.setOnClickListener(mOnClickListener);
		ReturnCanvasBtn.setOnClickListener(mOnClickListener);
	}

	private void getCategoryList() {
		CategoryList = DBEDIT.DBFN_CATEGORY_FIND_ALL(mHelper);
		ArrayList<String> CategoryAr = new ArrayList<String>();
		ArrayList<String> TagList = new ArrayList<String>();

		for (int i = 0; i < CategoryList.length; i++) {
			CategoryAr.add(CategoryList[i]);
			int tmpColor = DBEDIT.DBFN_CATEGORY_FIND_TAG_BY_NAME(mHelper,
					CategoryList[i]);
			TagList.add(Integer.toString(tmpColor));
		}
		ListViewCustomAdapter ListAdapter = new ListViewCustomAdapter(this,
				R.layout.m01_03_catelist_content, CategoryAr, TagList);

		CategoryListLayout.setAdapter(ListAdapter);
		CategoryListLayout.setOnItemClickListener(CategoryListOnClickListener);
		CategoryListLayout
				.setOnItemLongClickListener(CategoryListOnLongClickListener);
	}

	private void getFileList(String Category) {
		FileListLayout.setOnItemClickListener(null);
		FileListLayout.setOnItemLongClickListener(null);
		FileList = DBEDIT.DBFN_FILEINFO_FIND_FILENAME_BY_CATEGORY(mHelper,
				Category);
		ArrayList<String> CategoryAr = new ArrayList<String>();
		ArrayList<String> FileType = new ArrayList<String>();
		if (FileList == null) {
			CategoryAr.add(getResources().getString(R.string.No_File));
			FileType.add("-1");
		} else if (FileList.length == 0) {
			CategoryAr.add(getResources().getString(R.string.No_File));
			FileType.add("-1");
		} else {
			for (int i = 0; i < FileList.length; i++) {
				CategoryAr.add(FileList[i]);
				int tmp = DBEDIT.DBFN_FILEINFO_FIND_TYPE_BY_FILENAME(mHelper,
						FileList[i]);
				FileType.add(Integer.toString(tmp));
			}
			FileListLayout.setOnItemClickListener(FileListOnClickListener);
			FileListLayout
					.setOnItemLongClickListener(FileListOnLongClickListener);
		}
		FileListViewCustomAdapter ListAdapter = new FileListViewCustomAdapter(
				this, R.layout.m01_02_filelist_content, CategoryAr, FileType);
		menu_color_bar.setBackgroundColor(DBEDIT
				.DBFN_CATEGORY_FIND_TAG_BY_NAME(mHelper, Category));
		FileListLayout.setAdapter(ListAdapter);
	}

}
