package lei.com.example.wilocclient;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;











import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class sensorTagRx {

	private String mBTMac = new String();
	private BluetoothGatt mBluetoothGatt = null;
	private BluetoothDevice mBluetoothDevice;
	BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	 
	static final byte ENABLE_SENSOR_CODE = 1;
	private BluetoothGattService AccEnService;
	private static final int GATT_TIMEOUT = 250; // milliseconds
    public final static String NOTIFY_UUID =
            "00002902-0000-1000-8000-00805f9b34fb";
    public final static byte[] ENABLE_NOTIFICATION_VALUE =
            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
	private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");
	private TextView mAccValue;
	private TextView mMagValue;
	private TextView mLuxValue;
	private TextView mGyrValue;
	private TextView mObjValue;
	private TextView mAmbValue;
	private TextView mHumValue;
	private TextView mBarValue;
	private Context ctx;
	private Integer readCnt;
	private Long lastUpdateAt;
	private float refreshRate = 0;

	public sensorTagRx(Context context, BluetoothManager sBluetoothManager, BluetoothAdapter sBluetoothAdapter){
		BluetoothManager bluetoothManager = sBluetoothManager;
		mBluetoothAdapter = sBluetoothAdapter;
		ctx = context;
		readCnt = 0;
		refreshRate = 0;
	}

	protected BluetoothGattCallback GATTcb = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mBluetoothGatt.discoverServices();
				Log.i("BTMsg","connected");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
		
			}
		}

		@Override
		// New services discovered
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i("BTMsg", "Service discovered: " + status);
				List<BluetoothGattService> services = gatt.getServices();
				for (BluetoothGattService service : services) {
					Log.i("BTMsg", "Service :" + service.getUuid().toString());
					List<BluetoothGattCharacteristic> characteristics = service
							.getCharacteristics();
				}
				Log.i("BTMsg", "Service discovery finished");
				BluetoothGattService service = gatt
						.getService(SensorTagGatt.UUID_ACC_SERV);
				if (service == null) {
					Log.e("BTMsg", "Get acc service failed");
				} else {
					Log.i("BTMsg", "Get service" + service.toString());
				}
				BluetoothGattCharacteristic ACCConfCharacterize = service
						.getCharacteristic(SensorTagGatt.UUID_ACC_CONF);
				byte[] val = new byte[1];
				val[0] = ENABLE_SENSOR_CODE;
			//	val[0] = 0;
				ACCConfCharacterize.setValue(val);
				if (gatt.writeCharacteristic(ACCConfCharacterize) == true) {
					Log.i("BTMsgi", "Enabling acc successfully:");
				} else {
					Log.e("BTMsg", "Enabling acc fail");
				}
				
			} else {
				Log.i("BTMsg", "onServicesDiscovered received: " + status);
			}
		}

		@Override
		// Result of a characteristic read operation
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i("BTMsg", "Got characterize ");
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.i("BTMsg", "Characterize changed");
			String uuidStr = characteristic.getUuid().toString();
			byte rawValue[] = characteristic.getValue();
			Point3D v;
			String msg;

			if (uuidStr.equals(SensorTagGatt.UUID_ACC_DATA.toString())) {
				v = Sensor.ACCELEROMETER.convert(rawValue);
				msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
				    + decimal.format(v.z) + "\n";
				
				Intent sendBuf = new Intent();
				float sensingVal[] = {0,0,0,0,0,0,0,0,0};
				//float rawSensingVal[] = ByteBuffer.wrap(rawValue).asFloatBuffer().array();
				sensingVal[0]=(float) v.x;
				sensingVal[1]=(float) v.y;
				sensingVal[2]=(float) v.z;
				
				Integer lowerByte, upperByte;
				//= (int) c[offset] & 0xFF; 
				//Integer upperByte = (int) c[offset+1];
				upperByte = (int) rawValue[7] ;
				lowerByte = (int) rawValue[6] & 0xff ;
				sensingVal[3]= shortSignedAtOffset( rawValue,6) *(2000f / 32768f) ;
				sensingVal[4]= shortSignedAtOffset( rawValue,8) *(2000f / 32768f) ;
				sensingVal[5]= shortSignedAtOffset( rawValue,10) *(2000f / 32768f) ;
				
				Log.i("zmq","raw data length = "+rawValue.length);
				sendBuf.setAction("com.Lei.BTMotion");
				sendBuf.putExtra("Type","10003");
				sendBuf.putExtra("curTime", System.currentTimeMillis());
				
				if(readCnt == 0){
					lastUpdateAt = System.currentTimeMillis();
				}
				if(readCnt == 1000){
					Long curTime = System.currentTimeMillis();
					Log.w("BTMsg", "IMU refreshing rate ="+Float.toString(1000.0f*1000f/(curTime -lastUpdateAt)));
					refreshRate = 1000.0f*1000.0f/(curTime -lastUpdateAt);
					lastUpdateAt = System.currentTimeMillis();
					readCnt = 0;
				}
				readCnt++;
				sendBuf.putExtra("sensingVal",sensingVal);
				sendBuf.putExtra("refreshRate",refreshRate);
				ctx.sendBroadcast(sendBuf);
				Log.d("zmq","Sensor event captured");
				Log.i("BTMsg", "got ACC"+msg);
			}

			if (uuidStr.equals(SensorTagGatt.UUID_MAG_DATA.toString())) {
				v = Sensor.MAGNETOMETER.convert(rawValue);
				msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
				    + decimal.format(v.z) + "\n";
			}

			if (uuidStr.equals(SensorTagGatt.UUID_OPT_DATA.toString())) {
				v = Sensor.LUXOMETER.convert(rawValue);
				msg = decimal.format(v.x) + "\n";
			}

			if (uuidStr.equals(SensorTagGatt.UUID_GYR_DATA.toString())) {
				v = Sensor.GYROSCOPE.convert(rawValue);
				msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
				    + decimal.format(v.z) + "\n";
				mGyrValue.setText(msg);
			}

			if (uuidStr.equals(SensorTagGatt.UUID_IRT_DATA.toString())) {
				v = Sensor.IR_TEMPERATURE.convert(rawValue);
				msg = decimal.format(v.x) + "\n";
				mAmbValue.setText(msg);
				msg = decimal.format(v.y) + "\n";
				mObjValue.setText(msg);
			}

			if (uuidStr.equals(SensorTagGatt.UUID_HUM_DATA.toString())) {
				v = Sensor.HUMIDITY.convert(rawValue);
				msg = decimal.format(v.x) + "\n";
				mHumValue.setText(msg);
			}

			if (uuidStr.equals(SensorTagGatt.UUID_BAR_DATA.toString())) {
				v = Sensor.BAROMETER.convert(rawValue);
				double PA_PER_METER = 4;
				double h = (v.x - BarometerCalibrationCoefficients.INSTANCE.heightCalibration)
				    / PA_PER_METER;
				h = (double) Math.round(-h * 10.0) / 10.0;
				msg = decimal.format(v.x / 100.0f) + "\n" + h;
				mBarValue.setText(msg);
			}

			if (uuidStr.equals(SensorTagGatt.UUID_KEY_DATA.toString())) {
				int keys = rawValue[0];
				SimpleKeysStatus s;
				final int imgBtn;
				s = Sensor.SIMPLE_KEYS.convertKeys((byte) (keys&3));

				/*
				switch (s) {
				case OFF_ON:
					imgBtn = R.drawable.buttonsoffon;
					setBusy(true);
					break;
				case ON_OFF:
					imgBtn = R.drawable.buttonsonoff;
					setBusy(true);
					break;
				case ON_ON:
					imgBtn = R.drawable.buttonsonon;
					break;
				default:
					imgBtn = R.drawable.buttonsoffoff;
					setBusy(false);
					break;
				}

				mButton.setImageResource(imgBtn);

				if (mIsSensorTag2) {
					// Only applicable for SensorTag2
					final int imgRelay;

					if ((keys&4) == 4) {
						imgRelay = R.drawable.reed_open;
					} else {
						imgRelay = R.drawable.reed_closed;
					}
					mRelay.setImageResource(imgRelay);
				}*/
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i("BTMsg","Characterize write status:"+ Integer.toOctalString(status));
			
					
			BluetoothGattService service = gatt
					.getService(SensorTagGatt.UUID_ACC_SERV);
			if (service != null) {
				BluetoothGattCharacteristic charac = service.getCharacteristic(SensorTagGatt.UUID_ACC_DATA);
				if (gatt.setCharacteristicNotification(charac, true)) {					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.i("BTMsg", "set ACC_data character notificaiton success");
					BluetoothGattDescriptor descriptor =
							charac.getDescriptor(UUID.fromString(NOTIFY_UUID));
				        descriptor.setValue(ENABLE_NOTIFICATION_VALUE);
				        gatt.writeDescriptor(descriptor);
				} else {
				}
			}
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i("BTMsg", "gescribor read " + Integer.toOctalString(status));
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i("BTMsg", "descriptor write:" + Integer.toOctalString(status));
			// Log.i(TAG, "onDescriptorWrite: " +
			// descriptor.getUuid().toString());
		}
		
		
	};

	public void connectTo(String sBTMac) {
		mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(sBTMac);
		mBluetoothGatt = mBluetoothDevice.connectGatt(ctx, true,GATTcb);
		Log.i("BTMsg","connect to "+sBTMac);
	}

	  private static Integer shortSignedAtOffset(byte[] c, int offset) {
		    Integer lowerByte = (int) c[offset] & 0xFF; 
		    Integer upperByte = (int) c[offset+1]; // // Interpret MSB as signed
		    return (upperByte << 8) + lowerByte;
		  }


}
