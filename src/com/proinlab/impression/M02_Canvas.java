/*****************************************************************************
 * @author PROIN LAB [ Canvas For Samsung ]
 *         -------------------------------------------------------------------
 *         Variables
 *         -------------------------------------------------------------------
 *         About Activity : Cycles
 *         -------------------------------------------------------------------
 *         Listeners
 *         -------------------------------------------------------------------
 *         Thread & Handler
 *         -------------------------------------------------------------------
 *         Dialog
 *         -------------------------------------------------------------------
 *         OnCreate Initialize
 *         -------------------------------------------------------------------
 *         Custom Functions
 *         -------------------------------------------------------------------
 *         DB 관리
 *****************************************************************************/

package com.proinlab.impression;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.proinlab.functions.B;
import com.proinlab.functions.BitmapProcess;
import com.proinlab.functions.ColorSet;
import com.proinlab.functions.DataBaseHelper;
import com.proinlab.functions.FileManager;
import com.samsung.sdraw.CanvasView.OnInitializeFinishListener;
import com.samsung.sdraw.PenSettingInfo;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class M02_Canvas extends Activity {

	private DataBaseHelper mHelper;
	private DBEditFn DBEDIT = new DBEditFn();
	private FileManager FILEMANAGER = new FileManager();
	private ColorSet colorsetClass = new ColorSet();

	private int displayWidth, displayHeight;
	int CanvasWidth, CanvasHeight;
	private String filename, filedir, Cameraimgpath = null;
	private int filetype, filepage = 1;

	private boolean backgroundDisplayBool = true;
	private boolean penstylemenu = true;

	// ETC

	private LinearLayout.LayoutParams llp;
	private RelativeLayout.LayoutParams rlp;

	private SCanvasView mSCanvas;

	private LinearLayout LAYOUT_pagedetail_in;
	private HorizontalScrollView LAYOUT_pagedetail;

	private LinearLayout LAYOUT_Penselect;
	private ImageView PencilImg, PenImg, MarkerImg, BrushImg, EraserImg;
	private int PenStyle;
	private int PrePenSelect = 0;
	private int CurrentColor, PenColor, MarkerColor, PencilColor, BrushColor;
	private int CurrentWidth, PenWidth, MarkerWidth, EraserWidth, PencilWidth,
			BrushWidth;

	private ImageButton MenuBtn, PenBtn, undoBtn, redoBtn, clearBtn;

	private LinearLayout LAYOUT_colorlist;
	private RelativeLayout LAYOUT_colorbox;
	private ImageView[] colorBasicSet = new ImageView[30];
	private int[] ColorBasicInt = new int[30];

	private RelativeLayout LAYOUT_widthbox;
	private SeekBar SeekBar_width;
	private TextView TextView_width;
	private ImageView ImageView_width;

	private RelativeLayout LAYOUT_pagenavi;
	private TextView CurrentPageTV, NextPagebtn, PrePagebtn;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m02_main);

		mHelper = new DataBaseHelper(this);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		displayWidth = pref.getInt(B.PREF_TYPE_DISPLAY_WIDTH, 800);
		displayHeight = pref.getInt(B.PREF_TYPE_DISPLAY_HEIGHT, 1280);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		filename = getIntent().getExtras()
				.getString(B.INTENT_01_TO_02_FILENAME);

		Initialize();

		mSCanvas.setOnInitializeFinishListener(new OnInitializeFinishListener() {
			@Override
			public void onFinish() {
				CanvasWidth = mSCanvas.getWidth();
				CanvasHeight = mSCanvas.getHeight();
				CanvasInit();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (filetype == B.EDIT_TYPE_IMG) {
			FILEMANAGER.WORKSPACE_SAVE_CANVAS(mSCanvas.getBitmap(true), null,
					filedir);
		} else if (filetype == B.EDIT_TYPE_MEMO) {
			FILEMANAGER.WORKSPACE_SAVE_NOTE(mSCanvas.getBitmap(true), null,
					filedir + Integer.toString(filepage) + "_note.png");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == B.REQUESTCODE_TAKE_GALLERY) {
			if (data == null)
				;
			else {
				Uri currImageURI = data.getData();
				Cameraimgpath = getRealImagePath(currImageURI);
				showDialog(B.DIALOG_BITMAP_PROCESS);
				ImportBitmapThread();
				backgroundDisplayBool = false;
			}
		} else if (requestCode == B.REQUESTCODE_TAKE_CAMERA) {
			if (data == null)
				;
			else {
				Uri currImageURI = data.getData();
				Cameraimgpath = getRealImagePath(currImageURI);
				showDialog(B.DIALOG_BITMAP_PROCESS);
				ImportBitmapThread();
				backgroundDisplayBool = false;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (LAYOUT_pagedetail.getVisibility() == View.GONE)
				finish();
			else
				LAYOUT_pagedetail.setVisibility(View.GONE);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			LAYOUT_pagedetail.setVisibility(View.GONE);
			switch (filetype) {
			case B.EDIT_TYPE_IMG:
				showDialog(B.DIALOG_CANVAS_SUBMENU);
				break;
			case B.EDIT_TYPE_MEMO:
				showDialog(B.DIALOG_NOTE_SUBMENU);
				break;
			}
		}
		return false;
	}

	private void CanvasInit() {
		String[] FILEINFO = DBEDIT.DBFN_FILEINFO_FIND_BY_FILENAME(mHelper,
				filename);
		filename = FILEINFO[DataBaseHelper.CATEGORY];
		filetype = Integer.parseInt(FILEINFO[DataBaseHelper.FILETYPE]);
		filedir = FILEINFO[DataBaseHelper.FILEDIR];
		filedir = B.SDCARD_DIRECTORY + "FILE/" + filedir + "/";

		BitmapProcess bp = new BitmapProcess();
		filepage = 1;
		if (filetype == B.EDIT_TYPE_IMG) {
			String dir_bg = filedir + "background.png";
			mSCanvas.setBGImagePath(dir_bg);
			mSCanvas.setClearImageBitmap(bp.loadCanvas(filedir
					+ B.SAVE_FILE_NAME_CANVAS_FG));
		} else if (filetype == B.EDIT_TYPE_MEMO) {
			mSCanvas.setBGImagePath(filedir + "background.png");
			mSCanvas.setClearImageBitmap(bp.loadCanvas(filedir
					+ Integer.toString(filepage) + "_note.png"));
			Log.i(B.LOG_TAG, "before pageindexer");
			PageIndexer();
		}

		switch (filetype) {
		case B.EDIT_TYPE_IMG:
			LAYOUT_pagenavi.setVisibility(View.GONE);
			break;
		case B.EDIT_TYPE_MEMO:
			LAYOUT_pagenavi.setVisibility(View.VISIBLE);
			LAYOUT_pagenavi.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			break;
		}
	}

	private void PageIndexer() {
		String displayStr = Integer.toString(filepage) + " / "
				+ Integer.toString(FILEMANAGER.GET_LENGTH_SAVE_NOTE(filedir));
		CurrentPageTV.setText(displayStr);
		LAYOUT_pagenavi.setOnTouchListener(pageIndexeerTouchListener);
		CurrentPageTV.setOnTouchListener(pageIndexeerTouchListener);
		NextPagebtn.setOnClickListener(pageIndexerListener);
		PrePagebtn.setOnClickListener(pageIndexerListener);
	}

	private int preTouchY = 0;
	private View.OnTouchListener pageIndexeerTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				preTouchY = (int) event.getY();
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				int TouchY = (int) event.getY();
				if (preTouchY - TouchY < 100) {
					FILEMANAGER.WORKSPACE_SAVE_NOTE(mSCanvas.getBitmap(true),
							null, filedir + Integer.toString(filepage)
									+ "_note.png");
					LAYOUT_pagedetail.setVisibility(View.VISIBLE);
					CreatePageIndex();
				} else {
					LAYOUT_pagedetail.setVisibility(View.GONE);
				}
				Log.i("TAG",
						Integer.toString(preTouchY) + " "
								+ Integer.toString(TouchY));
				return true;
			}
			return true;
		}
	};

	private void CreatePageIndex() {
		LAYOUT_pagedetail_in.removeAllViewsInLayout();
		int all_pages = FILEMANAGER.GET_LENGTH_SAVE_NOTE(filedir);
		for (int i = 0; i < all_pages; i++) {
			LinearLayout l = new LinearLayout(this);
			l.setOrientation(LinearLayout.VERTICAL);

			BitmapProcess bp = new BitmapProcess();
			ImageView imgv = new ImageView(this);
			imgv.setImageBitmap(bp.loadImg(filedir + Integer.toString(i + 1)
					+ "_note.png", 8));
			imgv.setBackgroundColor(Color.WHITE);
			llp = new LinearLayout.LayoutParams(CanvasWidth / 8,
					CanvasHeight / 8);
			llp.setMargins(15, 0, 0, 5);
			imgv.setLayoutParams(llp);
			final int k = i + 1;
			imgv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					filepage = k;
					BitmapProcess bp = new BitmapProcess();
					mSCanvas.setClearImageBitmap(bp.loadCanvas(filedir
							+ Integer.toString(filepage) + "_note.png"));
					LAYOUT_pagedetail.setVisibility(View.GONE);
					PageIndexer();
				}
			});
			TextView tv = new TextView(this);
			tv.setText(Integer.toString(i + 1));
			tv.setGravity(Gravity.CENTER);

			l.addView(imgv);
			l.addView(tv);
			l.setGravity(Gravity.CENTER);

			LAYOUT_pagedetail_in.addView(l);
			LAYOUT_pagedetail_in.setGravity(Gravity.CENTER);
		}
	}

	private View.OnClickListener pageIndexerListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			BitmapProcess bp = new BitmapProcess();
			FILEMANAGER.WORKSPACE_SAVE_NOTE(mSCanvas.getBitmap(true), null,
					filedir + Integer.toString(filepage) + "_note.png");
			if (v.getId() == NextPagebtn.getId()) {
				if (filepage == FILEMANAGER.GET_LENGTH_SAVE_NOTE(filedir)) {
					filepage++;
					mSCanvas.setClearImageBitmap(null);
					FILEMANAGER.WORKSPACE_SAVE_NOTE(mSCanvas.getBitmap(true),
							null, filedir + Integer.toString(filepage)
									+ "_note.png");
				} else {
					filepage++;
					mSCanvas.setClearImageBitmap(bp.loadCanvas(filedir
							+ Integer.toString(filepage) + "_note.png"));
				}
			} else {
				if (filepage == 1) {
					B.CreateToast(M02_Canvas.this,
							getResources().getString(R.string.first_page));
				} else {
					filepage--;
					mSCanvas.setClearImageBitmap(bp.loadCanvas(filedir
							+ Integer.toString(filepage) + "_note.png"));
				}
			}
			PageIndexer();

		}
	};

	private void Initialize() {
		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);

		LAYOUT_pagedetail_in = (LinearLayout) findViewById(R.id.m02_main_Layout_pagedetail_inner);
		LAYOUT_pagedetail = (HorizontalScrollView) findViewById(R.id.m02_main_Layout_pagedetail);

		MenuBtn = (ImageButton) findViewById(R.id.m02_main_1_ImageButton_menubtn);
		PenBtn = (ImageButton) findViewById(R.id.m02_main_ImageButton_PenBtn);
		redoBtn = (ImageButton) findViewById(R.id.m02_main_ImageButton_redobtn);
		undoBtn = (ImageButton) findViewById(R.id.m02_main_ImageButton_undobtn);
		clearBtn = (ImageButton) findViewById(R.id.m02_main_ImageButton_clearbtn);

		LAYOUT_Penselect = (LinearLayout) findViewById(R.id.m02_main_Layout_2_PenSelect);
		PencilImg = (ImageView) findViewById(R.id.m02_main_2_ImageView_Pencil);
		PenImg = (ImageView) findViewById(R.id.m02_main_2_ImageView_Pen);
		MarkerImg = (ImageView) findViewById(R.id.m02_main_2_ImageView_marker);
		BrushImg = (ImageView) findViewById(R.id.m02_main_2_ImageView_brush);
		EraserImg = (ImageView) findViewById(R.id.m02_main_2_ImageView_eraser);

		LAYOUT_colorbox = (RelativeLayout) findViewById(R.id.m02_main_Layout_colorbox);
		LAYOUT_colorlist = (LinearLayout) findViewById(R.id.m02_main_Layout_colorlist);

		LAYOUT_pagenavi = (RelativeLayout) findViewById(R.id.m02_main_typebar);
		NextPagebtn = (TextView) findViewById(R.id.m02_main_typebar_nextpage);
		PrePagebtn = (TextView) findViewById(R.id.m02_main_typebar_prepage);
		CurrentPageTV = (TextView) findViewById(R.id.m02_main_typebar_textview_currentpage);

		LAYOUT_widthbox = (RelativeLayout) findViewById(R.id.m02_main_ScrollView_penwidth);
		SeekBar_width = (SeekBar) findViewById(R.id.m02_main_SeekBar_width);
		TextView_width = (TextView) findViewById(R.id.m02_main_TextView_widthsize);
		ImageView_width = (ImageView) findViewById(R.id.m02_main_ImageView_widthsize);

		// SCanvas 관련 초기화 CurrentFileType
		mSCanvas.getPenSettingInfo();
		mSCanvas.setSPenTouchListener(mSPenTouchListener);

		// PenStyle Initialize : Custom is Pencil
		PenStyle = PenSettingInfo.PEN_TYPE_PENCIL;
		PenWidth = 5;
		MarkerWidth = 20;
		EraserWidth = 20;
		PencilWidth = 3;
		BrushWidth = 7;
		PenColor = Color.BLACK;
		MarkerColor = Color.YELLOW;
		PencilColor = Color.BLACK;
		BrushColor = Color.BLACK;
		CurrentColor = PencilColor;
		CurrentWidth = PencilWidth;
		mSCanvas.getPenSettingInfo().setPenType(PenStyle);
		mSCanvas.getPenSettingInfo().setPenWidth(CurrentWidth);
		mSCanvas.getPenSettingInfo().setPenColor(CurrentColor);

		// 버튼 관련 세팅
		PencilImg.setOnClickListener(menuBtnClickListener);
		PenImg.setOnClickListener(menuBtnClickListener);
		MarkerImg.setOnClickListener(menuBtnClickListener);
		BrushImg.setOnClickListener(menuBtnClickListener);
		EraserImg.setOnClickListener(menuBtnClickListener);
		MenuBtn.setOnClickListener(menuBtnClickListener);
		PenBtn.setOnClickListener(menuBtnClickListener);
		redoBtn.setOnClickListener(menuBtnClickListener);
		undoBtn.setOnClickListener(menuBtnClickListener);
		clearBtn.setOnClickListener(menuBtnClickListener);

		rlp = new RelativeLayout.LayoutParams(displayWidth / 12,
				displayWidth / 12);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		MenuBtn.setLayoutParams(rlp);
		PenBtn.setLayoutParams(rlp);
		redoBtn.setLayoutParams(rlp);
		undoBtn.setLayoutParams(rlp);
		clearBtn.setLayoutParams(rlp);

		// Pen Width 관련 세팅
		SeekBar_width.setProgress(CurrentWidth);
		TextView_width.setText(Integer.toString(CurrentWidth));

		rlp = new RelativeLayout.LayoutParams(CurrentWidth, CurrentWidth);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		ImageView_width.setLayoutParams(rlp);

		SeekBar_width.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				TextView_width.setText(Integer.toString(progress));
				CurrentWidth = progress;
				rlp = new RelativeLayout.LayoutParams(CurrentWidth,
						CurrentWidth);
				rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
				ImageView_width.setLayoutParams(rlp);
				mSCanvas.getPenSettingInfo().setPenWidth(CurrentWidth);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		// Pen Color 관련 세팅
		rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				displayHeight / 4);
		rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		LAYOUT_colorlist.setLayoutParams(rlp);
		LAYOUT_colorlist
				.setBackgroundResource(R.drawable.m02_main_colorsetbar_bg);

		ColorBasicInt = colorsetClass.BasicColorSet();
		for (int i = 0; i < 3; i++) {
			LinearLayout tmpl = new LinearLayout(this);
			tmpl.setGravity(Gravity.CENTER);
			for (int j = 0; j < 10; j++) {
				colorBasicSet[i * 10 + j] = new ImageView(this);
				colorBasicSet[i * 10 + j].setBackgroundColor(ColorBasicInt[i
						* 10 + j]);
				llp = new LinearLayout.LayoutParams(displayWidth / 17,
						displayHeight / 16);
				llp.setMargins(4, displayHeight / 96, 4, displayHeight / 96);
				colorBasicSet[i * 10 + j].setLayoutParams(llp);
				colorBasicSet[i * 10 + j]
						.setOnClickListener(mColorPickListener);

				tmpl.addView(colorBasicSet[i * 10 + j]);
			}
			LAYOUT_colorlist.addView(tmpl);
		}
		colorBasicSet[4].setImageResource(R.drawable.m02_main_colorselect);

		LAYOUT_Penselect.setVisibility(View.GONE);
		LAYOUT_widthbox.setVisibility(View.GONE);
		LAYOUT_colorbox.setVisibility(View.GONE);
		LAYOUT_pagedetail.setVisibility(View.GONE);

	}

	private View.OnClickListener menuBtnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			LAYOUT_pagedetail.setVisibility(View.GONE);
			if (v.getId() == PencilImg.getId()) {
				PenStyleSetting(0);
			} else if (v.getId() == PenImg.getId()) {
				PenStyleSetting(1);
			} else if (v.getId() == MarkerImg.getId()) {
				PenStyleSetting(2);
			} else if (v.getId() == BrushImg.getId()) {
				PenStyleSetting(3);
			} else if (v.getId() == EraserImg.getId()) {
				PenStyleSetting(4);
			}
			// Main Menu Button
			else if (v.getId() == MenuBtn.getId()) {
				finish();
			} else if (v.getId() == PenBtn.getId()) {
				if (penstylemenu) {
					LAYOUT_Penselect.setVisibility(View.VISIBLE);
					LAYOUT_widthbox.setVisibility(View.VISIBLE);
					LAYOUT_colorbox.setVisibility(View.VISIBLE);
					penstylemenu = false;
				} else {
					LAYOUT_Penselect.setVisibility(View.GONE);
					LAYOUT_widthbox.setVisibility(View.GONE);
					LAYOUT_colorbox.setVisibility(View.GONE);
					penstylemenu = true;
				}
			} else if (v.getId() == redoBtn.getId()) {
				if (mSCanvas.isRedoable()) {
					mSCanvas.redo();
				}
			} else if (v.getId() == undoBtn.getId()) {
				if (mSCanvas.isUndoable()) {
					mSCanvas.undo();
				}
			} else if (v.getId() == clearBtn.getId()) {
				mSCanvas.setClearImageBitmap(null);
			}
		}
	};

	private OnClickListener mColorPickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			for (int i = 0; i < 30; i++) {
				colorBasicSet[i].setImageBitmap(null);
			}
			for (int i = 0; i < 30; i++) {
				if (v == colorBasicSet[i]) {
					CurrentColor = ColorBasicInt[i];
					colorBasicSet[i]
							.setImageResource(R.drawable.m02_main_colorselect);
					mSCanvas.getPenSettingInfo().setPenColor(CurrentColor);
				}
			}
		}
	};

	private SPenTouchListener mSPenTouchListener = new SPenTouchListener() {
		@Override
		public boolean onTouchFinger(View view, MotionEvent event) {
			LAYOUT_Penselect.setVisibility(View.GONE);
			LAYOUT_widthbox.setVisibility(View.GONE);
			LAYOUT_colorbox.setVisibility(View.GONE);
			LAYOUT_pagedetail.setVisibility(View.GONE);
			penstylemenu = true;
			return false;
		}

		@Override
		public boolean onTouchPen(View view, MotionEvent event) {
			LAYOUT_Penselect.setVisibility(View.GONE);
			LAYOUT_widthbox.setVisibility(View.GONE);
			LAYOUT_colorbox.setVisibility(View.GONE);
			LAYOUT_pagedetail.setVisibility(View.GONE);
			penstylemenu = true;
			return false;
		}

		@Override
		public boolean onTouchPenEraser(View view, MotionEvent event) {
			return false;
		}

		@Override
		public void onTouchButtonDown(View arg0, MotionEvent arg1) {
		}

		@Override
		public void onTouchButtonUp(View arg0, MotionEvent arg1) {
		}
	};

	private void PenSelectInitialize() {
		PencilImg.setImageResource(R.drawable.pensetting_icon_pencil_unselect);
		BrushImg.setImageResource(R.drawable.pensetting_icon_brush_unselect);
		MarkerImg.setImageResource(R.drawable.pensetting_icon_neon_unselect);
		PenImg.setImageResource(R.drawable.pensetting_icon_pen_unselect);
		EraserImg.setImageResource(R.drawable.pensetting_icon_eraser_unselect);
	}

	private void PenStyleSetting(int CurrentPenSelect) {
		PenSelectInitialize();

		switch (PrePenSelect) {
		case 0: // Pencil
			PencilColor = CurrentColor;
			PencilWidth = CurrentWidth;
			break;
		case 1: // Pen
			PenColor = CurrentColor;
			PenWidth = CurrentWidth;
			break;
		case 2: // Marker
			MarkerColor = CurrentColor;
			MarkerWidth = CurrentWidth;
			break;
		case 3: // Brush
			BrushColor = CurrentColor;
			BrushWidth = CurrentWidth;
			break;
		case 4: // Eraser
			EraserWidth = CurrentWidth;
			break;
		}
		switch (CurrentPenSelect) {
		case 0: // Pencil
			CurrentColor = PencilColor;
			CurrentWidth = PencilWidth;
			PenStyle = PenSettingInfo.PEN_TYPE_PENCIL;
			PencilImg.setImageResource(R.drawable.pensetting_icon_pencil);
			break;
		case 1: // Pen
			CurrentColor = PenColor;
			CurrentWidth = PenWidth;
			PenStyle = PenSettingInfo.PEN_TYPE_SOLID;
			PenImg.setImageResource(R.drawable.pensetting_icon_pen);
			break;
		case 2: // Marker
			CurrentColor = MarkerColor;
			CurrentWidth = MarkerWidth;
			PenStyle = PenSettingInfo.PEN_TYPE_MARKER;
			MarkerImg.setImageResource(R.drawable.pensetting_icon_neon);
			break;
		case 3: // Brush
			CurrentColor = BrushColor;
			CurrentWidth = BrushWidth;
			PenStyle = PenSettingInfo.PEN_TYPE_BRUSH;
			BrushImg.setImageResource(R.drawable.pensetting_icon_brush);
			break;
		case 4: // Eraser
			CurrentWidth = EraserWidth;
			PenStyle = PenSettingInfo.PEN_TYPE_ERASER;
			EraserImg.setImageResource(R.drawable.pensetting_icon_eraser);
			break;
		}
		mSCanvas.getPenSettingInfo().setPenType(PenStyle);
		mSCanvas.getPenSettingInfo().setPenWidth(CurrentWidth);
		mSCanvas.getPenSettingInfo().setPenColor(CurrentColor);
		PrePenSelect = CurrentPenSelect;

		for (int i = 0; i < 30; i++) {
			colorBasicSet[i].setImageBitmap(null);
			if (CurrentColor == ColorBasicInt[i])
				if (CurrentPenSelect != 4)
					colorBasicSet[i]
							.setImageResource(R.drawable.m02_main_colorselect);
		}

		SeekBar_width.setProgress(CurrentWidth);
		TextView_width.setText(Integer.toString(CurrentWidth));
		rlp = new RelativeLayout.LayoutParams(CurrentWidth, CurrentWidth);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		ImageView_width.setLayoutParams(rlp);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case B.DIALOG_NOTE_SUBMENU:
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(R.array.m02_main_notesetting,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										FILEMANAGER.WORKSPACE_SAVE_NOTE(
												mSCanvas.getBitmap(true),
												null,
												filedir
														+ Integer
																.toString(filepage)
														+ "_note.png");
										filepage = FILEMANAGER
												.GET_LENGTH_SAVE_NOTE(filedir) + 1;
										mSCanvas.clearSCanvasView();
										FILEMANAGER.WORKSPACE_SAVE_NOTE(
												mSCanvas.getBitmap(true),
												null,
												filedir
														+ Integer
																.toString(filepage)
														+ "_note.png");
										PageIndexer();
										break;
									case 1:
										if (filepage == 1) {
											if (filepage == FILEMANAGER
													.GET_LENGTH_SAVE_NOTE(filedir))
												mSCanvas.setClearImageBitmap(null);
											else {
												FILEMANAGER.DELETE_NOTE_PAGE(
														filedir, filepage);
												BitmapProcess bp = new BitmapProcess();
												mSCanvas.setClearImageBitmap(bp.loadCanvas(filedir
														+ Integer
																.toString(filepage)
														+ "_note.png"));
												PageIndexer();
											}
										} else {
											FILEMANAGER.DELETE_NOTE_PAGE(
													filedir, filepage);
											filepage--;
											BitmapProcess bp = new BitmapProcess();
											mSCanvas.setClearImageBitmap(bp.loadCanvas(filedir
													+ Integer
															.toString(filepage)
													+ "_note.png"));
											PageIndexer();
										}
										break;
									case 2:
										FILEMANAGER.WORKSPACE_SAVE_NOTE(
												mSCanvas.getBitmap(true),
												null,
												filedir
														+ Integer
																.toString(filepage)
														+ "_note.png");
										CreatePageIndex();
										LAYOUT_pagedetail
												.setVisibility(View.VISIBLE);
										break;
									}
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		case B.DIALOG_CANVAS_SUBMENU:
			alt_bld = new AlertDialog.Builder(this).setItems(
					R.array.m02_main_backgroundsetting,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								showDialog(B.DIALOG_CANVAS_BG_IMPORT);
								break;
							case 1:
								showDialog(B.DIALOG_BITMAP_PROCESS);
								RotateBitmapThread();
								break;
							case 2:
								if (backgroundDisplayBool) {
									mSCanvas.setBGImagePath(filedir
											+ "background.png");
									backgroundDisplayBool = false;
								} else {
									mSCanvas.setBGColor(Color.WHITE);
									backgroundDisplayBool = true;
								}
								break;
							case 3:
								showDialog(B.DIALOG_CANVAS_BG_ALPHA);
								break;
							}
						}
					});
			alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		case B.DIALOG_BITMAP_PROCESS:
			final LinearLayout linear = (LinearLayout) View.inflate(this,
					R.layout.m02_01_dialog_bitmapprocessing, null);
			alt_bld = new AlertDialog.Builder(this);
			alt_bld.setView(linear);
			alert = alt_bld.create();
			alert.show();
			return alert;
		case B.DIALOG_CANVAS_BG_IMPORT:
			alt_bld = new AlertDialog.Builder(this).setItems(
					R.array.main_import_menu,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								Intent intent = new Intent(Intent.ACTION_PICK);
								intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
								startActivityForResult(intent,
										B.REQUESTCODE_TAKE_GALLERY);
								break;
							case 1:
								Intent intent_camera = new Intent();
								intent_camera
										.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent_camera,
										B.REQUESTCODE_TAKE_CAMERA);
								break;
							}
						}
					});
			alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		case B.DIALOG_CANVAS_BG_ALPHA:
			final LinearLayout l = (LinearLayout) View.inflate(this,
					R.layout.m02_03_dialog_setalpha, null);
			alt_bld = new AlertDialog.Builder(this);
			alt_bld.setView(l);
			alt_bld.setMessage(
					getResources().getString(R.string.Input_transparency_value))
					.setCancelable(false)
					.setPositiveButton(
							getResources().getString(R.string.Apply),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									EditText AlphaValueEditText = (EditText) l
											.findViewById(R.id.main_alphadialog_titletxt);
									int alpha = Integer
											.parseInt(AlphaValueEditText
													.getText().toString());
									alpha = 255 * alpha / 100;

									showDialog(B.DIALOG_BITMAP_PROCESS);
									setAlphaBitmapThread(alpha);
									backgroundDisplayBool = false;
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.Cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			alert = alt_bld.create();
			alert.setTitle(getResources().getString(R.string.Transparency));
			return alert;
		}
		return null;
	}

	private Handler bitmapHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				mSCanvas.setBGImagePath(filedir + "background.png");
			else if (msg.what == 1)
				mSCanvas.setBGImagePath(filedir + "background_alpha.png");
			dismissDialog(B.DIALOG_BITMAP_PROCESS);
		}
	};

	private void ImportBitmapThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FILEMANAGER.PNG_SAVE_BY_DIR(Cameraimgpath, filedir
						+ "background.png", CanvasWidth, CanvasHeight);
				bitmapHandler.post(new Runnable() {
					public void run() {
						bitmapHandler.sendEmptyMessage(0);
					}
				});
			}
		}).start();
	}

	private void RotateBitmapThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final boolean b = FILEMANAGER.PNG_ROTATE_BY_DIR(filedir
						+ "background.png");
				bitmapHandler.post(new Runnable() {
					public void run() {
						if (b)
							bitmapHandler.sendEmptyMessage(0);
						else
							bitmapHandler.sendEmptyMessage(2);
					}
				});
			}
		}).start();
	}

	private void setAlphaBitmapThread(final int alpha) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final boolean b = FILEMANAGER.PNG_ALPHACHANGE_BY_DIR(filedir
						+ "background.png", filedir, alpha);
				bitmapHandler.post(new Runnable() {
					public void run() {
						if (b)
							bitmapHandler.sendEmptyMessage(1);
						else
							bitmapHandler.sendEmptyMessage(2);
					}
				});
			}
		}).start();
	}

	private String getRealImagePath(Uri uriPath) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uriPath, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		String path = cursor.getString(index);
		path = path.substring(5);
		return path;
	}
}