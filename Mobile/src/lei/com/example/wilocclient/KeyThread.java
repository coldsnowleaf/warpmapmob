package lei.com.example.wilocclient;
import static lei.com.example.wilocclient.ConstantUtil.*;


public class KeyThread extends Thread{
	GameView gv;		//GameView引用
	int sleepSpan = KEY_THREAD_SLEEP_SPAN;	//休眠时间
	boolean flag;		//线程是否执行标志位
	boolean isGameOn;	//游戏是否进行标志位
	//static int key=0;
	static float arc=0;
	
	
	public KeyThread(GameView gv){
		this.gv = gv;
		flag = true;
		isGameOn = true;
	}

	public void run(){
		while(flag){
			while(isGameOn){
				gv.hero.x+=HERO_MOVING_SPAN*Math.cos(arc/180.0*3.1415926);
				gv.hero.y+=HERO_MOVING_SPAN*Math.sin(arc/180.0*3.1415926);	
				if(checkCollision()){		//如果发生碰撞
					gv.hero.x = TILE_SIZE*gv.hero.col;
					gv.hero.y = TILE_SIZE*gv.hero.row;
				}
				else{
					//checkIfRollScreen(1);
					//checkIfRollScreen(2);
					//checkIfRollScreen(4);
					//checkIfRollScreen(8);
					gv.hero.row = (gv.hero.y+SPRITE_HEIGHT/2)/TILE_SIZE;
					gv.hero.col = (gv.hero.x+SPRITE_WIDTH/2)/TILE_SIZE;	//更新列数
				}
//				if(key==1){		//向上
//					gv.hero.y -= HERO_MOVING_SPAN;	//移动英雄位置
//					if(checkCollision()){		//如果发生碰撞
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{						//如果没有发生碰撞
//						checkIfRollScreen(1);		//检查是否需要滚屏,1,2,4,8代表上下左右
//						gv.hero.row = (gv.hero.y+SPRITE_HEIGHT-SPRITE_WIDTH/2)/TILE_SIZE;//更新行数
//					}
//					
//				}
//				else if(key==2){	//向下
//					gv.hero.y += HERO_MOVING_SPAN;	//移动英雄位置
//					if(checkCollision()){		//如果发生碰撞
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{						//如果没有发生碰撞
//						checkIfRollScreen(2);		//检查是否需要滚屏,1,2,4,8代表上下左右
//						gv.hero.row = (gv.hero.y+SPRITE_HEIGHT-SPRITE_WIDTH/2)/TILE_SIZE;//更新行数
//					}
//				}
//				else if(key== 4){	//向左
//					gv.hero.x -= HERO_MOVING_SPAN;	//移动英雄位置
//					if(checkCollision()){		//如果发生碰撞
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{						//如果没发生碰撞
//						checkIfRollScreen(4);		//检查是否需要滚屏,1,2,4,8代表上下左右
//						gv.hero.col = (gv.hero.x+SPRITE_WIDTH/2)/TILE_SIZE;	//更新列数
//					}
//				}
//				else if(key == 8){	//向右
//					gv.hero.x += HERO_MOVING_SPAN;		//移动英雄位置
//					if(checkCollision()){			//如果发生碰撞
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{							//如果没发生碰撞
//						checkIfRollScreen(8);		//检查是否需要滚屏,1,2,4,8代表上下左右
//						gv.hero.col = (gv.hero.x+SPRITE_WIDTH/2)/TILE_SIZE;	//更新列数
//					}
//				}
		
				
				
				try{
					Thread.sleep(sleepSpan);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			try{
				Thread.sleep(1500);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	
	}
	public boolean checkCollision(){
		int [][] notIn = gv.currNotIn;		//获取地图的不可通过矩阵
		int hx = gv.hero.x;					//获取英雄的x坐标	
		int hy = gv.hero.y;					//获取英雄的y坐标
		int row = 0;	//(hy+TILE_SIZE/2)/TILE_SIZE;	//中心点的行数
		int col = 0;	//(hx+TILE_SIZE/2)/TILE_SIZE;					//中心点的列数

		row = (hy+SPRITE_HEIGHT-TILE_SIZE+1)/TILE_SIZE;	//计算左上角所在行数
		col = hx/TILE_SIZE;								//计算左上角所在列数
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}
		//检查左下角
		row = (hy+SPRITE_HEIGHT-1)/TILE_SIZE;		//计算左下角所在行数
		col = hx/TILE_SIZE;						//计算左下角所在列数
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}		
		//检查右上角
		row = (hy+SPRITE_HEIGHT-TILE_SIZE+1)/TILE_SIZE;	//计算右上角所在行数
		col = (hx+SPRITE_WIDTH-1)/TILE_SIZE;				//计算右上角所在列数
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}
		//检查右下角
		row = (hy+SPRITE_HEIGHT-1)/TILE_SIZE;		//计算右下角所在行数
		col = (hx+SPRITE_WIDTH-1)/TILE_SIZE;				//计算右下角所在列数
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}
		return false;							//若没有发生碰撞则返回false
	}
	
//	public void checkIfRollScreen(int direction){
//		
//		int hx = gv.hero.x;
//		int hy = gv.hero.y;
//		switch(direction){
//		case 1:				//只向上检查滚屏与否
//			//if(hy - gv.startRow*TILE_SIZE - gv.offsetY <= SPACE_FOR_ROLL){//检查是否需要左滚屏
//				if(gv.startRow >0){//startRow 还能借
//					gv.offsetY -= SPAN_TO_ROLL;
//					if(gv.offsetY < 0){
//						gv.startRow -=1;;
//						gv.offsetY += TILE_SIZE;	
//					}
//				}
//				else if(gv.offsetY >= SPAN_TO_ROLL){//格子数不够减了，但是偏移量还有
//					gv.offsetY -= SPAN_TO_ROLL;
//				}
//			//}
//			break;
//		case 2:				//只向下检查滚屏与否
//			//if(hy - gv.startRow*TILE_SIZE -gv.offsetY + SPACE_FOR_ROLL >= SCREEN_HEIGHT){//检查是否需要下滚
//				if(gv.startRow + SCREEN_ROWS <= MAP_ROWS ){//可以接受进位就加
//					gv.offsetY += SPAN_TO_ROLL;
//					if(gv.offsetY > TILE_SIZE){//需要进位
//						gv.startRow += 1;
//						gv.offsetY -=TILE_SIZE;
//					}
//				}
//			//}
//			break;
//		case 4:				//只向左检查滚屏与否
//			//if(hx - gv.startCol*TILE_SIZE - gv.offsetX <= SPACE_FOR_ROLL){//检查是否需要左滚屏
//				if(gv.startCol > 0){//startCol还够减
//					gv.offsetX -= SPAN_TO_ROLL;//向左偏移英雄步进的像素数
//					if(gv.offsetX < 0){					
//						gv.startCol -=1;//
//						gv.offsetX += TILE_SIZE;//有待商议		
//					}
//				}
//				else if(gv.offsetX >= SPAN_TO_ROLL){//如果格子数不够减，但是偏移量还有
//					gv.offsetX -= SPAN_TO_ROLL;//向左偏移英雄步进的像素数
//				}
//			//}
//			break;
//		case 8:				//只向右检查滚屏与否
//			//if(hx - gv.startCol*TILE_SIZE - gv.offsetX + SPACE_FOR_ROLL >= SCREEN_WIDTH){//检查是否需要右滚屏
//				if(gv.startCol + SCREEN_COLS <= MAP_COLS ){//startCol还能加
//					gv.offsetX += SPAN_TO_ROLL;//向右偏移英雄步进的像素数
//					if(gv.offsetX > TILE_SIZE){//需要进位
//						gv.startCol += 1;
//						gv.offsetX -= TILE_SIZE;
//					}
//				}
//			//}
//			break;
//		}
//	}

}