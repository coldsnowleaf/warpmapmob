package lei.com.example.wilocclient;


import java.util.Arrays;

import lei.com.example.wilocclient.DeviceScanActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;








//import com.example.ti.ble.common.BluetoothLeService;
import com.google.common.net.InetAddresses;

public class MainActivity extends Activity {
	private Integer sExternPushPort;
	private String sExternHost;
	private Integer mobileID;
	private Integer sExternPubPort = 5556;
 	private ZMQCtrl zmqCtrl;
	private WiFiScanControl WiFiScanner;
	private sensorTagRx mSensorTagRx;
	private fall_detection mFallDetect;
	ListView lv;
	TextView tv;
	private PhoneSensorForwarder mPSF; // an instance of phone sensor forwarder
	private WifiManager wifi;
	int size = 0;	
	private Integer fallCnt;
	// motion sensor related
	private static final String PREFS_NAME = "myPrefsFile";
	BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private final static int REQUEST_ENABLE_BT = 1;
	private final static int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		mFallDetect = new fall_detection(this);
		String ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
		if(wifi.isWifiEnabled() == false){
			Toast.makeText(getApplicationContext(), "wifi is disabled, make it enable", Toast.LENGTH_SHORT).show();
			//wifi.setWifiEnabled(true);
		}
		TextView textView1 = (TextView)findViewById(R.id.textView1);
		textView1.setText("Phone IP:"+ip);
		SharedPreferences setting = getSharedPreferences(PREFS_NAME, 0);
		 sExternHost = setting.getString("IP","192.168.31.119");
		 sExternPushPort = Integer.parseInt( setting.getString("PORT", "5555"));
		 mobileID = setting.getInt("MOBILEID",1);
		((EditText)findViewById(R.id.Edit1)).setText(sExternHost,TextView.BufferType.EDITABLE);
		((EditText)findViewById(R.id.Edit2)).setText(String.valueOf(sExternPushPort),TextView.BufferType.EDITABLE);
		((EditText)findViewById(R.id.Edit3)).setText(String.valueOf(mobileID),TextView.BufferType.EDITABLE);
		//zmqCtrl = new ZMQCtrl(this);
		zmqCtrl.setPortAndHost(sExternPushPort,sExternPubPort,sExternHost,mobileID);

		fallCnt = 0;
		
		WiFiScanner = new WiFiScanControl(MainActivity.this);
		mPSF = new PhoneSensorForwarder(MainActivity.this);
		
		registerReceiver(mWiFiUpdateReceiver, makeGattUpdateIntentFilter());
		
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		mSensorTagRx = new sensorTagRx(MainActivity.this,mBluetoothManager,mBluetoothAdapter);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter fi = new IntentFilter();
		fi.addAction("com.Lei.WiFiScan");
		fi.addAction("com.Lei.PhoneMotion");
		fi.addAction("com.Lei.BTMotion");
		fi.addAction("com.Lei.FallDetection");
		return fi;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public void onZmqSWClicked(View view){
		boolean on = ((Switch)view).isChecked();
		if(on){
			 sExternHost = ((EditText)findViewById(R.id.Edit1)).getText().toString();
			 sExternPushPort = Integer.parseInt(((EditText)findViewById(R.id.Edit2)).getText().toString());
			 mobileID = Integer.parseInt(((EditText)findViewById(R.id.Edit3)).getText().toString());
			 //if the address is valid
			if (InetAddresses.isInetAddress(sExternHost)&&sExternPushPort>0&&sExternPushPort<65535){
				((EditText)findViewById(R.id.Edit1)).setEnabled(false);
				((EditText)findViewById(R.id.Edit2)).setEnabled(false);
				((EditText)findViewById(R.id.Edit3)).setEnabled(false);
				((CheckBox)findViewById(R.id.cb_publish_on_phone)).setEnabled(false);
				((CheckBox)findViewById(R.id.cb_push_to_server)).setEnabled(false);

				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();

				//store the preference 
				editor.putString("IP", sExternHost);
				editor.putString("PORT",String.valueOf(sExternPubPort));
				editor.putInt("MOBILEID", mobileID);
				editor.commit();
				//start the zmqCtrl
				boolean isCheckedCBPub = ((CheckBox)findViewById(R.id.cb_publish_on_phone)).isChecked();
				boolean isCheckedCBPush = ((CheckBox)findViewById(R.id.cb_push_to_server)).isChecked();
				zmqCtrl.start(isCheckedCBPub,isCheckedCBPush);
			}else{
				((Switch)view).setChecked(false);
				Toast.makeText(getApplicationContext(), "Invalid IP or Port", Toast.LENGTH_SHORT).show();
			}
			//zmqPusher.start();
		}else{
			//zmqCtrl.stop();
			zmqCtrl.stop();
			((EditText)findViewById(R.id.Edit1)).setEnabled(true);
			((EditText)findViewById(R.id.Edit2)).setEnabled(true);
			((EditText)findViewById(R.id.Edit3)).setEnabled(true);
			((CheckBox)findViewById(R.id.cb_publish_on_phone)).setEnabled(true);
			((CheckBox)findViewById(R.id.cb_push_to_server)).setEnabled(true);
		}
	}


