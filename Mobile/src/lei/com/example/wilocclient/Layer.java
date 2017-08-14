package lei.com.example.wilocclient;
import static lei.com.example.wilocclient.ConstantUtil.*;

public class Layer{
	MyDrawable [][] mapMatrix;		//���ƾ���
	int [][] notInMarix;	//ͨ���Ծ���
	//������
	public Layer(int[][] noThroughMatrix){
		//this.mapMatrix = getMyDrawableMatrix(mapMatrix);
		this.mapMatrix = getMyDrawableMatrix(noThroughMatrix);
		this.notInMarix = noThroughMatrix;
	}
	public int [][] getTotalNotInMatrix()
	{
		return notInMarix;
	}
	//���������ݵ�ͼ���ݴ���һ��MyDrawable����
	public static MyDrawable [][] getMyDrawableMatrix(int [][] mapMatrix){
		MyDrawable [][] result = new MyDrawable[MAP_ROWS][MAP_COLS];
		for(int i=0;i<MAP_ROWS;i++){
			for(int j=0;j<MAP_COLS;j++){
				switch(mapMatrix[i][j]){		//�жϸ�λ�õ�ͼԪ���
				case 0:		//·
					result[i][j] = new MyDrawable(0);
					break;
				case 1:		//�ݵ�     
					result[i][j] = new MyDrawable(1);
					break;
				}
			}
		}		
		return result;
	}
}