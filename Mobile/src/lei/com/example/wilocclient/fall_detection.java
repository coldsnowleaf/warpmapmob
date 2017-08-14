package lei.com.example.wilocclient;

import java.nio.ByteBuffer;

import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class fall_detection implements Runnable{

	private float[] accVal;
	private int ptr;
	private boolean isFull;
	private Context ctx;
	
	public fall_detection(Context thisCtx){
		ctx = thisCtx;
		accVal = new float[100];
		ptr = 0;
		thisCtx.registerReceiver(mWiFiUpdateReceiver,
				makeGattUpdateIntentFilter());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	private final BroadcastReceiver mWiFiUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Bundle extras = intent.getExtras();
			Integer BufferSize = 2048;
			ByteBuffer msgBuffer = ByteBuffer.allocate(BufferSize);
			msgBuffer.put(extras.getString("Type").getBytes());
			Log.i("zmq", "get Type:" + extras.getString("Type"));
			msgBuffer.putLong(extras.getLong("CurTime"));
			int sMobileID;
		//	msgBuffer.putInt(sMobileID);
			int Cnt;
		//	msgBuffer.putInt(Cnt++);
			if (action.equals("com.Lei.PhoneMotion")) {
				float[] sensingVal = extras.getFloatArray("sensingVal");
				for (int i = 0; i < sensingVal.length; i++) {
					msgBuffer.putFloat(sensingVal[i]);
				}

			}
			if (action.equals("com.Lei.BTMotion")) {
				float[] sensingVal = extras.getFloatArray("sensingVal");
				accVal[ptr] = sensingVal[0]*sensingVal[0]+sensingVal[1]*sensingVal[1]+sensingVal[2]*sensingVal[2];
				if(ptr>=99){
					ptr = 0;
					isFull = true;
				}else{
					ptr++;
				}
				if(isFull){
					int j;
					double cum =0,avg;
					for(j=0;j<100;j++){
						cum += accVal[j];
					}
					avg = cum/100;
					cum = 0;
					for (j=0;j<100;j++){
						cum += Math.abs(accVal[j]-avg);
					}
					Intent msg = new Intent();
					msg.setAction("com.Lei.FallDetection");
					msg.putExtra("RawValue", cum);
					if(cum>400){
					msg.putExtra("status", true);
					}else{
						msg.putExtra("status", false); 
					}
					Log.w("BTMsg","rawvalue updated"+" "+Double.toString(cum));
					ctx.sendBroadcast(msg);
				}
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter fi = new IntentFilter();
		fi.addAction("com.Lei.WiFiScan");
		fi.addAction("com.Lei.PhoneMotion");
		fi.addAction("com.Lei.BTMotion");
		return fi;
	}
}
