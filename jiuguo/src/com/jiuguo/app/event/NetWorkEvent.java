package com.jiuguo.app.event;

public class NetWorkEvent {
	public static final int NETTYPE_NONE = -1;
	public static final int NETTYPE_WIFI = 0;
	public static final int NETTYPE_CMWAP = 1;
	public static final int NETTYPE_CMNET = 2;

	private int curState;
	private boolean canLoad;

	public NetWorkEvent() {
		curState = NETTYPE_NONE;
		canLoad = false;
	}

	public NetWorkEvent(int state, boolean canLoad) {
		this.curState = state;
		this.canLoad = canLoad;
	}

	public int getCurState() {
		return curState;
	}

	public void setCurState(int curState) {
		this.curState = curState;
	}

	public boolean isCanLoad() {
		return canLoad;
	}

	public void setCanLoad(boolean canLoad) {
		this.canLoad = canLoad;
	}

}
