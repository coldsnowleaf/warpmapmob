package lei.com.example.wilocclient;
import static lei.com.example.wilocclient.ConstantUtil.*;

public class Layer{
	MyDrawable [][] mapMatrix;		//绘制矩阵
	int [][] notInMarix;	//通过性矩阵
	//构造器
	public Layer(int[][] noThroughMatrix){
		//this.mapMatrix = getMyDrawableMatrix(mapMatrix);
		this.mapMatrix = getMyDrawableMatrix(noThroughMatrix);
		this.notInMarix = noThroughMatrix;
	}
	public int [][] getTotalNotInMatrix()
	{
		return notInMarix;
	}
	//方法：根据地图数据创建一个MyDrawable矩阵
	public static MyDrawable [][] getMyDrawableMatrix(int [][] mapMatrix){
		MyDrawable [][] result = new MyDrawable[MAP_ROWS][MAP_COLS];
		for(int i=0;i<MAP_ROWS;i++){
			for(int j=0;j<MAP_COLS;j++){
				switch(mapMatrix[i][j]){		//判断该位置的图元编号
				case 0:		//路
					result[i][j] = new MyDrawable(0);
					break;
				case 1:		//草地     
					result[i][j] = new MyDrawable(1);
					break;
				}
			}
		}		
		return result;
	}
}