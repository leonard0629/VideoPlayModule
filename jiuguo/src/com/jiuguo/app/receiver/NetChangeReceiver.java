package com.jiuguo.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jiuguo.app.event.NetWorkEvent;
import de.greenrobot.event.EventBus;

public class NetChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		EventBus.getDefault().post(new NetWorkEvent());
	}

}
