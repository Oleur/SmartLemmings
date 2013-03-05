/**
 * 
 * Author: Julien SALVI
 */

package smartLemmings.environment;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import javax.swing.ImageIcon;

public class WorldLevel {
	
	private Image background;
	private Image ground;
	private Image rock;
	private Image entry;
	private Image exit;
	private int[][] worldGrid;
	private int rows;
	private int columns;
	
	public WorldLevel(String levelPath) {
		ImageIcon iconB = new ImageIcon("img/background.png");
		background = iconB.getImage();
		ImageIcon iconG = new ImageIcon("img/b_ground.png");
		ground = iconG.getImage();
		ImageIcon iconU = new ImageIcon("img/unb_ground.png");
		rock = iconU.getImage();
		ImageIcon iconEn = new ImageIcon("img/entry.png");
		entry = iconEn.getImage();
		ImageIcon iconEx = new ImageIcon("img/exit.png");
		exit = iconEx.getImage();
		
		//Reading the level text file.
		try {
			FileInputStream levelIS = new FileInputStream(levelPath);
			InputStreamReader levelSR = new InputStreamReader(levelIS);
			BufferedReader buffR = new BufferedReader(levelSR);
			
			//Setting the world grid with respect to the world size.
			LineNumberReader lnr = new LineNumberReader(new FileReader(new File(levelPath)));
			while ((lnr.readLine())!=null)
            {
               rows = lnr.getLineNumber();
            }
			System.out.println();
			worldGrid = new int[rows][27];
			
			for (int i=0; i<rows; i++) {
				String line = buffR.readLine();
				for (int j=0; j<27; j++) {
					char elem = line.charAt(j);
					String st = String.valueOf(elem);
					worldGrid[i][j] = Integer.parseInt(st);
				}
			}
			
		} catch (Exception e) {
			System.out.println("File cannot be read: "+e);
		}
		
	}

	public int[][] getWorldGrid() {
		return worldGrid;
	}

	public void setWorldGrid(int[][] worldGrid) {
		this.worldGrid = worldGrid;
	}

	public Image getBackground() {
		return background;
	}

	public void setBackground(Image background) {
		this.background = background;
	}

	public Image getGround() {
		return ground;
	}

	public void setGround(Image ground) {
		this.ground = ground;
	}

	public Image getRock() {
		return rock;
	}

	public void setRock(Image rock) {
		this.rock = rock;
	}

	public Image getEntry() {
		return entry;
	}

	public void setEntry(Image entry) {
		this.entry = entry;
	}

	public Image getExit() {
		return exit;
	}

	public void setExit(Image exit) {
		this.exit = exit;
	}
	
}


