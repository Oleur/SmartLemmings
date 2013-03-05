package smartLemmings.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import smartLemmings.agent.LemmingBody;
import smartLemmings.agent.LemmingBody.Direction;
import smartLemmings.agent.Lemmings;
import smartLemmings.environment.Environment;
import smartLemmings.environment.Perception;
import smartLemmings.environment.WorldLevel;
import smartLemmings.environment.Perception.PerceptionType;

/**
 * Build the level, instantiate the environment and start the agents.
 * @author Oleur
 *
 */
public class GameLevel extends Panel implements MouseListener, KeyListener {
	

	private static final long serialVersionUID = -476179148892856523L;
	private String file;
	public String choice;
	private WorldLevel level;
	private Environment environment;
	private int[][] gridLevel;
	private Image imgLem;
	private int posX = 0;
	private int posY = 0;
	private Timer timer;
	private Timer agentTimer;
	private int iteration = 0;
	private LemmingBody body1 = null;
	private int posYEntry;
	private int posXEntry;
	private Lemmings lemmingA;
	
	/**
	 * Panel for the level.
	 * @param filePath Path to the world file.
	 * @param nbLem Number of lemmings in action.
	 */
	public GameLevel(String filePath, int nbLem) {
		
		//Init
		this.file = filePath;
		
		//Create a new world level:
		level = new WorldLevel(filePath);
		gridLevel = level.getWorldGrid();
		
		//Option pane to choose manual mode or Agent mode
		JOptionPane jop = new JOptionPane();
		@SuppressWarnings("static-access")
		String ch = jop.showInputDialog(null, "Use the AI (agent) or play with lemmings (player)",
													"AI or Player ?",
													JOptionPane.QUESTION_MESSAGE);
		choice = ch;
		if (choice.equals("player")) {
			this.addKeyListener(this);
		}
		
		//Timer to display the lemming's motion in time every 0.5s.
		setTimer(50);
		
		//Create a new environment and add lemmings.
		environment = new Environment(27, 18, gridLevel);
		//setLayout(null);
		
		//Lemming image:
		ImageIcon iconEx = new ImageIcon("img/lemming.png");
		imgLem = iconEx.getImage();
		//Add the lemmings to the level:
		for (int i=0; i<18; i++) {//y
			for (int j=0; j<27; j++) {//x
				if (gridLevel[i][j] == 3) {
						posY=i;
						posX=j;
						posYEntry=i;
						posXEntry=j;
						
						//Creation of a new lemming
						body1 = new LemmingBody(environment, true, false, posX, posY, Direction.EAST);
						lemmingA = new Lemmings(true, "/graph/", body1);
						this.environment.addLemming(posX, posY, body1);
				}
			}
		}

		//KeyListeners:
		this.addMouseListener(this);
		this.setFocusable(true);
		
		if (choice.equals("agent"))
			timer.start();
		
	}
	
	private void run() {
		if (choice.equals("agent")) {
			
			//Check if the lemming has reached the exit or the end of the iteration
			if (body1.getEnvironment().envGrid[lemmingA.getBody().getPosY()][lemmingA.getBody().getPosX()].blockState != 4 
					&& iteration<3000) {
				
				//Let us call the live method.
				lemmingA.live();

				posX = lemmingA.getBody().getPosX();
				posY = lemmingA.getBody().getPosY();
				
				//Check if the lemmings is not out of the bounds.
				if (posX < 0 || posX >= environment.getWidth() || posY < 0 || posY >= environment.getHeight() || !lemmingA.getBody().isAlive()) {
					lemmingA.killedByDeath();
					lemmingA.getBody().setPosX(posXEntry);
					lemmingA.getBody().setPosY(posYEntry);
					lemmingA.getBody().setAlive(true);
					lemmingA.getBody().setParachuteIsOpen(false);
					lemmingA.getBody().setDirection(Direction.EAST);
				}
				
				iteration++;
			} else {
				lemmingA.killedByEnd("/graph/");
				System.out.println("iteration "+iteration);
				timer.stop();
			}
			
		}
	}
	
