package lei.com.example.wilocclient;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.zeromq.ZMQ;

import com.google.common.net.InetAddresses;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class RunActivity extends Activity {
	//View currentView;					
	GameView gv;						
	private SensorManager mSensorManager;
	boolean flagLock=true;
	float DownX ;
	float DownY;
	float moveX;
	float moveY;
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	private Integer sExternPushPort;
	private String sExternHost;
	private Integer mobileID;
	private Integer sExternPubPort = 5555;
 	private ZMQCtrl zmqCtrl;
	private WiFiScanControl WiFiScanner;
	private sensorTagRx mSensorTagRx;
	//private fall_detection mFallDetect;
//	ListView lv;
//	TextView tv;
	private PhoneSensorForwarder mPSF; // an instance of phone sensor forwarder
	private WifiManager wifi;
	//int size = 0;	
	//private Integer fallCnt;
	// motion sensor related
	private static final String PREFS_NAME = "myPrefsFile";
//	BluetoothManager mBluetoothManager;
//	private BluetoothAdapter mBluetoothAdapter;
	//private final static int REQUEST_ENABLE_BT = 1;
	private final static int REQUEST_CODE = 1;

	//float timeVector[][]=new float[100][2];
	//////////////////////////////////////////////////////////////////////////////////////////////////
	StringBuilder sb_wifi=new StringBuilder();
	StringBuilder sb_phone=new StringBuilder();
	StringBuilder sb_phone2 = new StringBuilder(); 
	int startsumPathI=0;
	int z=0;
	public Handler h=new Handler(){
		public void handleMessage(Message msg){
			
			String pos=msg.getData().getString("pos");
			//Toast.makeText(RunActivity.this, pos, Toast.LENGTH_LONG).show();
			
//			gv.pathI=-1;
			String path[]=pos.split("\n");
			//if(path.length==2)
			Toast.makeText(RunActivity.this, pos+"!!!"+path[0], Toast.LENGTH_LONG).show();
			if(path.length==2){
			String raw[]=path[0].split(",");
			String pre[]=path[1].split(",");
			if(raw.length>=2){
			for(int i=0;i<raw.length ;i++)
				{String xy[]=raw[i].split("%");
				gv.path[0][i][0]=(int) ((Float.parseFloat(xy[0].substring(1, xy[0].length()))+1070)/26.5*85);
				gv.path[0][i][1]=(int) ((Float.parseFloat(xy[1].substring(0,xy[1].length()-1))+250)/26.5*85+40);
				gv.pathI=i;
				}
			//Toast.makeText(RunActivity.this,pos+",,,"+gv.path[0][0][0]+gv.path[0][0][1], Toast.LENGTH_LONG).show();
			}
			
			if(pre.length>=2){
				for(int i=0;i<pre.length ;i++)
					{String xy[]=pre[i].split("%");
					gv.path[1][i][0]=(int) ((Float.parseFloat(xy[0].substring(1, xy[0].length()))+1070)/26.5*85);
					gv.path[1][i][1]=(int) ((Float.parseFloat(xy[1].substring(0,xy[1].length()-1))+250)/26.5*85+40);
					//gv.pathI=i;
					}
				//Toast.makeText(RunActivity.this,pos+",,,"+gv.path[1][0][0]+",,,"+gv.path[1][0][1], Toast.LENGTH_LONG).show();
				}
			
			gv.sumPathI=1;
			}
			
	}};
	
	 public Handler sdtwHandler=new Handler(){
			public void handleMessage(Message msg){
				String pos=msg.getData().getString("sdtwPos");
				String sdtwPos[]=pos.split(",");
				gv.sdtwPoint_x=(int) (Integer.parseInt(sdtwPos[0])*0.023*510/8.9);
				gv.sdtwPoint_y=(int) (Integer.parseInt(sdtwPos[1])*0.023*510/8.9);
				Toast.makeText(RunActivity.this,'['+sdtwPos[0]+','+sdtwPos[1]+']', Toast.LENGTH_LONG).show();
				
				
			}};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail);	
        gv = (GameView) findViewById(R.id.gv);
		OrientionDetector odetector = new OrientionDetector(this);
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		mSensorManager.registerListener(odetector,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);
		
		initDataCollect();
		
        
		//ParseWifiData pwd=new ParseWifiData(this);
		//pwd.readWifiData();
    
    }

	public boolean onTouchEvent(MotionEvent event) {
		if(flagLock){
		if(event.getAction() == MotionEvent.ACTION_UP){
			int x = (int)event.getX();			//获取屏幕点击处的X坐标
			int y = (int)event.getY();			//获取屏幕点击处的Y坐标
			return gv.myTouchEvent(x, y);	//调用GameView的相关事件处理方法
		}
		}
		else{

			
			switch (event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
			DownX = event.getX();//float DownX
			DownY = event.getY();//float DownY
			break;
			case MotionEvent.ACTION_MOVE:
			moveX = event.getX() - DownX;//X轴距离
			moveY = event.getY() - DownY;//y轴距离
			break;
			case MotionEvent.ACTION_UP:
			break;
			}
			if(moveX>=50)				gv.checkIfRollScreen(4);
			if(moveX<=-50)				gv.checkIfRollScreen(8);
			if(moveY>=50)				gv.checkIfRollScreen(1);
			if(moveY<=-50)				gv.checkIfRollScreen(2);
			
		}
		return true;
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu.FIRST, 0, "滚屏");
		menu.add(1, Menu.FIRST+1, 1, "撤销标记");
		menu.add(2, Menu.FIRST+2, 2, "完成标记");
		menu.add(3, Menu.FIRST+3, 3, "开始采集");
		menu.add(4, Menu.FIRST+4, 4, "结束采集");
		menu.add(5, Menu.FIRST+5, 5, "上传");
		//menu.add(6, Menu.FIRST+6, 6, "传输");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) { 
		case Menu.FIRST:
			if(flagLock)
				item.setTitle("标记");
			else
				item.setTitle("滚屏");
			flagLock=!flagLock;
			break;
			
		case Menu.FIRST+1:gv.path[gv.sumPathI][gv.pathI][0]=0;
						gv.path[gv.sumPathI][gv.pathI][1]=0;
						gv.pathI--;break;
		case Menu.FIRST+2:gv.sumPathI++;gv.pathI=-1;
		
		break;
		case Menu.FIRST+3:
		onInternalSensorSWClicked(true);onWiFiSWClicked(true);
		sb_wifi.append("************************start******************************\n");
		sb_phone.append("************************start******************************\n");
		onZmqSWClicked(true);
		//timeVector[gv.sumPathI-1][0]=System.currentTimeMillis();
		break;
		case Menu.FIRST+4:
		onInternalSensorSWClicked(false);onWiFiSWClicked(false);
		//timeVector[gv.sumPathI-1][1]=System.currentTimeMillis();
		sb_wifi.append("************************end******************************\n");
		sb_phone.append("************************end******************************\n");
		onZmqSWClicked(false);
		break;
		case Menu.FIRST+5:
		StringBuilder sb=new StringBuilder();
		int i,j;
		DecimalFormat d=new DecimalFormat(".0000");
		for(i=startsumPathI;i<gv.sumPathI&&i<100;i++)
		{	
			for(j=0;j<100;j++)
				{
				for(int k=0;k<=1;k++)
					if(gv.path[i][j][k]!=0)
						{if(k==0) sb.append("["+d.format(gv.path[i][j][k]*8.9/510.0)+",");
						if(k==1)  sb.append(d.format(gv.path[i][j][k]*8.9/510.0)+"],");
						}
				}
			sb.append("\n");
		}
		saveSDcar(sb.toString(),"path.txt",true);
		saveSDcar(sb_wifi.toString(),"wifi.txt",true);
		saveSDcar(sb_phone.toString(),"phone.txt",true);
		saveSDcar(sb_phone2.toString(),"phones.txt",true);
		sb.setLength(0);
		sb_wifi.setLength(0);
		sb_phone.setLength(0);
		startsumPathI=gv.sumPathI;
		break;
