package lei.com.example.wilocclient;

import static lei.com.example.wilocclient.ConstantUtil.*;	
import android.content.res.Resources;		
import android.graphics.Bitmap;				
import android.graphics.BitmapFactory;		
import android.graphics.Canvas;				
import android.graphics.Matrix;
import android.graphics.Paint;				
import android.graphics.RectF;

public class BitmapManager{

	private static Bitmap gameDot;
	
	private BitmapManager(){}


	//方法：根据图片ID绘制游戏中的公共图片
	public static void drawGameDot(Canvas canvas,int x,int y){
		Paint paint = new Paint();	
		paint.setARGB(0, 0, 255, 255);
		canvas.drawOval(
				new RectF(
					x, 
					y, 
					x+SPRITE_WIDTH,
					y+SPRITE_HEIGHT
				), paint);
	}
	//方法：根据图片ID绘制关卡图片
	public static void drawCurrentStage(int imgId,Canvas canvas,int x,int y){
//		Paint paint = new Paint();	
//		if(imgId==0)
//			paint.setARGB(128, 128, 128, 128);
//		else
//			paint.setARGB(128, 0, 0, 0);
//		canvas.drawRect(
//				x,
//				y,
//				x+TILE_SIZE,
//				y+TILE_SIZE,
//				paint);
	}


}