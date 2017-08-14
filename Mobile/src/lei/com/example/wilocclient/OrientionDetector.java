package lei.com.example.wilocclient;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class OrientionDetector implements SensorEventListener {

	public static int CURRENT_ORIEN=0;
	public OrientionDetector(Context context) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
//		if((int)(event.values[0]/90)==0||(int)(event.values[0]/90)==4)
//			 KeyThread.key=1;
//		if((int)(event.values[0]/90)==2)
//			 KeyThread.key=2;
//		if((int)(event.values[0]/90)==1)
//			 KeyThread.key=8;
//		if((int)(event.values[0]/90)==3)
//			 KeyThread.key=4;
		KeyThread.arc=event.values[0];
	}
	
}