package game;

import java.awt.image.BufferedImage;

public class Cell {
	/**
	 *���ӵ���row
	 */
	private int row;
	/**
	 * ���ӵ���col
	 */
	private int col;
	/**
	 * �����ϵ�����num
	 */
	private int num;

	private int b;

	
	
	
	public Cell() {
		super();
	}
	public Cell(int row, int col, int num) {
		super();
		this.row = row;
		this.col = col;
		this.num = num;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
	
	
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public void moveRight(){
		this.col++;
	}
	public void moveLeft(){
		this.col--;
	}
	public void moveUp(){
		this.row--;
	}
	public void moveDown(){
		this.row++;
	}
	public String toString() {
		return row+","+col+","+num;
	}
	
}







