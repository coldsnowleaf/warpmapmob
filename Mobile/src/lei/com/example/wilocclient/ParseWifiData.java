package lei.com.example.wilocclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class ParseWifiData {
	Context context;
	float tSegRss[][][]=new float[100][2][100];
	public ParseWifiData(Context context)
	{
		this.context=context;
	}
	public void readWifiData()
	{
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			File path = Environment.getExternalStorageDirectory();
			File sdfile = new File(path, "path-train.txt");
			char buffer[]=new char[4096];
			try {
				FileReader reader=new FileReader(sdfile);
				reader.read(buffer);
				parseArray(new String(buffer));
				reader.close();
			    Toast.makeText(context, new String(buffer), Toast.LENGTH_LONG).show();
			}catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
	void parseArray(String string)
	{
		String []pathline=string.split("\n");
		int pathNum=pathline.length;
		for(int i=0;i<pathNum;i++)
		{
			String []points=pathline[i].split(",");
			for(int j=0;j<points.length/2;j++)			
			{
				float xs=Float.parseFloat(points[2*j].substring(1, points[2*j].length()));
				float ys=Float.parseFloat(points[2*j+1].substring(0, points[2*j+1].length()-1));
				tSegRss[i][1][j]=xs;
				tSegRss[i][2][j]=ys;
				//System.out.println("xsys"+points[2*j].substring(1, points[2*j].length())+" "+points[2*j+1].substring(0, points[2*j+1].length()-1));
			}
		}
	}
//	class tSegRss{
//		int no;
//		float path[][];
//		tSegRss(int pointNum)
//		{
//			path=new float[2][pointNum];
//		}
//	}
	
}