package lei.com.example.wilocclient;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceScanActivity extends Activity {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    String[] values={"hello","bye"};
    private final ArrayList<String> macList = new ArrayList<String>();
    private final ArrayList<String> nameList = new ArrayList<String>();
    private final ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String mMac = new String();
    private String mName = new String();
    private Context thisContext = this;
    final int REQUEST_ENABLE_BT = 1;
     
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.device_scan);
    	final ListView listview = (ListView)findViewById(R.id.listview);
		TextView BTnameTextView = (TextView) findViewById(R.id.BTname);
		BTnameTextView.setText("no device selected");

    	bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    	mBluetoothAdapter = bluetoothManager.getAdapter();
    	
    	/*for(int i = 0; i < values.length;++i){
    		list.add(values[i]);
    	}*/
    	mHandler = new Handler();
    	final  Button button = (Button) findViewById(R.id.button2);
    	button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adapter.clear();
    			TextView BTnameTextView = (TextView) findViewById(R.id.BTname);
    			BTnameTextView.setText("no device selected");
				scanLeDevice(true);
			}
		});
    	final Button button_connect = (Button) findViewById(R.id.button_connect);
    	button_connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mMac.isEmpty()) {
				      Toast.makeText(thisContext,"no device selected",
				    		  Toast.LENGTH_SHORT).show();
				} else {
					finish();
				}
			}
		});
    	adapter =  new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
    	listview.setAdapter(adapter);
    	listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		@Override
    		public void onItemClick(AdapterView<?> parent,final View view,int position, long id){
    			final String item = (String)parent.getItemAtPosition(position);
    			TextView BTnameTextView = (TextView) findViewById(R.id.BTname);
    		//	BTnameTextView.setText(item);
    			BTnameTextView.setText(nameList.get(position));
    			mName = nameList.get(position);
    			mMac = macList.get(position);

    			//adapter.remove(item);
    			//adapter.notifyDataSetChanged();
    			/*
    			view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable(){
    				@Override
    				public void run(){
    					list.remove(item);
    					adapter.notifyDataSetChanged();
    					view.setAlpha(1);
    				}
    			});
    			*/
    		}
		});
    	
    	//init bluetooth
    	if(mBluetoothAdapter == null || mBluetoothAdapter.isEnabled()){
    		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }
    
    private void scanLeDevice(final boolean enable){
    	if(enable){
    		mHandler.postDelayed(new Runnable(){
    			@Override
    			public void run(){
    				mScanning = false;
    				mBluetoothAdapter.stopLeScan(mLeScanCallback);
    			}		
    		},SCAN_PERIOD);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
    	}else{
    		mScanning = false;
    		mBluetoothAdapter.stopLeScan(mLeScanCallback);
    	}
    }
    
    private BluetoothAdapter.LeScanCallback mLeScanCallback = 
    		new BluetoothAdapter.LeScanCallback() {
				
				@Override
				public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable(){
						@Override 
						public void run(){
							boolean needUpdate = false;
							Log.d("btname",device.getAddress()+" "+device.getName());
							if(!macList.contains(device.getAddress())){
								macList.add(device.getAddress());
								nameList.add(device.getName());
								needUpdate = true;
							}
							
							if(device.getName()!= null){
								int idx = macList.indexOf(device.getAddress());
								if(idx != 0){
									nameList.set(idx, device.getName());
									needUpdate = true;
								}
							}
							
							if(needUpdate){
								list.clear();
								for(int i=0;i<macList.size();i++){
									list.add(macList.get(i)+" "+nameList.get(i));
								}
								adapter.notifyDataSetChanged();
								needUpdate = false;
							}
						}
					});
				}
			};
			 
	@Override
	public void finish() {
		// Prepare data intent
		Intent data = new Intent();
		data.putExtra("mac", mMac);
		data.putExtra("name", mName);
		// Activity finished ok, return the data
		setResult(RESULT_OK, data);
		super.finish();
	}
}