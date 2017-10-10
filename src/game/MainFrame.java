package game;

import javax.swing.*;

/**
 * ´°¿Ú
 * @author Administrator
 *
 */
public class MainFrame extends JFrame {
	private static WallPanel panel;
	private JButton button;
	public MainFrame(){
		this.setTitle("2048JAVA°æ");
		this.setSize(580,680);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		panel =new WallPanel();
		panel.setLayout(null);
		this.add(panel);
		
	}
	
	public static void main(String[] args) {	
		MainFrame frame =new MainFrame();
		frame.setVisible(true);
		panel.start();
//		panel.action();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		panel.auto();

	}
}

