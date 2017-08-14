package lei.com.example.wilocclient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

class FileHelper
{
	FileOutputStream f_out;
	Context context;
	void OpenFileOut(Context context)
	{
		 try {
			 this.context=context;
			 f_out= context.openFileOutput("log.txt", Context.MODE_PRIVATE);
	        } catch (FileNotFoundException e1) {
	     	   e1.printStackTrace();
	        }
	}
	 void saveFile(String str) 
	 {
	       try {
			f_out.write(str.getBytes());
	       } catch (IOException e) {e.printStackTrace();}
	 }
	 void closeFile()
	 {
		 try {
	        	f_out.flush();
				f_out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }
}