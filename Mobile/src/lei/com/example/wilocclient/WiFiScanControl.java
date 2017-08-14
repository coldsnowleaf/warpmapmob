package lei.com.example.wilocclient;
import java.util.List;

import org.zeromq.ZMQ;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


public class WiFiScanControl {
	private List<ScanResult> results;
	private WifiManager wifi;
	private boolean continueScan;
	private Thread t;
	private Context ctx;
	private BroadcastReceiver WiFiRx;
	
	public WiFiScanControl(Context context){
		continueScan = false;
		t = null;
		ctx = context;
		wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
	}
	public void startScan(){
		if (t!= null){
			return;
		}
		
		t = new Thread(){
			@Override
			public void run(){
				WiFiRx = new BroadcastReceiver(){
					@Override
					public void onReceive(Context c, Intent intent){
						results = wifi.getScanResults();
						
						String[] SSIDArray =new String[results.size()];
						long[] MACArray = new long[results.size()];
						int[] RSSIArray = new int[results.size()];
						for(int i = 0; i < results.size();i++){
							SSIDArray[i] = results.get(i).SSID;
							String[] MACNamePart = results.get(i).BSSID.split(":");
							String thisMAC="";
							for(String S : MACNamePart){
								thisMAC += S; 
							}
							MACArray[i] = Long.decode("0x"+thisMAC);
							RSSIArray[i] = results.get(i).level;
						}

						//put all variable 
						Intent msg = new Intent();
						msg.setAction("com.Lei.WiFiScan");
						msg.putExtra("Type", "10001");
						//Number of AP
						msg.putExtra("numAP",results.size());
						//Current time
						msg.putExtra("curTime",System.currentTimeMillis());
						//SSID of AP
						msg.putExtra("SSID",SSIDArray);
						//MAC of AP
						msg.putExtra("MAC",MACArray);
						//RSSI of AP
						msg.putExtra("RSSI",RSSIArray);
						//    ZMQPubCtrl.msgType type = new ZMQPubCtrl.msgType();

					//	msg.PutExtra("order", msgTypeClass.valueOf("WiFi_Fingerprint").ordinal());

						ctx.sendBroadcast(msg);
						if(continueScan){
							wifi.startScan();
						}
					}
				};
				ctx.registerReceiver(WiFiRx, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				while(continueScan){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}		
				ctx.unregisterReceiver(WiFiRx);		
				Log.d("zmq"," WiFi Receiver unregisted");
			}
		};
		continueScan = true;
		t.start();
		wifi.startScan();
		Log.d("zmq","start WiFi Receive thread");

	}
	public void stopScan(){
		continueScan = false;
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = null;
	}
}
