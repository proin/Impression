package com.proinlab.functions;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListViewCustomAdapter extends BaseAdapter {
	Context maincon;
	LayoutInflater Inflater;
	ArrayList<String> arSrc;
	int layout;
	ArrayList<String> FileType;

	public FileListViewCustomAdapter(Context context, int alayout,
			ArrayList<String> aarSrc, ArrayList<String> fileType) {
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
				.findViewById(com.proinlab.impression.R.id.m01_02_TextView_filename);
		ImageView Img = (ImageView) convertView
				.findViewById(com.proinlab.impression.R.id.m01_02_ImageView_filetype);

		Title.setText(arSrc.get(position));
		
		int _FileType=Integer.parseInt(FileType.get(position));
		if(_FileType==-1)
			Img.setVisibility(View.GONE);
		else if (_FileType == B.EDIT_TYPE_IMG)
			Img.setImageResource(com.proinlab.impression.R.drawable.m01_01_gallery);
		else if (_FileType == B.EDIT_TYPE_MEMO)
			Img.setImageResource(com.proinlab.impression.R.drawable.m01_01_memo_icon);
		convertView.setTag(position);

		return convertView;
	}

}