	/**
	 * Timer to display the lemming's motion in time every X milliseconds.
	 * @param time
	 */
	private void setTimer(int time) {
		if (choice.equals("player")) {
			timer = new Timer(time, new AbstractAction() {
				private static final long serialVersionUID = 1L;
				int cpt = 0;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					GameLevel.this.removeKeyListener(GameLevel.this);
					environment.envGrid[posY][posX].isLem = false;
					posY++;
					cpt++;
					body1.setPosY(posY);
					environment.envGrid[posY][posX].isLem = true;
					repaint();
					
					if((cpt > 3 && !body1.isParachuteIsOpen()) || posY > environment.getHeight()){ 	
						body1.setAlive(false);
						System.out.println("You're dead. Try again!");
						
						for (int i=0; i<18; i++) {
							for (int j=0; j<27; j++) {
								if (gridLevel[i][j] == 3) {
										posY=i;
										posX=j;									
										GameLevel.this.addKeyListener(GameLevel.this);
										timer.stop();
										body1 = new LemmingBody(environment, true, false, posY, posX, Direction.SOUTH);
								}
							}
						}
					}
					if (posY < environment.getHeight() && environment.envGrid[posY+1][posX].blockState != 0) {
						cpt = 0;
						GameLevel.this.addKeyListener(GameLevel.this);
						timer.stop();
						if(body1.isParachuteIsOpen())
							body1.setParachuteIsOpen(false);
					}
					if(body1.isParachuteIsOpen() == true){
						ImageIcon iconEx = new ImageIcon("img/parachute.png");
						imgLem = iconEx.getImage();
					}
					else{
						ImageIcon iconEx = new ImageIcon("img/lemming.png");
						imgLem = iconEx.getImage();
					}
						
				}
			});
		} else {
			timer = new Timer(time, new AbstractAction() {
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent arg0) {
					GameLevel.this.run();
					if(body1.isParachuteIsOpen() == true){
						ImageIcon iconEx = new ImageIcon("img/parachute.png");
						imgLem = iconEx.getImage();
					}
					else{
						ImageIcon iconEx = new ImageIcon("img/lemming.png");
						imgLem = iconEx.getImage();
					}
					GameLevel.this.repaint();
				}
			});
		}
		
	}
	
	//@SuppressWarnings("static-access")
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(level.getBackground(), 0, 0, null);
		int boundX = 0;
		int boundY = 0;
		for (int i=0; i<18; i++) {
			for (int j=0; j<27; j++) {
				if (this.environment.envGrid[i][j].blockState == 0) {
					g.drawImage(null, boundX, boundY, null);
					boundX+=35;
					if (j == 26) {
						boundX = 0;
						boundY+=35;
					}
				}
				if (this.environment.envGrid[i][j].blockState == 1) {
					g.drawImage(level.getGround(), boundX, boundY, null);
					boundX+=35;
					if (j == 26) {
						boundX = 0;
						boundY+=35;
					}
				}
				if (this.environment.envGrid[i][j].blockState == 2) {
					g.drawImage(level.getRock(), boundX, boundY, null);
					boundX+=35;
					if (j == 26) {
						boundX = 0;
						boundY+=35;
					}
				}
				if (this.environment.envGrid[i][j].blockState == 3) {
					g.drawImage(level.getEntry(), boundX, boundY, null);
					boundX+=35;
					if (j == 26) {
						boundX = 0;
						boundY+=35;
					}
				}
				if (this.environment.envGrid[i][j].blockState == 4) {
					g.drawImage(level.getExit(), boundX, boundY, null);
					boundX+=35;
					if (j == 26) {
						boundX = 0;
						boundY+=35;
					}
				}
			}
		}
		g.drawImage(imgLem, posX*35, posY*35, null);
		if (posY>=0 && posX>=0 && posY< this.environment.getHeight() && posX<this.environment.getWidth() && this.environment.envGrid[posY][posX].blockState == 4) {
			JOptionPane jop = new JOptionPane();
			int win = jop.showConfirmDialog(null, "Congratulations !! The lemmings survive !", "WINNER!", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (win == JOptionPane.OK_OPTION) {
				System.exit(0);
			}
		}
	
	}

	/**
	 * Play god and remove/add blocks from the world
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int y = e.getX()/35;
		int x = e.getY()/35;
		if (e.getModifiers() == MouseEvent.BUTTON2_MASK) {
			if (body1.isParachuteIsOpen() == false) {
				System.out.println("PARACHUTE OPEN!");
				body1.setParachuteIsOpen(true);
			} else {
				body1.setParachuteIsOpen(false);
			}
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK && this.gridLevel[x][y] != 4 && this.gridLevel[x][y] != 3) {
			this.environment.envGrid[x][y].blockState = 0;
		} else if (e.getClickCount() == 1 && this.gridLevel[x][y] != 4 && this.gridLevel[x][y] != 3) {
			this.environment.envGrid[x][y].blockState = 1;
		} else if (e.getClickCount() == 2 && this.gridLevel[x][y] != 4 && this.gridLevel[x][y] != 3) {
			this.environment.envGrid[x][y].blockState = 2;
		}
		this.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
		case KeyEvent.VK_RIGHT :
			//*************************
			// Move forward
			//*************************
			if (posX < environment.getWidth() && (this.environment.envGrid[posY][posX+1].blockState == 0 || this.environment.envGrid[posY][posX+1].blockState == 4)) {
				environment.envGrid[posY][posX].isLem = false;
				posX++;
				body1.setPosX(posX);
				environment.envGrid[posY][posX].isLem = true;
			}
			else if(posX > 0 && (this.environment.envGrid[posY][posX+1].blockState == 1)){
				environment.envGrid[posY][posX].isLem = false;
				this.environment.envGrid[posY][posX+1].blockState = 0;
				posX++;
				body1.setPosX(posX);
				environment.envGrid[posY][posX].isLem = true;
			}
			timer.start();
			if (posY < environment.getHeight() && this.environment.envGrid[posY+1][posX].blockState != 0) {
				timer.stop();
			}
			break;
		case KeyEvent.VK_LEFT :
			//************
			// Move back *
			//************
			if(posX > 0 && (this.environment.envGrid[posY][posX-1].blockState == 0 || this.environment.envGrid[posY][posX-1].blockState == 4)) {
				environment.envGrid[posY][posX].isLem = false;
				posX--;
				body1.setPosX(posX);
				environment.envGrid[posY][posX].isLem = true;
			} else if(posX > 0 && (this.environment.envGrid[posY][posX-1].blockState == 1)){
				this.environment.envGrid[posY][posX-1].blockState = 0;
				environment.envGrid[posY][posX].isLem = false;
				posX--;
				body1.setPosX(posX);
				environment.envGrid[posY][posX].isLem = true;
			}
			timer.start();
			if (posY < environment.getHeight() && this.environment.envGrid[posY+1][posX].blockState != 0) {
				timer.stop();
			}
			break;
		case KeyEvent.VK_UP :
			//***************************************
			//Jump +1 on X-axis et -1 on the Y-axis *
			//***************************************
			if (posX < environment.getWidth() && posY > 0 && (this.environment.envGrid[posY-1][posX+1].blockState == 0 || this.environment.envGrid[posY-1][posX+1].blockState == 4)) {
				environment.envGrid[posY][posX].isLem = false;
				posX++;
				posY--;
				body1.setPosY(posY);
				body1.setPosX(posX);
				environment.envGrid[posY][posX].isLem = true;
			}
			timer.start();
			if (posY < environment.getHeight() && this.environment.envGrid[posY+1][posX].blockState != 0) {
				timer.stop();
			}
			break;
		case KeyEvent.VK_SHIFT:
			//***************************************
			//Jump -1 on X-axis et -1 on the Y-axis *
			//***************************************
			if (posX > 0 && posY > 0 && (this.environment.envGrid[posY-1][posX-1].blockState == 0 || this.environment.envGrid[posY-1][posX-1].blockState == 4)) {
				environment.envGrid[posY][posX].isLem = false;
				posX--;
				posY--;
				body1.setPosY(posY);
				body1.setPosX(posX);
				environment.envGrid[posY][posX].isLem = true;
			}
			timer.start();
			if (posY < environment.getHeight() && this.environment.envGrid[posY+1][posX].blockState != 0) {
				timer.stop();
			}
			break;
		case KeyEvent.VK_DOWN :
			//**************************************
			//Key event to dig in the environment. *
			//**************************************
			if (posY < environment.getHeight() && this.environment.envGrid[posY+1][posX].blockState == 1) {
				this.environment.envGrid[posY+1][posX].blockState = 0;
				environment.envGrid[posY][posX].isLem = false;
				posY++;
				body1.setPosY(posY);
				environment.envGrid[posY][posX].isLem = true;
			}
			timer.start();
			if (posY < environment.getHeight() && this.environment.envGrid[posY+1][posX].blockState != 0) {
				timer.stop();
			}
			break;
		case KeyEvent.VK_SPACE :
			//*******
			// fill *
			//*******
			if(posX < environment.getWidth() && this.environment.envGrid[posY][posX+1].blockState == 0)
				this.environment.envGrid[posY][posX+1].blockState = 1;
			break;
		}
		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
