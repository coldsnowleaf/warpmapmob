package lei.com.example.wilocclient;

import static lei.com.example.wilocclient.ConstantUtil.*;
import android.graphics.Canvas;				//���������
import android.graphics.Paint;
import android.graphics.RectF;

public class MyDrawable{
	int imgId;			//ͼƬID
	//������
	public MyDrawable(int imgId){
		this.imgId = imgId;
	}
	//���ղ�����������
	public void drawSelf(Canvas canvas,int row,int col,int offsetX,int offsetY){
		int topLeftCornerX = col*TILE_SIZE;						//���Ͻ�x����������½�x����
		int topLeftCornerY = row*TILE_SIZE;		//���Ͻ�y���������½�y�����ȥͼƬ�߶�
		BitmapManager.drawCurrentStage(imgId, canvas, topLeftCornerX-offsetX, topLeftCornerY-offsetY);
		
	}
}