//		case Menu.FIRST+6:
//			new Thread(){
//			public void run(){
//				zmqConnect();
//			}
//		}.start();
//			onZmqSWClicked(true);
//			break;
		}
		return true;  
		
	}
	
//	void tryToConnect(String ipText,int portNum)
//	{	Socket socket = null;
//		DataOutputStream dos=null;
//		 try {
//	  		socket = new Socket(ipText,portNum);
//	      }catch (Exception ioe) {
//	    	  System.out.print("socket  err ");
//	   	  }
//		 
//	  	  if(socket!=null){
//	  	  
//	  		  try{
//	  			  dos = new DataOutputStream(socket.getOutputStream());
//	  		  } catch (IOException ioe) {
//	        	  System.out.print("DataStream create err ");} 
//	  		  try{
//		       Thread.sleep(1000);  
//		       dos.writeUTF("aaa");
//		       dos.close();
//	  		   socket.close();
//	  		   System.out.println("socket 已经关闭......");
//	  		  }catch (Exception ioe) {
//		    	  System.out.println("socket close() err .......");}		
//		  }//  if(socket!=null)
//	 }
	public void zmqConnect()
	{
		final ZMQ.Context ctx = ZMQ.context(1);
		final ZMQ.Socket pusher = ctx.socket(ZMQ.PUB);
		pusher.bind("tcp://"+"*"+":5555");
		int cnt = 0;
		while (true) {//!Thread.interrupted()
			String mTest = new String("rb_walk");
			pusher.send("10006hello");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("10006"+Integer.toString(cnt++));
			
		}
		//System.out.println("terminated");
		//pusher.close();
		//ctx.term();
	}
	public void initDataCollect()
	{
		wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		//mFallDetect = new fall_detection(this);
		String ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
		if(wifi.isWifiEnabled() == false){
			Toast.makeText(getApplicationContext(), "wifi is disabled, make it enable", Toast.LENGTH_SHORT).show();
		}
		SharedPreferences setting = getSharedPreferences(PREFS_NAME, 0);
		 sExternHost = setting.getString("IP","172.19.125.1");//PC
		 //sExternHost = setting.getString("IP","192.168.253.1");//PC
		 sExternPushPort = Integer.parseInt( setting.getString("PORT", "5556"));
		 mobileID = setting.getInt("MOBILEID",1);
		zmqCtrl = new ZMQCtrl(this,h,sdtwHandler);
		zmqCtrl.setPortAndHost(sExternPushPort,sExternPubPort,sExternHost,mobileID);

		//fallCnt = 0;
		
		WiFiScanner = new WiFiScanControl(RunActivity.this);
		mPSF = new PhoneSensorForwarder(RunActivity.this);
		
		registerReceiver(mWiFiUpdateReceiver, makeGattUpdateIntentFilter());
		
//		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//		mBluetoothAdapter = bluetoothManager.getAdapter();
//		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//			Intent enableBtIntent = new Intent(
//					BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//		}
//		mSensorTagRx = new sensorTagRx(RunActivity.this,mBluetoothManager,mBluetoothAdapter);
//		onInternalSensorSWClicked(true);
//		onWiFiSWClicked(true);
	}
	private final BroadcastReceiver mWiFiUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if(action.equals("com.Lei.FallDetection")){
//				TextView textView1 = (TextView)findViewById(R.id.fall_status);
//				textView1.setText(Boolean.toString(intent.getBooleanExtra("status", false))
//						+" "
//						+Double.toString(intent.getDoubleExtra("RawValue", 0.0f)));
			}
			if(action.equals("com.Lei.WiFiScan")){

				int numAP = intent.getIntExtra("numAP", 0);
				Long curTime = intent.getLongExtra("curTime", 0);
				String[]SSIDArray =intent.getStringArrayExtra("SSID");
				long[] MACArray=intent.getLongArrayExtra("MAC");
				int[] RSSIArray=intent.getIntArrayExtra("RSSI");
				
				//Toast.makeText(RunActivity.this, Integer.toString(numAP)+"@"+Long.toString(curTime)+"\n"+Arrays.toString(SSIDArray)+"\n"+Arrays.toString(MACArray)+"\n"+Arrays.toString(RSSIArray)+"\n", Toast.LENGTH_LONG).show();
				sb_wifi.append(Integer.toString(numAP)+"@"+Long.toString(curTime)+"\n"+Arrays.toString(SSIDArray)+"\n"+Arrays.toString(MACArray)+"\n"+Arrays.toString(RSSIArray)+"\n");
			}
			if(action.equals("com.Lei.PhoneMotion")){
				float array[] = intent.getFloatArrayExtra("sensingVal");
				float subArray1[] = Arrays.copyOfRange(array, 0, 3);
				float subArray2[] = Arrays.copyOfRange(array, 3, 6);
				float subArray3[] = Arrays.copyOfRange(array, 6, 9);
				float subArray4[] = Arrays.copyOfRange(array, 9, 12);


				Long curTime = intent.getLongExtra("curTime", 0);

//				Toast.makeText(RunActivity.this, Arrays.toString(subArray1)+"\n"+
//						Arrays.toString(subArray2)+"\n"+
//						Arrays.toString(subArray3)+"\n"+
//						Arrays.toString(subArray4)+"\n"+
//						"@"+Long.toString(curTime), Toast.LENGTH_LONG).show();
				sb_phone.append(Arrays.toString(subArray1)+"\n"+
						Arrays.toString(subArray2)+"\n"+
						Arrays.toString(subArray3)+"\n"+
						Arrays.toString(subArray4)+"\n"+
						"@"+Long.toString(curTime)+"\n");
				sb_phone2.append(subArray1[0]+", "+subArray1[1]+", "+subArray1[2]+", "+subArray2[0]+", "+subArray2[1]+", "+subArray2[2]+", "+Long.toString(curTime)+"\n");
			}
			if(action.equals("com.Lei.BTMotion")){
				float array[] = intent.getFloatArrayExtra("sensingVal");
				
				float subArray1[] = Arrays.copyOfRange(array, 0, 3);
				float subArray2[] = Arrays.copyOfRange(array, 3, 6);
				float subArray3[] = Arrays.copyOfRange(array, 6, 9);
				float subArray4[] = Arrays.copyOfRange(array, 9, 12);
				float refreshRate = intent.getFloatExtra("refreshRate", 22);
				


				Long curTime = intent.getLongExtra("curTime", 0);
				//textView1.setText(Arrays.toString(array)+"\n"+"@"+Long.toString(curTime));				
//				textView1.setText(Arrays.toString(subArray1)+"\n"+
//						Arrays.toString(subArray2)+"\n"+
//						Arrays.toString(subArray3)+"\n"+
//						Arrays.toString(subArray4)+"\n"+
//						"@"+Long.toString(curTime)+"@"+Float.toString(refreshRate)+"hz");

			}
		}
	};
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter fi = new IntentFilter();
		fi.addAction("com.Lei.WiFiScan");
		fi.addAction("com.Lei.PhoneMotion");
