package com.proinlab.functions;

import java.io.File;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * SDCARD/.Impression/ Category Name / File Type / FileDir / 1~n.png
 * 
 * @author PROIN LAB
 */

public class FileManager {

	private static String SD_CARD_DIRECTORY = B.SDCARD_DIRECTORY;

	/**
	 * SD카드 , 입력한 경로로 저장
	 * 
	 * @param Directory
	 * @return
	 */

	public static boolean CREATE_FOLDER(String Directory) {
		File FILE = new File(Directory);
		if (!FILE.exists())
			while (FILE.mkdirs())
				;
		return true;
	}

	/**
	 * 파일 경로가 있나 확인
	 * 
	 * @param Category
	 *            : 저장할 카테고리
	 * @param Type
	 *            : 저장할 타입, int Basics.EDIT_TYPE_----
	 * @param FileDir
	 *            : 파일 경로
	 * @return
	 */

	public boolean IS_EXIST_FILE_FODLER(String FileDir) {
		File FILE = new File(SD_CARD_DIRECTORY + "FILE/" + FileDir);
		if (FILE.exists())
			return true;
		return false;
	}

	/**
	 * 파일 경로가 있나 확인
	 * 
	 * @param FileDir
	 *            : 파일 경로
	 * @return
	 */

	public boolean IS_EXIST_FILE(String FileDir) {
		File FILE = new File(FileDir);
		if (FILE.exists())
			return true;
		return false;
	}

	/**
	 * 파일에 따른 폴더 생성
	 * 
	 * @param Category
	 *            : 저장할 카테고리
	 * @param Type
	 *            : 저장할 타입, int Basics.EDIT_TYPE_xxxx
	 * @param FileDir
	 *            : 파일 경로
	 * @return 성공하면 true
	 */

	public boolean CREATE_FILE_FODLER(String FileDir) {

		File FILE = new File(SD_CARD_DIRECTORY + "FILE/" + FileDir);
		if (!FILE.exists())
			while (FILE.mkdirs())
				;
		return true;
	}

	/**
	 * 경로에 있는 파일 삭제
	 * 
	 * @param Directory
	 * @return
	 */

	public boolean DELETE_FOLDER(String Directory) {
		String mPath = SD_CARD_DIRECTORY + Directory;
		DeleteDir(mPath);
		return true;
	}

	/**
	 * 선택한 파일 삭제
	 * 
	 * @param FileDir
	 *            : 파일 경로
	 * @return
	 */

	public boolean DELETE_FILE_FODLER(String FileDir) {

		String mPath = SD_CARD_DIRECTORY + "FILE/" + FileDir;
		DeleteDir(mPath);

		return true;
	}

	public static void DeleteDir(String path) {
		File file = new File(path);
		if (!file.exists())
			return;

		File[] childFileList = file.listFiles();
		for (File childFile : childFileList) {
			if (childFile.isDirectory()) {
				DeleteDir(childFile.getAbsolutePath());
			} else {
				childFile.delete();
			}
		}
		file.delete();
	}

	/**
	 * TYPE : CANVAS 일경우 저장한다
	 * 
	 * @param foreground
	 * @param background
	 * @param saveDir
	 * @return
	 */
	public boolean WORKSPACE_SAVE_CANVAS(Bitmap foreground, Bitmap background,
			String saveDir) {
		File file = new File(saveDir);
		if (!file.exists())
			while (file.mkdir())
				;
		String savePath = saveDir + B.SAVE_FILE_NAME_CANVAS_BG;
		BitmapProcess bp = new BitmapProcess();
		if (background != null)
			bp.saveBitmapPNG(savePath, background);
		savePath = saveDir + B.SAVE_FILE_NAME_CANVAS_FG;
		if (foreground != null)
			bp.saveBitmapPNG(savePath, foreground);
		return true;
	}

