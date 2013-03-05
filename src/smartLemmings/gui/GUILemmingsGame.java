package smartLemmings.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GUILemmingsGame extends JFrame {
	
	private int hWin;
	private int wWin;
	
	
	//Environment and GUI attributes
	public GUILemmingsGame(int wWin, int hWin) {
		this.hWin = hWin;
		this.wWin = wWin;	
		
		this.setTitle("Smart Lemmings Game");
		this.setSize(this.wWin, this.hWin);
		
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Add panel:
		this.setContentPane(new GameMenu(this));
		this.setVisible(true);
		
		
	}
	
	public static void main(String[] args) {
		GUILemmingsGame plop = new GUILemmingsGame(960, 640);
	}
	

}
