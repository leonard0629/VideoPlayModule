package com.jiuguo.app.bean;

import android.graphics.Color;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Random;

public class Barrage extends BaseBean implements Comparable<Barrage> {
	private static final long serialVersionUID = -8715474483612427884L;

	private final static String TAG = "Barrage";

	public static final String[] danmakuColors = new String[] { "249033054",
			"255064129", "255152000", "255193007", "255235059", "238255065",
			"118255003", "044221090", "029233182", "007211237", "003169244",
			"102132255", "124077255", "209081246"};

	private String content;
	private int videoTime;
	private String postTime;
	private int color;
	private float speed = 0;
	private boolean isLive = true;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getVideoTime() {
		return videoTime;
	}

	public void setVideoTime(int videoTime) {
		this.videoTime = videoTime;
	}

	public String getPostTime() {
		return postTime;
	}

	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setIsLive(boolean isLive) {
		this.isLive = isLive;
	}

	public int getColor() {
		return color;
	}

	/**
	 * 
	 * @param colorStr rrrgggbbb
	 */
	public void setColor(String colorStr){
		colorStr = danmakuColors[new Random().nextInt(danmakuColors.length)];

		int b = 255;
		int g = 255;
		int r = 255;
		String colorBit = "";
		if(colorStr.length()==9){
			colorBit = colorStr.substring(0,3);
			if(colorBit.equals("000")){
				r = 0;
			}else {
				try {
					r = Integer.parseInt(colorBit);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			colorBit = colorStr.substring(3,6);
			if(colorBit.equals("000")){
				g = 0;
			}else {
				try {
					g = Integer.parseInt(colorBit);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			colorBit = colorStr.substring(6);
			if(colorBit.equals("000")){
				b = 0;
			}else {
				try {
					b = Integer.parseInt(colorBit);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}else if(colorStr.length()==6){
			r=0;
			colorBit = colorStr.substring(0,3);
			if(colorBit.equals("000")){
				g = 0;
			}else {
				try {
					g = Integer.parseInt(colorBit);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			colorBit = colorStr.substring(3);
			if(colorBit.equals("000")){
				b = 0;
			}else {
				try {
					b = Integer.parseInt(colorBit);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}else if(colorStr.length()==3){
			r=g=0;
			colorBit = colorStr;
			if(colorBit.equals("000")){
				b = 0;
			}else {
				try {
					b = Integer.parseInt(colorBit);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}

		int color = Color.rgb(r, g, b);
		setColor(color);
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	public static Barrage parse(String jsonStr) {
		Barrage barrage = null;

		try {
			JSONObject object = JSON.parseObject(jsonStr);
			barrage = new Barrage();
			barrage.setContent(object.getString("barrage"));
			barrage.setPostTime(object.getString("posttime"));
			barrage.setVideoTime(object.getIntValue("videotime"));
			String colorStr = object.getString("color");
			barrage.setColor(colorStr);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "get exception when parse, cause: " + e.getMessage());
		}

		return barrage;
	}
	
	
	
	@Override
	public int hashCode() {
		if (getContent() == null)
			return -1;
		int result = getContent().hashCode();
		result = 29 * result + getVideoTime();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Barrage) {
			Barrage tempBarrage = (Barrage) obj;
			if (tempBarrage.hashCode()== hashCode()) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public int compareTo(Barrage another) {
		if (another.getVideoTime() == getVideoTime()) {
			return 0;
		} else {
			return another.getVideoTime() < getVideoTime() ? 1 : -1;
		}
	}
}
