/**
 * 
 */
package lei.com.example.wilocclient;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.zeromq.ZMQ;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @author Lei
 *
 */
public class ZMQCtrl implements Runnable {
	// public Handler msgHandler;
	private Integer sExternPushPort;
	private Integer sExternPubPort;
	private Integer sMobileID;
	private Integer Cnt;
	private String sExternHost;

	private Thread t;
	private Handler zmqHandler;
	byte[] buf;
	private ZMQ.Socket publisher;
	private ZMQ.Socket pusher;
	private ByteBuffer msgBuffer;
	private boolean isCheckedPush,isCheckedPub;
	Thread r;
	boolean isListen=true;
	Handler h;
	Handler sdtwHandler;

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter fi = new IntentFilter();
		fi.addAction("com.Lei.WiFiScan");
		//fi.addAction("com.Lei.PhoneMotion");
		//fi.addAction("com.Lei.BTMotion");
		return fi;
	}

	private final BroadcastReceiver mWiFiUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Bundle extras = intent.getExtras();
			Integer BufferSize = 4096;
			msgBuffer = ByteBuffer.allocate(BufferSize);
			msgBuffer.put(extras.getString("Type").getBytes());
			Log.i("zmq", "get Type:" + extras.getString("Type"));
			msgBuffer.putLong(extras.getLong("CurTime"));
			msgBuffer.putInt(sMobileID);
			msgBuffer.putInt(Cnt++);
			if (action.equals("com.Lei.WiFiScan")) {
				// Toast.makeText(context, "get WiFi",
				// Toast.LENGTH_LONG).show();
				msgBuffer.putInt(extras.getInt("numAP"));
				long[] MACArray = extras.getLongArray("MAC");
				int[] RSSIArray = extras.getIntArray("RSSI");
				int numAP = extras.getInt("numAP");
				for (int i = 0; i < extras.getInt("numAP"); i++) {
					Log.d("zmq",
							Integer.toString(RSSIArray[i]) + " "
									+ Long.toString(MACArray[i]));
				}
				for (int i = 0; i < numAP; i++) {
					msgBuffer.putLong(MACArray[i]);
				}
				for (int i = 0; i < numAP; i++) {
					msgBuffer.putInt(RSSIArray[i]);
				}
			}
			if (action.equals("com.Lei.PhoneMotion")) {
				float[] sensingVal = extras.getFloatArray("sensingVal");
				for (int i = 0; i < sensingVal.length; i++) {
					msgBuffer.putFloat(sensingVal[i]);
				}

			}
			if (action.equals("com.Lei.BTMotion")) {
				float[] sensingVal = extras.getFloatArray("sensingVal");
				for (int i = 0; i < sensingVal.length; i++) {
					msgBuffer.putFloat(sensingVal[i]);
				}

			}

			/*Message myMsg = new Message();
			Bundle bundle = new Bundle();
			bundle.putByteArray("msg", msgBuffer.array());
			bundle.putInt("length", msgBuffer.position());
			myMsg.setData(bundle);*/
			
			// msgHandler.sendMessage(myMsg);
			if (zmqHandler != null) {
				zmqHandler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//buf=null;
						byte[] buf = new byte[4096];
						buf = Arrays.copyOf(msgBuffer.array(), msgBuffer.position());
						if(isCheckedPub)
							publisher.send(buf);
						if(isCheckedPush)
							pusher.send(buf);
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						
						Log.w("zmq",
								"sent message " + Integer.toString(buf.length)
										+ " Bytes");
						msgBuffer.clear();
					}

				});
			}
		}
	};

	@SuppressLint("HandlerLeak")
	@Override
	public void run() {
		ZMQ.Context ctx = ZMQ.context(1);
		publisher = ctx.socket(ZMQ.PUB);
		pusher = ctx.socket(ZMQ.PUSH);
		
		String mExternPubAddr = "tcp://*:" + Integer.toString(sExternPubPort);
		String mExternPushAddr = "tcp://" + sExternHost + ":"
				+ Integer.toString(sExternPushPort);
		Log.w("zmq", "bind to: " + mExternPubAddr);
		Log.w("zmq", "connect to: " + mExternPushAddr);
		publisher.bind(mExternPubAddr);
		pusher.connect(mExternPushAddr);

		Looper.prepare();
		zmqHandler = new Handler();
		Looper.loop();
	
		/*while(!Thread.interrupted()){
		}*/
//		while(true)
//		{
//			pusher.send("111");
//			publisher.send("222");
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		Log.w("zmq","looper exited");
		publisher.unbind(mExternPubAddr);
		pusher.disconnect(mExternPushAddr);
		publisher.close();
		pusher.close();
		Log.w("zmq","unbind and close");
	//	ctx.term();
	}

	public ZMQCtrl(Context thisCtx,Handler h,Handler sdtwHandler) {
		Cnt = 0;
		thisCtx.registerReceiver(mWiFiUpdateReceiver,
				makeGattUpdateIntentFilter());
		t = null;
		buf = new byte[1024];
		zmqHandler = null;
		msgBuffer = null;
		this.h=h;
		this.sdtwHandler=sdtwHandler;
		
	}

	public void stop() {
		if (t != null) {
			zmqHandler.getLooper().quit();
			zmqHandler = null;
			//t.interrupt();
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t = null;
		}
//		if(r!=null)
//		{
//			try {
//				r.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		isListen=false;
	}

	public void start(boolean mIsCheckedCBPub,boolean mIsCheckedCBPush) {
		if (t == null) {
			isCheckedPub = mIsCheckedCBPub;
			isCheckedPush = mIsCheckedCBPush;
			t = new Thread(this);
			t.start();
		}
		isListen=true;
		//if(r==null){
//		r=new Thread(new Runnable(){
//			
//			@Override
//			public void run() {
////				ZMQ.Context ctx = ZMQ.context(1);
////				ZMQ.Socket receiver = ctx.socket(ZMQ.PULL);
////				receiver.connect("tcp://192.168.23.1:6002");//PC_IP
//				ZMQ.Context ctx = ZMQ.context(1);
//				ZMQ.Socket sub = ctx.socket(ZMQ.SUB);
//				sub.connect("tcp://192.168.23.1:6002");
//				System.out.println("receive...");
//				while(true)
//				{
//
//					System.out.println("bbb");
//					byte[]rec=sub.recv();
//					System.out.println("aaa");
////					Message msg=new Message();
////					Bundle b=new Bundle();
////					b.putString("pos", new String(rec));
////					msg.setData(b);
////					h.sendMessage(msg);
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				
//			}});
//		r.start();
//		//}
		
		
		Thread sdtwThread=new Thread(new Runnable(){
			
			@Override
			public void run() {
				final ZMQ.Context ctx = ZMQ.context(1);
        		final ZMQ.Socket puller = ctx.socket(ZMQ.PULL);
        		puller.connect("tcp://172.19.125.1:9890");
        		//puller.subscribe("".getBytes());
        		//puller.bind("tcp://*:7890");
        		while (true) {
        			//h.sendEmptyMessage(0);
        			byte[] buf = puller.recv();
        			String tmp = new String(buf);
        			Message msg=new Message();
					Bundle b=new Bundle();
					b.putString("sdtwPos", tmp);
					msg.setData(b);
					sdtwHandler.sendMessage(msg);
        			try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
        		}
        		//System.out.println("terminated");
        		//puller.close();
        		//ctx.term();
				
			}});
		sdtwThread.start();
		
	}

	public void setPortAndHost(Integer mExternPushPort, Integer mExternPubPort,
			String mExternHost, Integer mMobileID) {
		sExternPushPort = mExternPushPort;
		sExternPubPort = mExternPubPort;
		sExternHost = mExternHost;
		sMobileID = mMobileID;
	}
}