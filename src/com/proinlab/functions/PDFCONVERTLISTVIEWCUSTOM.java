package com.proinlab.functions;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PDFCONVERTLISTVIEWCUSTOM extends BaseAdapter {
	Context maincon;
	LayoutInflater Inflater;
	ArrayList<String> arSrc;
	int layout;
	int[] FileType;

	public PDFCONVERTLISTVIEWCUSTOM(Context context, int alayout,
			ArrayList<String> aarSrc, int[] fileType) {
		maincon = context;
		Inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arSrc = aarSrc;
		layout = alayout;
		FileType = fileType;
	}

	public int getCount() {
		return arSrc.size();
	}

	public String getItem(int position) {
		return arSrc.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = Inflater.inflate(layout, parent, false);
		}

		TextView Title = (TextView) convertView
				.findViewById(com.proinlab.impression.R.id.m03_01_TextView_filename);
		ImageView Img = (ImageView) convertView
				.findViewById(com.proinlab.impression.R.id.m03_01_ImageView_filetype);

		Title.setText(arSrc.get(position));
		
		int fileType = FileType[position];
		if (fileType == 0)
			Img.setImageResource(com.proinlab.impression.R.drawable.m03_01_foldericon);
		else if (fileType == 1)
			Img.setImageResource(com.proinlab.impression.R.drawable.m03_01_pdf);
		convertView.setTag(position);

		return convertView;
	}

}