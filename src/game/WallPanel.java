package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 面板
 * @author Administrator
 *
 */
public class WallPanel extends JPanel {
	private static final int ROWS=4;
	private static final int COLS=4; 
	/**
	 * 棋盘的尺寸
	 */
	private static final int WALL_SIZE=500;
	/**
	 * 格子的尺寸
	 */
	private static final int CELL_SIZE=105;
	/**
	 * 格子间隔
	 */
	private static final int CELL_SPACE=16;
	/**
	 * 分数
	 */
	private int score;
	private int time;
	/**
	 * 格子图片
	 */
	private static BufferedImage overImage;
	private static BufferedImage background;
	private static BufferedImage wallImage;
	private static BufferedImage cell2 ;
	private static BufferedImage cell4 ;
	private static BufferedImage cell8 ;
	private static BufferedImage cell16 ;
	private static BufferedImage cell32;
	private static BufferedImage cell64;
	private static BufferedImage cell128;
	private static BufferedImage cell256;
	private static BufferedImage cell512;
	private static BufferedImage cell1024;
	private static BufferedImage cell2048;
	static{
		try {
			cell2 = ImageIO.read(
					WallPanel.class.getResource("2.png"));
			cell4 = ImageIO.read(
					WallPanel.class.getResource("4.png"));
			cell8 = ImageIO.read(
					WallPanel.class.getResource("8.png"));
			cell16 = ImageIO.read(
					WallPanel.class.getResource("16.png"));
			cell32 = ImageIO.read(
					WallPanel.class.getResource("32.png"));
			cell64 = ImageIO.read(
					WallPanel.class.getResource("64.png"));
			cell128 = ImageIO.read(
					WallPanel.class.getResource("128.png"));
			cell256 = ImageIO.read(
					WallPanel.class.getResource("256.png"));
			cell512= ImageIO.read(
					WallPanel.class.getResource("512.png"));
			cell1024 = ImageIO.read(
					WallPanel.class.getResource("1024.png"));
			cell2048 = ImageIO.read(
					WallPanel.class.getResource("2048.png"));
			overImage = ImageIO.read(
					WallPanel.class.getResource("gameover.png"));
			background = ImageIO.read(
					WallPanel.class.getResource("back.png"));
			wallImage = ImageIO.read(
					WallPanel.class.getResource("wall2.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 墙
	 */
	public Cell[][] wall = new Cell[ROWS][COLS];
	/**
	 * 新生成的方块
	 */
	private Cell newCell;
	/**
	 *  游戏是否结束
	 */
	public boolean over;
	//计时线程
	private Thread timer;
	/**
	 * 方块的移动 
	 */
	public void action(){
		KeyListener l =new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int type =e.getKeyCode();

				switch(type){
				case KeyEvent.VK_LEFT: if (canMove(moveLeft(getCopyOfWall()))){moveLeft(wall);	
				newCell =generateCell();}break;
				case KeyEvent.VK_RIGHT:if (canMove(moveRight(getCopyOfWall()))){moveRight(wall);
				newCell =generateCell();}break;
				case KeyEvent.VK_UP:if (canMove(moveUp(getCopyOfWall()))){moveUp(wall);
				newCell =generateCell();}break;
				case KeyEvent.VK_DOWN:if (canMove(moveDown(getCopyOfWall()))){moveDown(wall);
				newCell =generateCell();}break;
				}
				
				over=true;
				//判断游戏是否结束
				a:for(int row=0;row<wall.length;row++){
					for(int col=0;col<wall[row].length;col++){
						Cell cell = wall[row][col];
						if(cell==null){
							over = false; 
						}
						if(cell!=null&&cell.getCol()<3&&wall[row][cell.getCol() + 1] != null&&
								cell.getNum() == wall[row][cell.getCol() + 1].getNum()){
							over = false;
						}

						if(cell!=null&&cell.getRow()<3&&wall[cell.getRow()+1][col]!=null
								&&cell.getNum()==wall[cell.getRow()+1][col].getNum()){
							over = false;
						}
						if(over==false){
							break a;
						}
					}
				}
				//只要是结束了 就return退出 
				if(over){
					return;
				}
				repaint();
			}
		};
		this.addKeyListener(l);
		this.setFocusable(true);
		this.requestFocus();
	}
	
	public void start(){
		
		generateCell();
		newCell =generateCell();
		 timer = new Thread(){
				public void run(){
					while(true){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						time++;;
						repaint();
					}
				}
			};
			timer.start();
	}
	/**
	 * 随机生成新格子 并嵌入到墙中
	 */
	public Cell generateCell(){
		Cell cell = randomCell(wall);
		landIntoWall(cell);
		return cell;
	}
	
	/**
	 * 在空白的地方随机生成一个2或者4的格子
	 * @return 返回这个新生成的格子
	 */
	public Cell randomCell(Cell[][] w){
		double fnum = Math.random();
		int num = fnum<0.9?2:4;
		//int row,col;
		Cell cell=null;
		//空格子数量
		int numFree  = getNumOfFreeCells(w);
		List<Cell> list = new ArrayList<Cell>(numFree);
		//如果没满  就继续生成新格子
		if (numFree>0) {
			for(int i=0;i<w.length;i++){
				for(int j=0;j<w[i].length;j++){
					if(w[i][j]==null){
						Cell c = new Cell(i,j,num);
						list.add(c);
					}
				}
			}
			Random random = new Random();
			int n = random.nextInt(numFree);
			cell = list.get(n);
			//wall[cell.getRow()][cell.getCol()]=cell;			
		}
		return	cell;
	}
	//生成新格子之后 载入到墙中
	public void landIntoWall(Cell cell){
		if (cell!=null) {
			wall[cell.getRow()][cell.getCol()] = cell;
		}
	}
	/**
	 * 获得墙上空格子的数量
	 * @return
	 */
	public int getNumOfFreeCells(Cell[][] w){
		int sum=0;
		for(int i=0;i<w.length;i++){
			for(int j=0;j<w[i].length;j++){
				Cell cell = w[i][j];
				if(cell==null){
					sum++;
				}
			}
		}
		return sum;
	}
	/**
	 * 能否向左继续移动
	 * @param cell
	 * @return 返回boolean类型 表示 如果true则继续移动 false则不移动
	 */
	public boolean canMoveLeft(Cell cell,Cell[][] wall){
		if(cell.getCol()!=0&&wall[cell.getRow()][cell.getCol()-1]==null){
			return true;
		}
		return false;
	}
	/**
	 * 如果方块能移动 则移动
	 * a[i][j]=b[m-j-1][i]
	 */
	public Cell[][] moveLeft(Cell[][] w){
		for(int row=0;row<ROWS;row++){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				//如果墙上有格子 就移动
				if(cell!=null){
					//如果格子不是最左一列且左边的一列没有格子 则一直移动
					while(canMoveLeft(cell,w)){
						cell.moveLeft();
						w[row][cell.getCol()]=cell;
						w[row][cell.getCol()+1]=null;
					}
				}
			}
			//从最左边第一个开始判断 看格子的数字是否和右边的一个相等 相等则左边的变成数字相加的和的格子 右边的一个变为空
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					if (cell.getCol()<3) {
						if (w[row][cell.getCol() + 1] != null) {
							if (cell.getNum() == w[row][cell.getCol() + 1].getNum()) {
								int num = cell.getNum() * 2;
								if (w==wall) {
									score += num;
								}
								cell.setNum(num);
								w[row][cell.getCol()] = cell;
								w[row][cell.getCol() + 1] = null;
							}
						}
					}
				}
			}
			//合并之后再移动一次
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					while(canMoveLeft(cell,w)){
						cell.moveLeft();
						w[row][cell.getCol()]=cell;
						w[row][cell.getCol()+1]=null;
					}
				}
			}
		}
		return w;
	}
	
	public boolean canMoveRight(Cell cell,Cell[][] wall){
		if(cell.getCol()!=COLS-1&&wall[cell.getRow()][cell.getCol()+1]==null){
			return true;
		}
		return false;
	}
	
	public Cell[][] moveRight(Cell[][] w){
		for(int row=0;row<ROWS;row++){
			Cell[] line = w[row];
			for(int col=COLS-1;col>=0;col--){
				Cell cell =line[col];
				if(cell!=null){
					//移动
					while(canMoveRight(cell,w)){
						cell.moveRight();
						w[row][cell.getCol()]=cell;
						w[row][cell.getCol()-1]=null;
					}
				}
			}
			for(int col=COLS-1;col>=0;col--){
				Cell cell =line[col];
				if(cell!=null){
					//合并
					if (cell.getCol()>0) {
						if (w[row][cell.getCol() - 1] != null) {
							if (cell.getNum() == w[row][cell.getCol() - 1].getNum()) {
								int num = cell.getNum() * 2;
								if (w==wall) {
									score += num;
								}
								cell.setNum(num);
								w[row][cell.getCol()] = cell;
								w[row][cell.getCol() - 1] = null;
							}
						}
					}
				}
			}
			
			for(int col=COLS-1;col>=0;col--){
				Cell cell =line[col];
				if(cell!=null){
					//移动
					while(canMoveRight(cell,w)){
						cell.moveRight();
						w[row][cell.getCol()]=cell;
						w[row][cell.getCol()-1]=null;
					}
				}
			}
		}
		return w;
	}
	
	public boolean canMoveUp(Cell cell,Cell[][] wall){
		if(cell.getRow()!=0&&wall[cell.getRow()-1][cell.getCol()]==null){
			return true;
		}
		return false;
	}
	public Cell[][] moveUp(Cell[][] w){
		for(int row=0;row<ROWS;row++){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					//移动
					while(canMoveUp(cell,w)){
						cell.moveUp();
						w[cell.getRow()][col]=cell;
						w[cell.getRow()+1][col]=null;
					}
				}
			}
		}
		//
		for(int row=0;row<ROWS;row++){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					//合并
					if(cell.getRow()<3){
						if(w[cell.getRow()+1][col]!=null){
							if(cell.getNum()==w[cell.getRow()+1][col].getNum()){
								int num = cell.getNum()*2;
								if (w==wall) {
									score += num;
								}
								cell.setNum(num);
								w[cell.getRow()][col]=cell;
								w[cell.getRow()+1][col]=null;
							}
						}
					}
				}
			}
		}
		for(int row=0;row<ROWS;row++){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					//移动
					while(canMoveUp(cell,w)){
						cell.moveUp();
						w[cell.getRow()][col]=cell;
						w[cell.getRow()+1][col]=null;
					}
				}
			}
		}
		return w;
	}
	
	public boolean canMoveDown(Cell cell,Cell[][] wall){
		if(cell.getRow()!=ROWS-1&&wall[cell.getRow()+1][cell.getCol()]==null){
			return true;
		}
		return false;
	}
	public Cell[][] moveDown(Cell[][] w){
		for(int row=ROWS-1;row>=0;row--){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					//移动
					while(canMoveDown(cell, w)){
						cell.moveDown();
						w[cell.getRow()][col]=cell;
						w[cell.getRow()-1][col]=null;
					}
				}
			}
		}
		//
		for(int row=ROWS-1;row>=0;row--){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					//合并
					if(cell.getRow()>0){
						if(w[cell.getRow()-1][col]!=null){
							if(cell.getNum()==w[cell.getRow()-1][col].getNum()){
								int num=cell.getNum()*2;
								if (w==wall) {
									score += num;
								}
								cell.setNum(num);
								w[cell.getRow()][col]=cell;
								w[cell.getRow()-1][col]=null;
							}
						}
					}
				}
			}
		}
		for(int row=ROWS-1;row>=0;row--){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				if(cell!=null){
					//移动
					while(canMoveDown(cell, w)){
						cell.moveDown();
						w[cell.getRow()][col]=cell;
						w[cell.getRow()-1][col]=null;
					}
					
				}
			}
		}
		return w;
	}
	
	public void paint(Graphics g) {
		paintBackground(g);
		paintWall(g);
		paintScore(g);
		paintTime(g);
		paintGameOver(g);
	}
	public void paintBackground(Graphics g){
		int x =0;
		int y =0;
		g.drawImage(background, x, y, null);	
	}
	/**
	 * 绘制结束画面
	 * @param g
	 */
	public void paintGameOver(Graphics g){
		if(over){
			timer.stop();//游戏结束的时候 计时停止
			int x =40;
			int y =30;
			g.drawImage(overImage, x, y, null);	
		}
		
	}
	/*
	 * 绘制时间
	 */
	public void paintTime(Graphics g){
		int x = 300;
		int y = 600;
		Font f = new Font(
				Font.SANS_SERIF,Font.BOLD,30);
		g.setFont(f);
		g.setColor(Color.black);
		g.drawString("Time:"+time+"s", x, y);
	}
	/**
	 *绘制分数
	 * @param g
	 */
	private void paintScore(Graphics g) {
		int x = 61;
		int y = 600;
		Font f = new Font(
				Font.SANS_SERIF,Font.BOLD,30);
		g.setFont(f);
		g.setColor(Color.red);
		g.drawString("Score:"+score, x, y);
	}
	/**
	 * 画墙
	 * @param g 画笔
	 */
	public void paintWall(Graphics g){
		g.drawImage(wallImage, 0, 10, null);	
		/*
		 * 画格子
		 */
		for(int row=0;row<ROWS;row++){
			Cell[] line =wall[row];
			for(int col=0;col<COLS;col++){
				Cell cell=wall[row][col];
				int x =40+CELL_SPACE+col*(CELL_SPACE+CELL_SIZE);
				int y=60+CELL_SPACE+row*(CELL_SPACE+CELL_SIZE);
				if(cell==null){
					/*
					 * 画空格子
					 */
					g.setColor(Color.gray);
					g.fillRoundRect(x-7, y-7, CELL_SIZE+3, CELL_SIZE+3,20,20);

					g.setColor(new Color(204,192,178));
					g.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE,20,20);
					
				}else{
					/*
					 * 画有数字的格子
					 */
					paintCell(g,cell);
					
				}
			}
		}
		
	}
	/**
	 * 画方块
	 * @param g
	 * @param cell
	 */
	public  void paintCell(Graphics g,Cell cell){
		int num=cell.getNum();
		BufferedImage image = null;
		switch(num){
		case 2:image=cell2;break;
		case 4:image=cell4;break;
		case 8:image=cell8;break;
		case 16:image=cell16;break;
		case 32:image=cell32;break;
		case 64:image=cell64;break;
		case 128:image=cell128;break;
		case 256:image=cell256;break;
		case 512:image=cell512;break;
		case 1024:image=cell1024;break;
		case 2048:image=cell2048;break;

		}
		int x=40+CELL_SPACE+cell.getCol()*(CELL_SPACE+CELL_SIZE);
		int y=60+CELL_SPACE+cell.getRow()*(CELL_SPACE+CELL_SIZE);
		g.drawImage(image,x-5,y-5,null);
	}
	 
	
	//自动移动判断流程
	public void autoRun(){
		int direction =0;//下 1 右 2 左3 上 4
		double mincha =Math.pow(10, 20);
		int i,k;
		
		double c;
		Cell[][] wallCopy1 = getCopyOfWall();
		if (canMove(moveDown(wallCopy1))) {
			c = getCha(wallCopy1);
			if (c < mincha) {
				mincha = c;
				direction = 1;
			}
		}
		
		
		Cell[][] wallCopy2 = getCopyOfWall();
		if (canMove(moveRight(wallCopy2))) {
			c = getCha(wallCopy2);
			if (c < mincha) {
				mincha = c;
				direction = 2;
			}
		}
		
	
		Cell[][] wallCopy3 = getCopyOfWall();
		if (canMove(moveLeft(wallCopy3))) {
			c = getCha(wallCopy3);
			if (c < mincha) {
				mincha = c;
				direction = 3;
			}
		}
		
	
		Cell[][] wallCopy4 = getCopyOfWall();
		if (canMove(moveUp(wallCopy4))) {
			c = getCha(wallCopy4);
			if (c < mincha) {
				mincha = c;
				direction = 4;
			}
		}
		//下 1 右 2 左3 上 4
		switch(direction){
		case 1:moveDown(wall);generateCell();break;
		case 2:moveRight(wall);generateCell();break;
		case 3:moveLeft(wall);generateCell();break;
		case 4:moveUp(wall);generateCell();break;
		}
		
		over=true;
		//判断游戏是否结束
		a:for(int row=0;row<wall.length;row++){
			for(int col=0;col<wall[row].length;col++){
				Cell cell = wall[row][col];
				if(cell==null){
					over = false; 
				}
				if(cell!=null&&cell.getCol()<3&&wall[row][cell.getCol() + 1] != null&&
						cell.getNum() == wall[row][cell.getCol() + 1].getNum()){
					over = false;
				}

				if(cell!=null&&cell.getRow()<3&&wall[cell.getRow()+1][col]!=null
						&&cell.getNum()==wall[cell.getRow()+1][col].getNum()){
					over = false;
				}
				if(over==false){
					break a;
				}
			}
		}
		
		repaint();
	}
	
	public void auto(){
		while(!over){
			autoRun();
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获得墙的复制
	 * @return
	 */
	public Cell[][] getCopyOfWall(){
		Cell[][] wallCopy = new Cell[ROWS][COLS];
		for(int i=0;i<wall.length;i++){
			for(int j=0;j<wall[i].length;j++){
				Cell cell = null;
				if((cell=wall[i][j])!=null){
					wallCopy[i][j] = new Cell(cell.getRow(),cell.getCol(),cell.getNum());
				}
			}
		}
		return wallCopy;
	}
	/**
	 * 获得墙上的每个位置数字的数组
	 * @param cells
	 * @return
	 */
	public int[][] getInt(Cell[][] cells){
		int[][] arr = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(cells[i][j]==null){
					arr[i][j]=0;
				}else{
					arr[i][j]=cells[i][j].getNum();
				}
			}
		}
		return arr;
	}
	/**
	 *  
	 * @param
	 * @return
	 */
	public double getCha(Cell[][] cells){
		int[][] n=getInt(cells);
		double c =0;
		int i,k,u;
		for(i=0;i<4;i++){
			for(k=0;k<3;k++){
				u=Math.abs(n[i][k]-n[i][k+1]);
				if(u==0){
					c -= 3*n[i][k];
				}else{
					c += u*1.5;
				}
			}
		}
		for(k=0;k<4;k++){
			for(i=0;i<3;i++){
				u=Math.abs(n[i][k]-n[i+1][k]);
				if(u==0){
					c -= 3*n[i][k];
				}else{
					c += u*1.5;
				}
			}
		}
		for(i=0;i<4;i++){
			for(k=0;k<4;k++){
				u = n[i][k];
				c=c - u*(getN(u)-1)*4;
			}
		}
		return c;
	}
	//看当前数是2的多少次方
	public int getN(int m){
		int n=0;
		while(m>0){
			n++;
			m/=2;
		}
		return n;
	}
	
	//根据移动后的墙跟移动前是不是一样 判断还不能按当前方向继续移动
	public boolean canMove(Cell[][] cells){
		boolean can = false;
		a:for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(cells[i][j]==null){
					if(wall[i][j]==null){
						can =false;
					}else{
						can = true;
						break a;
					}
				}else{
					try {
						if(cells[i][j].getNum()==wall[i][j].getNum()){
							can =false;
						}else{
							can =true;
							break a;
						}
					} catch (NullPointerException e) {
						can =true;
						break a;
					}
				}
			}
		}
		return can;
	}
		
}
