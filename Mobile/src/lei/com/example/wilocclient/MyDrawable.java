package lei.com.example.wilocclient;

import static lei.com.example.wilocclient.ConstantUtil.*;
import android.graphics.Canvas;				//引入相关类
import android.graphics.Paint;
import android.graphics.RectF;

public class MyDrawable{
	int imgId;			//图片ID
	//构造器
	public MyDrawable(int imgId){
		this.imgId = imgId;
	}
	//接收参数绘制自身
	public void drawSelf(Canvas canvas,int row,int col,int offsetX,int offsetY){
		int topLeftCornerX = col*TILE_SIZE;						//左上角x坐标就是左下角x坐标
		int topLeftCornerY = row*TILE_SIZE;		//左上角y坐标是左下角y坐标减去图片高度
		BitmapManager.drawCurrentStage(imgId, canvas, topLeftCornerX-offsetX, topLeftCornerY-offsetY);
		
	}
}