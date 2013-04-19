package com.proinlab.functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * @author PROIN LAB 비트맵을 파일경로로부터 가져오거나 내보내는 클래스
 */
public class BitmapProcess {

	static Bitmap bitmap;

	/**
	 * Canvas를 불러온다, PDF 이미지 전체 사용
	 * 
	 * @param location
	 *            : 파일 저장 경로, Full Location
	 * @param fileName
	 *            : 파일의 번호 또는 저장명
	 * @return
	 */
	public Bitmap loadCanvas(String location) {

		String loadPath = location;
		bitmap = BitmapFactory.decodeFile(loadPath);

		return bitmap;
	}

	/**
	 * Canvas의 배경을 불러온다. 이미지에서만 사용
	 * 
	 * @param loadPath
	 *            : 저장된 경로 전체, 파일명까지
	 * @param width
	 *            : 디바이스 화면 Width
	 * @param height
	 *            : 디바이스 화면 Height
	 * @return Bitmap
	 */
	public Bitmap loadBackgroud(String loadPath, int width, int height) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(loadPath, opts);
		double SampleSize;
		if (opts.outHeight / height > opts.outWidth / width)
			SampleSize = opts.outHeight / height;
		else
			SampleSize = opts.outWidth / width;
		if (SampleSize == 1)
			SampleSize = 1;

		opts.inSampleSize = (int) Math.ceil(SampleSize);

		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(loadPath, opts);
		return bitmap;
	}

	/**
	 * 압축률을 지정해서 이미지를 불러온다
	 * 
	 * @param loadPath
	 * @param SampleSize
	 * @return
	 */
	public Bitmap loadImg(String loadPath, int SampleSize) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = SampleSize;
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(loadPath, opts);
		return bitmap;
	}

	/**
	 * 비트맵을 png 형식으로 저장한다
	 * 
	 * @param FileDir
	 *            : 저장하려는 전체 경로
	 * @param bitmap
	 *            : 저장하려는 비트맵
	 * @return 저장이 되면 true 반환
	 */
	public boolean saveBitmapPNG(String FileDir, Bitmap bitmap) {
		if (FileDir == null || bitmap == null)
			return false;

		File saveFile = new File(FileDir);

		if (saveFile.exists()) {
			while (!saveFile.delete())
				;
		}

		try {
			saveFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			bitmap.compress(CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	/**
	 * 비트맵을 90도 회전시킨다
	 * 
	 * @param bmp
	 * @return Bitmap
	 */
	public Bitmap imgRotate90(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		Matrix matrix = new Matrix();
		matrix.postRotate(90);

		Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height,
				matrix, true);
		bmp.recycle();

		return resizedBitmap;
	}

	/**
	 * 비트맵을 180도 회전시킨다
	 * 
	 * @param bmp
	 * @return Bitmap
	 */
	public Bitmap imgRotate180(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		Matrix matrix = new Matrix();
		matrix.postRotate(180);

		Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height,
				matrix, true);
		bmp.recycle();

		return resizedBitmap;
	}

	/**
	 * 비트맵의 투명도를 변경한다
	 * 
	 * @param Bitmap
	 *            org_bitmap
	 * @param int alpha
	 * @param int width
	 * @param int height
	 * @return
	 */
	public Bitmap SetBitmapAlpha(Bitmap org_bitmap, int alpha, int width,
			int height) {
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Paint p = new Paint();
		p.setDither(true);
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		p.setAlpha(alpha);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(org_bitmap, 0, 0, p);
		
		return bitmap;
	}

	/**
	 * 비트맵을 화면 해상도에 맞게 변경해준다 화면비율과 안맞을 경우 상하 여백처리
	 * 
	 * @param org_bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap ResizeBitmapMatchCanvas(Bitmap org_bitmap, int width,
			int height) {
		bitmap = org_bitmap;
		if (bitmap.getHeight() < bitmap.getWidth())
			bitmap = imgRotate90(bitmap);
		org_bitmap = bitmap;
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		org_bitmap = Bitmap.createScaledBitmap(org_bitmap, width, width
				* org_bitmap.getHeight() / org_bitmap.getWidth(), true);
		int margin_top = (height - org_bitmap.getHeight()) / 2;
		Paint p = new Paint();
		p.setDither(true);
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(org_bitmap, 0, margin_top, p);
		return bitmap;
	}

	/**
	 * Drawable을 비트맵으로 변환해준다
	 * @param Drawable
	 * @return Bitmap
	 */
	public Bitmap DrawableToBitmap(Drawable d) {
		bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(),
				d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		d.draw(canvas);
		return bitmap;
	}
}