//		fi.addAction("com.Lei.BTMotion");
//		fi.addAction("com.Lei.FallDetection");
		return fi;
	}
	
	public void onZmqSWClicked(boolean on){
		//boolean on = ((Switch)view).isChecked();
		if(on){
			zmqCtrl.start(true,true);
		}else{
			zmqCtrl.stop();
		}
	}


	public void onWiFiSWClicked(boolean on){
//		boolean on = ((Switch) view).isChecked();
		if(on){
			WiFiScanner.startScan();
			}else{
			WiFiScanner.stopScan();
		}
	}
	public void onInternalSensorSWClicked(boolean on){
		//boolean on = ((Switch) view).isChecked();
		if(on){
			mPSF.start();
		}else{
			mPSF.stop();
		}
	}
//	public void onFallForwardBtn(View view){
//		fallCnt ++;
//		((TextView)findViewById(R.id.fall_cnt)).setText(Integer.toString(fallCnt));
//	}
	public void onBTSWClicked(View view){
		boolean on = ((Switch) view).isChecked();
		if(on){
			Intent k = new Intent(RunActivity.this,
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
	   void saveSDcar(String str,String filename,boolean add)
	   {   
		   //str= edit.getText().toString();
		   //判断SD卡是否允许读写操作
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				File path = Environment.getExternalStorageDirectory();
				File sdfile = new File(path, filename);
				try {
					//FileOutputStream f_out = new FileOutputStream(sdfile);
					//f_out.write(str.getBytes());
					FileWriter writer=new FileWriter(sdfile, add);
					writer.write(str);
					writer.close();
				     Toast.makeText(RunActivity.this, "文件保存到SD卡", Toast.LENGTH_LONG).show();
				}catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
	   }
	

}