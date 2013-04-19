package com.proinlab.impression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.proinlab.functions.BitmapProcess;
import com.proinlab.functions.PDFCONVERTLISTVIEWCUSTOM;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;

@SuppressLint({ "HandlerLeak", "HandlerLeak" })
public class M03_PDF_CONVERT extends Activity {

	private ListView FileListView;
	private ImageButton returnbtn, exitbtn;
	private TextView titleText;

	private ArrayList<String> FileExpArrayList;
	private int[] fileType;
	private String path = Environment.getExternalStorageDirectory().toString();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m03_main);

		FileListView = (ListView) findViewById(R.id.m03_main_filelistview);
		returnbtn = (ImageButton) findViewById(R.id.m03_main_Button_returnmain);
		titleText = (TextView) findViewById(R.id.m03_main_TextView_currentdir);
		exitbtn = (ImageButton) findViewById(R.id.m03_main_ImageButton_exit);

		titleText.setText("sdcard");

		returnbtn.setOnClickListener(mOnclickListener);
		exitbtn.setOnClickListener(mOnclickListener);

		FN_FILE_EXPRORER();

	}

	private View.OnClickListener mOnclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == returnbtn.getId()) {
				if (path.equals(Environment.getExternalStorageDirectory()
						.toString()))
					finish();
				else {
					path = path.substring(0, path.lastIndexOf("/"));
					titleText
							.setText(path.substring(path.lastIndexOf("/") + 1));
					FN_FILE_EXPRORER();
				}
			} else if (v.getId() == exitbtn.getId()) {
				finish();
			}
		}
	};

	private Handler mainHandler = new Handler() {
		public void handleMessage(Message msg) {
			PDFCONVERTLISTVIEWCUSTOM adapter = new PDFCONVERTLISTVIEWCUSTOM(
					M03_PDF_CONVERT.this, R.layout.m03_01_filelist_content,
					FileExpArrayList, fileType);
			FileListView.setAdapter(adapter);
			FileListView.setOnItemClickListener(mListViewItemClick);
		}
	};

	public void FN_FILE_EXPRORER() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				File ParentsFile = new File(path);
				File[] ChildFile = ParentsFile.listFiles();
				FileExpArrayList = new ArrayList<String>();

				// 숨긴파일 안보이기
				for (int i = 0; i < ParentsFile.listFiles().length; i++) {
					String fileName = ChildFile[i].getName();
					if (!fileName.substring(0, 1).equals("."))
						FileExpArrayList.add(fileName);
				}

				// 확장자 제거
				for (int i = 0; i < FileExpArrayList.size(); i++)
					if (FileExpArrayList.get(i).indexOf(".") != -1) {
						String str = FileExpArrayList.get(i);
						if (!str.substring(str.lastIndexOf(".")).equals(".pdf")) {
							FileExpArrayList.remove(i);
							i--;
						}
					}

				Collections.sort(FileExpArrayList);

				fileType = new int[FileExpArrayList.size()];

				for (int i = 0; i < FileExpArrayList.size(); i++) {
					if (FileExpArrayList.get(i).indexOf(".") != -1)
						fileType[i] = 1;
					else
						fileType[i] = 0;
				}

				mainHandler.post(new Runnable() {
					public void run() {
						mainHandler.sendEmptyMessage(0);
					}
				});
			}

		}).start();

	}

	OnItemClickListener mListViewItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (fileType[arg2] == 0) {
				path = path + "/" + FileExpArrayList.get(arg2);
				titleText.setText(FileExpArrayList.get(arg2));
				FN_FILE_EXPRORER();
			} else {
				// TODO PDF CONVERT 실행
				convertPDF(path + "/" + FileExpArrayList.get(arg2), 800);
				Log.i("PDFPAGE", path + "/" + FileExpArrayList.get(arg2));
			}
		}
	};

	public void convertPDF(String pdfpath, int displayWidth) {

		File file = new File(pdfpath);
		FileInputStream fis;
		try {
			PDFFile pdffile;
			RandomAccessFile raf;
			FileChannel channel;
			BitmapProcess bp = new BitmapProcess();

			PDFImage.sShowImages = true;
			PDFPaint.s_doAntiAlias = true;
			HardReference.sKeepCaches = false;

			fis = new FileInputStream(file);
			long length = file.length();
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = fis
							.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			raf = new RandomAccessFile(file, "r");
			channel = raf.getChannel();
			ByteBuffer buffer = ByteBuffer.NEW(channel.map(
					FileChannel.MapMode.READ_ONLY, 0, channel.size()));

			pdffile = new PDFFile(buffer);
		//	int totalpages = pdffile.getNumPages(); // PDF 전체 페이지 숫자

			for (int i = 1; i <= 1; i++) {
				PDFPage pages = pdffile.getPage(i, true);
				Log.i("PDFPAGE", Integer.toString(i));

				int pageWidth = (int) pages.getBBox().width();
				int pageHeight = (int) pages.getBBox().height();
				Log.e("PDFPAGE",
						Integer.toString(pageWidth) + " : "
								+ Integer.toString(pageHeight));

				RectF rect = new RectF(0,0,pageWidth,pageHeight);
				Bitmap image = pages.getImage(pageWidth, pageHeight, rect, true, true);

				String savePath = "/mnt/sdcard/pdftest/";
				File folder = new File(savePath);
				if (!folder.exists())
					while (!folder.mkdir())
						;

				if (bp.saveBitmapPNG(savePath + Integer.toString(i) + ".png",
						image))
					Log.d("PDFPAGE", "succcess");
				else
					Log.d("PDFPAGE", "fail");
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	}
}
