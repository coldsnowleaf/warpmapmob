package lei.com.example.wilocclient;
/*
 * 该类提供一些列的静态成员作为各个类的常量
 */
public class ConstantUtil{
	//与线程休眠有关的常量
	public static final int STARTSETY = 180;
	public static final int DRAW_THREAD_SLEEP_SPAN = 67;	//后台重绘线程休眠时间
	public static final int KEY_THREAD_SLEEP_SPAN = 120;		//键盘线程休眠时间

	//public static final int STAGE_BITMAP_LENGTH =7;		//每一关卡中用到的图片数组长度
	
	//与Sprite相关的常量
	public static final int SPRITE_WIDTH = 45;			//英雄图片的宽度
	public static final int SPRITE_HEIGHT = 45;			//英雄图片的高度
	public static final int HERO_MOVING_SPAN = 6;		//英雄的移动步进
	public static final int SPAN_TO_ROLL = 20;		//每次滚屏像素数

	//与地图相关的常量
	public static final int TILE_SIZE = 45;					//地图图元的大小30
	public static final int MAP_ROWS = 70;		//地图行数41
	public static final int MAP_COLS = 110;		//地图列数148
//	public static final int SCREEN_ROWS = 37;	//屏幕能显示的行数 40
//	public static final int SCREEN_COLS = 27;	//屏幕能显示的列数 24
	public static final int SCREEN_ROWS = 35;	//屏幕能显示的行数 40
	public static final int SCREEN_COLS = 24;	//屏幕能显示的列数 24
//	public static final int SCREEN_WIDTH = 720;	//屏幕宽度
//	public static final int SCREEN_HEIGHT = 1280;	//屏幕高度
//	public static final int SCREEN_WIDTH = 800;	//屏幕宽度
//	public static final int SCREEN_HEIGHT = 1280;	//屏幕高度
//	public static final int SCREEN_WIDTH = 1080;	//屏幕宽度
//	public static final int SCREEN_HEIGHT = 1920;	//屏幕高度
	public static final int SPACE_FOR_ROLL = 124;	//英雄与边界的距离小于该值进行滚屏
	public static final float AREA_PERCENT = 0.6f;		//重叠面积比例超过该值则判定为发生碰撞
//	//与缩略地图相关的常量
//	public static final int MINI_MAP_TILE_SIZE = 8;		//缩略地图的每块大小
//	public static final int MINI_MAP_START_X = 200;		//缩略地图的开始x坐标
//	public static final int MINI_MAP_START_Y = 0;		//缩略地图的开始y坐标
}