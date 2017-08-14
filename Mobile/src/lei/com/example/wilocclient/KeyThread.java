package lei.com.example.wilocclient;
import static lei.com.example.wilocclient.ConstantUtil.*;


public class KeyThread extends Thread{
	GameView gv;		//GameView����
	int sleepSpan = KEY_THREAD_SLEEP_SPAN;	//����ʱ��
	boolean flag;		//�߳��Ƿ�ִ�б�־λ
	boolean isGameOn;	//��Ϸ�Ƿ���б�־λ
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
				if(checkCollision()){		//���������ײ
					gv.hero.x = TILE_SIZE*gv.hero.col;
					gv.hero.y = TILE_SIZE*gv.hero.row;
				}
				else{
					//checkIfRollScreen(1);
					//checkIfRollScreen(2);
					//checkIfRollScreen(4);
					//checkIfRollScreen(8);
					gv.hero.row = (gv.hero.y+SPRITE_HEIGHT/2)/TILE_SIZE;
					gv.hero.col = (gv.hero.x+SPRITE_WIDTH/2)/TILE_SIZE;	//��������
				}
//				if(key==1){		//����
//					gv.hero.y -= HERO_MOVING_SPAN;	//�ƶ�Ӣ��λ��
//					if(checkCollision()){		//���������ײ
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{						//���û�з�����ײ
//						checkIfRollScreen(1);		//����Ƿ���Ҫ����,1,2,4,8������������
//						gv.hero.row = (gv.hero.y+SPRITE_HEIGHT-SPRITE_WIDTH/2)/TILE_SIZE;//��������
//					}
//					
//				}
//				else if(key==2){	//����
//					gv.hero.y += HERO_MOVING_SPAN;	//�ƶ�Ӣ��λ��
//					if(checkCollision()){		//���������ײ
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{						//���û�з�����ײ
//						checkIfRollScreen(2);		//����Ƿ���Ҫ����,1,2,4,8������������
//						gv.hero.row = (gv.hero.y+SPRITE_HEIGHT-SPRITE_WIDTH/2)/TILE_SIZE;//��������
//					}
//				}
//				else if(key== 4){	//����
//					gv.hero.x -= HERO_MOVING_SPAN;	//�ƶ�Ӣ��λ��
//					if(checkCollision()){		//���������ײ
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{						//���û������ײ
//						checkIfRollScreen(4);		//����Ƿ���Ҫ����,1,2,4,8������������
//						gv.hero.col = (gv.hero.x+SPRITE_WIDTH/2)/TILE_SIZE;	//��������
//					}
//				}
//				else if(key == 8){	//����
//					gv.hero.x += HERO_MOVING_SPAN;		//�ƶ�Ӣ��λ��
//					if(checkCollision()){			//���������ײ
//						gv.hero.x = TILE_SIZE*gv.hero.col;
//						gv.hero.y = TILE_SIZE*gv.hero.row;
//					}
//					else{							//���û������ײ
//						checkIfRollScreen(8);		//����Ƿ���Ҫ����,1,2,4,8������������
//						gv.hero.col = (gv.hero.x+SPRITE_WIDTH/2)/TILE_SIZE;	//��������
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
		int [][] notIn = gv.currNotIn;		//��ȡ��ͼ�Ĳ���ͨ������
		int hx = gv.hero.x;					//��ȡӢ�۵�x����	
		int hy = gv.hero.y;					//��ȡӢ�۵�y����
		int row = 0;	//(hy+TILE_SIZE/2)/TILE_SIZE;	//���ĵ������
		int col = 0;	//(hx+TILE_SIZE/2)/TILE_SIZE;					//���ĵ������

		row = (hy+SPRITE_HEIGHT-TILE_SIZE+1)/TILE_SIZE;	//�������Ͻ���������
		col = hx/TILE_SIZE;								//�������Ͻ���������
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}
		//������½�
		row = (hy+SPRITE_HEIGHT-1)/TILE_SIZE;		//�������½���������
		col = hx/TILE_SIZE;						//�������½���������
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}		
		//������Ͻ�
		row = (hy+SPRITE_HEIGHT-TILE_SIZE+1)/TILE_SIZE;	//�������Ͻ���������
		col = (hx+SPRITE_WIDTH-1)/TILE_SIZE;				//�������Ͻ���������
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}
		//������½�
		row = (hy+SPRITE_HEIGHT-1)/TILE_SIZE;		//�������½���������
		col = (hx+SPRITE_WIDTH-1)/TILE_SIZE;				//�������½���������
		if(row>=0&&row<MAP_ROWS&&col>=0&&col<MAP_COLS&&notIn[row][col]==1){
			return true;
		}
		return false;							//��û�з�����ײ�򷵻�false
	}
	
//	public void checkIfRollScreen(int direction){
//		
//		int hx = gv.hero.x;
//		int hy = gv.hero.y;
//		switch(direction){
//		case 1:				//ֻ���ϼ��������
//			//if(hy - gv.startRow*TILE_SIZE - gv.offsetY <= SPACE_FOR_ROLL){//����Ƿ���Ҫ�����
//				if(gv.startRow >0){//startRow ���ܽ�
//					gv.offsetY -= SPAN_TO_ROLL;
//					if(gv.offsetY < 0){
//						gv.startRow -=1;;
//						gv.offsetY += TILE_SIZE;	
//					}
//				}
//				else if(gv.offsetY >= SPAN_TO_ROLL){//�������������ˣ�����ƫ��������
//					gv.offsetY -= SPAN_TO_ROLL;
//				}
//			//}
//			break;
//		case 2:				//ֻ���¼��������
//			//if(hy - gv.startRow*TILE_SIZE -gv.offsetY + SPACE_FOR_ROLL >= SCREEN_HEIGHT){//����Ƿ���Ҫ�¹�
//				if(gv.startRow + SCREEN_ROWS <= MAP_ROWS ){//���Խ��ܽ�λ�ͼ�
//					gv.offsetY += SPAN_TO_ROLL;
//					if(gv.offsetY > TILE_SIZE){//��Ҫ��λ
//						gv.startRow += 1;
//						gv.offsetY -=TILE_SIZE;
//					}
//				}
//			//}
//			break;
//		case 4:				//ֻ������������
//			//if(hx - gv.startCol*TILE_SIZE - gv.offsetX <= SPACE_FOR_ROLL){//����Ƿ���Ҫ�����
//				if(gv.startCol > 0){//startCol������
//					gv.offsetX -= SPAN_TO_ROLL;//����ƫ��Ӣ�۲�����������
//					if(gv.offsetX < 0){					
//						gv.startCol -=1;//
//						gv.offsetX += TILE_SIZE;//�д�����		
//					}
//				}
//				else if(gv.offsetX >= SPAN_TO_ROLL){//���������������������ƫ��������
//					gv.offsetX -= SPAN_TO_ROLL;//����ƫ��Ӣ�۲�����������
//				}
//			//}
//			break;
//		case 8:				//ֻ���Ҽ��������
//			//if(hx - gv.startCol*TILE_SIZE - gv.offsetX + SPACE_FOR_ROLL >= SCREEN_WIDTH){//����Ƿ���Ҫ�ҹ���
//				if(gv.startCol + SCREEN_COLS <= MAP_COLS ){//startCol���ܼ�
//					gv.offsetX += SPAN_TO_ROLL;//����ƫ��Ӣ�۲�����������
//					if(gv.offsetX > TILE_SIZE){//��Ҫ��λ
//						gv.startCol += 1;
//						gv.offsetX -= TILE_SIZE;
//					}
//				}
//			//}
//			break;
//		}
//	}

}