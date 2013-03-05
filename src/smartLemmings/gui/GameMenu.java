package smartLemmings.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameMenu extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private JButton loadTest;
	private JButton loadGame;
	private File file;
	private String filePath;
	private Image imgStarter;
	
	public GameMenu(final GUILemmingsGame window) {
		
		setLayout(null);
		//Button to open world's file
		loadTest = new JButton("Test Level");
		loadTest.setBounds(550, 270, 200, 40);
		loadTest.setFont(new Font("Comic sans MS", Font.BOLD, 30));
		loadTest.setBackground(Color.LIGHT_GRAY);
		loadGame = new JButton("Game Level");
		loadGame.setBounds(550, 320, 200, 40);
		loadGame.setFont(new Font("Comic sans MS", Font.BOLD, 30));
		loadGame.setBackground(Color.LIGHT_GRAY);
		
		//Background image:
		ImageIcon iconB = new ImageIcon("img/startGame.png");
		imgStarter = iconB.getImage();
		
		final JFileChooser fc = new JFileChooser();
		
		loadTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ret = fc.showOpenDialog(GameMenu.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					filePath = file.getPath();
					window.setContentPane(new GameLevel(filePath, 1));
					window.repaint();
					window.validate();
				}
			}
		});
		
		//Choose the world file and ask the use how many lemmings he wants in the world.
		loadGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ret = fc.showOpenDialog(GameMenu.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					int nbLemmings = 0;
					JOptionPane jop = new JOptionPane();
					@SuppressWarnings("static-access")
					String nbEntities = jop.showInputDialog(null, "How many Lemmings do you want to deploy ?",
																"How many entities ?",
																JOptionPane.QUESTION_MESSAGE);
					nbLemmings = Integer.parseInt(nbEntities);
					file = fc.getSelectedFile();
					filePath = file.getPath();
					window.setContentPane(new GameLevel(filePath, nbLemmings));
					window.repaint();
					window.validate();
				}
			}
		});
		
		//Adding the components to the panel
		this.add(loadGame);
		this.add(loadTest);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(imgStarter, 0, 0, null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
