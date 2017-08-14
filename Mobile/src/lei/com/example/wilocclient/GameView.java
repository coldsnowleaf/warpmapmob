package lei.com.example.wilocclient;

import java.util.HashMap;

import static lei.com.example.wilocclient.ConstantUtil.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements SurfaceHolder.Callback{
	RunActivity father;		//Activity对象引用
	DrawThread dt;			//DrawThread对象引用
	KeyThread kt;			//KeyThread对象引用
	Sprite hero;				//英雄对象引用
	

	Layer layer;		//存放当前关卡的地图矩阵
	int [][] currNotIn;		//存放当前关卡的不可通过矩阵
	int [] heroLocation;	//存放初始的英雄的位置，列在前行在后
	int startRow = 0;			//屏幕左上角在大地图中的行数
	int startCol = 0;			//屏幕左上角在大地图中的行数
	int offsetX = 0;			//屏幕左上角相对于startCol的偏移量
	int offsetY = 0;			//屏幕左上角相对于startRow的偏移量
	
	int path[][][]=new int[100][100][2];
	int pathI=-1;
	int sumPathI=0;
	int rollPathI;
	boolean showMiniMap;
	Bitmap floor;

	//int startsetY=200;
	int sdtwPoint_x=100;
	int sdtwPoint_y=100;
	
	public GameView(Context context,AttributeSet paramAttributeSet) {
		super(context,paramAttributeSet);
		this.father = (RunActivity) context;
		initStageData();					//初始化关卡数据
		getHolder().addCallback(this);				//添加Callback接口
		dt = new DrawThread(this, getHolder());		//创建DrawThreaed对象
		kt = new KeyThread(this);					//创建KeyThread对象
		floor = BitmapFactory.decodeResource(getResources(), R.drawable.henlong);
		//floor = BitmapFactory.decodeResource(getResources(), R.drawable.ruc);
		Matrix matrix = new Matrix(); //接收图片之后放大 1.5倍
		matrix.postScale(1.0f, 1.0f);
		floor= Bitmap.createBitmap(floor, 0, 0, floor.getWidth(),
                  floor.getHeight(), matrix, true);
	}

	public void initStageData(){
		//BitmapManager.loadCurrentStage(getResources());
		layer = new Layer(GameData.notInData);
		currNotIn = layer.getTotalNotInMatrix();
		heroLocation = GameData.getHeroLocation();
		hero = new Sprite(heroLocation[0], heroLocation[1]);		
		startRow = 0;					//将startRow置零
		startCol = 0;					//将startCol置零
		offsetX = 0;					//将offsetX置零
		offsetY = 0;					//将offsetY置零
	}	
	//屏幕绘制方法
	public void doDraw(Canvas canvas){
		int heroX = hero.x;				//获取英雄X坐标
		int heroY = hero.y;				//获取英雄Y坐标
		int hRow = (heroY+SPRITE_HEIGHT-1) / TILE_SIZE ;//求出英雄右下角在大地图上的行和列
		int hCol = (heroX+SPRITE_WIDTH-1) / TILE_SIZE;//求出英雄右下角在大地图上的行列
		int tempStartRow = this.startRow;	//获取绘制起始行
		int tempStartCol = this.startCol;	//获取绘制起始列
		int tempOffsetX = this.offsetX;		//获取相对于tempStartRow的偏移量
		int tempOffsetY = this.offsetY;		//获取相对于tempStartCol的偏移量
		canvas.drawColor(Color.WHITE);		//清屏幕
		canvas.drawBitmap(floor, - tempStartCol*TILE_SIZE - tempOffsetX, - tempStartRow*TILE_SIZE - tempOffsetY, null);
		for(int i=-1; i<=SCREEN_ROWS; i++){     
			if(tempStartRow+i < 0 || tempStartRow+i>=MAP_ROWS){//如果多画的那一行不存在，就继续
				continue;		//进行下一轮循环
			}
			for(int j=-1; j<=SCREEN_COLS; j++){
				if(tempStartCol+j <0 || tempStartCol+j>=MAP_COLS){//如果多画的那一列不存在，就继续
					continue;		//进行下一轮循环
				}
				
					//if(layer.mapMatrix[tempStartRow+i][tempStartCol+j] != null){
					//	layer.mapMatrix[tempStartRow+i][tempStartCol+j].drawSelf(canvas, i, j, tempOffsetX, tempOffsetY);
					//}
				
				
				//检查是否需要绘制英雄
				if(hRow - tempStartRow == i && hCol-tempStartCol == j){		//英雄的右下角点位于此地图块
					int screenX = heroX - tempStartCol*TILE_SIZE - tempOffsetX;
					int screenY = heroY - tempStartRow*TILE_SIZE - tempOffsetY;
					hero.drawSelf(canvas, screenX, screenY);
				}
			}
		}
		
		Paint paint1 = new Paint();	
		paint1.setARGB(255, 225, 0, 0);
		paint1.setStrokeWidth(5);
		paint1.setAntiAlias(true);

		canvas.drawCircle(sdtwPoint_x- tempStartCol*TILE_SIZE - tempOffsetX, sdtwPoint_y- tempStartRow*TILE_SIZE - tempOffsetY, 10, paint1);
		
		Paint paint = new Paint();	
		paint.setARGB(128, 225, 0, 255);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);
		
		for(int j=0;j<=sumPathI;j++){
			if(j==sumPathI){
				for(int i=1;i<=pathI;i++)
					canvas.drawLine(path[j][i-1][0]- tempStartCol*TILE_SIZE - tempOffsetX,path[j][i-1][1]- tempStartRow*TILE_SIZE - tempOffsetY, path[j][i][0]- tempStartCol*TILE_SIZE - tempOffsetX, path[j][i][1] - tempStartRow*TILE_SIZE - tempOffsetY, paint);
			}
			else{
				for(int i=1;i<100;i++)
					if(path[j][i][0]!=0&&path[j][i][1]!=0){
						canvas.drawLine(path[j][i-1][0]- tempStartCol*TILE_SIZE - tempOffsetX,path[j][i-1][1]- tempStartRow*TILE_SIZE - tempOffsetY, path[j][i][0]- tempStartCol*TILE_SIZE - tempOffsetX, path[j][i][1] - tempStartRow*TILE_SIZE - tempOffsetY, paint);
					}
			}
					
		
		}
		canvas.drawOval(
		new RectF(
				path[sumPathI][pathI][0]-10- tempStartCol*TILE_SIZE - tempOffsetX,
				path[sumPathI][pathI][1]-10- tempStartRow*TILE_SIZE - tempOffsetY,
				path[sumPathI][pathI][0]+10- tempStartCol*TILE_SIZE - tempOffsetX,
				path[sumPathI][pathI][1]+10- tempStartRow*TILE_SIZE - tempOffsetY
			
		), paint);
		
		
	}
	

	public void startGame(){
		kt.isGameOn = true;	//恢复游戏进行
		if(!kt.isAlive()){
			kt.start();		//启动键盘线程
		}
	}
		

	public void pauseGame(){
		kt.isGameOn = false;		//暂停键盘线程
		
	}

	public void stopGame(){
		kt.flag = false;
		kt.isGameOn = false;
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {//重写接口方法
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {//重写surfaceCreated方法
		dt.isViewOn = true;//恢复游戏进行
		if(! dt.isAlive()){//如果没启动就启动
			dt.start();		//启动刷屏线程
		}
		startGame();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {//重写surfaceDestroyed方法
		dt.flag = false;
		dt.isViewOn = false;
	}
	
		public boolean myTouchEvent(int x,int y){//屏幕x,y
			int row = 0;	
			int col = 0;
			int realX=x + startCol*TILE_SIZE + offsetX;
			//int realY=y + startRow*TILE_SIZE + offsetY-startsetY;
			int realY=y + startRow*TILE_SIZE + offsetY-ConstantUtil.STARTSETY;
			row=realY/TILE_SIZE;
			col=realX/TILE_SIZE;
			//if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&currNotIn[row][col]==0){//可以标记
				pathI++;
				path[sumPathI][pathI][0]=realX;
				path[sumPathI][pathI][1]=realY;
				return true;
			//}
			//return true;
		}
		
		public void checkIfRollScreen(int direction){
			
			int hx = hero.x;
			int hy = hero.y;
			switch(direction){
			case 1:				//只向上检查滚屏与否
				//if(hy - gv.startRow*TILE_SIZE - gv.offsetY <= SPACE_FOR_ROLL){//检查是否需要左滚屏
					if(startRow >0){//startRow 还能借
						offsetY -= SPAN_TO_ROLL;
						if(offsetY < 0){
							startRow -=1;;
							offsetY += TILE_SIZE;	
						}
					}
					else if(offsetY >= SPAN_TO_ROLL){//格子数不够减了，但是偏移量还有
						offsetY -= SPAN_TO_ROLL;
					}
				//}
				break;
			case 2:				//只向下检查滚屏与否
				//if(hy - gv.startRow*TILE_SIZE -gv.offsetY + SPACE_FOR_ROLL >= SCREEN_HEIGHT){//检查是否需要下滚
					if(startRow + SCREEN_ROWS <= MAP_ROWS ){//可以接受进位就加
						offsetY += SPAN_TO_ROLL;
						if(offsetY > TILE_SIZE){//需要进位
							startRow += 1;
							offsetY -=TILE_SIZE;
						}
					}
				//}
				break;
			case 4:				//只向左检查滚屏与否
				//if(hx - gv.startCol*TILE_SIZE - gv.offsetX <= SPACE_FOR_ROLL){//检查是否需要左滚屏
					if(startCol > 0){//startCol还够减
						offsetX -= SPAN_TO_ROLL;//向左偏移英雄步进的像素数
						if(offsetX < 0){					
							startCol -=1;//
							offsetX += TILE_SIZE;//有待商议		
						}
					}
					else if(offsetX >= SPAN_TO_ROLL){//如果格子数不够减，但是偏移量还有
						offsetX -= SPAN_TO_ROLL;//向左偏移英雄步进的像素数
					}
				//}
				break;
			case 8:				//只向右检查滚屏与否
				//if(hx - gv.startCol*TILE_SIZE - gv.offsetX + SPACE_FOR_ROLL >= SCREEN_WIDTH){//检查是否需要右滚屏
					if(startCol + SCREEN_COLS <= MAP_COLS ){//startCol还能加
						offsetX += SPAN_TO_ROLL;//向右偏移英雄步进的像素数
						if(offsetX > TILE_SIZE){//需要进位
							startCol += 1;
							offsetX -= TILE_SIZE;
						}
					}
				//}
				break;
			}
		}

}