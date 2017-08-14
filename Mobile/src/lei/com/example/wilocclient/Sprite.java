package lei.com.example.wilocclient;			

import static lei.com.example.wilocclient.ConstantUtil.*;
import java.util.ArrayList;					//引入相关类
import android.graphics.Canvas;				//引入相关类

public class Sprite{
	int x;		//精灵在大地图的坐标
	int y;		//精灵在大地图的坐标
	int col;	//精灵在地图上的列数，通过求靠下31×31方块的中心所在的地方求出
	int row;	//精灵在地图上的行数，通过求靠下31×31方块的中心所在的地方求出
	SpriteThread st;
	int currentSegment;
	//构造器
	public Sprite(int col,int row){
		this.col = col;
		this.row = row;
		this.x = col * TILE_SIZE;								//x坐标对应于图片左上角
		this.y = row * TILE_SIZE+TILE_SIZE-SPRITE_HEIGHT;		//y坐标对应于图片左上角
		st = new SpriteThread(this);
	}

	public void drawSelf(Canvas canvas,int screenX,int screenY){
		BitmapManager.drawGameDot(canvas, screenX, screenY);		//调用BitmapManager的静态方法绘制图片
	}

}