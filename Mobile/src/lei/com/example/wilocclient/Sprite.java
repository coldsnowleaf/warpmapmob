package lei.com.example.wilocclient;			

import static lei.com.example.wilocclient.ConstantUtil.*;
import java.util.ArrayList;					//���������
import android.graphics.Canvas;				//���������

public class Sprite{
	int x;		//�����ڴ��ͼ������
	int y;		//�����ڴ��ͼ������
	int col;	//�����ڵ�ͼ�ϵ�������ͨ������31��31������������ڵĵط����
	int row;	//�����ڵ�ͼ�ϵ�������ͨ������31��31������������ڵĵط����
	SpriteThread st;
	int currentSegment;
	//������
	public Sprite(int col,int row){
		this.col = col;
		this.row = row;
		this.x = col * TILE_SIZE;								//x�����Ӧ��ͼƬ���Ͻ�
		this.y = row * TILE_SIZE+TILE_SIZE-SPRITE_HEIGHT;		//y�����Ӧ��ͼƬ���Ͻ�
		st = new SpriteThread(this);
	}

	public void drawSelf(Canvas canvas,int screenX,int screenY){
		BitmapManager.drawGameDot(canvas, screenX, screenY);		//����BitmapManager�ľ�̬��������ͼƬ
	}

}