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
 * ���
 * @author Administrator
 *
 */
public class WallPanel extends JPanel {
	private static final int ROWS=4;
	private static final int COLS=4; 
	/**
	 * ���̵ĳߴ�
	 */
	private static final int WALL_SIZE=500;
	/**
	 * ���ӵĳߴ�
	 */
	private static final int CELL_SIZE=105;
	/**
	 * ���Ӽ��
	 */
	private static final int CELL_SPACE=16;
	/**
	 * ����
	 */
	private int score;
	private int time;
	/**
	 * ����ͼƬ
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
	 * ǽ
	 */
	public Cell[][] wall = new Cell[ROWS][COLS];
	/**
	 * �����ɵķ���
	 */
	private Cell newCell;
	/**
	 *  ��Ϸ�Ƿ����
	 */
	public boolean over;
	//��ʱ�߳�
	private Thread timer;
	/**
	 * ������ƶ� 
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
				//�ж���Ϸ�Ƿ����
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
				//ֻҪ�ǽ����� ��return�˳� 
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
	 * ��������¸��� ��Ƕ�뵽ǽ��
	 */
	public Cell generateCell(){
		Cell cell = randomCell(wall);
		landIntoWall(cell);
		return cell;
	}
	
	/**
	 * �ڿհ׵ĵط��������һ��2����4�ĸ���
	 * @return ������������ɵĸ���
	 */
	public Cell randomCell(Cell[][] w){
		double fnum = Math.random();
		int num = fnum<0.9?2:4;
		//int row,col;
		Cell cell=null;
		//�ո�������
		int numFree  = getNumOfFreeCells(w);
		List<Cell> list = new ArrayList<Cell>(numFree);
		//���û��  �ͼ��������¸���
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
	//�����¸���֮�� ���뵽ǽ��
	public void landIntoWall(Cell cell){
		if (cell!=null) {
			wall[cell.getRow()][cell.getCol()] = cell;
		}
	}
	/**
	 * ���ǽ�Ͽո��ӵ�����
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
	 * �ܷ���������ƶ�
	 * @param cell
	 * @return ����boolean���� ��ʾ ���true������ƶ� false���ƶ�
	 */
	public boolean canMoveLeft(Cell cell,Cell[][] wall){
		if(cell.getCol()!=0&&wall[cell.getRow()][cell.getCol()-1]==null){
			return true;
		}
		return false;
	}
	/**
	 * ����������ƶ� ���ƶ�
	 * a[i][j]=b[m-j-1][i]
	 */
	public Cell[][] moveLeft(Cell[][] w){
		for(int row=0;row<ROWS;row++){
			Cell[] line = w[row];
			for(int col=0;col<COLS;col++){
				Cell cell =line[col];
				//���ǽ���и��� ���ƶ�
				if(cell!=null){
					//������Ӳ�������һ������ߵ�һ��û�и��� ��һֱ�ƶ�
					while(canMoveLeft(cell,w)){
						cell.moveLeft();
						w[row][cell.getCol()]=cell;
						w[row][cell.getCol()+1]=null;
					}
				}
			}
			//������ߵ�һ����ʼ�ж� �����ӵ������Ƿ���ұߵ�һ����� �������ߵı��������ӵĺ͵ĸ��� �ұߵ�һ����Ϊ��
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
			//�ϲ�֮�����ƶ�һ��
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
					//�ƶ�
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
					//�ϲ�
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
					//�ƶ�
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
					//�ƶ�
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
					//�ϲ�
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
					//�ƶ�
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
					//�ƶ�
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
					//�ϲ�
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
					//�ƶ�
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
	 * ���ƽ�������
	 * @param g
	 */
	public void paintGameOver(Graphics g){
		if(over){
			timer.stop();//��Ϸ������ʱ�� ��ʱֹͣ
			int x =40;
			int y =30;
			g.drawImage(overImage, x, y, null);	
		}
		
	}
	/*
	 * ����ʱ��
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
	 *���Ʒ���
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
	 * ��ǽ
	 * @param g ����
	 */
	public void paintWall(Graphics g){
		g.drawImage(wallImage, 0, 10, null);	
		/*
		 * ������
		 */
		for(int row=0;row<ROWS;row++){
			Cell[] line =wall[row];
			for(int col=0;col<COLS;col++){
				Cell cell=wall[row][col];
				int x =40+CELL_SPACE+col*(CELL_SPACE+CELL_SIZE);
				int y=60+CELL_SPACE+row*(CELL_SPACE+CELL_SIZE);
				if(cell==null){
					/*
					 * ���ո���
					 */
					g.setColor(Color.gray);
					g.fillRoundRect(x-7, y-7, CELL_SIZE+3, CELL_SIZE+3,20,20);

					g.setColor(new Color(204,192,178));
					g.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE,20,20);
					
				}else{
					/*
					 * �������ֵĸ���
					 */
					paintCell(g,cell);
					
				}
			}
		}
		
	}
	/**
	 * ������
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
	 
	
	//�Զ��ƶ��ж�����
	public void autoRun(){
		int direction =0;//�� 1 �� 2 ��3 �� 4
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
		//�� 1 �� 2 ��3 �� 4
		switch(direction){
		case 1:moveDown(wall);generateCell();break;
		case 2:moveRight(wall);generateCell();break;
		case 3:moveLeft(wall);generateCell();break;
		case 4:moveUp(wall);generateCell();break;
		}
		
		over=true;
		//�ж���Ϸ�Ƿ����
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
	 * ���ǽ�ĸ���
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
	 * ���ǽ�ϵ�ÿ��λ�����ֵ�����
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
	//����ǰ����2�Ķ��ٴη�
	public int getN(int m){
		int n=0;
		while(m>0){
			n++;
			m/=2;
		}
		return n;
	}
	
	//�����ƶ����ǽ���ƶ�ǰ�ǲ���һ�� �жϻ����ܰ���ǰ��������ƶ�
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
