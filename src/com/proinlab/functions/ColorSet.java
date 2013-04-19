package com.proinlab.functions;

import android.graphics.Color;

public class ColorSet {

	public int[] BasicColorSet() {
		int[] ColorBasicInt = new int[30];
		ColorBasicInt[0] = Color.rgb(255, 255, 255);
		ColorBasicInt[1] = Color.rgb(175, 175, 175);
		ColorBasicInt[2] = Color.rgb(128, 128, 128);
		ColorBasicInt[3] = Color.rgb(66, 66, 66);
		ColorBasicInt[4] = Color.rgb(0, 0, 0);
		ColorBasicInt[5] = Color.rgb(250, 185, 190);
		ColorBasicInt[6] = Color.rgb(240, 100, 105);
		ColorBasicInt[7] = Color.rgb(245, 30, 30);
		ColorBasicInt[8] = Color.rgb(190, 30, 40);
		ColorBasicInt[9] = Color.rgb(115, 10, 10);
		ColorBasicInt[10] = Color.rgb(240, 220, 195);
		ColorBasicInt[11] = Color.rgb(245, 185, 110);
		ColorBasicInt[12] = Color.rgb(250, 150, 30);
		ColorBasicInt[13] = Color.rgb(195, 90, 30);
		ColorBasicInt[14] = Color.rgb(125, 60, 0);
		ColorBasicInt[15] = Color.rgb(250, 245, 195);
		ColorBasicInt[16] = Color.rgb(250, 235, 145);
		ColorBasicInt[17] = Color.rgb(255, 255, 0);
		ColorBasicInt[18] = Color.rgb(200, 190, 20);
		ColorBasicInt[19] = Color.rgb(115, 115, 0);
		ColorBasicInt[20] = Color.rgb(206, 230, 185);
		ColorBasicInt[21] = Color.rgb(135, 200, 155);
		ColorBasicInt[22] = Color.rgb(0, 165, 75);
		ColorBasicInt[23] = Color.rgb(0, 115, 50);
		ColorBasicInt[24] = Color.rgb(0, 80, 45);
		ColorBasicInt[25] = Color.rgb(180, 210, 250);
		ColorBasicInt[26] = Color.rgb(130, 185, 217);
		ColorBasicInt[27] = Color.rgb(60, 155, 210);
		ColorBasicInt[28] = Color.rgb(0, 90, 150);
		ColorBasicInt[29] = Color.rgb(0, 45, 95);
		return ColorBasicInt;
	}

	public int[] CategoryTagColorSet() {
		int[] CategoryTagColorInt = new int[10];
		CategoryTagColorInt[0] = Color.rgb(112, 112, 112); // 진한 회색
		CategoryTagColorInt[1] = Color.rgb(242, 155, 118); // 파스텔 빨강
		CategoryTagColorInt[2] = Color.rgb(255, 247, 153); // 파스텔 노랑
		CategoryTagColorInt[3] = Color.rgb(137, 201, 151); // 파스텔 녹색
		CategoryTagColorInt[4] = Color.rgb(140, 151, 203); // 파스텔 파랑
		CategoryTagColorInt[5] = Color.rgb(170, 137, 189); // 파스텔 보라
		CategoryTagColorInt[6] = Color.rgb(241, 158, 194); // 파스텔 마젠타
		CategoryTagColorInt[7] = Color.rgb(178, 136, 80); // 밝은 갈색
		CategoryTagColorInt[8] = Color.rgb(106, 57, 6); // 진한 갈색
		CategoryTagColorInt[9] = Color.rgb(99, 140, 11); // 진한 녹황
		return CategoryTagColorInt;
	}

	public int CategoryTagRandCreate() {
		int[] _ColorSet = CategoryTagColorSet();
		int color = _ColorSet[(int) (Math.random() * 10)];
		return color;
	}
}
