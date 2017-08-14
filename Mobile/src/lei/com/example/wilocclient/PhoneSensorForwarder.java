package lei.com.example.wilocclient;

import java.util.List;

import org.zeromq.ZMQ;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class PhoneSensorForwarder {
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Sensor gSensor;
	private Sensor magSensor;
	private Sensor oriSensor;
	private float[] sensingVal ={ 0,0,0,0,0,0,0,0,0,0,0,0};
	private boolean getAcc;
	private boolean getGyro;
	private boolean getMag;
	private boolean getOri;
	private Context ctx;
	
	
	public PhoneSensorForwarder(Context context){
		mSensorManager =  (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor =  mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		oriSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		ctx = context;
		
		List<Sensor> gyro =mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
		Log.d("zmq","number of gyro = "+gyro.size());
		for(int i=0;i<gyro.size();i++) { 
		Log.d("zmq","The vendor is: " +gyro.get(i).getVendor() + " **" +gyro.get(i).getVersion());
		}
		
//		Log.d("zmq","mSensor = "+mSensor.getName());
//		Log.d("zmq","magSensor = "+magSensor.getName());
//		Log.d("zmq","gSensor = "+gSensor.getName());
	}
	public void start(){
		mSensorManager.registerListener(AccSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(GyroSensorEventListener,gSensor, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(MagSensorEventListener,magSensor, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(OriSensorEventListener,oriSensor,SensorManager.SENSOR_DELAY_FASTEST);
		
		//mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_FASTEST);
		getAcc = false;
		getGyro = false;
		getMag = false;
		getOri = false;
	}
	public void stop(){
		mSensorManager.unregisterListener(AccSensorEventListener, mSensor);
		mSensorManager.unregisterListener(GyroSensorEventListener,gSensor);
		mSensorManager.unregisterListener(MagSensorEventListener,magSensor);
		mSensorManager.unregisterListener(OriSensorEventListener,oriSensor);
		getAcc = false;
		getGyro = false;
		getMag = false;
		getOri = false;
	}
	
	private SensorEventListener AccSensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
				return;
			}
			getAcc = true;
			sensingVal[0]=event.values[0];
			sensingVal[1]=event.values[1];
			sensingVal[2]=event.values[2];
			//Log.d("zmq","ACC event captured");
			if((gSensor == null | getGyro == true)&&(mSensor == null|getAcc == true)&&(magSensor == null|getMag == true)&&(oriSensor == null|getOri == true)){
				sendMsg();   
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub			
		}
	};
	
	private SensorEventListener OriSensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if(event.sensor.getType() != Sensor.TYPE_ORIENTATION){
				return;
			}
			getOri = true;
			sensingVal[9]=event.values[0];
			sensingVal[10]=event.values[1];
			sensingVal[11]=event.values[2];
			//Log.d("zmq","ACC event captured");
			if((gSensor == null | getGyro == true)&&(mSensor == null|getAcc == true)&&(magSensor == null|getMag == true)&&(oriSensor == null|getOri == true)){
				sendMsg();
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub			
		}
	};
	
	private SensorEventListener GyroSensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if(event.sensor.getType() != Sensor.TYPE_GYROSCOPE){
				return;
			}
			getGyro = true;
			sensingVal[3]=event.values[0];
			sensingVal[4]=event.values[1];
			sensingVal[5]=event.values[2];
			//Log.d("zmq","Gyro event captured");
			if((gSensor == null | getGyro == true)&&(mSensor == null|getAcc == true)&&(magSensor == null|getMag == true)&&(oriSensor == null|getOri == true)){
				sendMsg();
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub			
		}
	};
	private SensorEventListener MagSensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if(event.sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD){
				return;
			}
			getMag = true;
			sensingVal[6]=event.values[0];
			sensingVal[7]=event.values[1];
			sensingVal[8]=event.values[2];
			//Log.d("zmq","Gyro event captured");
			if((gSensor == null | getGyro == true)&&(mSensor == null|getAcc == true)&&(magSensor == null|getMag == true)&&(oriSensor == null|getOri == true)){
				sendMsg();
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub			
		}
	};
	private void sendMsg(){
	//	Bundle bundle = new Bundle();
	//	bundle.putInt("order", msgTypeClass.valueOf("Motion").ordinal());
		Intent msg = new Intent();
		msg.setAction("com.Lei.PhoneMotion");
		msg.putExtra("Type","10002");
		msg.putExtra("curTime", System.currentTimeMillis());
		msg.putExtra("sensingVal",sensingVal);
		getAcc = false;
		getGyro = false;
		getMag = false;
		ctx.sendBroadcast(msg);
		Log.d("zmq","Sensor event captured");
	}

}