	public void onWiFiSWClicked(View view){
		boolean on = ((Switch) view).isChecked();
		if(on){
			WiFiScanner.startScan();
		}else{
			WiFiScanner.stopScan();
		}
	}
	public void onInternalSensorSWClicked(View view){
		boolean on = ((Switch) view).isChecked();
		if(on){
			mPSF.start();
		}else{
			mPSF.stop();
		}
	}
	public void onFallForwardBtn(View view){
		fallCnt ++;
		((TextView)findViewById(R.id.fall_cnt)).setText(Integer.toString(fallCnt));
	}
	public void onBTSWClicked(View view){
		boolean on = ((Switch) view).isChecked();
		if(on){
			Intent k = new Intent(MainActivity.this,
					DeviceScanActivity.class);
			Bundle bundle;
			startActivityForResult(k, REQUEST_CODE);
		}else{
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("name")
					&& !data.getExtras().getString("name").isEmpty()) {
				//mBluetoothGatt.discoverServices();
				// mBluetoothGatt.getService(SensorTagGatt.UUID_ACC_SERV);
				// mBluetoothGatt.setCharacteristicNotification(characteristic,
				// enabled);
				((TextView)findViewById(R.id.BTStatus)).setText(data.getExtras().getString("name")+data.getExtras().getString("mac"));
				mSensorTagRx.connectTo(data.getExtras().getString("mac"));
			}
		}
	}
	
	private final BroadcastReceiver mWiFiUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if(action.equals("com.Lei.FallDetection")){
				TextView textView1 = (TextView)findViewById(R.id.fall_status);
				textView1.setText(Boolean.toString(intent.getBooleanExtra("status", false))
						+" "
						+Double.toString(intent.getDoubleExtra("RawValue", 0.0f)));
			}
			if(action.equals("com.Lei.WiFiScan")){
			//Toast.makeText(context, "get WiFi", Toast.LENGTH_LONG).show();
				TextView textView1 = (TextView)findViewById(R.id.WiFiResult);
				int numAP = intent.getIntExtra("numAP", 0);
				Long curTime = intent.getLongExtra("curTime", 0);
				textView1.setText(Integer.toString(numAP)+"@"+Long.toString(curTime));
			}
			if(action.equals("com.Lei.PhoneMotion")){
				TextView textView1 = (TextView)findViewById(R.id.PhoneStatus);
				float array[] = intent.getFloatArrayExtra("sensingVal");
				float subArray1[] = Arrays.copyOfRange(array, 0, 3);
				float subArray2[] = Arrays.copyOfRange(array, 3, 6);
				float subArray3[] = Arrays.copyOfRange(array, 6, 9);
				float subArray4[] = Arrays.copyOfRange(array, 9, 12);


				Long curTime = intent.getLongExtra("curTime", 0);
				textView1.setText(Arrays.toString(subArray1)+"\n"+
						Arrays.toString(subArray2)+"\n"+
						Arrays.toString(subArray3)+"\n"+
						Arrays.toString(subArray4)+"\n"+
						"@"+Long.toString(curTime));
				Log.i("zmq","get phone motion intent");
			}
			if(action.equals("com.Lei.BTMotion")){
				TextView textView1 = (TextView)findViewById(R.id.BTStatus);
				float array[] = intent.getFloatArrayExtra("sensingVal");
				
				float subArray1[] = Arrays.copyOfRange(array, 0, 3);
				float subArray2[] = Arrays.copyOfRange(array, 3, 6);
				float subArray3[] = Arrays.copyOfRange(array, 6, 9);
				float subArray4[] = Arrays.copyOfRange(array, 9, 12);
				float refreshRate = intent.getFloatExtra("refreshRate", 22);
				


				Long curTime = intent.getLongExtra("curTime", 0);
				//textView1.setText(Arrays.toString(array)+"\n"+"@"+Long.toString(curTime));
				
				textView1.setText(Arrays.toString(subArray1)+"\n"+
						Arrays.toString(subArray2)+"\n"+
						Arrays.toString(subArray3)+"\n"+
						Arrays.toString(subArray4)+"\n"+
						"@"+Long.toString(curTime)+"@"+Float.toString(refreshRate)+"hz");
						
				Log.i("zmq","get BT motion intent");
			}
		}
	};
}