	/**
	 * TYPE : NOTE 일경우 저장한다
	 * 
	 * @param foreground
	 * @param background
	 *            : 배경 바꿨을시만 저장
	 * @param saveDir
	 *            : 파일명까지 전부 경로
	 * @return
	 */
	public boolean WORKSPACE_SAVE_NOTE(Bitmap foreground, Bitmap background,
			String saveDir) {
		File file = new File(saveDir);
		if (!file.exists())
			while (file.mkdir())
				;
		String savePath = saveDir;
		BitmapProcess bp = new BitmapProcess();
		if (background != null)
			bp.saveBitmapPNG(savePath, background);
		savePath = saveDir;
		if (foreground != null)
			bp.saveBitmapPNG(savePath, foreground);
		return true;
	}

	/**
	 * 경로로 부터 이미지를 가져와 원하는 경로로 배경을 저장한다 해당 경로로 저장된다
	 * 
	 * @param imgDir
	 *            이미지의 경로
	 * @param saveDir
	 *            저장할 경로, 파일명, 확장자까지
	 * @param width
	 *            원하는 가로길이
	 * @param height
	 *            원하는 세로길이
	 * @return 실패여부
	 */
	public boolean PNG_SAVE_BY_DIR(String imgDir, String saveDir, int width,
			int height) {
		File file = new File(saveDir);
		if (!file.exists())
			while (file.mkdir())
				;
		BitmapProcess bp = new BitmapProcess();
		Bitmap bitmap = bp.loadBackgroud(imgDir, width, height);
		bitmap = bp.ResizeBitmapMatchCanvas(bitmap, width, height);

		bp.saveBitmapPNG(saveDir, bitmap);

		return true;
	}

	/**
	 * 경로에 있는 이미지를 회전시켜서 저장한다
	 * 
	 * @param imgDir
	 *            이미지의 경로
	 * @return 실패여부
	 */
	public boolean PNG_ROTATE_BY_DIR(String imgDir) {
		BitmapProcess bp = new BitmapProcess();
		Bitmap bitmap = bp.loadImg(imgDir, 1);
		if (bitmap == null)
			return false;
		bitmap = bp.imgRotate180(bitmap);
		bp.saveBitmapPNG(imgDir, bitmap);
		return true;
	}

	/**
	 * 경로에 있는 이미지의 알파값을 변경시켜 _alpha.png 로 저장한다
	 * 
	 * @param imgDir
	 *            이미지의 전체 경로
	 * @param parentPath
	 *            이미지의 부모 경로
	 * @param alpha
	 * @return
	 */
	public boolean PNG_ALPHACHANGE_BY_DIR(String imgDir, String parentPath,
			int alpha) {
		BitmapProcess bp = new BitmapProcess();
		Bitmap bitmap = bp.loadImg(imgDir, 1);
		if (bitmap == null)
			return false;

		bitmap = bp.SetBitmapAlpha(bitmap, alpha, bitmap.getWidth(),
				bitmap.getHeight());
		bp.saveBitmapPNG(parentPath + "background_alpha.png", bitmap);

		return true;
	}
	
	/**
	 * 노트의 전체 페이지수를 센다
	 * 
	 * @param Dir
	 *            전체 경로
	 * @return
	 */
	public int GET_LENGTH_SAVE_NOTE(String Dir) {
		File file = new File(Dir);
		String[] lists = file.list();
		Arrays.sort(lists, String.CASE_INSENSITIVE_ORDER);
		String lastindex = "1";
		if (lists.length > 1)
			lastindex = lists[lists.length - 2].substring(0, lists[lists.length - 2].indexOf("_"));
		Log.i("TAG", Integer.toString(lists.length - 2));
		return lists.length - 1;
	}

	/**
	 * 노트의 전체 페이지수를 센다
	 * 
	 * @param Dir
	 *            전체 경로
	 * @return
	 */
	public void DELETE_NOTE_PAGE(String Dir, int page) {
		File file = new File(Dir);
		String[] lists = file.list();
		Arrays.sort(lists, String.CASE_INSENSITIVE_ORDER);

		File delete = new File(Dir + Integer.toString(page) + "_note.png");
		delete.delete();

		for (int i = page; i < lists.length; i++) {
			File rename = new File(Dir + Integer.toString(i) + "_note.png");
			File target = new File(Dir + Integer.toString(i - 1) + "_note.png");
			rename.renameTo(target);
		}
	}
}
