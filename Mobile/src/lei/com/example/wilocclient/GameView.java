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
	RunActivity father;		//Activity��������
	DrawThread dt;			//DrawThread��������
	KeyThread kt;			//KeyThread��������
	Sprite hero;				//Ӣ�۶�������
	

	Layer layer;		//��ŵ�ǰ�ؿ��ĵ�ͼ����
	int [][] currNotIn;		//��ŵ�ǰ�ؿ��Ĳ���ͨ������
	int [] heroLocation;	//��ų�ʼ��Ӣ�۵�λ�ã�����ǰ���ں�
	int startRow = 0;			//��Ļ���Ͻ��ڴ��ͼ�е�����
	int startCol = 0;			//��Ļ���Ͻ��ڴ��ͼ�е�����
	int offsetX = 0;			//��Ļ���Ͻ������startCol��ƫ����
	int offsetY = 0;			//��Ļ���Ͻ������startRow��ƫ����
	
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
		initStageData();					//��ʼ���ؿ�����
		getHolder().addCallback(this);				//���Callback�ӿ�
		dt = new DrawThread(this, getHolder());		//����DrawThreaed����
		kt = new KeyThread(this);					//����KeyThread����
		floor = BitmapFactory.decodeResource(getResources(), R.drawable.henlong);
		//floor = BitmapFactory.decodeResource(getResources(), R.drawable.ruc);
		Matrix matrix = new Matrix(); //����ͼƬ֮��Ŵ� 1.5��
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
		startRow = 0;					//��startRow����
		startCol = 0;					//��startCol����
		offsetX = 0;					//��offsetX����
		offsetY = 0;					//��offsetY����
	}	
	//��Ļ���Ʒ���
	public void doDraw(Canvas canvas){
		int heroX = hero.x;				//��ȡӢ��X����
		int heroY = hero.y;				//��ȡӢ��Y����
		int hRow = (heroY+SPRITE_HEIGHT-1) / TILE_SIZE ;//���Ӣ�����½��ڴ��ͼ�ϵ��к���
		int hCol = (heroX+SPRITE_WIDTH-1) / TILE_SIZE;//���Ӣ�����½��ڴ��ͼ�ϵ�����
		int tempStartRow = this.startRow;	//��ȡ������ʼ��
		int tempStartCol = this.startCol;	//��ȡ������ʼ��
		int tempOffsetX = this.offsetX;		//��ȡ�����tempStartRow��ƫ����
		int tempOffsetY = this.offsetY;		//��ȡ�����tempStartCol��ƫ����
		canvas.drawColor(Color.WHITE);		//����Ļ
		canvas.drawBitmap(floor, - tempStartCol*TILE_SIZE - tempOffsetX, - tempStartRow*TILE_SIZE - tempOffsetY, null);
		for(int i=-1; i<=SCREEN_ROWS; i++){     
			if(tempStartRow+i < 0 || tempStartRow+i>=MAP_ROWS){//����໭����һ�в����ڣ��ͼ���
				continue;		//������һ��ѭ��
			}
			for(int j=-1; j<=SCREEN_COLS; j++){
				if(tempStartCol+j <0 || tempStartCol+j>=MAP_COLS){//����໭����һ�в����ڣ��ͼ���
					continue;		//������һ��ѭ��
				}
				
					//if(layer.mapMatrix[tempStartRow+i][tempStartCol+j] != null){
					//	layer.mapMatrix[tempStartRow+i][tempStartCol+j].drawSelf(canvas, i, j, tempOffsetX, tempOffsetY);
					//}
				
				
				//����Ƿ���Ҫ����Ӣ��
				if(hRow - tempStartRow == i && hCol-tempStartCol == j){		//Ӣ�۵����½ǵ�λ�ڴ˵�ͼ��
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
		kt.isGameOn = true;	//�ָ���Ϸ����
		if(!kt.isAlive()){
			kt.start();		//���������߳�
		}
	}
		

	public void pauseGame(){
		kt.isGameOn = false;		//��ͣ�����߳�
		
	}

	public void stopGame(){
		kt.flag = false;
		kt.isGameOn = false;
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {//��д�ӿڷ���
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {//��дsurfaceCreated����
		dt.isViewOn = true;//�ָ���Ϸ����
		if(! dt.isAlive()){//���û����������
			dt.start();		//����ˢ���߳�
		}
		startGame();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {//��дsurfaceDestroyed����
		dt.flag = false;
		dt.isViewOn = false;
	}
	
		public boolean myTouchEvent(int x,int y){//��Ļx,y
			int row = 0;	
			int col = 0;
			int realX=x + startCol*TILE_SIZE + offsetX;
			//int realY=y + startRow*TILE_SIZE + offsetY-startsetY;
			int realY=y + startRow*TILE_SIZE + offsetY-ConstantUtil.STARTSETY;
			row=realY/TILE_SIZE;
			col=realX/TILE_SIZE;
			//if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&currNotIn[row][col]==0){//���Ա��
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
			case 1:				//ֻ���ϼ��������
				//if(hy - gv.startRow*TILE_SIZE - gv.offsetY <= SPACE_FOR_ROLL){//����Ƿ���Ҫ�����
					if(startRow >0){//startRow ���ܽ�
						offsetY -= SPAN_TO_ROLL;
						if(offsetY < 0){
							startRow -=1;;
							offsetY += TILE_SIZE;	
						}
					}
					else if(offsetY >= SPAN_TO_ROLL){//�������������ˣ�����ƫ��������
						offsetY -= SPAN_TO_ROLL;
					}
				//}
				break;
			case 2:				//ֻ���¼��������
				//if(hy - gv.startRow*TILE_SIZE -gv.offsetY + SPACE_FOR_ROLL >= SCREEN_HEIGHT){//����Ƿ���Ҫ�¹�
					if(startRow + SCREEN_ROWS <= MAP_ROWS ){//���Խ��ܽ�λ�ͼ�
						offsetY += SPAN_TO_ROLL;
						if(offsetY > TILE_SIZE){//��Ҫ��λ
							startRow += 1;
							offsetY -=TILE_SIZE;
						}
					}
				//}
				break;
			case 4:				//ֻ������������
				//if(hx - gv.startCol*TILE_SIZE - gv.offsetX <= SPACE_FOR_ROLL){//����Ƿ���Ҫ�����
					if(startCol > 0){//startCol������
						offsetX -= SPAN_TO_ROLL;//����ƫ��Ӣ�۲�����������
						if(offsetX < 0){					
							startCol -=1;//
							offsetX += TILE_SIZE;//�д�����		
						}
					}
					else if(offsetX >= SPAN_TO_ROLL){//���������������������ƫ��������
						offsetX -= SPAN_TO_ROLL;//����ƫ��Ӣ�۲�����������
					}
				//}
				break;
			case 8:				//ֻ���Ҽ��������
				//if(hx - gv.startCol*TILE_SIZE - gv.offsetX + SPACE_FOR_ROLL >= SCREEN_WIDTH){//����Ƿ���Ҫ�ҹ���
					if(startCol + SCREEN_COLS <= MAP_COLS ){//startCol���ܼ�
						offsetX += SPAN_TO_ROLL;//����ƫ��Ӣ�۲�����������
						if(offsetX > TILE_SIZE){//��Ҫ��λ
							startCol += 1;
							offsetX -= TILE_SIZE;
						}
					}
				//}
				break;
			}
		}

}